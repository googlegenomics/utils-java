package com.google.cloud.genomics.utils;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.cloud.genomics.utils.ShardUtils.SexChromosomeFilter;
import com.google.genomics.v1.StreamVariantsRequest;

public class ShardUtilsITCase {

  static IntegrationTestHelper helper;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    helper = new IntegrationTestHelper();
  }

  @Test
  public void testGetVariantRequestsStringSexChromosomeFilterLongOfflineAuth() throws IOException, GeneralSecurityException {

    StreamVariantsRequest[] EXPECTED_RESULT_XY = {
        new Contig("chrX", 0L, 150000000L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chrX", 150000000L, 156231278L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chrY", 0L, 60032946L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET)
    };
    
    StreamVariantsRequest[] EXPECTED_RESULT = {
        new Contig("chr1", 0L, 150000000L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr1", 150000000L, 250226910L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr10", 0L, 136466007L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr11", 0L, 135762137L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr12", 0L, 134049696L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr13", 0L, 115800144L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr14", 0L, 107857350L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr15", 0L, 103000009L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr16", 0L, 90760361L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr17", 0L, 81983044L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr18", 0L, 78776233L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr19", 0L, 59544813L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr2", 0L, 150000000L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr2", 150000000L, 243800708L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr20", 0L, 62993757L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr21", 0L, 48724643L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr22", 0L, 51891601L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr3", 0L, 150000000L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr3", 150000000L, 198316350L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr4", 0L, 150000000L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr4", 150000000L, 191970744L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr5", 0L, 150000000L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr5", 150000000L, 181054248L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr6", 0L, 150000000L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr6", 150000000L, 171796962L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr7", 0L, 150000000L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr7", 150000000L, 159737113L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr8", 0L, 147299246L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chr9", 0L, 142027288L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET),
        new Contig("chrM", 0L, 1000001L)
        .getStreamVariantsRequest(helper.PLATINUM_GENOMES_VARIANTSET)
    };
    
    // These shards are "too big" to use in practice but for this test it keeps the
    // expected result from getting crazy long.
    assertThat(ShardUtils.getVariantRequests(helper.PLATINUM_GENOMES_VARIANTSET,
        SexChromosomeFilter.EXCLUDE_XY, 150000000L, helper.getAuth()),
        CoreMatchers.allOf(CoreMatchers.hasItems(EXPECTED_RESULT)));
    
    // Include sex chromosomes this time.
    assertThat(ShardUtils.getVariantRequests(helper.PLATINUM_GENOMES_VARIANTSET,
        SexChromosomeFilter.INCLUDE_XY, 150000000L, helper.getAuth()),
        CoreMatchers.allOf(CoreMatchers.hasItems(EXPECTED_RESULT),
            CoreMatchers.hasItems(EXPECTED_RESULT_XY)));
  }
}
