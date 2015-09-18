// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/genomics/v1/variants.proto

package com.google.genomics.v1;

/**
 * Protobuf type {@code google.genomics.v1.StreamVariantsResponse}
 */
public  final class StreamVariantsResponse extends
    com.google.protobuf.GeneratedMessage implements
    // @@protoc_insertion_point(message_implements:google.genomics.v1.StreamVariantsResponse)
    StreamVariantsResponseOrBuilder {
  // Use StreamVariantsResponse.newBuilder() to construct.
  private StreamVariantsResponse(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
    super(builder);
  }
  private StreamVariantsResponse() {
    variants_ = java.util.Collections.emptyList();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private StreamVariantsResponse(
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
            if (!((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
              variants_ = new java.util.ArrayList<com.google.genomics.v1.Variant>();
              mutable_bitField0_ |= 0x00000001;
            }
            variants_.add(input.readMessage(com.google.genomics.v1.Variant.parser(), extensionRegistry));
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
      if (((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
        variants_ = java.util.Collections.unmodifiableList(variants_);
      }
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.google.genomics.v1.VariantsProto.internal_static_google_genomics_v1_StreamVariantsResponse_descriptor;
  }

  protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.genomics.v1.VariantsProto.internal_static_google_genomics_v1_StreamVariantsResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.genomics.v1.StreamVariantsResponse.class, com.google.genomics.v1.StreamVariantsResponse.Builder.class);
  }

  public static final int VARIANTS_FIELD_NUMBER = 1;
  private java.util.List<com.google.genomics.v1.Variant> variants_;
  /**
   * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
   */
  public java.util.List<com.google.genomics.v1.Variant> getVariantsList() {
    return variants_;
  }
  /**
   * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
   */
  public java.util.List<? extends com.google.genomics.v1.VariantOrBuilder> 
      getVariantsOrBuilderList() {
    return variants_;
  }
  /**
   * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
   */
  public int getVariantsCount() {
    return variants_.size();
  }
  /**
   * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
   */
  public com.google.genomics.v1.Variant getVariants(int index) {
    return variants_.get(index);
  }
  /**
   * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
   */
  public com.google.genomics.v1.VariantOrBuilder getVariantsOrBuilder(
      int index) {
    return variants_.get(index);
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
    for (int i = 0; i < variants_.size(); i++) {
      output.writeMessage(1, variants_.get(i));
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    for (int i = 0; i < variants_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, variants_.get(i));
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  public static com.google.genomics.v1.StreamVariantsResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.genomics.v1.StreamVariantsResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.genomics.v1.StreamVariantsResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.genomics.v1.StreamVariantsResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.genomics.v1.StreamVariantsResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.google.genomics.v1.StreamVariantsResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }
  public static com.google.genomics.v1.StreamVariantsResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input);
  }
  public static com.google.genomics.v1.StreamVariantsResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input, extensionRegistry);
  }
  public static com.google.genomics.v1.StreamVariantsResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.google.genomics.v1.StreamVariantsResponse parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.google.genomics.v1.StreamVariantsResponse prototype) {
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
   * Protobuf type {@code google.genomics.v1.StreamVariantsResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessage.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:google.genomics.v1.StreamVariantsResponse)
      com.google.genomics.v1.StreamVariantsResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.google.genomics.v1.VariantsProto.internal_static_google_genomics_v1_StreamVariantsResponse_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.genomics.v1.VariantsProto.internal_static_google_genomics_v1_StreamVariantsResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.genomics.v1.StreamVariantsResponse.class, com.google.genomics.v1.StreamVariantsResponse.Builder.class);
    }

    // Construct using com.google.genomics.v1.StreamVariantsResponse.newBuilder()
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
        getVariantsFieldBuilder();
      }
    }
    public Builder clear() {
      super.clear();
      if (variantsBuilder_ == null) {
        variants_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
      } else {
        variantsBuilder_.clear();
      }
      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.google.genomics.v1.VariantsProto.internal_static_google_genomics_v1_StreamVariantsResponse_descriptor;
    }

    public com.google.genomics.v1.StreamVariantsResponse getDefaultInstanceForType() {
      return com.google.genomics.v1.StreamVariantsResponse.getDefaultInstance();
    }

    public com.google.genomics.v1.StreamVariantsResponse build() {
      com.google.genomics.v1.StreamVariantsResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public com.google.genomics.v1.StreamVariantsResponse buildPartial() {
      com.google.genomics.v1.StreamVariantsResponse result = new com.google.genomics.v1.StreamVariantsResponse(this);
      int from_bitField0_ = bitField0_;
      if (variantsBuilder_ == null) {
        if (((bitField0_ & 0x00000001) == 0x00000001)) {
          variants_ = java.util.Collections.unmodifiableList(variants_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.variants_ = variants_;
      } else {
        result.variants_ = variantsBuilder_.build();
      }
      onBuilt();
      return result;
    }

    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.google.genomics.v1.StreamVariantsResponse) {
        return mergeFrom((com.google.genomics.v1.StreamVariantsResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.genomics.v1.StreamVariantsResponse other) {
      if (other == com.google.genomics.v1.StreamVariantsResponse.getDefaultInstance()) return this;
      if (variantsBuilder_ == null) {
        if (!other.variants_.isEmpty()) {
          if (variants_.isEmpty()) {
            variants_ = other.variants_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureVariantsIsMutable();
            variants_.addAll(other.variants_);
          }
          onChanged();
        }
      } else {
        if (!other.variants_.isEmpty()) {
          if (variantsBuilder_.isEmpty()) {
            variantsBuilder_.dispose();
            variantsBuilder_ = null;
            variants_ = other.variants_;
            bitField0_ = (bitField0_ & ~0x00000001);
            variantsBuilder_ = 
              com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders ?
                 getVariantsFieldBuilder() : null;
          } else {
            variantsBuilder_.addAllMessages(other.variants_);
          }
        }
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
      com.google.genomics.v1.StreamVariantsResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.google.genomics.v1.StreamVariantsResponse) e.getUnfinishedMessage();
        throw e;
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private java.util.List<com.google.genomics.v1.Variant> variants_ =
      java.util.Collections.emptyList();
    private void ensureVariantsIsMutable() {
      if (!((bitField0_ & 0x00000001) == 0x00000001)) {
        variants_ = new java.util.ArrayList<com.google.genomics.v1.Variant>(variants_);
        bitField0_ |= 0x00000001;
       }
    }

    private com.google.protobuf.RepeatedFieldBuilder<
        com.google.genomics.v1.Variant, com.google.genomics.v1.Variant.Builder, com.google.genomics.v1.VariantOrBuilder> variantsBuilder_;

    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public java.util.List<com.google.genomics.v1.Variant> getVariantsList() {
      if (variantsBuilder_ == null) {
        return java.util.Collections.unmodifiableList(variants_);
      } else {
        return variantsBuilder_.getMessageList();
      }
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public int getVariantsCount() {
      if (variantsBuilder_ == null) {
        return variants_.size();
      } else {
        return variantsBuilder_.getCount();
      }
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public com.google.genomics.v1.Variant getVariants(int index) {
      if (variantsBuilder_ == null) {
        return variants_.get(index);
      } else {
        return variantsBuilder_.getMessage(index);
      }
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public Builder setVariants(
        int index, com.google.genomics.v1.Variant value) {
      if (variantsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureVariantsIsMutable();
        variants_.set(index, value);
        onChanged();
      } else {
        variantsBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public Builder setVariants(
        int index, com.google.genomics.v1.Variant.Builder builderForValue) {
      if (variantsBuilder_ == null) {
        ensureVariantsIsMutable();
        variants_.set(index, builderForValue.build());
        onChanged();
      } else {
        variantsBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public Builder addVariants(com.google.genomics.v1.Variant value) {
      if (variantsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureVariantsIsMutable();
        variants_.add(value);
        onChanged();
      } else {
        variantsBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public Builder addVariants(
        int index, com.google.genomics.v1.Variant value) {
      if (variantsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureVariantsIsMutable();
        variants_.add(index, value);
        onChanged();
      } else {
        variantsBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public Builder addVariants(
        com.google.genomics.v1.Variant.Builder builderForValue) {
      if (variantsBuilder_ == null) {
        ensureVariantsIsMutable();
        variants_.add(builderForValue.build());
        onChanged();
      } else {
        variantsBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public Builder addVariants(
        int index, com.google.genomics.v1.Variant.Builder builderForValue) {
      if (variantsBuilder_ == null) {
        ensureVariantsIsMutable();
        variants_.add(index, builderForValue.build());
        onChanged();
      } else {
        variantsBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public Builder addAllVariants(
        java.lang.Iterable<? extends com.google.genomics.v1.Variant> values) {
      if (variantsBuilder_ == null) {
        ensureVariantsIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, variants_);
        onChanged();
      } else {
        variantsBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public Builder clearVariants() {
      if (variantsBuilder_ == null) {
        variants_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
      } else {
        variantsBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public Builder removeVariants(int index) {
      if (variantsBuilder_ == null) {
        ensureVariantsIsMutable();
        variants_.remove(index);
        onChanged();
      } else {
        variantsBuilder_.remove(index);
      }
      return this;
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public com.google.genomics.v1.Variant.Builder getVariantsBuilder(
        int index) {
      return getVariantsFieldBuilder().getBuilder(index);
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public com.google.genomics.v1.VariantOrBuilder getVariantsOrBuilder(
        int index) {
      if (variantsBuilder_ == null) {
        return variants_.get(index);  } else {
        return variantsBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public java.util.List<? extends com.google.genomics.v1.VariantOrBuilder> 
         getVariantsOrBuilderList() {
      if (variantsBuilder_ != null) {
        return variantsBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(variants_);
      }
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public com.google.genomics.v1.Variant.Builder addVariantsBuilder() {
      return getVariantsFieldBuilder().addBuilder(
          com.google.genomics.v1.Variant.getDefaultInstance());
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public com.google.genomics.v1.Variant.Builder addVariantsBuilder(
        int index) {
      return getVariantsFieldBuilder().addBuilder(
          index, com.google.genomics.v1.Variant.getDefaultInstance());
    }
    /**
     * <code>repeated .google.genomics.v1.Variant variants = 1;</code>
     */
    public java.util.List<com.google.genomics.v1.Variant.Builder> 
         getVariantsBuilderList() {
      return getVariantsFieldBuilder().getBuilderList();
    }
    private com.google.protobuf.RepeatedFieldBuilder<
        com.google.genomics.v1.Variant, com.google.genomics.v1.Variant.Builder, com.google.genomics.v1.VariantOrBuilder> 
        getVariantsFieldBuilder() {
      if (variantsBuilder_ == null) {
        variantsBuilder_ = new com.google.protobuf.RepeatedFieldBuilder<
            com.google.genomics.v1.Variant, com.google.genomics.v1.Variant.Builder, com.google.genomics.v1.VariantOrBuilder>(
                variants_,
                ((bitField0_ & 0x00000001) == 0x00000001),
                getParentForChildren(),
                isClean());
        variants_ = null;
      }
      return variantsBuilder_;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:google.genomics.v1.StreamVariantsResponse)
  }

  // @@protoc_insertion_point(class_scope:google.genomics.v1.StreamVariantsResponse)
  private static final com.google.genomics.v1.StreamVariantsResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.google.genomics.v1.StreamVariantsResponse();
  }

  public static com.google.genomics.v1.StreamVariantsResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<StreamVariantsResponse>
      PARSER = new com.google.protobuf.AbstractParser<StreamVariantsResponse>() {
    public StreamVariantsResponse parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      try {
        return new StreamVariantsResponse(input, extensionRegistry);
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

  public static com.google.protobuf.Parser<StreamVariantsResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<StreamVariantsResponse> getParserForType() {
    return PARSER;
  }

  public com.google.genomics.v1.StreamVariantsResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

