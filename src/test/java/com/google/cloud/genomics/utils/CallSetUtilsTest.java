/*
 * Copyright (C) 2016 Google Inc.
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

import com.google.api.services.genomics.model.CallSet;
import com.google.common.collect.BiMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class CallSetUtilsTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testGetCallSetNameMapping() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("value already present: duplicate");

    List<CallSet> callSets = Arrays.asList(
        new CallSet().setName("unique").setId("123-0"),
        new CallSet().setName("duplicate").setId("123-1"),
        new CallSet().setName("duplicate").setId("123-2")
        );
    BiMap<String, String> namesToIds = CallSetUtils.getCallSetNameMapping(callSets);
  }

}
