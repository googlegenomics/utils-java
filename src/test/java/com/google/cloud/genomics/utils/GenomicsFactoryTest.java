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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.genomics.Genomics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class GenomicsFactoryTest {

  @Test
  public void testBasic() throws Exception {
    GenomicsFactory genomicsFactory = GenomicsFactory.builder("test_client").build();

    Genomics genomics = genomicsFactory.fromApiKey("xyz");
    assertEquals(0, genomicsFactory.initializedRequestsCount());

    // TODO: Mock out more of this test if it becomes a problem
    try {
      genomics.jobs().get("123").execute();
    } catch (GoogleJsonResponseException e) {
      // Expected
    }
    assertEquals(1, genomicsFactory.initializedRequestsCount());
    assertEquals(1, genomicsFactory.unsuccessfulResponsesCount());
    assertEquals(0, genomicsFactory.ioExceptionsCount());

    try {
      genomics.readgroupsets().get("123").execute();
    } catch (GoogleJsonResponseException e) {
      // Expected
    }
    assertEquals(2, genomicsFactory.initializedRequestsCount());
    assertEquals(2, genomicsFactory.unsuccessfulResponsesCount());
    assertEquals(0, genomicsFactory.ioExceptionsCount());
  }
}
