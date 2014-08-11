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

import com.google.api.services.genomics.model.Read;

/**
 * Utilities for working with flags in Reads
 */
public class ReadFlagUtils {
  
  // Read flags defined by SAM/BAM File format
  public static final int READ_PAIRED_FLAG = 0x1;
  public static final int PROPER_PAIR_FLAG = 0x2;
  public static final int READ_UNMAPPED_FLAG = 0x4;
  public static final int MATE_UNMAPPED_FLAG = 0x8;
  public static final int READ_STRAND_FLAG = 0x10;
  public static final int MATE_STRAND_FLAG = 0x20;
  public static final int FIRST_OF_PAIR_FLAG = 0x40;
  public static final int SECOND_OF_PAIR_FLAG = 0x80;
  public static final int NOT_PRIMARY_ALIGNMENT_FLAG = 0x100;
  public static final int READ_FAILS_VENDOR_QUALITY_CHECK_FLAG = 0x200;
  public static final int DUPLICATE_READ_FLAG = 0x400;
  public static final int SUPPLEMENTARY_ALIGNMENT_FLAG = 0x800;
  
  // Getters
  
  public static boolean hasFlag(Read read, int flag) {
    return read.getFlags() != null && ((read.getFlags() & flag) != 0);
  }
  
  public static boolean hasReadPairedFlag(Read read) {
    return hasFlag(read, READ_PAIRED_FLAG);
  }

  public static boolean hasProperPairFlag(Read read) {
    return hasFlag(read, PROPER_PAIR_FLAG);
  }
  
  public static boolean hasReadUnmappedFlag(Read read) {
    return hasFlag(read, READ_UNMAPPED_FLAG);
  }
  
  public static boolean hasMateUnmappedFlag(Read read) {
    return hasFlag(read, MATE_UNMAPPED_FLAG);
  }
  
  public static boolean hasReadStrandFlag(Read read) {
    return hasFlag(read, READ_STRAND_FLAG);
  }
  
  public static boolean hasMateStrandFlag(Read read) {
    return hasFlag(read, MATE_STRAND_FLAG);
  }
  
  public static boolean hasFirstOfPairFlag(Read read) {
    return hasFlag(read, FIRST_OF_PAIR_FLAG);
  }
  
  public static boolean hasSecondOfPairFlag(Read read) {
    return hasFlag(read, SECOND_OF_PAIR_FLAG);
  }
  
  public static boolean hasNotPrimaryAlignmentFlag(Read read) {
    return hasFlag(read, NOT_PRIMARY_ALIGNMENT_FLAG);
  }
  
  public static boolean hasReadFailsVendorQualityCheckFlag(Read read) {
    return hasFlag(read, READ_FAILS_VENDOR_QUALITY_CHECK_FLAG);
  }
  
  public static boolean hasDuplicateReadFlag(Read read) {
    return hasFlag(read, DUPLICATE_READ_FLAG);
  }
  
  public static boolean hasSupplementaryAlignmentFlag(Read read) {
    return hasFlag(read, SUPPLEMENTARY_ALIGNMENT_FLAG);
  }
  
  // Setters
  
  public static void setFlag(Read read, int flag, boolean value) {
    if (value) {
      read.setFlags(read.getFlags() | flag);
    } else {
      read.setFlags(read.getFlags() & ~flag);
    }
  }
  
  public static void setReadPairedFlag(Read read, boolean value) {
    setFlag(read, READ_PAIRED_FLAG, value);
  }

  public static void setProperPairFlag(Read read, boolean value) {
    setFlag(read, PROPER_PAIR_FLAG, value);
  }
  
  public static void setReadUnmappedFlag(Read read, boolean value) {
    setFlag(read, READ_UNMAPPED_FLAG, value);
  }
  
  public static void setMateUnmappedFlag(Read read, boolean value) {
    setFlag(read, MATE_UNMAPPED_FLAG, value);
  }
  
  public static void setReadStrandFlag(Read read, boolean value) {
    setFlag(read, READ_STRAND_FLAG, value);
  }
  
  public static void setMateStrandFlag(Read read, boolean value) {
    setFlag(read, MATE_STRAND_FLAG, value);
  }
  
  public static void setFirstOfPairFlag(Read read, boolean value) {
    setFlag(read, FIRST_OF_PAIR_FLAG, value);
  }
  
  public static void setSecondOfPairFlag(Read read, boolean value) {
    setFlag(read, SECOND_OF_PAIR_FLAG, value);
  }
  
  public static void setNotPrimaryAlignmentFlag(Read read, boolean value) {
    setFlag(read, NOT_PRIMARY_ALIGNMENT_FLAG, value);
  }
  
  public static void setReadFailsVendorQualityCheckFlag(Read read, boolean value) {
    setFlag(read, READ_FAILS_VENDOR_QUALITY_CHECK_FLAG, value);
  }
  
  public static void setDuplicateReadFlag(Read read, boolean value) {
    setFlag(read, DUPLICATE_READ_FLAG, value);
  }
  
  public static void setSupplementaryAlignmentFlag(Read read, boolean value) {
    setFlag(read, SUPPLEMENTARY_ALIGNMENT_FLAG, value);
  }
}
