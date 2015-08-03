package com.google.cloud.genomics.utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.model.CallSet;
import com.google.api.services.genomics.model.ReadGroupSet;
import com.google.api.services.genomics.model.ReferenceBound;
import com.google.api.services.genomics.model.SearchCallSetsRequest;
import com.google.api.services.genomics.model.SearchReadGroupSetsRequest;
import com.google.api.services.genomics.model.SearchVariantSetsRequest;
import com.google.api.services.genomics.model.VariantSet;
import com.google.common.collect.Lists;

/**
 * Convenience routines for fetching ids in the hierarchy of data within the Genomics API and other data lookups.
 */
public class GenomicsUtils {
  
  /**
   * Gets ReadGroupSetIds from a given datasetId using the Genomics API.
   */
  public static List<String> getReadGroupSetIds(String datasetId, GenomicsFactory.OfflineAuth auth)
      throws IOException, GeneralSecurityException {
    List<String> output = Lists.newArrayList();
    Iterable<ReadGroupSet> rgs = Paginator.ReadGroupSets.create(
        auth.getGenomics(auth.getDefaultFactory()))
        .search(new SearchReadGroupSetsRequest().setDatasetIds(Lists.newArrayList(datasetId)),
            "readGroupSets/id,nextPageToken");
    for (ReadGroupSet r : rgs) {
      output.add(r.getId());
    }
    if (output.isEmpty()) {
      throw new IOException("Dataset " + datasetId + " does not contain any ReadGroupSets");
    }
    return output;
  }
  
  /**
   * Gets VariantSetIds from a given datasetId using the Genomics API.
   */
  public static List<String> getVariantSetIds(String datasetId, GenomicsFactory.OfflineAuth auth)
      throws IOException, GeneralSecurityException {
    List<String> output = Lists.newArrayList();
    Iterable<VariantSet> vs = Paginator.Variantsets.create(
        auth.getGenomics(auth.getDefaultFactory()))
        .search(new SearchVariantSetsRequest().setDatasetIds(Lists.newArrayList(datasetId)),
            "variantSets/id,nextPageToken");
    for (VariantSet v : vs) {
      output.add(v.getId());
    }
    if (output.isEmpty()) {
      throw new IOException("Dataset " + datasetId + " does not contain any VariantSets");
    }
    return output;
  }
  
  /**
   * Gets CallSets Names for a given variantSetId using the Genomics API.
   */
  public static List<String> getCallSetsNames(String variantSetId, GenomicsFactory.OfflineAuth auth)
      throws IOException, GeneralSecurityException {
    List<String> output = Lists.newArrayList();
    Iterable<CallSet> cs = Paginator.Callsets.create(
        auth.getGenomics(auth.getDefaultFactory()))
        .search(new SearchCallSetsRequest().setVariantSetIds(Lists.newArrayList(variantSetId)),
            "callSets/name,nextPageToken");
    for (CallSet c : cs) {
      output.add(c.getName());
    }
    if (output.isEmpty()) {
      throw new IOException("VariantSet " + variantSetId + " does not contain any CallSets");
    }
    return output;
  }

  /**
   * Gets the ReferenceBounds for a given variantSetId using the Genomics API.
   */
  public static List<ReferenceBound> getReferenceBounds(String variantSetId, GenomicsFactory.OfflineAuth auth)
      throws IOException, GeneralSecurityException {
    Genomics genomics = auth.getGenomics(auth.getDefaultFactory());
    VariantSet variantSet = genomics.variantsets().get(variantSetId).execute();
    return variantSet.getReferenceBounds();
  }

}
