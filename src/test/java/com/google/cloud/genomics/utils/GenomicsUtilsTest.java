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

import static org.junit.Assert.assertTrue;

import com.google.api.services.genomics.model.Header;
import com.google.api.services.genomics.model.HeaderSection;
import com.google.api.services.genomics.model.Read;
import com.google.api.services.genomics.model.Readset;
import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(JUnit4.class)
public class GenomicsUtilsTest {

  @Test
  public void testGetReadgroup() {
    Read read = new Read();
    
    assertTrue(GenomicsUtils.getReadgroup(read).equals(GenomicsUtils.NO_READGROUP));
    
    Map<String, List<String>> tags = new HashMap<String, List<String>>();
    tags.put("RG", Lists.newArrayList("TEST_READGROUP"));
    read.setTags(tags);
    assertTrue(GenomicsUtils.getReadgroup(read).equals("TEST_READGROUP"));
  }
  
  @Test
  public void testGetHeaderSection() {
    Readset readset = new Readset();
    
    assertTrue(GenomicsUtils.getHeaderSection(readset) == null);
    
    HeaderSection header = new HeaderSection();
    header.set("CO", "TEST_HEADERSECTION");
    readset.setFileData(Lists.newArrayList(header));
    assertTrue(GenomicsUtils.getHeaderSection(readset).equals(header));
  }
  
  @Test
  public void testGetHeader() {
    HeaderSection headerSection = new HeaderSection();
    
    assertTrue(GenomicsUtils.getHeader(headerSection) == null);
    
    Header header = new Header();
    header.setVersion("TEST_VERSION");
    header.setSortingOrder("unmapped");
    headerSection.setHeaders(Lists.newArrayList(header));
    assertTrue(GenomicsUtils.getHeader(headerSection).equals(header));
  }
}
