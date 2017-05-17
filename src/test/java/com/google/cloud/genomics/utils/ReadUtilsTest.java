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
package com.google.cloud.genomics.utils;

import static org.junit.Assert.assertEquals;

import com.google.api.services.genomics.model.CigarUnit;
import com.google.api.services.genomics.model.LinearAlignment;
import com.google.api.services.genomics.model.Position;
import com.google.api.services.genomics.model.Read;
import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RunWith(JUnit4.class)
public class ReadUtilsTest {

  @Test
  public void testGetCigarString() throws Exception {
    Read read = new Read();
    assertEquals(null, ReadUtils.getCigarString(read));

    List<CigarUnit> cigar = Lists.newArrayList(
        new CigarUnit().setOperation("ALIGNMENT_MATCH").setOperationLength(100L),
        new CigarUnit().setOperation("CLIP_SOFT").setOperationLength(3L));
    read.setAlignment(new LinearAlignment().setCigar(cigar));
    assertEquals("100M3S", ReadUtils.getCigarString(read));
  }

  @Test
  public void testGetFlags() throws Exception {
    Read read = new Read();

    // Read unmapped (4)
    assertEquals(4, ReadUtils.getFlags(read));

    // All conditions false
    Position position = new Position().setPosition(1L);
    read.setAlignment(new LinearAlignment().setPosition(position)).setNextMatePosition(position);
    assertEquals(0, ReadUtils.getFlags(read));

    // Read paired (1) + Proper Pair (2) + Read and Mate unmapped (12) +
    // First in pair (64) + Secondary (256) + Duplicate (1024) + Supplementary (2048)
    read = new Read();
    read.setNumberReads(2);
    read.setProperPlacement(true);
    read.setReadNumber(0);
    read.setSecondaryAlignment(true);
    read.setDuplicateFragment(true);
    read.setSupplementaryAlignment(true);

    assertEquals(3407, ReadUtils.getFlags(read));
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

    Read read = ReadUtils.makeRead(record);
    assertEquals((long)0, (long)read.getAlignment().getPosition().getPosition());
    assertEquals(1, read.getAlignment().getCigar().size());
    assertEquals("chr20", read.getAlignment().getPosition().getReferenceName());
    assertEquals(0, (int)read.getReadNumber());
    assertEquals((long)99, (long)read.getNextMatePosition().getPosition());
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

    Read read = ReadUtils.makeRead(record);
    assertEquals((long)0, (long)read.getAlignment().getPosition().getPosition());
    assertEquals(1, read.getAlignment().getCigar().size());
    assertEquals("chr20", read.getAlignment().getPosition().getReferenceName());
    assertEquals(s, read.getInfo().get("FZ").get(0));
  }

  @Test
  public void SamToReadToSamTest() throws IOException {
    String filePath = "src/test/resources/com/google/cloud/genomics/utils/conversion_test.sam";
    File samInput = new File(filePath);
    SamReader reads = SamReaderFactory.makeDefault().open(samInput);
    SAMFileHeader header = reads.getFileHeader();

    int numReads = 0;
    for (SAMRecord sam : reads){
      Read read = ReadUtils.makeRead(sam);
      SAMRecord newSam = ReadUtils.makeSAMRecord(read, header );
      assertEquals(newSam.getSAMString(), sam.getSAMString());
      numReads++;
    }
    assertEquals(19, numReads);//sanity check to make sure we actually read the file
  }
}
