// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/genomics/v1/reads.proto

package com.google.genomics.v1;

/**
 * Protobuf type {@code google.genomics.v1.DeleteReadGroupSetRequest}
 */
public  final class DeleteReadGroupSetRequest extends
    com.google.protobuf.GeneratedMessage implements
    // @@protoc_insertion_point(message_implements:google.genomics.v1.DeleteReadGroupSetRequest)
    DeleteReadGroupSetRequestOrBuilder {
  // Use DeleteReadGroupSetRequest.newBuilder() to construct.
  private DeleteReadGroupSetRequest(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
    super(builder);
  }
  private DeleteReadGroupSetRequest() {
    readGroupSetId_ = "";
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private DeleteReadGroupSetRequest(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry) {
    this();
    int mutable_bitField0_ = 0;
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!input.skipField(tag)) {
              done = true;
            }
            break;
          }
          case 10: {
            String s = input.readStringRequireUtf8();

            readGroupSetId_ = s;
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw new RuntimeException(e.setUnfinishedMessage(this));
    } catch (java.io.IOException e) {
      throw new RuntimeException(
          new com.google.protobuf.InvalidProtocolBufferException(
              e.getMessage()).setUnfinishedMessage(this));
    } finally {
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.google.genomics.v1.ReadsProto.internal_static_google_genomics_v1_DeleteReadGroupSetRequest_descriptor;
  }

  protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.genomics.v1.ReadsProto.internal_static_google_genomics_v1_DeleteReadGroupSetRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.genomics.v1.DeleteReadGroupSetRequest.class, com.google.genomics.v1.DeleteReadGroupSetRequest.Builder.class);
  }

  public static final int READ_GROUP_SET_ID_FIELD_NUMBER = 1;
  private volatile java.lang.Object readGroupSetId_;
  /**
   * <code>optional string read_group_set_id = 1;</code>
   *
   * <pre>
   * The ID of the read group set to be deleted. The caller must have WRITE
   * permissions to the dataset associated with this read group set.
   * </pre>
   */
  public java.lang.String getReadGroupSetId() {
    java.lang.Object ref = readGroupSetId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      readGroupSetId_ = s;
      return s;
    }
  }
  /**
   * <code>optional string read_group_set_id = 1;</code>
   *
   * <pre>
   * The ID of the read group set to be deleted. The caller must have WRITE
   * permissions to the dataset associated with this read group set.
   * </pre>
   */
  public com.google.protobuf.ByteString
      getReadGroupSetIdBytes() {
    java.lang.Object ref = readGroupSetId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      readGroupSetId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getReadGroupSetIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessage.writeString(output, 1, readGroupSetId_);
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getReadGroupSetIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessage.computeStringSize(1, readGroupSetId_);
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  public static com.google.genomics.v1.DeleteReadGroupSetRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.genomics.v1.DeleteReadGroupSetRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.genomics.v1.DeleteReadGroupSetRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.genomics.v1.DeleteReadGroupSetRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.genomics.v1.DeleteReadGroupSetRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.google.genomics.v1.DeleteReadGroupSetRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }
  public static com.google.genomics.v1.DeleteReadGroupSetRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input);
  }
  public static com.google.genomics.v1.DeleteReadGroupSetRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input, extensionRegistry);
  }
  public static com.google.genomics.v1.DeleteReadGroupSetRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.google.genomics.v1.DeleteReadGroupSetRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.google.genomics.v1.DeleteReadGroupSetRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessage.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code google.genomics.v1.DeleteReadGroupSetRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessage.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:google.genomics.v1.DeleteReadGroupSetRequest)
      com.google.genomics.v1.DeleteReadGroupSetRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.google.genomics.v1.ReadsProto.internal_static_google_genomics_v1_DeleteReadGroupSetRequest_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.genomics.v1.ReadsProto.internal_static_google_genomics_v1_DeleteReadGroupSetRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.genomics.v1.DeleteReadGroupSetRequest.class, com.google.genomics.v1.DeleteReadGroupSetRequest.Builder.class);
    }

    // Construct using com.google.genomics.v1.DeleteReadGroupSetRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
      }
    }
    public Builder clear() {
      super.clear();
      readGroupSetId_ = "";

      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.google.genomics.v1.ReadsProto.internal_static_google_genomics_v1_DeleteReadGroupSetRequest_descriptor;
    }

    public com.google.genomics.v1.DeleteReadGroupSetRequest getDefaultInstanceForType() {
      return com.google.genomics.v1.DeleteReadGroupSetRequest.getDefaultInstance();
    }

    public com.google.genomics.v1.DeleteReadGroupSetRequest build() {
      com.google.genomics.v1.DeleteReadGroupSetRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public com.google.genomics.v1.DeleteReadGroupSetRequest buildPartial() {
      com.google.genomics.v1.DeleteReadGroupSetRequest result = new com.google.genomics.v1.DeleteReadGroupSetRequest(this);
      result.readGroupSetId_ = readGroupSetId_;
      onBuilt();
      return result;
    }

    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.google.genomics.v1.DeleteReadGroupSetRequest) {
        return mergeFrom((com.google.genomics.v1.DeleteReadGroupSetRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.genomics.v1.DeleteReadGroupSetRequest other) {
      if (other == com.google.genomics.v1.DeleteReadGroupSetRequest.getDefaultInstance()) return this;
      if (!other.getReadGroupSetId().isEmpty()) {
        readGroupSetId_ = other.readGroupSetId_;
        onChanged();
      }
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.google.genomics.v1.DeleteReadGroupSetRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.google.genomics.v1.DeleteReadGroupSetRequest) e.getUnfinishedMessage();
        throw e;
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object readGroupSetId_ = "";
    /**
     * <code>optional string read_group_set_id = 1;</code>
     *
     * <pre>
     * The ID of the read group set to be deleted. The caller must have WRITE
     * permissions to the dataset associated with this read group set.
     * </pre>
     */
    public java.lang.String getReadGroupSetId() {
      java.lang.Object ref = readGroupSetId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        readGroupSetId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string read_group_set_id = 1;</code>
     *
     * <pre>
     * The ID of the read group set to be deleted. The caller must have WRITE
     * permissions to the dataset associated with this read group set.
     * </pre>
     */
    public com.google.protobuf.ByteString
        getReadGroupSetIdBytes() {
      java.lang.Object ref = readGroupSetId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        readGroupSetId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string read_group_set_id = 1;</code>
     *
     * <pre>
     * The ID of the read group set to be deleted. The caller must have WRITE
     * permissions to the dataset associated with this read group set.
     * </pre>
     */
    public Builder setReadGroupSetId(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      readGroupSetId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string read_group_set_id = 1;</code>
     *
     * <pre>
     * The ID of the read group set to be deleted. The caller must have WRITE
     * permissions to the dataset associated with this read group set.
     * </pre>
     */
    public Builder clearReadGroupSetId() {
      
      readGroupSetId_ = getDefaultInstance().getReadGroupSetId();
      onChanged();
      return this;
    }
    /**
     * <code>optional string read_group_set_id = 1;</code>
     *
     * <pre>
     * The ID of the read group set to be deleted. The caller must have WRITE
     * permissions to the dataset associated with this read group set.
     * </pre>
     */
    public Builder setReadGroupSetIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      readGroupSetId_ = value;
      onChanged();
      return this;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:google.genomics.v1.DeleteReadGroupSetRequest)
  }

  // @@protoc_insertion_point(class_scope:google.genomics.v1.DeleteReadGroupSetRequest)
  private static final com.google.genomics.v1.DeleteReadGroupSetRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.google.genomics.v1.DeleteReadGroupSetRequest();
  }

  public static com.google.genomics.v1.DeleteReadGroupSetRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<DeleteReadGroupSetRequest>
      PARSER = new com.google.protobuf.AbstractParser<DeleteReadGroupSetRequest>() {
    public DeleteReadGroupSetRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      try {
        return new DeleteReadGroupSetRequest(input, extensionRegistry);
      } catch (RuntimeException e) {
        if (e.getCause() instanceof
            com.google.protobuf.InvalidProtocolBufferException) {
          throw (com.google.protobuf.InvalidProtocolBufferException)
              e.getCause();
        }
        throw e;
      }
    }
  };

  public static com.google.protobuf.Parser<DeleteReadGroupSetRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<DeleteReadGroupSetRequest> getParserForType() {
    return PARSER;
  }

  public com.google.genomics.v1.DeleteReadGroupSetRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

