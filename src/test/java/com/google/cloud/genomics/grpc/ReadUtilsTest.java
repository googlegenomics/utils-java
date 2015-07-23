/*
 * Copyright 2015 Google.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.genomics.grpc;

import static org.junit.Assert.assertEquals;

import com.google.genomics.v1.CigarUnit;
import com.google.genomics.v1.LinearAlignment;
import com.google.genomics.v1.Read;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;

import htsjdk.samtools.Cigar;
import htsjdk.samtools.CigarElement;
import htsjdk.samtools.TextCigarCodec;

import org.junit.Test;

import java.io.IOException;

/**
 * Tests for the ReadUtils class.
 */
public class ReadUtilsTest {

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
