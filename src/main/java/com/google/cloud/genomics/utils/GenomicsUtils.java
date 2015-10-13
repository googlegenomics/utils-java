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
package com.google.cloud.genomics.utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.services.genomics.Genomics;
import com.google.api.services.genomics.model.CallSet;
import com.google.api.services.genomics.model.CoverageBucket;
import com.google.api.services.genomics.model.ListCoverageBucketsResponse;
import com.google.api.services.genomics.model.ReadGroupSet;
import com.google.api.services.genomics.model.Reference;
import com.google.api.services.genomics.model.ReferenceBound;
import com.google.api.services.genomics.model.SearchCallSetsRequest;
import com.google.api.services.genomics.model.SearchReadGroupSetsRequest;
import com.google.api.services.genomics.model.SearchReferencesRequest;
import com.google.api.services.genomics.model.SearchVariantSetsRequest;
import com.google.api.services.genomics.model.VariantSet;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * Convenience routines for fetching ids in the hierarchy of data within the Genomics API and other data lookups.
 */
public class GenomicsUtils {
  
  /**
   * Gets ReadGroupSetIds from a given datasetId using the Genomics API.
   *
   * @param datasetId The id of the dataset to query.
   * @param auth The OfflineAuth for the API request.
   * @return The list of readGroupSetIds in the dataset.
   * @throws IOException If dataset does not contain any readGroupSets.
   * @throws GeneralSecurityException
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
   * Gets the ReferenceSetId for a given readGroupSetId using the Genomics API.
   *
   * @param readGroupSetId The id of the readGroupSet to query.
   * @param auth The OfflineAuth for the API request.
   * @return The referenceSetId for the redGroupSet (which may be null).
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public static String getReferenceSetId(String readGroupSetId, GenomicsFactory.OfflineAuth auth)
      throws IOException, GeneralSecurityException {
    Genomics genomics = auth.getGenomics(auth.getDefaultFactory());
    ReadGroupSet readGroupSet = genomics.readgroupsets().get(readGroupSetId)
        .setFields("referenceSetId").execute();
    return readGroupSet.getReferenceSetId();
  }
  
  /**
   * Gets the CoverageBuckets for a given readGroupSetId using the Genomics API.
   *
   * @param readGroupSetId The id of the readGroupSet to query.
   * @param auth The OfflineAuth for the API request.
   * @return The list of reference bounds in the variantSet.
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public static List<CoverageBucket> getCoverageBuckets(String readGroupSetId, GenomicsFactory.OfflineAuth auth)
      throws IOException, GeneralSecurityException {
    Genomics genomics = auth.getGenomics(auth.getDefaultFactory());
    ListCoverageBucketsResponse response =
        genomics.readgroupsets().coveragebuckets().list(readGroupSetId).execute();
    // Requests of this form return one result per reference name, so therefore many fewer than
    // the default page size, but verify that the assumption holds true.
    if (!Strings.isNullOrEmpty(response.getNextPageToken())) {
      throw new IllegalArgumentException("Read group set " + readGroupSetId 
          + " has more Coverage Buckets than the default page size for the CoverageBuckets list operation.");
    }
    return response.getCoverageBuckets();
  }  


  /**
   * Gets the references for a given referenceSetId using the Genomics API.
   *
   * @param referenceSetId The id of the referenceSet to query.
   * @param auth The OfflineAuth for the API request.
   * @return The list of references in the referenceSet.
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public static Iterable<Reference> getReferences(String referenceSetId, GenomicsFactory.OfflineAuth auth)
      throws IOException, GeneralSecurityException {
    Genomics genomics = auth.getGenomics(auth.getDefaultFactory());
    return Paginator.References.create(
        genomics).search(new SearchReferencesRequest().setReferenceSetId(referenceSetId));
  }

  /**
   * Gets VariantSetIds from a given datasetId using the Genomics API.
   *
   * @param datasetId The id of the dataset to query.
   * @param auth The OfflineAuth for the API request.
   * @return The list of variantSetIds in the dataset.
   * @throws IOException If dataset does not contain any variantSets.
   * @throws GeneralSecurityException
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
   * 
   * @param variantSetId The id of the variantSet to query.
   * @param auth The OfflineAuth for the API request.
   * @return The list of callSet names in the variantSet.
   * @throws IOException If variantSet does not contain any CallSets.
   * @throws GeneralSecurityException

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
   *
   * @param variantSetId The id of the variantSet to query.
   * @param auth The OfflineAuth for the API request.
   * @return The list of reference bounds in the variantSet.
   * @throws IOException
   * @throws GeneralSecurityException
   */
  public static List<ReferenceBound> getReferenceBounds(String variantSetId, GenomicsFactory.OfflineAuth auth)
      throws IOException, GeneralSecurityException {
    Genomics genomics = auth.getGenomics(auth.getDefaultFactory());
    VariantSet variantSet = genomics.variantsets().get(variantSetId).execute();
    return variantSet.getReferenceBounds();
  }  
}
