// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/genomics/v1/variants.proto

package com.google.genomics.v1;

/**
 * Protobuf type {@code google.genomics.v1.GetVariantSetRequest}
 *
 * <pre>
 * The variant set request.
 * </pre>
 */
public  final class GetVariantSetRequest extends
    com.google.protobuf.GeneratedMessage implements
    // @@protoc_insertion_point(message_implements:google.genomics.v1.GetVariantSetRequest)
    GetVariantSetRequestOrBuilder {
  // Use GetVariantSetRequest.newBuilder() to construct.
  private GetVariantSetRequest(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
    super(builder);
  }
  private GetVariantSetRequest() {
    variantSetId_ = "";
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private GetVariantSetRequest(
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

            variantSetId_ = s;
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
    return com.google.genomics.v1.VariantsProto.internal_static_google_genomics_v1_GetVariantSetRequest_descriptor;
  }

  protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.genomics.v1.VariantsProto.internal_static_google_genomics_v1_GetVariantSetRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.genomics.v1.GetVariantSetRequest.class, com.google.genomics.v1.GetVariantSetRequest.Builder.class);
  }

  public static final int VARIANT_SET_ID_FIELD_NUMBER = 1;
  private volatile java.lang.Object variantSetId_;
  /**
   * <code>optional string variant_set_id = 1;</code>
   *
   * <pre>
   * Required. The ID of the variant set.
   * </pre>
   */
  public java.lang.String getVariantSetId() {
    java.lang.Object ref = variantSetId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      variantSetId_ = s;
      return s;
    }
  }
  /**
   * <code>optional string variant_set_id = 1;</code>
   *
   * <pre>
   * Required. The ID of the variant set.
   * </pre>
   */
  public com.google.protobuf.ByteString
      getVariantSetIdBytes() {
    java.lang.Object ref = variantSetId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      variantSetId_ = b;
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
    if (!getVariantSetIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessage.writeString(output, 1, variantSetId_);
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getVariantSetIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessage.computeStringSize(1, variantSetId_);
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  public static com.google.genomics.v1.GetVariantSetRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.genomics.v1.GetVariantSetRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.genomics.v1.GetVariantSetRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.genomics.v1.GetVariantSetRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.genomics.v1.GetVariantSetRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.google.genomics.v1.GetVariantSetRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }
  public static com.google.genomics.v1.GetVariantSetRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input);
  }
  public static com.google.genomics.v1.GetVariantSetRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input, extensionRegistry);
  }
  public static com.google.genomics.v1.GetVariantSetRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.google.genomics.v1.GetVariantSetRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.google.genomics.v1.GetVariantSetRequest prototype) {
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
   * Protobuf type {@code google.genomics.v1.GetVariantSetRequest}
   *
   * <pre>
   * The variant set request.
   * </pre>
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessage.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:google.genomics.v1.GetVariantSetRequest)
      com.google.genomics.v1.GetVariantSetRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.google.genomics.v1.VariantsProto.internal_static_google_genomics_v1_GetVariantSetRequest_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.genomics.v1.VariantsProto.internal_static_google_genomics_v1_GetVariantSetRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.genomics.v1.GetVariantSetRequest.class, com.google.genomics.v1.GetVariantSetRequest.Builder.class);
    }

    // Construct using com.google.genomics.v1.GetVariantSetRequest.newBuilder()
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
      variantSetId_ = "";

      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.google.genomics.v1.VariantsProto.internal_static_google_genomics_v1_GetVariantSetRequest_descriptor;
    }

    public com.google.genomics.v1.GetVariantSetRequest getDefaultInstanceForType() {
      return com.google.genomics.v1.GetVariantSetRequest.getDefaultInstance();
    }

    public com.google.genomics.v1.GetVariantSetRequest build() {
      com.google.genomics.v1.GetVariantSetRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public com.google.genomics.v1.GetVariantSetRequest buildPartial() {
      com.google.genomics.v1.GetVariantSetRequest result = new com.google.genomics.v1.GetVariantSetRequest(this);
      result.variantSetId_ = variantSetId_;
      onBuilt();
      return result;
    }

    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.google.genomics.v1.GetVariantSetRequest) {
        return mergeFrom((com.google.genomics.v1.GetVariantSetRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.genomics.v1.GetVariantSetRequest other) {
      if (other == com.google.genomics.v1.GetVariantSetRequest.getDefaultInstance()) return this;
      if (!other.getVariantSetId().isEmpty()) {
        variantSetId_ = other.variantSetId_;
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
      com.google.genomics.v1.GetVariantSetRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.google.genomics.v1.GetVariantSetRequest) e.getUnfinishedMessage();
        throw e;
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object variantSetId_ = "";
    /**
     * <code>optional string variant_set_id = 1;</code>
     *
     * <pre>
     * Required. The ID of the variant set.
     * </pre>
     */
    public java.lang.String getVariantSetId() {
      java.lang.Object ref = variantSetId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        variantSetId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string variant_set_id = 1;</code>
     *
     * <pre>
     * Required. The ID of the variant set.
     * </pre>
     */
    public com.google.protobuf.ByteString
        getVariantSetIdBytes() {
      java.lang.Object ref = variantSetId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        variantSetId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string variant_set_id = 1;</code>
     *
     * <pre>
     * Required. The ID of the variant set.
     * </pre>
     */
    public Builder setVariantSetId(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      variantSetId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string variant_set_id = 1;</code>
     *
     * <pre>
     * Required. The ID of the variant set.
     * </pre>
     */
    public Builder clearVariantSetId() {
      
      variantSetId_ = getDefaultInstance().getVariantSetId();
      onChanged();
      return this;
    }
    /**
     * <code>optional string variant_set_id = 1;</code>
     *
     * <pre>
     * Required. The ID of the variant set.
     * </pre>
     */
    public Builder setVariantSetIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      variantSetId_ = value;
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


    // @@protoc_insertion_point(builder_scope:google.genomics.v1.GetVariantSetRequest)
  }

  // @@protoc_insertion_point(class_scope:google.genomics.v1.GetVariantSetRequest)
  private static final com.google.genomics.v1.GetVariantSetRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.google.genomics.v1.GetVariantSetRequest();
  }

  public static com.google.genomics.v1.GetVariantSetRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<GetVariantSetRequest>
      PARSER = new com.google.protobuf.AbstractParser<GetVariantSetRequest>() {
    public GetVariantSetRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      try {
        return new GetVariantSetRequest(input, extensionRegistry);
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

  public static com.google.protobuf.Parser<GetVariantSetRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<GetVariantSetRequest> getParserForType() {
    return PARSER;
  }

  public com.google.genomics.v1.GetVariantSetRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

