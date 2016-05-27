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

import com.google.genomics.v1.Variant;

/**
 * Strategy pattern interface for variant merging logic.
 */
public interface VariantMergeStrategy {

  /**
   * Given an ordered collection of variants and non-variant segments that overlap a genomic region,
   * emit their merged representation via the emitter.
   *
   * The ordering of the input is assumed to be the order in which data is returned by the the
   * genomics API which is sorted by (variantset id, contig, start pos, variant id).
   *
   * @param variants
   * @param emitter
   */
  public void merge(Iterable<Variant> variants, VariantEmitterStrategy emitter);

}
