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
  private static final int READ_PAIRED_FLAG = 0x1;
  private static final int PROPER_PAIR_FLAG = 0x2;
  private static final int READ_UNMAPPED_FLAG = 0x4;
  private static final int MATE_UNMAPPED_FLAG = 0x8;
  private static final int READ_STRAND_FLAG = 0x10;
  private static final int MATE_STRAND_FLAG = 0x20;
  private static final int FIRST_OF_PAIR_FLAG = 0x40;
  private static final int SECOND_OF_PAIR_FLAG = 0x80;
  private static final int NOT_PRIMARY_ALIGNMENT_FLAG = 0x100;
  private static final int READ_FAILS_VENDOR_QUALITY_CHECK_FLAG = 0x200;
  private static final int DUPLICATE_READ_FLAG = 0x400;
  private static final int SUPPLEMENTARY_ALIGNMENT_FLAG = 0x800;
  
  // Getters
  
  public static final boolean hasFlag(Read read, int flag) {
    return read.getFlags() != null && ((read.getFlags() & flag) != 0);
  }
  
  public static final boolean hasReadPairedFlag(Read read) {
    return hasFlag(read, READ_PAIRED_FLAG);
  }

  public static final boolean hasProperPairFlag(Read read) {
    return hasFlag(read, PROPER_PAIR_FLAG);
  }
  
  public static final boolean hasReadUnmappedFlag(Read read) {
    return hasFlag(read, READ_UNMAPPED_FLAG);
  }
  
  public static final boolean hasMateUnmappedFlag(Read read) {
    return hasFlag(read, MATE_UNMAPPED_FLAG);
  }
  
  public static final boolean hasReadStrandFlag(Read read) {
    return hasFlag(read, READ_STRAND_FLAG);
  }
  
  public static final boolean hasMateStrandFlag(Read read) {
    return hasFlag(read, MATE_STRAND_FLAG);
  }
  
  public static final boolean hasFirstOfPairFlag(Read read) {
    return hasFlag(read, FIRST_OF_PAIR_FLAG);
  }
  
  public static final boolean hasSecondOfPairFlag(Read read) {
    return hasFlag(read, SECOND_OF_PAIR_FLAG);
  }
  
  public static final boolean hasNotPrimaryAlignmentFlag(Read read) {
    return hasFlag(read, NOT_PRIMARY_ALIGNMENT_FLAG);
  }
  
  public static final boolean hasReadFailsVendorQualityCheckFlag(Read read) {
    return hasFlag(read, READ_FAILS_VENDOR_QUALITY_CHECK_FLAG);
  }
  
  public static final boolean hasDuplicateReadFlag(Read read) {
    return hasFlag(read, DUPLICATE_READ_FLAG);
  }
  
  public static final boolean hasSupplementaryAlignmentFlag(Read read) {
    return hasFlag(read, SUPPLEMENTARY_ALIGNMENT_FLAG);
  }
  
  // Setters
  
  public static final void setFlag(Read read, int flag, boolean value) {
    if (value) {
      read.setFlags(read.getFlags() | flag);
    } else {
      read.setFlags(read.getFlags() & ~flag);
    }
  }
  
  public static final void setReadPairedFlag(Read read, boolean value) {
    setFlag(read, READ_PAIRED_FLAG, value);
  }

  public static final void setProperPairFlag(Read read, boolean value) {
    setFlag(read, PROPER_PAIR_FLAG, value);
  }
  
  public static final void setReadUnmappedFlag(Read read, boolean value) {
    setFlag(read, READ_UNMAPPED_FLAG, value);
  }
  
  public static final void setMateUnmappedFlag(Read read, boolean value) {
    setFlag(read, MATE_UNMAPPED_FLAG, value);
  }
  
  public static final void setReadStrandFlag(Read read, boolean value) {
    setFlag(read, READ_STRAND_FLAG, value);
  }
  
  public static final void setMateStrandFlag(Read read, boolean value) {
    setFlag(read, MATE_STRAND_FLAG, value);
  }
  
  public static final void setFirstOfPairFlag(Read read, boolean value) {
    setFlag(read, FIRST_OF_PAIR_FLAG, value);
  }
  
  public static final void setSecondOfPairFlag(Read read, boolean value) {
    setFlag(read, SECOND_OF_PAIR_FLAG, value);
  }
  
  public static final void setNotPrimaryAlignmentFlag(Read read, boolean value) {
    setFlag(read, NOT_PRIMARY_ALIGNMENT_FLAG, value);
  }
  
  public static final void setReadFailsVendorQualityCheckFlag(Read read, boolean value) {
    setFlag(read, READ_FAILS_VENDOR_QUALITY_CHECK_FLAG, value);
  }
  
  public static final void setDuplicateReadFlag(Read read, boolean value) {
    setFlag(read, DUPLICATE_READ_FLAG, value);
  }
  
  public static final void setSupplementaryAlignmentFlag(Read read, boolean value) {
    setFlag(read, SUPPLEMENTARY_ALIGNMENT_FLAG, value);
  }
}
