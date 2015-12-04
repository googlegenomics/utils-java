/*
 * Copyright (C) 2015 Google Inc.
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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.services.genomics.model.ReferenceBound;

/**
 * Helpers for common tasks involved in integration tests against the Genomics API.
 */
public class IntegrationTestHelper {
  // Test data constants
  public static final String PLATINUM_GENOMES_DATASET = "3049512673186936334";
  public static final int PLATINUM_GENOMES_NUMBER_OF_SAMPLES = 17;
  public static final String PLATINUM_GENOMES_VARIANTSET = "3049512673186936334";
  public static final String[] PLATINUM_GENOMES_CALLSET_NAMES = {
    "NA12877",
    "NA12893",
    "NA12885",
    "NA12889",
    "NA12887",
    "NA12881",
    "NA12888",
    "NA12882",
    "NA12879",
    "NA12891",
    "NA12883",
    "NA12892",
    "NA12886",
    "NA12890",
    "NA12878",
    "NA12884",
    "NA12880",
  };
  public static final String[] PLATINUM_GENOMES_READGROUPSETS = {
    "CMvnhpKTFhCAv6TKo6Dglgg",
    "CMvnhpKTFhDw8e3V6aCB-Q8",
    "CMvnhpKTFhDo08GNkfe-jxo",
    "CMvnhpKTFhD3he72j4KZuyc",
    "CMvnhpKTFhCIy4KD0qrtjzA",
    "CMvnhpKTFhCyz5LDrZ7Jozs",
    "CMvnhpKTFhDyy__v0qfPpkw",
    "CMvnhpKTFhDIr8bKjdbdolc",
    "CMvnhpKTFhC1m-r_6Omp5X0",
    "CMvnhpKTFhCw5I3dqMq6mYMB",
    "CMvnhpKTFhD_tKnl__-RqJwB",
    "CMvnhpKTFhDLyY-4kYvurqsB",
    "CMvnhpKTFhCoyJTFk73Eyq0B",
    "CMvnhpKTFhDE9a7F7Yai2rAB",
    "CMvnhpKTFhCUpIDDveWE-r0B",
    "CMvnhpKTFhCrvIOEw4Ol__sB",
  };
  public static final String PLATINUM_GENOMES_REFERENCE_SET_ID = "CNfS6aHAoved2AEQ6PnzkOzw15rqAQ";
  public static final String PLATINUM_GENOMES_BRCA1_REFERENCES = "chr17:41196311:41277499";
  public static final int PLATINUM_GENOMES_BRCA1_EXPECTED_NUM_VARIANTS = 19517;
  public static final String PLATINUM_GENOMES_KLOTHO_REFERENCES = "chr13:33628137:33628138";
  public static final ReferenceBound[] PLATINUM_GENOMES_VARIANTSET_BOUNDS = {
    new ReferenceBound().setReferenceName("chr1").setUpperBound(250226910L),
    new ReferenceBound().setReferenceName("chr10").setUpperBound(136466007L),
    new ReferenceBound().setReferenceName("chr11").setUpperBound(135762137L),
    new ReferenceBound().setReferenceName("chr12").setUpperBound(134049696L),
    new ReferenceBound().setReferenceName("chr13").setUpperBound(115800144L),
    new ReferenceBound().setReferenceName("chr14").setUpperBound(107857350L),
    new ReferenceBound().setReferenceName("chr15").setUpperBound(103000009L),
    new ReferenceBound().setReferenceName("chr16").setUpperBound(90760361L),
    new ReferenceBound().setReferenceName("chr17").setUpperBound(81983044L),
    new ReferenceBound().setReferenceName("chr18").setUpperBound(78776233L),
    new ReferenceBound().setReferenceName("chr19").setUpperBound(59544813L),
    new ReferenceBound().setReferenceName("chr2").setUpperBound(243800708L),
    new ReferenceBound().setReferenceName("chr20").setUpperBound(62993757L),
    new ReferenceBound().setReferenceName("chr21").setUpperBound(48724643L),
    new ReferenceBound().setReferenceName("chr22").setUpperBound(51891601L),
    new ReferenceBound().setReferenceName("chr3").setUpperBound(198316350L),
    new ReferenceBound().setReferenceName("chr4").setUpperBound(191970744L),
    new ReferenceBound().setReferenceName("chr5").setUpperBound(181054248L),
    new ReferenceBound().setReferenceName("chr6").setUpperBound(171796962L),
    new ReferenceBound().setReferenceName("chr7").setUpperBound(159737113L),
    new ReferenceBound().setReferenceName("chr8").setUpperBound(147299246L),
    new ReferenceBound().setReferenceName("chr9").setUpperBound(142027288L),
    new ReferenceBound().setReferenceName("chrM").setUpperBound(1000001L),
    new ReferenceBound().setReferenceName("chrX").setUpperBound(156231278L),
    new ReferenceBound().setReferenceName("chrY").setUpperBound(60032946L)
  };

  private static final String API_KEY = System.getenv("GOOGLE_API_KEY");
  
  /**
   * @return the API_KEY
   */
  public static String getAPI_KEY() {
    return API_KEY;
  }

  public static OfflineAuth getAuthFromApiKey() {
    assertNotNull("You must set the GOOGLE_API_KEY environment variable for this test.", API_KEY);
    return new OfflineAuth(API_KEY);
  }
  
  public static OfflineAuth getAuthFromApplicationDefaultCredential() {
    return new OfflineAuth();
  }
}
