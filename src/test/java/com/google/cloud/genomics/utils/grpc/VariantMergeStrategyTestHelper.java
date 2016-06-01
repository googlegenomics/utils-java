/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.genomics.utils.grpc;

import static org.junit.Assert.assertEquals;

import com.google.genomics.v1.Variant;

import java.util.ArrayList;
import java.util.List;

public class VariantMergeStrategyTestHelper {

  public static class AccumulatingVariantEmitter implements VariantEmitterStrategy {
    private List<Variant> results = new ArrayList();

    @Override
    public void emit(Variant variant) {
      results.add(variant);
    }

    public List<Variant> getVariants() {
      return results;
    }
  }

  public static void mergeTest(List<Variant> input, List<Variant> expectedOutput,
      Class<? extends VariantMergeStrategy> clazz) throws InstantiationException, IllegalAccessException {
    VariantMergeStrategy merger = clazz.newInstance();
    VariantMergeStrategyTestHelper.AccumulatingVariantEmitter emitter =
        new VariantMergeStrategyTestHelper.AccumulatingVariantEmitter();

    merger.merge(input, emitter);
    List<Variant> output = emitter.getVariants();
    assertEquals(expectedOutput.size(), output.size());

    for (int i = 0; i < expectedOutput.size(); i++) {
      assertEquals(expectedOutput.get(i), output.get(i));
    }
  }

}
