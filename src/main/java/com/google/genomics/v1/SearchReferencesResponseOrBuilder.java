// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/genomics/v1/references.proto

package com.google.genomics.v1;

public interface SearchReferencesResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.genomics.v1.SearchReferencesResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .google.genomics.v1.Reference references = 1;</code>
   *
   * <pre>
   * The matching references.
   * </pre>
   */
  java.util.List<com.google.genomics.v1.Reference> 
      getReferencesList();
  /**
   * <code>repeated .google.genomics.v1.Reference references = 1;</code>
   *
   * <pre>
   * The matching references.
   * </pre>
   */
  com.google.genomics.v1.Reference getReferences(int index);
  /**
   * <code>repeated .google.genomics.v1.Reference references = 1;</code>
   *
   * <pre>
   * The matching references.
   * </pre>
   */
  int getReferencesCount();
  /**
   * <code>repeated .google.genomics.v1.Reference references = 1;</code>
   *
   * <pre>
   * The matching references.
   * </pre>
   */
  java.util.List<? extends com.google.genomics.v1.ReferenceOrBuilder> 
      getReferencesOrBuilderList();
  /**
   * <code>repeated .google.genomics.v1.Reference references = 1;</code>
   *
   * <pre>
   * The matching references.
   * </pre>
   */
  com.google.genomics.v1.ReferenceOrBuilder getReferencesOrBuilder(
      int index);

  /**
   * <code>optional string next_page_token = 2;</code>
   *
   * <pre>
   * The continuation token, which is used to page through large result sets.
   * Provide this value in a subsequent request to return the next page of
   * results. This field will be empty if there aren't any additional results.
   * </pre>
   */
  java.lang.String getNextPageToken();
  /**
   * <code>optional string next_page_token = 2;</code>
   *
   * <pre>
   * The continuation token, which is used to page through large result sets.
   * Provide this value in a subsequent request to return the next page of
   * results. This field will be empty if there aren't any additional results.
   * </pre>
   */
  com.google.protobuf.ByteString
      getNextPageTokenBytes();
}