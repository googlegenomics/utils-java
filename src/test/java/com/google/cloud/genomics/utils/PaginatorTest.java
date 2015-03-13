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

import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.model.LinearAlignment;
import com.google.api.services.genomics.model.Position;
import com.google.api.services.genomics.model.Read;
import com.google.api.services.genomics.model.ReadGroupSet;
import com.google.api.services.genomics.model.SearchReadGroupSetsRequest;
import com.google.api.services.genomics.model.SearchReadGroupSetsResponse;
import com.google.api.services.genomics.model.SearchReadsRequest;
import com.google.api.services.genomics.model.SearchReadsResponse;
import com.google.api.services.genomics.model.SearchVariantsRequest;
import com.google.api.services.genomics.model.SearchVariantsResponse;
import com.google.api.services.genomics.model.Variant;
import com.google.cloud.genomics.utils.Paginator.ShardBoundary;
import com.google.common.collect.Lists;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class PaginatorTest {

  @Mock Genomics genomics;
  @Mock Genomics.Readgroupsets readGroupSets;
  @Mock Genomics.Readgroupsets.Search readGroupSetSearch;
  @Mock Genomics.Variants variants;
  @Mock Genomics.Variants.Search variantsSearch;
  @Mock Genomics.Reads reads;
  @Mock Genomics.Reads.Search readsSearch;

  @Rule
  public ExpectedException thrown = ExpectedException.none();
  
  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(genomics.readgroupsets()).thenReturn(readGroupSets);
    Mockito.when(genomics.variants()).thenReturn(variants);
    Mockito.when(genomics.reads()).thenReturn(reads);
  }

  @Test
  public void testPagination() throws Exception {

    // Page 1
    Mockito.when(readGroupSets.search(new SearchReadGroupSetsRequest().setName("HG")))
        .thenReturn(readGroupSetSearch);

    // Page 2
    Mockito.when(readGroupSets.search(new SearchReadGroupSetsRequest().setName("HG")
        .setPageToken("page2")))
        .thenReturn(readGroupSetSearch);
    Mockito.when(readGroupSetSearch.execute()).thenReturn(
        new SearchReadGroupSetsResponse()
            .setReadGroupSets(Lists.newArrayList(new ReadGroupSet().setId("r1")))
            .setNextPageToken("page2"),
        new SearchReadGroupSetsResponse()
            .setReadGroupSets(Lists.newArrayList(new ReadGroupSet().setId("r2"))));

    Paginator.ReadGroupSets paginator = Paginator.ReadGroupSets.create(genomics);
    List<String> ids = Lists.newArrayList();
    for (ReadGroupSet readGroupSet : paginator.search(
        new SearchReadGroupSetsRequest().setName("HG"))) {
      ids.add(readGroupSet.getId());
    }

    assertEquals(Lists.newArrayList("r1", "r2"), ids);
  }

  @Test
  public void testPagination_withNullResults() throws Exception {
    Mockito.when(readGroupSets.search(new SearchReadGroupSetsRequest()))
        .thenReturn(readGroupSetSearch);
    Mockito.when(readGroupSetSearch.execute()).thenReturn(new SearchReadGroupSetsResponse());

    Paginator.ReadGroupSets paginator = Paginator.ReadGroupSets.create(genomics);
    List<String> ids = Lists.newArrayList();
    for (ReadGroupSet readGroupSet : paginator.search(new SearchReadGroupSetsRequest())) {
      ids.add(readGroupSet.getId());
    }

    // No results and no exceptions
    assertTrue(ids.isEmpty());
  }

  @Test
  public void testFields() throws Exception {
    Mockito.when(readGroupSets.search(new SearchReadGroupSetsRequest().setName("HG")))
        .thenReturn(readGroupSetSearch);
    Mockito.when(readGroupSetSearch.setFields(Mockito.anyString()))
        .thenReturn(readGroupSetSearch);
    Mockito.when(readGroupSetSearch.execute()).thenReturn(
        new SearchReadGroupSetsResponse()
            .setReadGroupSets(Lists.newArrayList(new ReadGroupSet().setId("r1"))));

    Paginator.ReadGroupSets paginator = Paginator.ReadGroupSets.create(genomics);
    List<String> ids = Lists.newArrayList();
    for (ReadGroupSet set : paginator.search(
        new SearchReadGroupSetsRequest().setName("HG"), "readGroupSets(id,name)")) {
      ids.add(set.getId());
    }

    assertEquals(Lists.newArrayList("r1"), ids);

    // Make sure the fields parameter actually gets passed along
    Mockito.verify(readGroupSetSearch, Mockito.atLeastOnce()).setFields("readGroupSets(id,name)");
  }

  @Test
  public void testVariantPagination() throws Exception {

    SearchVariantsRequest request = new SearchVariantsRequest().setStart(1000L).setEnd(2000L);
    Mockito.when(variants.search(request)).thenReturn(variantsSearch);

    Variant overlapStartWithinExtent = new Variant().setStart(900L).setEnd(1005L);
    Variant overlapStartExtent = new Variant().setStart(999L).setEnd(5000L);
    Variant atStartWithinExtent = new Variant().setStart(1000L).setEnd(1002L);
    Variant atStartOverlapExtent = new Variant().setStart(1000L).setEnd(5000L);
    Variant beyondStartWithinExtent = new Variant().setStart(1500L).setEnd(1502L);
    Variant beyondOverlapExtent = new Variant().setStart(1500L).setEnd(5000L);
    Variant[] input = new Variant[] { overlapStartWithinExtent, overlapStartExtent, atStartWithinExtent,
            atStartOverlapExtent, beyondStartWithinExtent, beyondOverlapExtent };

    Mockito.when(variantsSearch.execute()).thenReturn(
        new SearchVariantsResponse().setVariants(Arrays.asList(input)));

    Paginator.Variants filteredPaginator = Paginator.Variants.create(genomics, ShardBoundary.STRICT);
    List<Variant> filteredVariants = Lists.newArrayList();
    for (Variant variant : filteredPaginator.search(request)) {
      filteredVariants.add(variant);
    }
    assertEquals(4, filteredVariants.size());
    assertThat(filteredVariants, CoreMatchers.hasItems(atStartWithinExtent,
        atStartOverlapExtent, beyondStartWithinExtent, beyondOverlapExtent));

    // Ensure searches with fields verify the preconditions for strict shards.
    final String nullFields = null;
    assertNotNull(filteredPaginator.search(request, nullFields).iterator().next());
    assertNotNull(filteredPaginator.search(request, "nextPageToken,variants(start,id,calls(genotype,callSetName))").iterator().next());
    try {
      filteredPaginator.search(request, "nextPageToken,variants(id,calls(genotype,callSetName))").iterator().next();
      fail("should have thrown an IllegalArgumentxception");
    } catch (IllegalArgumentException e) {} 

    Paginator.Variants overlappingPaginator = Paginator.Variants.create(genomics, ShardBoundary.OVERLAPS);
    List<Variant> overlappingVariants = Lists.newArrayList();
    for (Variant variant : overlappingPaginator.search(request)) {
      overlappingVariants.add(variant);
    }
    assertEquals(6, overlappingVariants.size());
    assertThat(overlappingVariants, CoreMatchers.hasItems(input));

    // There are no preconditions on fields for overlapping shards.
    assertNotNull(overlappingPaginator.search(request, nullFields).iterator().next());
    assertNotNull(overlappingPaginator.search(request, "nextPageToken,variants(start,id,calls(genotype,callSetName))").iterator().next());
    assertNotNull(overlappingPaginator.search(request, "nextPageToken,variants(id,calls(genotype,callSetName))").iterator().next());
  }
    
  @Test
  public void testVariantPaginationEmptyShard() throws Exception {

    SearchVariantsRequest request = new SearchVariantsRequest().setStart(1000L).setEnd(2000L);
    Mockito.when(variants.search(request)).thenReturn(variantsSearch);

    Mockito.when(variantsSearch.execute()).thenReturn(
        new SearchVariantsResponse());

    Paginator.Variants filteredPaginator = Paginator.Variants.create(genomics, ShardBoundary.STRICT);
    assertNotNull(filteredPaginator.search(request));
  }
    
  static Read readHelper(int start, int end) {
    Position position = new Position().setPosition((long) start);
    LinearAlignment alignment = new LinearAlignment().setPosition(position);
    return new Read().setAlignment(alignment).setFragmentLength(end-start);
  }
  
  @Test
  public void testReadPagination() throws Exception {

    SearchReadsRequest request = new SearchReadsRequest().setStart(1000L).setEnd(2000L);
    Mockito.when(reads.search(request)).thenReturn(readsSearch);

    Read overlapStartWithinExtent = readHelper(900,1005);
    Read overlapStartExtent = readHelper(999, 5000);
    Read atStartWithinExtent = readHelper(1000, 1002);
    Read atStartOverlapExtent = readHelper(1000, 5000);
    Read beyondStartWithinExtent = readHelper(1500, 1502);
    Read beyondOverlapExtent = readHelper(1500, 5000);
    Read[] input = new Read[] { overlapStartWithinExtent, overlapStartExtent, atStartWithinExtent,
            atStartOverlapExtent, beyondStartWithinExtent, beyondOverlapExtent };

    Mockito.when(readsSearch.execute()).thenReturn(
        new SearchReadsResponse().setAlignments(Arrays.asList(input)));

    Paginator.Reads filteredPaginator = Paginator.Reads.create(genomics, ShardBoundary.STRICT);
    List<Read> filteredReads = Lists.newArrayList();
    for (Read read : filteredPaginator.search(request)) {
      filteredReads.add(read);
    }
    assertEquals(4, filteredReads.size());
    assertThat(filteredReads, CoreMatchers.hasItems(atStartWithinExtent,
        atStartOverlapExtent, beyondStartWithinExtent, beyondOverlapExtent));

    Paginator.Reads overlappingPaginator = Paginator.Reads.create(genomics, ShardBoundary.OVERLAPS);
    List<Read> overlappingReads = Lists.newArrayList();
    for (Read read : overlappingPaginator.search(request)) {
      overlappingReads.add(read);
    }
    assertEquals(6, overlappingReads.size());
    assertThat(overlappingReads, CoreMatchers.hasItems(input));
  }
  
  @Test
  public void testReadPaginationStrictShardPrecondition() throws Exception {
    SearchReadsRequest request = new SearchReadsRequest().setStart(1000L).setEnd(2000L);
    Mockito.when(reads.search(request)).thenReturn(readsSearch);

    Paginator.Reads filteredPaginator = Paginator.Reads.create(genomics, ShardBoundary.STRICT);
    thrown.expect(IllegalArgumentException.class);
    filteredPaginator.search(request, "nextPageToken,reads(id,alignment(cigar))").iterator().next();
  }
}
