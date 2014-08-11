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

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import net.sf.samtools.SAMProgramRecord;
import net.sf.samtools.SAMSequenceRecord;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.SAMReadGroupRecord;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMRecord;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests for GenomicsConverter class
 * 
 * NOTE:
 * SAMRecord defaults BaseQualityString, CigarString, MateReferenceSequenceName, OriginalBases,
 * and ReferenceSequenceName to "*" instead of null. Behavior is not documented and may change.
 * 
 * SAMFileHeader defaults version to 1.4 and sort order to unsorted. Again, may change.
 */
@RunWith(JUnit4.class)
public class GenomicsConverterTest {

  @Test
  public void testFullReadToSAMRecord() {
    // Setup
    Read read = new Read();
    read.setBaseQuality("TEST_QUALITY");
    read.setCigar("TEST_CIGAR");
    read.setFlags(122);
    read.setMappingQuality(255);
    read.setMatePosition(40);
    read.setMateReferenceSequenceName("TEST_MATEREFNAME");
    read.setName("TEST_NAME");
    read.setOriginalBases("TEST_BASES");
    read.setPosition(1);
    read.setReferenceSequenceName("TEST_REFNAME");
    read.setTemplateLength(100);
    Map<String, List<String>> tags = new HashMap<String, List<String>>();
    tags.put("RG", Lists.newArrayList("TEST_READGROUP"));
    read.setTags(tags);
    
    // Conversion
    SAMRecord record = GenomicsConverter.makeSAMRecord(read);
    
    // Tests
    assertEquals(record.getBaseQualityString(), "TEST_QUALITY");
    assertEquals(record.getCigarString(), "TEST_CIGAR");
    assertEquals(record.getFlags(), 122);
    assertEquals(record.getMappingQuality(), 255);
    assertEquals(record.getMateAlignmentStart(), 40);
    assertEquals(record.getMateReferenceName(), "TEST_MATEREFNAME");
    assertEquals(record.getReadName(), "TEST_NAME");
    assertEquals(record.getReadString(), "TEST_BASES");
    assertEquals(record.getAlignmentStart(), 1);
    assertEquals(record.getReferenceName(), "TEST_REFNAME");
    assertEquals(record.getInferredInsertSize(), 100);
    assertEquals(record.getAttribute("RG"), "TEST_READGROUP");
  }
  
  @Test
  public void testEmptyReadToSAMRecord() {
    // Behavior of new SAMRecord is not documented, so make no assertions about fields. 
    // Just make sure that no readgroups exist
    
    Read read = new Read();
    
    SAMRecord record = GenomicsConverter.makeSAMRecord(read);
    
    assertEquals(record.getAttribute("RG"), null);
  }
  
  @Test
  public void testFullSAMRecordToRead() {
    // Setup
    SAMRecord record = new SAMRecord(new SAMFileHeader());
    record.setBaseQualityString("TEST_QUALITY");
    record.setCigarString("TEST_CIGAR");
    record.setFlags(122);
    record.setMappingQuality(255);
    record.setMateAlignmentStart(40);
    record.setMateReferenceName("TEST_MATEREFNAME");
    record.setReadName("TEST_NAME");
    record.setReadString("TEST_BASES");
    record.setAlignmentStart(1);
    record.setReferenceName("TEST_REFNAME");
    record.setInferredInsertSize(100);
    record.setAttribute("RG", "TEST_READGROUP");
    
    // Conversion
    Read read = GenomicsConverter.makeRead(record);
    
    // Tests
    assertEquals(read.getBaseQuality(), "TEST_QUALITY");
    assertEquals(read.getCigar(), "TEST_CIGAR");
    assertEquals(read.getFlags(), Integer.valueOf(122));
    assertEquals(read.getMappingQuality(), Integer.valueOf(255));
    assertEquals(read.getMatePosition(), Integer.valueOf(40));
    assertEquals(read.getMateReferenceSequenceName(), "TEST_MATEREFNAME");
    assertEquals(read.getName(), "TEST_NAME");
    assertEquals(read.getOriginalBases(), "TEST_BASES");
    assertEquals(read.getPosition(), Integer.valueOf(1));
    assertEquals(read.getReferenceSequenceName(), "TEST_REFNAME");
    assertEquals(read.getTemplateLength(), Integer.valueOf(100));
  }
  
  @Test
  public void testEmptySAMRecordToRead() {
    // Behavior of new SAMRecord is not documented, so make no assertions about fields. 
    // Just make sure that no readgroups exist
    
    SAMRecord record = new SAMRecord(new SAMFileHeader());
    
    Read read = GenomicsConverter.makeRead(record);
    
    assertEquals(GenomicsUtils.getReadgroup(read), GenomicsUtils.NO_READGROUP);
  }
  
