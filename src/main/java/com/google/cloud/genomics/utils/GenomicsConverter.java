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

import com.google.api.services.genomics.model.Header;
import com.google.api.services.genomics.model.HeaderSection;
import com.google.api.services.genomics.model.Program;
import com.google.api.services.genomics.model.Read;
import com.google.api.services.genomics.model.ReadGroup;
import com.google.api.services.genomics.model.ReferenceSequence;
import com.google.common.collect.Lists;

import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.SAMProgramRecord;
import net.sf.samtools.SAMReadGroupRecord;
import net.sf.samtools.SAMSequenceRecord;
import net.sf.samtools.SAMRecord.SAMTagAndValue;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMRecord;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

/**
 * A utility class for converting between genomics data representations from the Google Genomics API
 * and that of Picard Tools.
 *
 * Currently this is relying on the third party picard maven artifact, so the SAM objects are
 * in net.sf.samtools. This code will switch to the real picard maven dependency as soon as it's
 * available (in progress as of 8/2014).
 *
 * Note: Conversion between Picard and Google Genomics is lossy, some information will be lost.
 *
 * Currently supported conversions:
 *      Read/SAMRecord
 *      HeaderSection/SAMFileHeader
 */
public abstract class GenomicsConverter {
  private static final Calendar GMT_CALENDAR = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

  /**
   * Create a Google Genomics Read from a SAMRecord, preserving as much information as possible.
   * Note: Id, ReadsetId, and AlignedBases fields are lost.
   *
   * @param record The SAMRecord to transform into a Read.
   * @return The resulting Google Genomics Read.
   */
  public static final Read makeRead(SAMRecord record) {
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
    read.setBaseQuality(record.getBaseQualityString());

    if (record.getAttributes() != null) {
      HashMap<String, List<String>> tags = new HashMap<String, List<String>>();
      for (SAMTagAndValue tagPair : record.getAttributes()) {
        if (tags.containsKey(tagPair.tag)) {
          tags.get(tagPair.tag).add(tagPair.value.toString());
        } else {
          tags.put(tagPair.tag, Lists.newArrayList((tagPair.value.toString())));
        }
      }
      read.setTags(tags);
    }

    return read;
  }

  /**
   * Create a SAMRecord from a Google Genomics Read, preserving as much information as possible.
   *
   * @param read The Read to transform into a SAMRecord.
   * @param header The SAMFileHeader to initiate the SAMRecord from.
   * @return The resulting SAMRecord.
   */
  public static final SAMRecord makeSAMRecord(Read read, SAMFileHeader header) {
    SAMRecord record = new SAMRecord(header);
    if (read.getName() != null) {
      record.setReadName(read.getName());
    }
    if (read.getFlags() != null) {
      record.setFlags(read.getFlags());
    }
    if (read.getReferenceSequenceName() != null) {
      record.setReferenceName(read.getReferenceSequenceName());
    }
    if (read.getPosition() != null) {
      record.setAlignmentStart(read.getPosition());
    }
    if (read.getMappingQuality() != null) {
      record.setMappingQuality(read.getMappingQuality());
    }
    if (read.getCigar() != null) {
      record.setCigarString(read.getCigar());
    }
    if (read.getMateReferenceSequenceName() != null) {
      record.setMateReferenceName(read.getMateReferenceSequenceName());
    }
    if (read.getMatePosition() != null) {
      record.setMateAlignmentStart(read.getMatePosition());
    }
    if (read.getTemplateLength() != null) {
      record.setInferredInsertSize(read.getTemplateLength());
    }
    if (read.getOriginalBases() != null) {
      record.setReadString(read.getOriginalBases());
    }
    if (read.getBaseQuality() != null) {
      record.setBaseQualityString(read.getBaseQuality());
    }

    Map<String, List<String>> tags = read.getTags();
    if (tags != null) {
      for (String tag : tags.keySet()) {
        List<String> values = tags.get(tag);
        if (values != null) {
          for (String value : values) {
            record.setAttribute(tag, value);
          }
        }
      }
    }

    return record;
  }

  /**
   * Create a SAMRecord from a Google Genomics Read, preserving as much information as possible.
   * Input HeaderSection will be converted to a SAMFileHeader and used to initialize the SAMRecord
   * 
   * @param read The Read to transform into a SAMRecord.
   * @param header The HeaderSection to initiate the SAMRecord from.
   * @return The resulting SAMRecord.
   */
  public static final SAMRecord makeSAMRecord(Read read, HeaderSection header) {
    return makeSAMRecord(read, makeSAMFileHeader(header));
  }

