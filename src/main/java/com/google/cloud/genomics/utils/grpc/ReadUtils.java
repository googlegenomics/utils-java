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

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.genomics.v1.CigarUnit;
import com.google.genomics.v1.LinearAlignment;
import com.google.genomics.v1.Position;
import com.google.genomics.v1.Read;
import com.google.genomics.v1.ReadGroup;
import com.google.genomics.v1.ReadGroup.Program;
import com.google.genomics.v1.ReadGroupSet;
import com.google.genomics.v1.Reference;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;

import htsjdk.samtools.CigarElement;
import htsjdk.samtools.CigarOperator;
import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMProgramRecord;
import htsjdk.samtools.SAMReadGroupRecord;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SAMTextHeaderCodec;
import htsjdk.samtools.SamFileHeaderMerger;
import htsjdk.samtools.TagValueAndUnsignedArrayFlag;
import htsjdk.samtools.TextTagCodec;
import htsjdk.samtools.ValidationStringency;
import htsjdk.samtools.util.SequenceUtil;
import htsjdk.samtools.util.StringLineReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadUtils extends com.google.cloud.genomics.utils.ReadUtils {
  protected static BiMap<String, CigarUnit.Operation> CIGAR_OPERATIONS_INV_GRPC;

  static {
    CIGAR_OPERATIONS_INV_GRPC = HashBiMap.create();
    CIGAR_OPERATIONS_INV_GRPC.put("M", CigarUnit.Operation.ALIGNMENT_MATCH);
    CIGAR_OPERATIONS_INV_GRPC.put("H", CigarUnit.Operation.CLIP_HARD);
    CIGAR_OPERATIONS_INV_GRPC.put("S", CigarUnit.Operation.CLIP_SOFT);
    CIGAR_OPERATIONS_INV_GRPC.put("D", CigarUnit.Operation.DELETE);
    CIGAR_OPERATIONS_INV_GRPC.put("I", CigarUnit.Operation.INSERT);
    CIGAR_OPERATIONS_INV_GRPC.put("P", CigarUnit.Operation.PAD);
    CIGAR_OPERATIONS_INV_GRPC.put("=", CigarUnit.Operation.SEQUENCE_MATCH);
    CIGAR_OPERATIONS_INV_GRPC.put("X", CigarUnit.Operation.SEQUENCE_MISMATCH);
    CIGAR_OPERATIONS_INV_GRPC.put("N", CigarUnit.Operation.SKIP);
  }
  public static String getCigarString(Read read) {
    List<CigarUnit> cigar = read.getAlignment() == null ? null : read.getAlignment().getCigarList();
    if (cigar != null && cigar.size() > 0) {
      StringBuilder cigarString = new StringBuilder();

      for (CigarUnit unit : cigar) {
        cigarString.append(String.valueOf(unit.getOperationLength()));
        cigarString.append(CIGAR_OPERATIONS.get(unit.getOperation().toString()));
      }
      return cigarString.toString();
    }
    return null;
  }

  private static boolean isUnmapped(Position position) {
    return position == null;
  }

  private static boolean isReverseStrand(Position position) {
    return position != null && Boolean.TRUE.equals(position.getReverseStrand());
  }

  public static int getFlags(Read read) {
    Position position = !read.hasAlignment() || !read.getAlignment().hasPosition() ?
        null : read.getAlignment().getPosition();
    Position nextMatePosition = !read.hasNextMatePosition() ? null : read.getNextMatePosition();

    int flags = 0;

    flags += Integer.valueOf(2).equals(read.getNumberReads()) ? 1 : 0; // read_paired
    flags += Boolean.TRUE.equals(read.getProperPlacement()) ? 2 : 0; // read_proper_pair
    flags += isUnmapped(position) ? 4 : 0; // read_unmapped
    flags += isUnmapped(nextMatePosition) ? 8 : 0; // mate_unmapped
    flags += isReverseStrand(position) ? 16 : 0 ; // read_reverse_strand
    flags += isReverseStrand(nextMatePosition) ? 32 : 0; // mate_reverse_strand
    flags += read.getNumberReads() > 0 && Integer.valueOf(0).equals(read.getReadNumber()) ? 64 : 0; // first_in_pair
    flags += Integer.valueOf(1).equals(read.getReadNumber()) ? 128 : 0; // second_in_pair
    flags += Boolean.TRUE.equals(read.getSecondaryAlignment()) ? 256 : 0; // secondary_alignment
    flags += Boolean.TRUE.equals(read.getFailedVendorQualityChecks()) ? 512 : 0; // failed_quality
    flags += Boolean.TRUE.equals(read.getDuplicateFragment()) ? 1024 : 0; // duplicate_read
    flags += Boolean.TRUE.equals(read.getSupplementaryAlignment()) ? 2048 : 0; // supplementary

    return flags;
  }

  static String safeStr(String s) {
    if (s == null) {
      return "";
    }
    return s;
  }

  /**
   * Generates a Read from a SAMRecord.
   */
  public static final Read makeReadGrpc(final SAMRecord record) {
    Read.Builder read = Read.newBuilder();
    read.setId(safeStr(record.getReadName())); // TODO: make more unique
    read.setFragmentName(safeStr(record.getReadName()));
    read.setReadGroupId(safeStr(getAttr(record, "RG")));
    read.setNumberReads(record.getReadPairedFlag() ? 2 : 1);
    read.setProperPlacement(record.getReadPairedFlag() && record.getProperPairFlag());
    if (!record.getReadUnmappedFlag() && record.getAlignmentStart() > 0) {
      LinearAlignment.Builder alignment = LinearAlignment.newBuilder();

      Position.Builder position = Position.newBuilder();
      position.setPosition((long) record.getAlignmentStart() - 1);
      position.setReferenceName(safeStr(record.getReferenceName()));
      position.setReverseStrand(record.getReadNegativeStrandFlag());
      alignment.setPosition(position);

      alignment.setMappingQuality(record.getMappingQuality());

      final String referenceSequence = (record.getAttribute("MD") != null) ? new String(
              SequenceUtil.makeReferenceFromAlignment(record, true))
              : null;
      List<CigarUnit> cigar = Lists.transform(record.getCigar().getCigarElements(),
              new Function<CigarElement, CigarUnit>() {
                @Override
                public CigarUnit apply(CigarElement c) {
                  CigarUnit.Builder u = CigarUnit.newBuilder();
                  CigarOperator o = c.getOperator();
                  u.setOperation(CIGAR_OPERATIONS_INV_GRPC.get(o.toString()));
                  u.setOperationLength(c.getLength());
                  if (referenceSequence != null && (u.getOperation().equals("SEQUENCE_MISMATCH")
                          || u.getOperation().equals("DELETE"))) {
                    u.setReferenceSequence(referenceSequence);
                  }
                  return u.build();
                }
              });
      alignment.addAllCigar(cigar);
      read.setAlignment(alignment);
    }
    read.setDuplicateFragment(record.getDuplicateReadFlag());
    read.setFragmentLength(record.getInferredInsertSize());
    if (record.getReadPairedFlag()) {
      if (record.getFirstOfPairFlag()) {
        read.setReadNumber(0);
      } else if (record.getSecondOfPairFlag()) {
        read.setReadNumber(1);
      }

      if (!record.getMateUnmappedFlag()) {
        Position.Builder matePosition = Position.newBuilder();
        matePosition.setPosition((long) record.getMateAlignmentStart() - 1);
        matePosition.setReferenceName(record.getMateReferenceName());
        matePosition.setReverseStrand(record.getMateNegativeStrandFlag());
        read.setNextMatePosition(matePosition);
      }
    }
    read.setFailedVendorQualityChecks(record.getReadFailsVendorQualityCheckFlag());
    read.setSecondaryAlignment(record.getNotPrimaryAlignmentFlag());
    read.setSupplementaryAlignment(record.getSupplementaryAlignmentFlag());
    read.setAlignedSequence(safeStr(record.getReadString()));
    byte[] baseQualities = record.getBaseQualities();
    if (baseQualities.length > 0) {
      List<Integer> readBaseQualities = new ArrayList<>(baseQualities.length);
      for (byte b : baseQualities) {
        readBaseQualities.add(new Integer(b));
      }
      read.addAllAlignedQuality(readBaseQualities);
    }

    Map<String, ListValue> attributes =  read.getMutableInfo();
    for( SAMRecord.SAMTagAndValue tagAndValue: record.getAttributes()) {
      if (tagAndValue.value == null) {
        continue;
      }
      final String tag = tagAndValue.tag;
      String value;
      if (tagAndValue.value instanceof byte[]) {
        // It's possible for client code of SamRecord to pass byte[]
        // to setAttribute. toString is not defined for byte[], so
        // it produces garbage. The solution to create a string directly.
        value = new String(((byte[]) tagAndValue.value));
      } else {
        value = tagAndValue.value.toString();
      }

      ListValue listValue = attributes.get(tag);
      ListValue.Builder listValueBuilder = listValue == null ?
          ListValue.newBuilder() :
          ListValue.newBuilder(listValue);
      listValueBuilder.addValues(Value.newBuilder().setStringValue(value));
      attributes.put(tag, listValueBuilder.build());
    }


    return read.build();
  }

  public static String getAttr(SAMRecord record, String attributeName) {
    try {
      return record.getStringAttribute(attributeName);
    } catch (SAMException ex) {
      return "";
    }
  }

  /** Returns SAM Tag type. If not a known tag - defaults to "Z". */
  public static String getTagType(String tagName) {
    final String result = SAM_TAGS.get(tagName);
    return result != null ? result : "Z";
  }

  public static final SAMRecord makeSAMRecord(Read read, SAMFileHeader header) {
    SAMRecord record = new SAMRecord(header);
    if (read.getFragmentName() != null) {
      record.setReadName(read.getFragmentName());
    }
    if (read.getReadGroupId() != null && !read.getReadGroupId().isEmpty()) {
      record.setAttribute("RG" ,read.getReadGroupId());
    }
    // Set flags, as advised in http://google-genomics.readthedocs.org/en/latest/migrating_tips.html
    int flags = getFlags(read);
    record.setFlags(flags);

    String referenceName = null;
    Long alignmentStart = null;
    if (read.hasAlignment()) {
      if (read.getAlignment().hasPosition()) {
        referenceName = read.getAlignment().getPosition().getReferenceName();
        if (referenceName != null && !referenceName.isEmpty()) {
          record.setReferenceName(referenceName);
        }
        alignmentStart = read.getAlignment().getPosition().getPosition();
        if (alignmentStart != null) {
          // API positions are 0-based and SAMRecord is 1-based.
          record.setAlignmentStart(alignmentStart.intValue() + 1);
        }
      }
      Integer mappingQuality = read.getAlignment().getMappingQuality();
      if (mappingQuality != null) {
        record.setMappingQuality(mappingQuality);
      }

      List<CigarUnit> cigar = read.getAlignment().getCigarList();
      if (cigar != null && cigar.size() > 0) {
        StringBuffer cigarString = new StringBuffer(cigar.size());

        for (CigarUnit unit : cigar) {
          cigarString.append(String.valueOf(unit.getOperationLength()));
          cigarString.append(CIGAR_OPERATIONS.get(unit.getOperation().toString()));
        }
        record.setCigarString(cigarString.toString());
      }
    }

    if (read.hasNextMatePosition()) {
      String mateReferenceName = read.getNextMatePosition().getReferenceName();
      if (mateReferenceName != null && !mateReferenceName.isEmpty()) {
        record.setMateReferenceName(mateReferenceName);
      }
      Long matePosition = read.getNextMatePosition().getPosition();
      if (matePosition != null) {
        // API positions are 0-based and SAMRecord is 1-based.
        record.setMateAlignmentStart(matePosition.intValue() + 1);
      }
    }

    record.setInferredInsertSize(read.getFragmentLength());

    if (read.getAlignedSequence() != null) {
      record.setReadString(read.getAlignedSequence());
    }

    List<Integer> baseQuality = read.getAlignedQualityList();
    if (baseQuality != null && baseQuality.size() > 0) {
      byte[] qualityArray = new byte[baseQuality.size()];
      int idx = 0;
      for (Integer i : baseQuality) {
        qualityArray[idx++] = i.byteValue();
      }
      record.setBaseQualities(qualityArray);
    }

    TextTagCodec textTagCodec = new TextTagCodec();
    Map<String, ListValue> tags = read.getInfo();
    if (tags != null) {
      for (String tag : tags.keySet()) {
        ListValue values = tags.get(tag);
        if (values != null) {
          for (Value value : values.getValuesList()) {
              Object attrValue = textTagCodec.decode(
                  tag + ":" + getTagType(tag) + ":" + value.getStringValue())
                  .getValue();
                  if (attrValue instanceof TagValueAndUnsignedArrayFlag) {
                    record.setUnsignedArrayAttribute(tag,
                        ((TagValueAndUnsignedArrayFlag)attrValue).value);
                  } else {
                    record.setAttribute(tag, attrValue);
                  }
          }
        }
      }
    }

    return record;
  }

  public static final SAMRecord makeSAMRecord(Read read,
                                              ReadGroupSet readGroupSet, List<Reference> references,
                                              boolean forceSetMatePositionToThisPosition) {
    return makeSAMRecord(read, makeSAMFileHeader(readGroupSet, references));
  }

  public static final SAMRecord makeSAMRecord(Read read,
                                              boolean forceSetMatePositionToThisPosition) {
    return makeSAMRecord(read, new SAMFileHeader());
  }

  /**
   * Generates a SAMFileHeader from a ReadGroupSet and Reference metadata
   */
  public static final SAMFileHeader makeSAMFileHeader(ReadGroupSet readGroupSet,
                                                      List<Reference> references) {
    List<SAMFileHeader> samHeaders = new ArrayList<SAMFileHeader>(2);
    SAMFileHeader samHeader = new SAMFileHeader();
    samHeaders.add(samHeader);

    // Reads are always returned in coordinate order form the API.
    samHeader.setSortOrder(SAMFileHeader.SortOrder.coordinate);

    if (references != null && references.size() > 0) {
      SAMSequenceDictionary dict = new SAMSequenceDictionary();
      for (Reference reference : references) {
        if (reference.getName() != null && reference.getLength() != 0) {
          SAMSequenceRecord sequence = new SAMSequenceRecord(reference.getName(),
              (int)reference.getLength());
          dict.addSequence(sequence);
        }
      }
      samHeader.setSequenceDictionary(dict);
    }

    List<SAMProgramRecord> programs = null;
    if (readGroupSet.getReadGroupsCount() != 0) {
      List<SAMReadGroupRecord> readgroups = Lists.newArrayList();
      for (ReadGroup RG : readGroupSet.getReadGroupsList()) {
        if (RG.getId() != null && RG.getName() != null) {
          String readGroupName = RG.getName();
          if (readGroupName == null || readGroupName.isEmpty()) {
            // We have to set the name to something, so if for some reason the proper
            // SAM tag for name was missing, we will use the generated id.
            readGroupName = RG.getId();
          }
          SAMReadGroupRecord readgroup = new SAMReadGroupRecord(readGroupName);
          if (RG.getDescription() != null && !RG.getDescription().isEmpty()) {
            readgroup.setDescription(RG.getDescription());
          }

          readgroup.setPredictedMedianInsertSize(RG.getPredictedInsertSize());

          if (RG.getSampleId() != null) {
            readgroup.setSample(RG.getSampleId());
          }
          if (RG.getExperiment() != null) {
            if (RG.getExperiment().getLibraryId() != null && !RG.getExperiment().getLibraryId().isEmpty()) {
              readgroup.setLibrary(RG.getExperiment().getLibraryId());
            }
            if (RG.getExperiment().getSequencingCenter() != null && !RG.getExperiment().getSequencingCenter().isEmpty()) {
              readgroup.setSequencingCenter(RG.getExperiment().getSequencingCenter());
            }
            if (RG.getExperiment().getInstrumentModel() != null && !RG.getExperiment().getInstrumentModel().isEmpty()) {
              readgroup.setPlatform(RG.getExperiment().getInstrumentModel());
            }
            if (RG.getExperiment().getPlatformUnit() != null && !RG.getExperiment().getPlatformUnit().isEmpty()) {
              readgroup.setPlatformUnit(RG.getExperiment().getPlatformUnit());
            }
          }
          readgroups.add(readgroup);
        }
        if (RG.getProgramsCount() > 0) {
          if (programs == null) {
            programs = Lists.newArrayList();
          }
          for (Program PG : RG.getProgramsList()) {
            SAMProgramRecord program = new SAMProgramRecord(PG.getId());
            if (PG.getCommandLine() != null && !PG.getCommandLine().isEmpty()) {
              program.setCommandLine(PG.getCommandLine());
            }
            if (PG.getName() != null  && !PG.getName().isEmpty()) {
              program.setProgramName(PG.getName());
            }
            if (PG.getPrevProgramId() != null  && !PG.getPrevProgramId().isEmpty()) {
              program.setPreviousProgramGroupId(PG.getPrevProgramId());
            }
            if (PG.getVersion() != null  && !PG.getVersion().isEmpty()) {
              program.setProgramVersion(PG.getVersion());
            }
            programs.add(program);
          }
        }
      }
      samHeader.setReadGroups(readgroups);
      if (programs != null) {
        samHeader.setProgramRecords(programs);
      }
    }

    // If BAM file is imported with non standard reference, the SQ tags
    // are preserved in the info key/value array.
    // Attempt to read them form there.
    if (references == null || references.size() <= 0) {
      @SuppressWarnings("unchecked")
      Map<String, ListValue> tags = readGroupSet.getInfo();
      if (tags != null) {
        LOG.info("Getting @SQ header data from readgroupset info");
        StringBuffer buf = new StringBuffer();
        for (String tag : tags.keySet()) {
          if (!tag.startsWith(HEADER_SAM_TAG_INFO_KEY_PREFIX)) {
            continue;
          }
          final String headerName = tag.substring(HEADER_SAM_TAG_INFO_KEY_PREFIX.length());
          ListValue values = tags.get(tag);
          if (values == null) {
            continue;
          }
          for (Value value : values.getValuesList()) {
            buf.append(headerName);
            buf.append("\t");
            buf.append(value.getStringValue());
            buf.append("\r\n");
          }
          final String headerString = buf.toString();
          final SAMTextHeaderCodec codec = new SAMTextHeaderCodec();
          codec.setValidationStringency(ValidationStringency.STRICT);
          final SAMFileHeader parsedHeader = codec.decode(
              new StringLineReader(headerString), null);
          samHeaders.add(parsedHeader);
        }
      }
    }

    final SAMFileHeader finalHeader =
        (new SamFileHeaderMerger(
            SAMFileHeader.SortOrder.coordinate, samHeaders, true))
        .getMergedHeader();

    return finalHeader;
  }

  /*
   * Regular expression for MD tag.
   *
   * \G = end of previous match.
   * (?:[0-9]+) Number of bases in which read matches reference.
   *  - or -
   * Single reference base for case in which reference differs from read.
   *  - or -
   * ^one or more reference bases that are deleted in read.
   *
   */
  private static final Pattern mdPattern =
      Pattern.compile("\\G(?:([0-9]+)|([ACTGNactgn])|(\\^[ACTGNactgn]+))");

  /**
   * Grab the value of the provided SAM tag if it exists (returns null otherwise)
   * @param read    the read whose SAM tags need to be searched
   * @param tag     the desired tag
   * @return        string corresponding to SAM tag or null if it doesn't exist
   */
  public static String getSamTag(Read read, String tag) {
    ListValue value = read.getInfo().get(tag);
    if (value == null) {
      return null;
    }
    return value.getValues(0).getStringValue();
  }

  /**
   * Create the overlapping reference sequence by parsing the read and it's MD tag. This function
   * will return null if the read is unaligned or doesn't contain a usable MD tag. If there is an
   * insertion in the read the reference contains '-'. If the read is soft-clipped the reference
   * contains '0'.
   * @param read    the read to parse
   * @return        overlapping reference string or null if not possible to compute
   */
  public static String inferReferenceSequenceByParsingMdFlag(Read read) {
    String mdTag = getSamTag(read, "MD");

    // Make sure this read has a valid alignment with Cigar Units and usable MD tag
    if (!read.hasAlignment() || (read.getAlignment().getCigarCount() == 0) || mdTag == null) {
      return null;
    }

    String readSeq = read.getAlignedSequence();
    StringBuilder refSeqBuilder = new StringBuilder();
    Matcher match = mdPattern.matcher(mdTag);
    int curReadPos = 0;
    int numSavedBases = 0;

    for (CigarUnit unit : read.getAlignment().getCigarList()) {
      CigarUnit.Operation cigarOp = unit.getOperation();
      int cigarOpLength = (int) unit.getOperationLength();
      switch(cigarOp) {
        case ALIGNMENT_MATCH:
        case SEQUENCE_MISMATCH:
        case SEQUENCE_MATCH:
        case DELETE:
          // these operators consume both read and reference bases so we need to parse the MD tag
          int numMatchedBases = 0;

          // Do we have any saved matched bases from a previous cigar operator?
          while ((numSavedBases > 0) && (numMatchedBases < cigarOpLength)) {
            refSeqBuilder.append(readSeq.charAt(curReadPos++));
            numSavedBases--;
            numMatchedBases++;
          }

          while (numMatchedBases < cigarOpLength) {
            boolean matched = match.find();
            if (matched) {
              // need to use the regular expression to parse the MD tag
              String mg1 = match.group(1);
              String mg2 = match.group(2);
              String mg3 = match.group(3);
              if (mg1 != null && mg1.length() > 0) {
                // this token is a number which means a series of matches
                int numMatches = Integer.parseInt(mg1);
                for (int i = 0; i < numMatches; i++) {
                  if (numMatchedBases < cigarOpLength) {
                    refSeqBuilder.append(readSeq.charAt(curReadPos++));
                  } else {
                    numSavedBases++;
                  }
                  numMatchedBases++;
                }
              } else if (mg2 != null && mg2.length() > 0) {
                // this token is a single nucleotide which means a mismatching base
                refSeqBuilder.append(mg2.charAt(0));
                curReadPos++;
                numMatchedBases++;
              } else if (mg3 != null && mg3.length() > 0) {
                // this token starts with a caret which means deleted bases
                numMatchedBases += mg3.length() - 1; // don't include the caret
              } else {
                matched = false;
              }
            }

            if (!matched) {
              throw new IllegalStateException("Unusable or malformatted MD tag found: " + mdTag);
            }
          }

          break;
        case CLIP_SOFT:
        case INSERT:
          // these operators consume read bases but not reference bases
          for (int i = 0; i < unit.getOperationLength(); i++) {
            refSeqBuilder.append(cigarOp.equals(CigarUnit.Operation.CLIP_SOFT) ? '0' : '-');
            curReadPos++;
          }
          break;
        case SKIP:
        case PAD:
        case CLIP_HARD:
        default:
          // these operators don't consume aligned read bases and aren't included in the
          //   reference sequence so just skip them
          break;
      }
    }

    return refSeqBuilder.toString();
  }
}
