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

import com.google.api.services.genomics.model.CigarUnit;
import com.google.api.services.genomics.model.Position;
import com.google.api.services.genomics.model.Read;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadUtils {

  /**
   * Map from CIGAR operations as represented in the API to standard SAM ones.
   */
  private static Map<String, String> CIGAR_OPERATIONS;
  static {
    CIGAR_OPERATIONS = new HashMap<String, String>();
    CIGAR_OPERATIONS.put("ALIGNMENT_MATCH","M");
    CIGAR_OPERATIONS.put("CLIP_HARD", "H");
    CIGAR_OPERATIONS.put("CLIP_SOFT","S");
    CIGAR_OPERATIONS.put("DELETE", "D");
    CIGAR_OPERATIONS.put("INSERT", "I");
    CIGAR_OPERATIONS.put("PAD", "P");
    CIGAR_OPERATIONS.put("SEQUENCE_MATCH", "=");
    CIGAR_OPERATIONS.put("SEQUENCE_MISMATCH", "X");
    CIGAR_OPERATIONS.put("SKIP", "N");
  }

  public static String getCigarString(Read read) {
    List<CigarUnit> cigar = read.getAlignment() == null ? null : read.getAlignment().getCigar();
    if (cigar != null && cigar.size() > 0) {
      StringBuilder cigarString = new StringBuilder();

      for (CigarUnit unit : cigar) {
        cigarString.append(String.valueOf(unit.getOperationLength()));
        cigarString.append(CIGAR_OPERATIONS.get(unit.getOperation()));
      }
      return cigarString.toString();
    }
    return null;
  }

  private static boolean isUnmapped(Position position) {
    return position == null || position.getPosition() == null;
  }

  private static boolean isReverseStrand(Position position) {
    return position != null && Boolean.TRUE.equals(position.getReverseStrand());
  }

  public static int getFlags(Read read) {
    Position position = read.getAlignment() == null ? null : read.getAlignment().getPosition();
    Position nextMatePosition = read.getNextMatePosition();

    int flags = 0;

    flags += Integer.valueOf(2).equals(read.getNumberReads()) ? 1 : 0; // read_paired
    flags += Boolean.TRUE.equals(read.getProperPlacement()) ? 2 : 0; // read_proper_pair
    flags += isUnmapped(position) ? 4 : 0; // read_unmapped
    flags += isUnmapped(nextMatePosition) ? 8 : 0; // mate_unmapped
    flags += isReverseStrand(position) ? 16 : 0 ; // read_reverse_strand
    flags += isReverseStrand(nextMatePosition) ? 32 : 0; // mate_reverse_strand
    flags += Integer.valueOf(0).equals(read.getReadNumber()) ? 64 : 0; // first_in_pair
    flags += Integer.valueOf(1).equals(read.getReadNumber()) ? 128 : 0; // second_in_pair
    flags += Boolean.TRUE.equals(read.getSecondaryAlignment()) ? 256 : 0; // secondary_alignment
    flags += Boolean.TRUE.equals(read.getFailedVendorQualityChecks()) ? 512 : 0; // failed_quality
    flags += Boolean.TRUE.equals(read.getDuplicateFragment()) ? 1024 : 0; // duplicate_read
    flags += Boolean.TRUE.equals(read.getSupplementaryAlignment()) ? 2048 : 0; // supplementary

    return flags;
  }
}
