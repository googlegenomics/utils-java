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

import com.google.genomics.v1.CigarUnit;
import com.google.genomics.v1.Read;
import com.google.protobuf.ListValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods for working with genetic read data.
 */
public class ReadUtils {
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