  /**
   * Create a SAMRecord from a Google Genomics Read, preserving as much information as possible.
   * Default SAMFileHeader will be used to initialize the SAMRecord.
   * 
   * @param read The Read to transform into a SAMRecord.
   * @return The resulting SAMRecord.
   */
  public static final SAMRecord makeSAMRecord(Read read) {
    return makeSAMRecord(read, new SAMFileHeader());
  }

  /**
   * Generates a HeaderSection from a SAMFileHeader.
   * Lost fields:
   * FileUri, RefSequences.md5Checksum, RefSequences.uri, ReadGroups.ProcessingProgram,
   * ReadGroups.ReadGroupId, ProgramRecords.ProgramGroupId, ReferenceSequence.uri, etc
   * 
   * @param samHeader The SAMFileHeader to transform to a HeaderSection
   * @return The resulting HeaderSection
   */
  public static final HeaderSection makeHeaderSection(SAMFileHeader samHeader) {
    HeaderSection headerSection = new HeaderSection();

    Header header = new Header();
    header.setVersion(samHeader.getVersion());
    header.setSortingOrder(samHeader.getSortOrder().toString());
    headerSection.setHeaders(Lists.newArrayList(header));

    if (samHeader.getSequenceDictionary() != null 
        && samHeader.getSequenceDictionary().getSequences() != null) {
      List<ReferenceSequence> sequenceList = Lists.newArrayList();
      for (SAMSequenceRecord sequence : samHeader.getSequenceDictionary().getSequences()) {
        ReferenceSequence refSequence = new ReferenceSequence();
        refSequence.setAssemblyId(sequence.getAssembly());
        refSequence.setLength(sequence.getSequenceLength());
        refSequence.setName(sequence.getSequenceName());
        refSequence.setSpecies(sequence.getSpecies());
        sequenceList.add(refSequence);
      }
      headerSection.setRefSequences(sequenceList);
    }

    if (samHeader.getReadGroups() != null) {
      List<ReadGroup> readgroupList = Lists.newArrayList();
      for (SAMReadGroupRecord samReadgroup : samHeader.getReadGroups()) {
        ReadGroup readgroup = new ReadGroup();
        if(samReadgroup.getRunDate() != null) {
          Calendar calendar = GMT_CALENDAR;
          calendar.setTime(samReadgroup.getRunDate());
          readgroup.setDate(DatatypeConverter.printDateTime(calendar));
        }
        readgroup.setDescription(samReadgroup.getDescription());
        readgroup.setFlowOrder(samReadgroup.getFlowOrder());
        readgroup.setId(samReadgroup.getId());
        readgroup.setKeySequence(samReadgroup.getKeySequence());
        readgroup.setLibrary(samReadgroup.getLibrary());
        readgroup.setPlatformUnit(samReadgroup.getPlatformUnit());
        readgroup.setPredictedInsertSize(samReadgroup.getPredictedMedianInsertSize());
        readgroup.setSample(samReadgroup.getSample());
        readgroup.setSequencingCenterName(samReadgroup.getSequencingCenter());
        readgroup.setSequencingTechnology(samReadgroup.getPlatform());
        readgroupList.add(readgroup);
      }
      headerSection.setReadGroups(readgroupList);
    }

    if (samHeader.getProgramRecords() != null) {
      List<Program> programList = Lists.newArrayList();
      for (SAMProgramRecord samProgram : samHeader.getProgramRecords()) {
        Program program = new Program();
        program.setCommandLine(samProgram.getCommandLine());
        program.setId(samProgram.getProgramGroupId());
        program.setName(samProgram.getProgramName());
        program.setPrevProgramId(samProgram.getPreviousProgramGroupId());
        program.setVersion(samProgram.getProgramVersion());
        programList.add(program);
      }
      headerSection.setPrograms(programList);
    }

    if (samHeader.getComments() != null) {
      List<String> commentList = Lists.newArrayList(); 
      for (String comment : samHeader.getComments()) {
        // SAMFileHeader makes all comments have a @CO\t prefix
        commentList.add(comment.replaceAll("@CO\t", ""));
      }
      headerSection.setComments(commentList);
    }

    return headerSection;
  }

