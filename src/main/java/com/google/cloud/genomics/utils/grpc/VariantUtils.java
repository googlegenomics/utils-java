/*
 * Copyright (C) 2015 Google Inc.
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

import com.google.cloud.genomics.utils.grpc.SequenceUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.genomics.v1.Position;
import com.google.genomics.v1.Variant;
import com.google.genomics.v1.VariantCall;
import com.google.protobuf.ListValue;

import java.util.Comparator;
import java.util.Map;
import java.util.List;

/**
 * Predicates, comparators, and other utility functions for variants.
 *
 */
public class VariantUtils {

  /** Types of SNP mutations. <code>REFERENCE</code> corresponds to no mutation. */
  public enum SnpTitvStatus {
    REFERENCE,
    TRANSITION,
    TRANSVERSION
  }

  public static final String INFO_FIELD_ANNOTATION_VQSLOD = "VQSLOD"; // assigned by the GATK's VQSR

  /**
   * Classifies mutation based on nucleotide changes. Assumes the variant is a SNP.
   *
   * @param allele integer representing an allele. 0 represents the reference allele and values > 0
   *    represent alternatives.
   * @param variant variant specifying the reference and alternative bases for this allele.
   * @return a classification of the allele as reference, transition, or transversion.
   */
  public static final SnpTitvStatus snpTitvStatus(int allele, Variant variant) {
    if (allele < 1) {
      return SnpTitvStatus.REFERENCE;
    }
    String mutation = variant.getReferenceBases() + variant.getAlternateBases(allele - 1);
    switch (mutation) {
      case "AG":
      case "GA":
      case "CT":
      case "TC":
        return SnpTitvStatus.TRANSITION;

      default:
        return SnpTitvStatus.TRANSVERSION;
    }
  }

  /**
   * Pulls the VQSLOD (or some other measure of quality) from the input variant.
   * @param variant the variant from which to pull the quality
   * @return detection quality measure as double for the variant
   */
  public static final double getVariantQualityMeasure(Variant variant) {
    Map<String, ListValue> infoField = variant.getInfo();
    return infoField.containsKey(INFO_FIELD_ANNOTATION_VQSLOD)
        ? Double.parseDouble(
        infoField.get(INFO_FIELD_ANNOTATION_VQSLOD).getValues(0).getStringValue())
        : variant.getQuality();
  }

  /**
   * Extracts a position object from a variant object.
   * @param variant the variant from which to extract a position
   * @return position of the variant
   */
  public static final Position getPosition(Variant variant) {
    return Position.newBuilder()
        .setReferenceName(variant.getReferenceName())
        .setPosition(variant.getStart())
        .build();
  }

  /**
   * Determines if the provided allele is a SNP mutation.
   * @param allele the allele to check
   * @param variant the variant from which the allele originated
   * @return true if the allele is a SNP
   */
  public static final boolean isSnpAllele(int allele, Variant variant) {
    return variant.getReferenceBases().length() == 1
            && variant.getAlternateBases(allele - 1).length() == 1;
  }

  /**
   * Determines if the provided allele is a deletion mutation.
   * @param allele the allele to check
   * @param variant the variant from which the allele originated
   * @return true if the allele is a deletion
   */
  public static final boolean isDeletionAllele(int allele, Variant variant) {
    return variant.getReferenceBases().length() > variant.getAlternateBases(allele - 1).length();
  }

  /**
   * Determines if the provided allele is an insertion mutation.
   * @param allele the allele to check
   * @param variant the variant from which the allele originated
   * @return true if the allele is an insertion
   */
  public static final boolean isInsertionAllele(int allele, Variant variant) {
    return variant.getReferenceBases().length() < variant.getAlternateBases(allele - 1).length();
  }

  /**
   * For data processed by GATK the value of ALT is "&lt;NON_REF&gt;" for non-variant
   * segments.
   *
   * See https://www.broadinstitute.org/gatk/guide/article?id=4017 for more detail.
   */
  public static final String GATK_NON_VARIANT_SEGMENT_ALT = "<NON_REF>";

  /**
   * Determines if the variant has at least one non-reference allele in at least one individual.
   */
  public static final Predicate<Variant> HAS_VARIATION = new Predicate<Variant>() {
    @Override
    public boolean apply(Variant variant) {
      return !Iterables.all(variant.getCallsList(), VariantCallUtils.HAS_REFERENCE_GENOTYPE);
    }
  };