  @Test
  public void testFullHeaderSectionToSAMFileHeader() {
    // Setup
    HeaderSection header = new HeaderSection();
    
    Header HD = new Header();
    HD.setSortingOrder("queryname");
    header.setHeaders(Lists.newArrayList(HD));
    
    ReferenceSequence SQ = new ReferenceSequence();
    SQ.setAssemblyId("TEST_ASSEMBLY");
    SQ.setLength(141);
    SQ.setName("TEST_SQNAME");
    SQ.setSpecies("TEST_SPECIES");
    header.setRefSequences(Lists.newArrayList(SQ));
    
    ReadGroup RG = new ReadGroup();
    RG.setDate("1970-01-01T00:00:00Z");
    RG.setDescription("TEST_DESCRIPTION");
    RG.setId("TEST_RGID");
    RG.setKeySequence("TEST_SEQUENCE");
    RG.setLibrary("TEST_LIBRARY");
    RG.setPlatformUnit("TEST_PLATFORM");
    RG.setPredictedInsertSize(144);
    RG.setSample("TEST_SAMPLE");
    RG.setSequencingCenterName("TEST_SEQUENCECENTER");
    RG.setSequencingTechnology("TEST_TECHNOLOGY");
    header.setReadGroups(Lists.newArrayList(RG));
    
    Program PG = new Program();
    PG.setCommandLine("TEST_COMMAND");
    PG.setId("TEST_PGID");
    PG.setName("TEST_PGNAME");
    PG.setPrevProgramId("TEST_PREVID");
    PG.setVersion("TEST_PGVERSION");
    header.setPrograms(Lists.newArrayList(PG));
    
    header.setComments(Lists.newArrayList("TEST_COMMENT"));
    
    // Conversion
    SAMFileHeader samHeader = GenomicsConverter.makeSAMFileHeader(header);
    
    // Tests
    assertEquals(samHeader.getSortOrder(), SAMFileHeader.SortOrder.valueOf("queryname"));
    
    SAMSequenceDictionary dict = samHeader.getSequenceDictionary();
    assertEquals(dict.size(), 1);
    SAMSequenceRecord sqrec = dict.getSequence(0);
    assertEquals(sqrec.getAssembly(), "TEST_ASSEMBLY");
    assertEquals(sqrec.getSequenceLength(), 141);
    assertEquals(sqrec.getSequenceName(), "TEST_SQNAME");
    assertEquals(sqrec.getSpecies(), "TEST_SPECIES");
    assertNotNull(dict.getSequence("TEST_SQNAME"));
    assertNull(dict.getSequence("DOESNT_EXIST"));
    
    assertEquals(samHeader.getReadGroups().size(), 1);
    SAMReadGroupRecord rgrec = samHeader.getReadGroups().get(0);
    assertEquals(rgrec.getRunDate(), new Date(0));
    assertEquals(rgrec.getDescription(), "TEST_DESCRIPTION");
    assertEquals(rgrec.getId(), "TEST_RGID");
    assertEquals(rgrec.getKeySequence(), "TEST_SEQUENCE");
    assertEquals(rgrec.getLibrary(), "TEST_LIBRARY");
    assertEquals(rgrec.getPlatformUnit(), "TEST_PLATFORM");
    assertEquals(rgrec.getPredictedMedianInsertSize(), Integer.valueOf(144));
    assertEquals(rgrec.getSample(), "TEST_SAMPLE");
    assertEquals(rgrec.getSequencingCenter(), "TEST_SEQUENCECENTER");
    assertEquals(rgrec.getPlatform(), "TEST_TECHNOLOGY");
    assertNotNull(samHeader.getReadGroup("TEST_RGID"));
    assertNull(samHeader.getReadGroup("DOESNT_EXIST"));
    
    assertEquals(samHeader.getProgramRecords().size(), 1);
    SAMProgramRecord pgrec = samHeader.getProgramRecords().get(0);
    assertEquals(pgrec.getCommandLine(), "TEST_COMMAND");
    assertEquals(pgrec.getId(), "TEST_PGID");
    assertEquals(pgrec.getProgramName(), "TEST_PGNAME");
    assertEquals(pgrec.getPreviousProgramGroupId(), "TEST_PREVID");
    assertEquals(pgrec.getProgramVersion(), "TEST_PGVERSION");
    assertNotNull(samHeader.getProgramRecord("TEST_PGID"));
    assertNull(samHeader.getProgramRecord("DOESNT_EXIST"));
    
    assertEquals(samHeader.getComments().size(), 1);
    assertEquals(samHeader.getComments().get(0), "@CO\tTEST_COMMENT");
  }
  
  @Test
  public void testEmptyHeaderSectionToSAMFileHeader() {
    // Behavior of empty SAMFileHeader is not documented, so make no assertions about fields. 
    // Just make sure that no readgroups, sequences, programs, or comments exist.
    
    HeaderSection header = new HeaderSection();
    
    SAMFileHeader samHeader = GenomicsConverter.makeSAMFileHeader(header);
    
    assertTrue(samHeader.getReadGroups().size() == 0);
    assertTrue(samHeader.getSequenceDictionary().size() == 0);
    assertTrue(samHeader.getProgramRecords().size() == 0);
    assertTrue(samHeader.getComments().size() == 0);
  }
  
