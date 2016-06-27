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

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Predicate;

import java.util.List;

/**
 * Various sequence and genome utility functions
 */
public class SequenceUtils {

  /** Types of genotypes possible for diploid and haploid variants based on zygosity. */
  public enum GenotypeCategory {
    REF_HOMOZYGOUS, // Diploid
    HETEROZYGOUS, // Diploid
    NON_REF_HOMOZYGOUS, // Diploid
    REF, // Haploid
    NON_REF // Haploid
  }

  /**
   * Classifies a genotype based on zygosity and ploidy.
   *
   * @param genotype list of integers representing a genotype within which 0 represents the
   *    reference allele and values > 0 represent alternatives.
   * @return Classification of this genotype base on zygosity, with special handling for haploids.
   */
  public static final GenotypeCategory classifyGenotype(List<Integer> genotype) {
    boolean isHaploid = genotype.size() < 2;
    boolean hasRef = false;
    boolean allRef = true;
    for (Integer allele : genotype) {
      if (allele == 0) {
        hasRef = true;
      } else {
        allRef = false;
      }
    }

    if (allRef) {
      // Reference homozygous iff {0,0}, haploid reference if {0}
      return isHaploid ? GenotypeCategory.REF : GenotypeCategory.REF_HOMOZYGOUS;
    } else if (hasRef) {
      return GenotypeCategory.HETEROZYGOUS;
    }
    // We group all alternative alleles into non-reference for QC purposes.
    return isHaploid ? GenotypeCategory.NON_REF : GenotypeCategory.NON_REF_HOMOZYGOUS;
  }

  /**
   * Returns a new haplotype that has refAllele in ref substituted with altAllele.
   *
   * <p>ref should be a sequence of reference bases starting immediately at refAllele. This function
   * splices out the refAllele bases from ref and replaces those with altAllele, returning the new
   * haplotype. This is the core "substitution" operation of a Variant, where a variant AG => A
   * generates a new haplotype by first removing the AG bases and then replacing them with the A
   * base. For example:
   * {@literal
   *   ref         : AGTGC
   *   refAllele   : AG
   *   altAllele   : A
   *   result      : ATGC
   * }
   *
   * @param ref a string of DNA bases starting with refAllele. Must be non-empty.
   * @param refAllele the reference allele bases that we will remove from ref. ref must startWith
   *     refAllele or a check will fail. Must be non-empty.
   * @param altAllele a string of bases to replace those of refAllele in ref. Must be non-empty.
   * @return A string of bases with refAllele substituted for altAllele.
   */
  public static String substituteAllele(String ref, String refAllele, String altAllele) {
    checkArgument(!ref.isEmpty(), "ref cannot be empty");
    checkArgument(!refAllele.isEmpty(), "refAllele cannot be empty");
    checkArgument(!altAllele.isEmpty(), "altAllele cannot be empty");
    checkArgument(
        ref.startsWith(refAllele), "Reference %s should start with refAllele %s", ref, refAllele);
    return altAllele + ref.substring(refAllele.length());
  }

  /**
   * Predicate for the common pattern of using FluentIterable over a list of genotypes and operating
   * on only the non-reference alleles.
   */
  public static class NonRefAllelesPredicate implements Predicate<Integer> {
    @Override
    public boolean apply(Integer allele) {
      return allele > 0;
    }
  }

}