  /**
   * Determine whether the variant has any values in alternate bases.
   */
  public static final Predicate<Variant> HAS_ALTERNATE = new Predicate<Variant>() {
    @Override
    public boolean apply(Variant variant) {
      List<String> alternateBases = variant.getAlternateBasesList();
      return !(null == alternateBases || alternateBases.isEmpty());
    }
  };

  /**
   * Determine whether the sequence is of length one (e.g., a SNP).
   */
  public static final Predicate<String> LENGTH_IS_1 = Predicates.compose(Predicates.equalTo(1),
      new Function<String, Integer>() {
        @Override
        public Integer apply(String string) {
          return string.length();
        }
      });

  /**
   * Determines whether the variant is filtered according to the FILTER field,
   * using the notation required by the VCF 4.2 standard. This means that the
   * FILTER field of the variant must either be empty, or contain only "PASS"
   * or ".".
   */
  public static final Predicate<Variant> IS_FILTERED = new Predicate<Variant>() {
    @Override
    public boolean apply(Variant variant) {
      return variant.getFilterCount() != 0 && !((variant.getFilterCount() == 1)
          && (variant.getFilter(0).equalsIgnoreCase("PASS") ||
          variant.getFilter(0).equalsIgnoreCase(".")));
    }
  };

  /**
   * Determines whether a variant has one and only one alternate allele.
   */
  public static final Predicate<Variant> IS_BIALLELIC = Predicates.and(HAS_ALTERNATE,
      new Predicate<Variant>() {
        @Override
        public boolean apply(Variant variant) {
          return variant.getAlternateBasesList().size() == 1;
        }
      }
  );

  /**
   * Determines whether a variant is a gVCF record.
   */
  public static final Predicate<Variant> IS_GVCF = new Predicate<Variant>() {
    @Override
    public boolean apply(Variant variant) {
      return variant.getAlternateBasesCount() == 1 && variant.getAlternateBases(0).equals("<*>");
    }
  };

  /**
   * Determines whether a variant represents a multi-nucleotide change.
   */
  public static final Predicate<Variant> IS_MULTI_NUCLEOTIDE = new Predicate<Variant>() {
    @Override
    public boolean apply(Variant variant) {
      int refSize = variant.getReferenceBases().length();
      if (refSize < 1) {
        return false;
      } else if (refSize > 1) {
        return true;
      }
      for (String alt : variant.getAlternateBasesList()) {
        if (alt.length() != refSize) {
          return true;
        }
      }

      return false;
    }
  };

  /**
   * Determine whether the variant is a SNP.
   */
  public static final Predicate<Variant> IS_SNP = Predicates.and(HAS_ALTERNATE,
      new Predicate<Variant>() {
        @Override
        public boolean apply(Variant variant) {
          return LENGTH_IS_1.apply(variant.getReferenceBases())
              && Iterables.all(variant.getAlternateBasesList(), LENGTH_IS_1);
        }
      });

  /**
   * Determine whether the variant is a non-variant segment (a.k.a. non-variant block record).
   *
   * For Complete Genomics data and gVCFs such as Platinum Genomes, we wind up with zero alternates
   * (the missing value indicator "." in the VCF ALT field gets converted to null). See
   * https://sites.google.com/site/gvcftools/home/about-gvcf for more detail.
   */
  public static final Predicate<Variant> IS_NON_VARIANT_SEGMENT_WITH_MISSING_ALT = Predicates.and(
      Predicates.not(HAS_ALTERNATE), new Predicate<Variant>() {
        @Override
        public boolean apply(Variant variant) {
          // The same deletion can be specified as [CAG -> C] or [AG -> null], so double check that
          // the reference bases are also of length 1 when there are no alternates.
          return LENGTH_IS_1.apply(variant.getReferenceBases());
        }
      });

  /**
   * Determine whether the variant is a non-variant segment (a.k.a. non-variant block record).
   *
   * For data processed by GATK the value of ALT is "&lt;NON_REF&gt;". See
   * https://www.broadinstitute.org/gatk/guide/article?id=4017 for more detail.
   *
   * Note that alternate bases may include true variants (e.g., C,&lt;NON_REF&gt;) but still correspond to
   * a non-variant segment.
   */
  public static final Predicate<Variant> IS_NON_VARIANT_SEGMENT_WITH_GATK_ALT = new Predicate<Variant>() {
        @Override
        public boolean apply(Variant variant) {
          return Iterables.all(variant.getAlternateBasesList(),
              Predicates.equalTo(GATK_NON_VARIANT_SEGMENT_ALT));
        }
      };

