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

import com.google.api.services.genomics.model.Header;
import com.google.api.services.genomics.model.HeaderSection;
import com.google.api.services.genomics.model.Read;
import com.google.api.services.genomics.model.Readset;

import java.util.List;
import java.util.logging.Logger;

/**
 * Contains some common utilities for managing Genomics classes
 */
public class GenomicsUtils {
  private static final Logger LOG = Logger.getLogger(GenomicsUtils.class.getName());
  
  /**
   * Returns the ReadGroup if it exists for a given Read, otherwise returns noReadgroup.
   * 
   * @param read The read to extract the readgroup from.
   * @param noReadgroup The default value to return in case of no readgroup.
   * @return The readgroup as a string if exists, otherwise noReadgroup.
   */
  public static final String getReadgroup(Read read, String noReadgroup) {
    if (read.getTags() == null) {
      LOG.fine("Read " + read.getName() + " with ID " 
            + read.getId() + " has no tag field. Returning default");
      return noReadgroup;
    }
    List<String> readgroups = read.getTags().get("RG");
    if (readgroups == null || readgroups.size() == 0) {
      LOG.fine("Read " + read.getName() + " with ID " 
          + read.getId() + " has no RG. Returning default");
      return noReadgroup;
    } else { 
      if (readgroups.size() > 1) {
        LOG.warning("Read " + read.getName() + " with ID " 
            + read.getId() + " has two readgroups");
      } 
      return readgroups.get(0);
    }
  }
  
  /**
   * Returns the ReadGroup if it exists for a given Read, otherwise returns null.
   * 
   * @param read The read to extract the readgroup from.
   * @return The readgroup as a string if exists, otherwise null.
   */
  public static final String getReadgroup(Read read) {
    return getReadgroup(read, null);
  }
  
  /**
   * Returns the HeaderSection if it exists for a given Readset, otherwise returns null.
   * 
   * @param readset The readset to get the HeaderSection from.
   * @return The resulting Headersection if exists, otherwise null.
   */
  public static final HeaderSection getHeaderSection(Readset readset) {
    List<HeaderSection> headers = readset.getFileData();
    if (headers == null || headers.size() == 0) {
      LOG.warning("Readset " + readset.getName() + " with ID "
          + readset.getId() + " has no filedata section. Returning null");
      return null;
    } else {
      if (headers.size() > 1) {
        LOG.warning("Readset " + readset.getName() + " with ID " 
            + readset.getId() + " has two filedata sections");
      }
      return headers.get(0);
    }
  }
  
  /**
   * Pulls the Header from a HeaderSection if exists, otherwise returns null.
   * 
   * @param headerSection The HeaderSection to extract the Header from.
   * @return The resulting Header if exists, otherwise null.
   */
  public static final Header getHeader(HeaderSection headerSection) {
    List<Header> headers = headerSection.getHeaders();
    if (headers == null || headers.size() == 0) {
      LOG.warning("HeaderSection has no header section");
      return null;
    } else {
      if (headers.size() > 1) {
        LOG.warning("HeaderSection has multiple header sections");
        return null;
      }
      return headers.get(0);
    }
  }
}