  /**
   * Genereates a SAMFileHeader from a HeaderSection.
   * Note: Version field is not setable and @CO\t is prepended to all comments.
   * 
   * @param headerSection The HeaderSection to transform to SAMFileHeader.
   * @return The resulting SAMFileHeader.
   */
  public static final SAMFileHeader makeSAMFileHeader(HeaderSection headerSection) {
    SAMFileHeader samHeader = new SAMFileHeader();

    Header header = GenomicsUtils.getHeader(headerSection);
    if (header != null && header.getSortingOrder() != null) {
      samHeader.setSortOrder(SAMFileHeader.SortOrder.valueOf(header.getSortingOrder()));
    }

    if (headerSection.getRefSequences() != null) {
      SAMSequenceDictionary dict = new SAMSequenceDictionary();
      for (ReferenceSequence refSequence : headerSection.getRefSequences()) {
        if (refSequence.getName() != null && refSequence.getLength() != null) {
          SAMSequenceRecord sequence = new SAMSequenceRecord(
              refSequence.getName(), refSequence.getLength());
          if (refSequence.getAssemblyId() != null) {
            sequence.setAssembly(refSequence.getAssemblyId());
          }
          if (refSequence.getSpecies() != null) {
            sequence.setSpecies(refSequence.getSpecies());
          }
          dict.addSequence(sequence);
        }
      }
      samHeader.setSequenceDictionary(dict);
    }

    if (headerSection.getReadGroups() != null) {
      List<SAMReadGroupRecord> readgroups = Lists.newArrayList();
      for (ReadGroup readgroup : headerSection.getReadGroups()) {
        if (readgroup.getId() != null) {
          SAMReadGroupRecord samReadgroup = new SAMReadGroupRecord(readgroup.getId());
          if (readgroup.getDate() != null) {
            samReadgroup.setRunDate(DatatypeConverter.parseDateTime(readgroup.getDate()).getTime());
          }
          if (readgroup.getDescription() != null) {
            samReadgroup.setDescription(readgroup.getDescription());
          }
          if (readgroup.getFlowOrder() != null) {
            samReadgroup.setFlowOrder(readgroup.getFlowOrder());
          }
          if (readgroup.getKeySequence() != null) {
            samReadgroup.setKeySequence(readgroup.getKeySequence());
          }
          if (readgroup.getLibrary() != null) {
            samReadgroup.setLibrary(readgroup.getLibrary());
          }
          if (readgroup.getPlatformUnit() != null) {
            samReadgroup.setPlatformUnit(readgroup.getPlatformUnit());
          }
          if (readgroup.getPredictedInsertSize() != null) {
            samReadgroup.setPredictedMedianInsertSize(readgroup.getPredictedInsertSize());
          }
          if (readgroup.getSample() != null) {
            samReadgroup.setSample(readgroup.getSample());
          }
          if (readgroup.getSequencingCenterName() != null) {
            samReadgroup.setSequencingCenter(readgroup.getSequencingCenterName());
          }
          if (readgroup.getSequencingTechnology() != null) {
            samReadgroup.setPlatform(readgroup.getSequencingTechnology());
          }
          readgroups.add(samReadgroup);
        }
      }
      samHeader.setReadGroups(readgroups);
    }

    if (headerSection.getPrograms() != null) {
      List<SAMProgramRecord> programs = Lists.newArrayList();
      for (Program program : headerSection.getPrograms()) {
        if (program.getId() != null) {
          SAMProgramRecord samProgram = new SAMProgramRecord(program.getId());
          if (program.getCommandLine() != null) {
            samProgram.setCommandLine(program.getCommandLine());
          }
          if (program.getName() != null) {
            samProgram.setProgramName(program.getName());
          }
          if (program.getPrevProgramId() != null) {
            samProgram.setPreviousProgramGroupId(program.getPrevProgramId());
          }
          if (program.getVersion() != null) {
            samProgram.setProgramVersion(program.getVersion());
          }
          programs.add(samProgram);
        }
      }
      samHeader.setProgramRecords(programs);
    }

    if (headerSection.getComments() != null) {
      List<String> comments = Lists.newArrayList();
      for (String comment : headerSection.getComments()) {
        if (comment != null) {
          comments.add(comment);
        }
      }
      samHeader.setComments(comments);
    }

    return samHeader;
  }  
}
