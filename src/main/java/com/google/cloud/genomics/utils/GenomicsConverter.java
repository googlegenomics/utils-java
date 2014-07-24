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
import com.google.common.collect.Lists;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecord.SAMTagAndValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class for converting between genomics data representations from the Google Genomics API
 * and that of Picard Tools.
 *
 * Currently this is relying on the third party picard maven artifact, so the SAM objects are
 * in net.sf.samtools. This code will switch to the real picard maven dependency as soon as it's
 * available (in progress as of 7/2014).
 *
 * Note: Conversion between Picard and Google Genomics is lossy, some information will be lost.
 *
 * Currently supported conversions:
 *      Read/SAMRecord
 */
public abstract class GenomicsConverter {

  /**
   * Create a Google Genomics Read from a SAMRecord, preserving as much information as possible.
   *
   * @param record The SAMRecord to transform into a Read.
   * @return The resulting Google Genomics Read.
   */
  public static Read makeRead(SAMRecord record) {
    Read read = new Read();
    read.setName(record.getReadName());
    read.setFlags(record.getFlags());
    read.setReferenceSequenceName(record.getReferenceName());
    read.setPosition(record.getAlignmentStart());
    read.setMappingQuality(record.getMappingQuality());
    read.setCigar(record.getCigarString());
    read.setMateReferenceSequenceName(record.getMateReferenceName());
    read.setMatePosition(record.getMateAlignmentStart());
    read.setTemplateLength(record.getInferredInsertSize());
    read.setOriginalBases(record.getReadString());
    read.setAlignedBases(null); // SAMRecord does not have Aligned Bases
    read.setBaseQuality(record.getBaseQualityString());

    HashMap<String, List<String>> tags = new HashMap<String, List<String>>();
    for(SAMTagAndValue tagPair : record.getAttributes()) {
      if (tags.containsKey(tagPair.tag)) {
        tags.get(tagPair.tag).add(tagPair.value.toString());
      } else {
        tags.put(tagPair.tag, Lists.newArrayList((tagPair.value.toString())));
      }
    }
    read.setTags(tags);

    return read;
  }

  /**
   * Create a SAMRecord from a Google Genomics Read, preserving as much information as possible.
   *
   * @param read The Read to transform into a SAMRecord.
   * @return The resulting SAMRecord.
   */
  public static SAMRecord makeSAMRecord(Read read) {
    SAMRecord record = new SAMRecord(new SAMFileHeader());
    record.setReadName(read.getName());
    record.setFlags(read.getFlags());
    record.setReferenceName(read.getReferenceSequenceName());
    record.setAlignmentStart(read.getPosition());
    record.setMappingQuality(read.getMappingQuality());
    record.setCigarString(read.getCigar());
    record.setMateReferenceName(read.getMateReferenceSequenceName());
    record.setMateAlignmentStart(read.getMatePosition());
    record.setInferredInsertSize(read.getTemplateLength());
    record.setReadString(read.getOriginalBases());
    record.setBaseQualityString(read.getBaseQuality());

    Map<String, List<String>> tags = read.getTags();
    for (String tag : tags.keySet()) {
      List<String> values = tags.get(tag);
      for (String value : values) {
        record.setAttribute(tag, value);
      }
    }

    return record;
  }
}