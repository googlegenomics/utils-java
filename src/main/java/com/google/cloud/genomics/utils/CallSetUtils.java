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
import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * A collection of utility methods for working with call sets.
 *
 */
public class CallSetUtils {

  /**
   * Given a collection of callsets, return all the names.
   */
  public static final Function<CallSet, String> GET_NAMES = new Function<CallSet, String>() {
    @Override
    public String apply(CallSet c) {
      return c.getName();
    }
  };

  /**
   * Given a collection of callsets, return all the ids.
   */
  public static final Function<CallSet, String> GET_IDS = new Function<CallSet, String>() {
    @Override
    public String apply(CallSet c) {
      return c.getId();
    }
  };

  /**
   * Create a bi-directional map of names to ids for a collection of callsets.
   *
   * As a side effect, this will throw an IllegalArgumentException when the collection of callsets
   * is malformed due to multiple is mapping to the same name.
   *
   * @param callSets
   * @return the bi-directional map
   */
  public static final BiMap<String, String> getCallSetNameMapping(Iterable<CallSet> callSets) {
    BiMap<String, String> idToName = HashBiMap.create();
    for(CallSet callSet : callSets) {
      // Dev Note: Be sure to keep this map loading as id -> name since it ensures that
      // the values are unique.
      idToName.put(callSet.getId(), callSet.getName());
    }
    return idToName.inverse();
  }
}