  /**
   * Determine whether the variant is a non-variant segment (a.k.a. non-variant block record).
   */
  public static final Predicate<Variant> IS_NON_VARIANT_SEGMENT = Predicates.or(
      IS_NON_VARIANT_SEGMENT_WITH_MISSING_ALT, IS_NON_VARIANT_SEGMENT_WITH_GATK_ALT);

  public static final Ordering<Variant> BY_START = Ordering.natural().onResultOf(
      new Function<Variant, Long>() {
        @Override
        public Long apply(Variant variant) {
          return variant.getStart();
        }
      });

  public static final Ordering<Variant> BY_NON_VARIANT_SEGMENT_STATUS = Ordering.natural().onResultOf(
      new Function<Variant, Boolean>() {
        @Override
        public Boolean apply(Variant variant) {
          return !VariantUtils.IS_NON_VARIANT_SEGMENT.apply(variant);
        }
      });

  public static final Ordering<Variant> BY_REFERENCE_BASES = Ordering.natural()
      .nullsFirst().onResultOf(new Function<Variant, String>() {
        @Override
        public String apply(Variant variant) {
          return variant.getReferenceBases();
        }
      });

  public static final Ordering<Variant> BY_ALTERNATE_BASES = new Ordering<Variant>() {
    @Override
    public int compare(Variant v1, Variant v2) {
      int v1Alts = v1.getAlternateBasesCount();
      int v2Alts = v2.getAlternateBasesCount();
      int i = 0;
      while (i < v1Alts && i < v2Alts) {
        int cmp = v1.getAlternateBases(i).compareTo(v2.getAlternateBases(i));
        if (0 != cmp) {
          return cmp;
        }
        i++;
      }
      if (i == v1Alts && i == v2Alts) {
        return 0;
      } else if (v1Alts < v2Alts) {
        return -1;
      } else {
        return 1;
      }
    }
  };

  /**
   * Special-purpose comparator for use in dealing with both variant and non-variant segment data.
   * Sort by start position ascending and ensure that if a variant and a ref-matching block are at
   * the same position, the non-variant segment record comes first.
   */
  public static final Comparator<Variant> NON_VARIANT_SEGMENT_COMPARATOR = BY_START
      .compound(BY_NON_VARIANT_SEGMENT_STATUS.compound(BY_REFERENCE_BASES.compound(
          BY_ALTERNATE_BASES)));

  /**
   * A Comparator that orders {@code Variant} objects by chromosome, position, and alleles.
   */
  public static final Comparator<Variant> CHROMOSOMAL_ORDER = new ChromosomalOrderComparator();

  private static final class ChromosomalOrderComparator implements Comparator<Variant> {
    @Override
    public int compare(Variant v1, Variant v2) {
      int comparison =
          ComparisonChain.start()
              .compare(v1.getReferenceName(), v2.getReferenceName())
              .compare(v1.getStart(), v2.getStart())
              .compare(v1.getEnd(), v2.getEnd())
              .compare(v1.getReferenceBases(), v2.getReferenceBases())
              .compare(v1.getAlternateBasesList().size(), v2.getAlternateBasesList().size())
              .result();

      // If there is no difference yet, compare based on alternate alleles until one is found.
      // This is a safe iteration since the list sizes are determined to be equal in the above
      // chain.
      int ix = 0;
      int numAlternateAlleles = v1.getAlternateBasesList().size();
      while (comparison == 0 && ix < numAlternateAlleles) {
        comparison = v1.getAlternateBases(ix).compareTo(v2.getAlternateBases(ix));
        ix++;
      }
      return comparison;
    }
  }

  /**
   * Determine whether the first variant overlaps the second variant.
   *
   * This is particularly useful when determining whether a non-variant segment overlaps a variant.
   *
   * @param blockRecord
   * @param variant
   * @return true, if the first variant overlaps the second variant
   */
  public static final boolean isOverlapping(Variant blockRecord, Variant variant) {
    return blockRecord.getStart() <= variant.getStart()
        && blockRecord.getEnd() >= variant.getStart() + 1;
  }

  /**
   * Determine whether two variants occur at the same site in the genome, where "site"
   * is defined by reference name, start position, and reference bases.
   *
   * The reference bases are taken into account particularly for indels.
   *
   * @param variant1
   * @param variant2
   * @return true, if they occur at the same site
   */
  public static final boolean isSameVariantSite(Variant.Builder variant1, Variant variant2) {
    return variant1.getReferenceName().equals(variant2.getReferenceName())
        && variant1.getReferenceBases().equals(variant2.getReferenceBases())
        && variant1.getStart() == variant2.getStart();
  }
}