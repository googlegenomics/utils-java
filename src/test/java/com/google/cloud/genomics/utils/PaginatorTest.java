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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.model.Readset;
import com.google.api.services.genomics.model.SearchReadsetsRequest;
import com.google.api.services.genomics.model.SearchReadsetsResponse;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

@RunWith(JUnit4.class)
public class PaginatorTest {

  @Mock Genomics genomics;
  @Mock Genomics.Readsets readsets;
  @Mock Genomics.Readsets.Search readsetSearch;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(genomics.readsets()).thenReturn(readsets);
  }

  @Test
  public void testPagination() throws Exception {

    // Page 1
    Mockito.when(readsets.search(new SearchReadsetsRequest().setName("HG")))
        .thenReturn(readsetSearch);

    // Page 2
    Mockito.when(readsets.search(new SearchReadsetsRequest().setName("HG").setPageToken("page2")))
        .thenReturn(readsetSearch);
    Mockito.when(readsetSearch.execute()).thenReturn(
        new SearchReadsetsResponse()
            .setReadsets(Lists.newArrayList(new Readset().setId("r1")))
            .setNextPageToken("page2"),
        new SearchReadsetsResponse()
            .setReadsets(Lists.newArrayList(new Readset().setId("r2"))));

    Paginator.Readsets paginator = Paginator.Readsets.create(genomics);
    List<String> ids = Lists.newArrayList();
    for (Readset readset : paginator.search(new SearchReadsetsRequest().setName("HG"))) {
      ids.add(readset.getId());
    }

    assertEquals(Lists.newArrayList("r1", "r2"), ids);
  }

  @Test
  public void testPagination_withNullResults() throws Exception {
    Mockito.when(readsets.search(new SearchReadsetsRequest())).thenReturn(readsetSearch);
    Mockito.when(readsetSearch.execute()).thenReturn(new SearchReadsetsResponse());

    Paginator.Readsets paginator = Paginator.Readsets.create(genomics);
    List<String> ids = Lists.newArrayList();
    for (Readset readset : paginator.search(new SearchReadsetsRequest())) {
      ids.add(readset.getId());
    }

    // No results and no exceptions
    assertTrue(ids.isEmpty());
  }

}
