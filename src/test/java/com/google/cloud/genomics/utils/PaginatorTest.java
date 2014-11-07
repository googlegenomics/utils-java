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
import com.google.api.services.genomics.model.ReadGroupSet;
import com.google.api.services.genomics.model.SearchReadGroupSetsRequest;
import com.google.api.services.genomics.model.SearchReadGroupSetsResponse;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class PaginatorTest {

  @Mock Genomics genomics;
  @Mock Genomics.Readgroupsets readGroupSets;
  @Mock Genomics.Readgroupsets.Search readGroupSetSearch;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(genomics.readgroupsets()).thenReturn(readGroupSets);
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

}
