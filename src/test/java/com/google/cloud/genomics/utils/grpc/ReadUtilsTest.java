/*
 * Copyright (C) 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.genomics.utils.grpc;

import static org.junit.Assert.assertEquals;
import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.TextCigarCodec;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.Lists;
import com.google.genomics.v1.CigarUnit;
import com.google.genomics.v1.CigarUnit.Operation;
import com.google.genomics.v1.LinearAlignment;
import com.google.genomics.v1.Position;
import com.google.genomics.v1.Read;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;

@RunWith(JUnit4.class)
public class ReadUtilsTest {

  @Test
  public void testGetCigarString() throws Exception {
    Read.Builder read = Read.newBuilder();
    assertEquals(null, ReadUtils.getCigarString(read.build()));

    List<CigarUnit> cigar = Lists.newArrayList(
        CigarUnit.newBuilder().setOperation(Operation.ALIGNMENT_MATCH).setOperationLength(100L).build(),
        CigarUnit.newBuilder().setOperation(Operation.CLIP_SOFT).setOperationLength(3L).build());
    read.setAlignment(LinearAlignment.newBuilder().addAllCigar(cigar));
    assertEquals("100M3S", ReadUtils.getCigarString(read.build()));
  }

  @Test
  public void testGetFlags() throws Exception {
    Read.Builder read = Read.newBuilder();

    // Read unmapped (4) + Mate unmapped (8)
    assertEquals(12, ReadUtils.getFlags(read.build()));

    // All conditions false
    Position position = Position.newBuilder().setPosition(1L).build();
    read.setAlignment(LinearAlignment.newBuilder().setPosition(position)).setNextMatePosition(position).build();
    assertEquals(0, ReadUtils.getFlags(read.build()));

    // Read paired (1) + Proper Pair (2) + Read and Mate unmapped (12) +
    // First in pair (64) + Secondary (256) + Duplicate (1024) + Supplementary (2048)
    read = Read.newBuilder();
    read.setNumberReads(2);
    read.setProperPlacement(true);
    read.setReadNumber(0);
    read.setSecondaryAlignment(true);
    read.setDuplicateFragment(true);
    read.setSupplementaryAlignment(true);

    assertEquals(3407, ReadUtils.getFlags(read.build()));
  }

  @Test
  public void testConversion() {
    SAMRecord record = new SAMRecord(null);
    record.setReferenceName("chr20");
    record.setAlignmentStart(1);
    record.setCigarString(String.format("%dM", 10));
    record.setMateReferenceName("chr20");
    record.setMateAlignmentStart(100);
    record.setReadPairedFlag(true);
    record.setFirstOfPairFlag(true);
    record.setMateNegativeStrandFlag(true);

    Read read = ReadUtils.makeReadGrpc(record);
    assertEquals(0, read.getAlignment().getPosition().getPosition());
    assertEquals(1, read.getAlignment().getCigarList().size());
    assertEquals("chr20", read.getAlignment().getPosition().getReferenceName());
    assertEquals(0, read.getReadNumber());
    assertEquals(99, read.getNextMatePosition().getPosition());
    assertEquals("chr20", read.getNextMatePosition().getReferenceName());
    assertEquals(true, read.getNextMatePosition().getReverseStrand());
  }
  @Test
  public void testByteArrayAttributes() {
    // Client code of SamRecord can pass anything to setAttribute including
    // byte[] (which doesn't have toString defined). This verifies
    // we handle that case correctly.
    SAMRecord record = new SAMRecord(null);
    record.setReferenceName("chr20");
    record.setAlignmentStart(1);
    record.setCigarString(String.format("%dM", 10));
    String s = "123456";
    record.setAttribute("FZ", s.getBytes());

    Read read = ReadUtils.makeReadGrpc(record);
    assertEquals(0, read.getAlignment().getPosition().getPosition());
    assertEquals(1, read.getAlignment().getCigarList().size());
    assertEquals("chr20", read.getAlignment().getPosition().getReferenceName());
    assertEquals(s, read.getInfo().get("FZ").getValues(0).getStringValue());
  }

  @Test
  public void SamToReadToSamTest() {
    String filePath = "src/test/resources/com/google/cloud/genomics/utils/conversion_test.sam";
    File samInput = new File(filePath);
    SamReader reads = SamReaderFactory.makeDefault().open(samInput);
    SAMFileHeader header = reads.getFileHeader();

    int numReads = 0;
    for (SAMRecord sam : reads){
      Read read = ReadUtils.makeReadGrpc(sam);
      SAMRecord newSam = ReadUtils.makeSAMRecord(read, header );
      final String originalSamString = sam.getSAMString();
      final String postConversionString = newSam.getSAMString();
      assertEquals(originalSamString, postConversionString);
      numReads++;
    }
    assertEquals(19, numReads);//sanity check to make sure we actually read the file
  }

  private void testGetReferenceSequenceHelper(final String seq, final String cigar, final String md,
      final String expectedReference) throws IOException {
    LinearAlignment.Builder alignment = LinearAlignment.newBuilder();
    Cigar cigars = TextCigarCodec.decode(cigar);
    for (int i = 0; i < cigars.numCigarElements(); i++) {
      CigarElement c = cigars.getCigarElement(i);
      CigarUnit.Builder unit = CigarUnit.newBuilder().setOperationLength(c.getLength());
      switch (c.getOperator()) {
        case M:
          unit.setOperation(CigarUnit.Operation.ALIGNMENT_MATCH);
          break;
        case I:
          unit.setOperation(CigarUnit.Operation.INSERT);
          break;
        case D:
          unit.setOperation(CigarUnit.Operation.DELETE);
          break;
        case N:
          unit.setOperation(CigarUnit.Operation.SKIP);
          break;
        case S:
          unit.setOperation(CigarUnit.Operation.CLIP_SOFT);
          break;
        case H:
          unit.setOperation(CigarUnit.Operation.CLIP_HARD);
          break;
        case P:
          unit.setOperation(CigarUnit.Operation.PAD);
          break;
        case EQ:
          unit.setOperation(CigarUnit.Operation.SEQUENCE_MATCH);
          break;
        case X:
          unit.setOperation(CigarUnit.Operation.SEQUENCE_MISMATCH);
          break;
      }
      alignment.addCigar(unit.build());
    }
    final Read.Builder rec = Read.newBuilder()
        .setFragmentName("test")
        .setAlignedSequence(seq)
        .setAlignment(alignment.build());
    rec.getMutableInfo().put("MD",
        ListValue.newBuilder().addValues(0, Value.newBuilder().setStringValue(md).build()).build());
    final String refBases = ReadUtils.inferReferenceSequenceByParsingMdFlag(rec.build());
    assertEquals(refBases, expectedReference);
  }

  private static final Object[][] TEST_DATA = new Object[][]{
    {"ACGTACGTACGT", "2H12M", "12", "ACGTACGTACGT"},
    {"ACGTACGTACGT", "4M4I4M2H", "8", "ACGT----ACGT"},
    {"ACGTACGTACGT", "2S4M2I4M2S", "8", "00GTAC--ACGT00"},
    {"ACGTACGTACGT", "6M2D6M2H", "4GA^TT0TG4", "ACGTGATGACGT"},
    {"ACGTACGTACGT", "6M2N6M2H", "4GA0TG4", "ACGTGATGACGT"},
    {"ACGTACGTACGT", "6M2N6M2H", "4GATG4", "ACGTGATGACGT"}};

  @Test
  public void testGetReferenceSequence() throws IOException {
    for (Object[] o : TEST_DATA) {
      testGetReferenceSequenceHelper((String) o[0],
          (String) o[1],
          (String) o[2],
          (String) o[3]);
    }
  }
}