  @Test
  public void testFullSAMFileHeaderToHeaderSection() {
    // Setup
    SAMFileHeader samHeader = new SAMFileHeader();
    
    samHeader.setSortOrder(SAMFileHeader.SortOrder.queryname);
    
    SAMSequenceRecord sequence = new SAMSequenceRecord("TEST_SQNAME", 141);
    sequence.setAssembly("TEST_ASSEMBLY");
    sequence.setSpecies("TEST_SPECIES");
    samHeader.addSequence(sequence);
    
    SAMReadGroupRecord readgroup = new SAMReadGroupRecord("TEST_RGID");
    readgroup.setRunDate(new Date(0));
    readgroup.setDescription("TEST_DESCRIPTION");
    readgroup.setKeySequence("TEST_SEQUENCE");
    readgroup.setLibrary("TEST_LIBRARY");
    readgroup.setPlatformUnit("TEST_PLATFORM");
    readgroup.setPredictedMedianInsertSize(144);
    readgroup.setSample("TEST_SAMPLE");
    readgroup.setSequencingCenter("TEST_SEQUENCECENTER");
    readgroup.setPlatform("TEST_TECHNOLOGY");
    samHeader.addReadGroup(readgroup);

    SAMProgramRecord program = new SAMProgramRecord("TEST_PGID");
    program.setCommandLine("TEST_COMMAND");
    program.setProgramName("TEST_PGNAME");
    program.setPreviousProgramGroupId("TEST_PREVID");
    program.setProgramVersion("TEST_PGVERSION");
    samHeader.addProgramRecord(program);
    
    samHeader.addComment("TEST_COMMENT");
    
    // Conversion
    HeaderSection header = GenomicsConverter.makeHeaderSection(samHeader);
    
    // Tests
    assertEquals(header.getHeaders().size(), 1);
    Header HD = header.getHeaders().get(0);
    assertEquals(HD.getSortingOrder(), "queryname");
    assertEquals(HD.getVersion(), samHeader.getVersion());
    
    assertEquals(header.getRefSequences().size(), 1);
    ReferenceSequence SQ = header.getRefSequences().get(0);
    assertEquals(SQ.getName(), "TEST_SQNAME");
    assertEquals(SQ.getAssemblyId(), "TEST_ASSEMBLY");
    assertEquals(SQ.getLength(), Integer.valueOf(141));
    assertEquals(SQ.getSpecies(), "TEST_SPECIES");
    
    assertEquals(header.getReadGroups().size(), 1);
    ReadGroup RG = header.getReadGroups().get(0);
    assertEquals(RG.getId(), "TEST_RGID");
    assertEquals(RG.getDate(), "1970-01-01T00:00:00Z");
    assertEquals(RG.getDescription(), "TEST_DESCRIPTION");
    assertEquals(RG.getKeySequence(), "TEST_SEQUENCE");
    assertEquals(RG.getLibrary(), "TEST_LIBRARY");
    assertEquals(RG.getPlatformUnit(), "TEST_PLATFORM");
    assertEquals(RG.getPredictedInsertSize(), Integer.valueOf(144));
    assertEquals(RG.getSample(), "TEST_SAMPLE");
    assertEquals(RG.getSequencingCenterName(), "TEST_SEQUENCECENTER");
    assertEquals(RG.getSequencingTechnology(), "TEST_TECHNOLOGY");
    
    assertEquals(header.getPrograms().size(), 1);
    Program PG = header.getPrograms().get(0);
    assertEquals(PG.getId(), "TEST_PGID");
    assertEquals(PG.getCommandLine(), "TEST_COMMAND");
    assertEquals(PG.getName(), "TEST_PGNAME");
    assertEquals(PG.getPrevProgramId(), "TEST_PREVID");
    assertEquals(PG.getVersion(), "TEST_PGVERSION");
    
    assertEquals(header.getComments().size(), 1);
    assertEquals(header.getComments().get(0), "TEST_COMMENT");
  }
  
  @Test
  public void testEmptySAMFileHeaderToHeaderSection() {
    // Behavior of empty SAMFileHeader is not documented, so make no assertions about fields. 
    // Just make sure that no readgroups, sequences, programs, or comments exist.
    
    SAMFileHeader samHeader = new SAMFileHeader();
    
    HeaderSection header = GenomicsConverter.makeHeaderSection(samHeader);
    
    assertTrue(header.getReadGroups().size() == 0);
    assertTrue(header.getRefSequences().size() == 0);
    assertTrue(header.getPrograms().size() == 0);
    assertTrue(header.getComments().size() == 0);
  }
}
