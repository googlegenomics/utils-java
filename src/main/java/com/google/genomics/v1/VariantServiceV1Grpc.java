package com.google.genomics.v1;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;

@javax.annotation.Generated("by gRPC proto compiler")
public class VariantServiceV1Grpc {

  private VariantServiceV1Grpc() {}

  public static final String SERVICE_NAME = "google.genomics.v1.VariantServiceV1";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.ImportVariantsRequest,
      com.google.longrunning.Operation> METHOD_IMPORT_VARIANTS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "ImportVariants"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.ImportVariantsRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.longrunning.Operation.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.ExportVariantSetRequest,
      com.google.longrunning.Operation> METHOD_EXPORT_VARIANT_SET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "ExportVariantSet"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.ExportVariantSetRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.longrunning.Operation.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.GetVariantSetRequest,
      com.google.genomics.v1.VariantSet> METHOD_GET_VARIANT_SET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "GetVariantSet"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.GetVariantSetRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.VariantSet.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.SearchVariantSetsRequest,
      com.google.genomics.v1.SearchVariantSetsResponse> METHOD_SEARCH_VARIANT_SETS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "SearchVariantSets"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.SearchVariantSetsRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.SearchVariantSetsResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.DeleteVariantSetRequest,
      com.google.protobuf.Empty> METHOD_DELETE_VARIANT_SET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "DeleteVariantSet"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.DeleteVariantSetRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.protobuf.Empty.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.UpdateVariantSetRequest,
      com.google.genomics.v1.VariantSet> METHOD_UPDATE_VARIANT_SET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "UpdateVariantSet"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.UpdateVariantSetRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.VariantSet.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.SearchVariantsRequest,
      com.google.genomics.v1.SearchVariantsResponse> METHOD_SEARCH_VARIANTS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "SearchVariants"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.SearchVariantsRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.SearchVariantsResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.CreateVariantRequest,
      com.google.genomics.v1.Variant> METHOD_CREATE_VARIANT =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "CreateVariant"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.CreateVariantRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.Variant.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.UpdateVariantRequest,
      com.google.genomics.v1.Variant> METHOD_UPDATE_VARIANT =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "UpdateVariant"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.UpdateVariantRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.Variant.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.DeleteVariantRequest,
      com.google.protobuf.Empty> METHOD_DELETE_VARIANT =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "DeleteVariant"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.DeleteVariantRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.protobuf.Empty.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.GetVariantRequest,
      com.google.genomics.v1.Variant> METHOD_GET_VARIANT =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "GetVariant"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.GetVariantRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.Variant.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.MergeVariantsRequest,
      com.google.protobuf.Empty> METHOD_MERGE_VARIANTS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "MergeVariants"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.MergeVariantsRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.protobuf.Empty.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.SearchCallSetsRequest,
      com.google.genomics.v1.SearchCallSetsResponse> METHOD_SEARCH_CALL_SETS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "SearchCallSets"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.SearchCallSetsRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.SearchCallSetsResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.CreateCallSetRequest,
      com.google.genomics.v1.CallSet> METHOD_CREATE_CALL_SET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "CreateCallSet"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.CreateCallSetRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.CallSet.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.UpdateCallSetRequest,
      com.google.genomics.v1.CallSet> METHOD_UPDATE_CALL_SET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "UpdateCallSet"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.UpdateCallSetRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.CallSet.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.DeleteCallSetRequest,
      com.google.protobuf.Empty> METHOD_DELETE_CALL_SET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "DeleteCallSet"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.DeleteCallSetRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.protobuf.Empty.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.GetCallSetRequest,
      com.google.genomics.v1.CallSet> METHOD_GET_CALL_SET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.VariantServiceV1", "GetCallSet"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.GetCallSetRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.CallSet.getDefaultInstance()));

  public static VariantServiceV1Stub newStub(io.grpc.Channel channel) {
    return new VariantServiceV1Stub(channel);
  }

  public static VariantServiceV1BlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new VariantServiceV1BlockingStub(channel);
  }

  public static VariantServiceV1FutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new VariantServiceV1FutureStub(channel);
  }

  public static interface VariantServiceV1 {

    public void importVariants(com.google.genomics.v1.ImportVariantsRequest request,
        io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver);

    public void exportVariantSet(com.google.genomics.v1.ExportVariantSetRequest request,
        io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver);

    public void getVariantSet(com.google.genomics.v1.GetVariantSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.VariantSet> responseObserver);

    public void searchVariantSets(com.google.genomics.v1.SearchVariantSetsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchVariantSetsResponse> responseObserver);

    public void deleteVariantSet(com.google.genomics.v1.DeleteVariantSetRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver);

    public void updateVariantSet(com.google.genomics.v1.UpdateVariantSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.VariantSet> responseObserver);

    public void searchVariants(com.google.genomics.v1.SearchVariantsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchVariantsResponse> responseObserver);

    public void createVariant(com.google.genomics.v1.CreateVariantRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Variant> responseObserver);

    public void updateVariant(com.google.genomics.v1.UpdateVariantRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Variant> responseObserver);

    public void deleteVariant(com.google.genomics.v1.DeleteVariantRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver);

    public void getVariant(com.google.genomics.v1.GetVariantRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Variant> responseObserver);

    public void mergeVariants(com.google.genomics.v1.MergeVariantsRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver);

    public void searchCallSets(com.google.genomics.v1.SearchCallSetsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchCallSetsResponse> responseObserver);

    public void createCallSet(com.google.genomics.v1.CreateCallSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.CallSet> responseObserver);

    public void updateCallSet(com.google.genomics.v1.UpdateCallSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.CallSet> responseObserver);

    public void deleteCallSet(com.google.genomics.v1.DeleteCallSetRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver);

    public void getCallSet(com.google.genomics.v1.GetCallSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.CallSet> responseObserver);
  }

  public static interface VariantServiceV1BlockingClient {

    public com.google.longrunning.Operation importVariants(com.google.genomics.v1.ImportVariantsRequest request);

    public com.google.longrunning.Operation exportVariantSet(com.google.genomics.v1.ExportVariantSetRequest request);

    public com.google.genomics.v1.VariantSet getVariantSet(com.google.genomics.v1.GetVariantSetRequest request);

    public com.google.genomics.v1.SearchVariantSetsResponse searchVariantSets(com.google.genomics.v1.SearchVariantSetsRequest request);

    public com.google.protobuf.Empty deleteVariantSet(com.google.genomics.v1.DeleteVariantSetRequest request);

    public com.google.genomics.v1.VariantSet updateVariantSet(com.google.genomics.v1.UpdateVariantSetRequest request);

    public com.google.genomics.v1.SearchVariantsResponse searchVariants(com.google.genomics.v1.SearchVariantsRequest request);

    public com.google.genomics.v1.Variant createVariant(com.google.genomics.v1.CreateVariantRequest request);

    public com.google.genomics.v1.Variant updateVariant(com.google.genomics.v1.UpdateVariantRequest request);

    public com.google.protobuf.Empty deleteVariant(com.google.genomics.v1.DeleteVariantRequest request);

    public com.google.genomics.v1.Variant getVariant(com.google.genomics.v1.GetVariantRequest request);

    public com.google.protobuf.Empty mergeVariants(com.google.genomics.v1.MergeVariantsRequest request);

    public com.google.genomics.v1.SearchCallSetsResponse searchCallSets(com.google.genomics.v1.SearchCallSetsRequest request);

    public com.google.genomics.v1.CallSet createCallSet(com.google.genomics.v1.CreateCallSetRequest request);

    public com.google.genomics.v1.CallSet updateCallSet(com.google.genomics.v1.UpdateCallSetRequest request);

    public com.google.protobuf.Empty deleteCallSet(com.google.genomics.v1.DeleteCallSetRequest request);

    public com.google.genomics.v1.CallSet getCallSet(com.google.genomics.v1.GetCallSetRequest request);
  }

  public static interface VariantServiceV1FutureClient {

    public com.google.common.util.concurrent.ListenableFuture<com.google.longrunning.Operation> importVariants(
        com.google.genomics.v1.ImportVariantsRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.longrunning.Operation> exportVariantSet(
        com.google.genomics.v1.ExportVariantSetRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.VariantSet> getVariantSet(
        com.google.genomics.v1.GetVariantSetRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchVariantSetsResponse> searchVariantSets(
        com.google.genomics.v1.SearchVariantSetsRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteVariantSet(
        com.google.genomics.v1.DeleteVariantSetRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.VariantSet> updateVariantSet(
        com.google.genomics.v1.UpdateVariantSetRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchVariantsResponse> searchVariants(
        com.google.genomics.v1.SearchVariantsRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Variant> createVariant(
        com.google.genomics.v1.CreateVariantRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Variant> updateVariant(
        com.google.genomics.v1.UpdateVariantRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteVariant(
        com.google.genomics.v1.DeleteVariantRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Variant> getVariant(
        com.google.genomics.v1.GetVariantRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> mergeVariants(
        com.google.genomics.v1.MergeVariantsRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchCallSetsResponse> searchCallSets(
        com.google.genomics.v1.SearchCallSetsRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.CallSet> createCallSet(
        com.google.genomics.v1.CreateCallSetRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.CallSet> updateCallSet(
        com.google.genomics.v1.UpdateCallSetRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteCallSet(
        com.google.genomics.v1.DeleteCallSetRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.CallSet> getCallSet(
        com.google.genomics.v1.GetCallSetRequest request);
  }

  public static class VariantServiceV1Stub extends io.grpc.stub.AbstractStub<VariantServiceV1Stub>
      implements VariantServiceV1 {
    private VariantServiceV1Stub(io.grpc.Channel channel) {
      super(channel);
    }

    private VariantServiceV1Stub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VariantServiceV1Stub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new VariantServiceV1Stub(channel, callOptions);
    }

    @java.lang.Override
    public void importVariants(com.google.genomics.v1.ImportVariantsRequest request,
        io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_IMPORT_VARIANTS, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void exportVariantSet(com.google.genomics.v1.ExportVariantSetRequest request,
        io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_EXPORT_VARIANT_SET, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void getVariantSet(com.google.genomics.v1.GetVariantSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.VariantSet> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_VARIANT_SET, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void searchVariantSets(com.google.genomics.v1.SearchVariantSetsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchVariantSetsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SEARCH_VARIANT_SETS, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void deleteVariantSet(com.google.genomics.v1.DeleteVariantSetRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_DELETE_VARIANT_SET, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void updateVariantSet(com.google.genomics.v1.UpdateVariantSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.VariantSet> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_UPDATE_VARIANT_SET, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void searchVariants(com.google.genomics.v1.SearchVariantsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchVariantsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SEARCH_VARIANTS, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void createVariant(com.google.genomics.v1.CreateVariantRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Variant> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CREATE_VARIANT, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void updateVariant(com.google.genomics.v1.UpdateVariantRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Variant> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_UPDATE_VARIANT, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void deleteVariant(com.google.genomics.v1.DeleteVariantRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_DELETE_VARIANT, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void getVariant(com.google.genomics.v1.GetVariantRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Variant> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_VARIANT, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void mergeVariants(com.google.genomics.v1.MergeVariantsRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_MERGE_VARIANTS, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void searchCallSets(com.google.genomics.v1.SearchCallSetsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchCallSetsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SEARCH_CALL_SETS, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void createCallSet(com.google.genomics.v1.CreateCallSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.CallSet> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CREATE_CALL_SET, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void updateCallSet(com.google.genomics.v1.UpdateCallSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.CallSet> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_UPDATE_CALL_SET, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void deleteCallSet(com.google.genomics.v1.DeleteCallSetRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_DELETE_CALL_SET, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void getCallSet(com.google.genomics.v1.GetCallSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.CallSet> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_CALL_SET, getCallOptions()), request, responseObserver);
    }
  }

  public static class VariantServiceV1BlockingStub extends io.grpc.stub.AbstractStub<VariantServiceV1BlockingStub>
      implements VariantServiceV1BlockingClient {
    private VariantServiceV1BlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private VariantServiceV1BlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VariantServiceV1BlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new VariantServiceV1BlockingStub(channel, callOptions);
    }

    @java.lang.Override
    public com.google.longrunning.Operation importVariants(com.google.genomics.v1.ImportVariantsRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_IMPORT_VARIANTS, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.longrunning.Operation exportVariantSet(com.google.genomics.v1.ExportVariantSetRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_EXPORT_VARIANT_SET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.VariantSet getVariantSet(com.google.genomics.v1.GetVariantSetRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_GET_VARIANT_SET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.SearchVariantSetsResponse searchVariantSets(com.google.genomics.v1.SearchVariantSetsRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_SEARCH_VARIANT_SETS, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.protobuf.Empty deleteVariantSet(com.google.genomics.v1.DeleteVariantSetRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_DELETE_VARIANT_SET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.VariantSet updateVariantSet(com.google.genomics.v1.UpdateVariantSetRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_UPDATE_VARIANT_SET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.SearchVariantsResponse searchVariants(com.google.genomics.v1.SearchVariantsRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_SEARCH_VARIANTS, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.Variant createVariant(com.google.genomics.v1.CreateVariantRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_CREATE_VARIANT, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.Variant updateVariant(com.google.genomics.v1.UpdateVariantRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_UPDATE_VARIANT, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.protobuf.Empty deleteVariant(com.google.genomics.v1.DeleteVariantRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_DELETE_VARIANT, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.Variant getVariant(com.google.genomics.v1.GetVariantRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_GET_VARIANT, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.protobuf.Empty mergeVariants(com.google.genomics.v1.MergeVariantsRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_MERGE_VARIANTS, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.SearchCallSetsResponse searchCallSets(com.google.genomics.v1.SearchCallSetsRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_SEARCH_CALL_SETS, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.CallSet createCallSet(com.google.genomics.v1.CreateCallSetRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_CREATE_CALL_SET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.CallSet updateCallSet(com.google.genomics.v1.UpdateCallSetRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_UPDATE_CALL_SET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.protobuf.Empty deleteCallSet(com.google.genomics.v1.DeleteCallSetRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_DELETE_CALL_SET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.CallSet getCallSet(com.google.genomics.v1.GetCallSetRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_GET_CALL_SET, getCallOptions()), request);
    }
  }

  public static class VariantServiceV1FutureStub extends io.grpc.stub.AbstractStub<VariantServiceV1FutureStub>
      implements VariantServiceV1FutureClient {
    private VariantServiceV1FutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private VariantServiceV1FutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VariantServiceV1FutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new VariantServiceV1FutureStub(channel, callOptions);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.longrunning.Operation> importVariants(
        com.google.genomics.v1.ImportVariantsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_IMPORT_VARIANTS, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.longrunning.Operation> exportVariantSet(
        com.google.genomics.v1.ExportVariantSetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_EXPORT_VARIANT_SET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.VariantSet> getVariantSet(
        com.google.genomics.v1.GetVariantSetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_VARIANT_SET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchVariantSetsResponse> searchVariantSets(
        com.google.genomics.v1.SearchVariantSetsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SEARCH_VARIANT_SETS, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteVariantSet(
        com.google.genomics.v1.DeleteVariantSetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_DELETE_VARIANT_SET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.VariantSet> updateVariantSet(
        com.google.genomics.v1.UpdateVariantSetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_UPDATE_VARIANT_SET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchVariantsResponse> searchVariants(
        com.google.genomics.v1.SearchVariantsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SEARCH_VARIANTS, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Variant> createVariant(
        com.google.genomics.v1.CreateVariantRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CREATE_VARIANT, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Variant> updateVariant(
        com.google.genomics.v1.UpdateVariantRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_UPDATE_VARIANT, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteVariant(
        com.google.genomics.v1.DeleteVariantRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_DELETE_VARIANT, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Variant> getVariant(
        com.google.genomics.v1.GetVariantRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_VARIANT, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> mergeVariants(
        com.google.genomics.v1.MergeVariantsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_MERGE_VARIANTS, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchCallSetsResponse> searchCallSets(
        com.google.genomics.v1.SearchCallSetsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SEARCH_CALL_SETS, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.CallSet> createCallSet(
        com.google.genomics.v1.CreateCallSetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CREATE_CALL_SET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.CallSet> updateCallSet(
        com.google.genomics.v1.UpdateCallSetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_UPDATE_CALL_SET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteCallSet(
        com.google.genomics.v1.DeleteCallSetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_DELETE_CALL_SET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.CallSet> getCallSet(
        com.google.genomics.v1.GetCallSetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_CALL_SET, getCallOptions()), request);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final VariantServiceV1 serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder(SERVICE_NAME)
      .addMethod(
        METHOD_IMPORT_VARIANTS,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.ImportVariantsRequest,
              com.google.longrunning.Operation>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.ImportVariantsRequest request,
                io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
              serviceImpl.importVariants(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_EXPORT_VARIANT_SET,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.ExportVariantSetRequest,
              com.google.longrunning.Operation>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.ExportVariantSetRequest request,
                io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
              serviceImpl.exportVariantSet(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_GET_VARIANT_SET,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.GetVariantSetRequest,
              com.google.genomics.v1.VariantSet>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.GetVariantSetRequest request,
                io.grpc.stub.StreamObserver<com.google.genomics.v1.VariantSet> responseObserver) {
              serviceImpl.getVariantSet(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_SEARCH_VARIANT_SETS,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.SearchVariantSetsRequest,
              com.google.genomics.v1.SearchVariantSetsResponse>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.SearchVariantSetsRequest request,
                io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchVariantSetsResponse> responseObserver) {
              serviceImpl.searchVariantSets(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_DELETE_VARIANT_SET,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.DeleteVariantSetRequest,
              com.google.protobuf.Empty>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.DeleteVariantSetRequest request,
                io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
              serviceImpl.deleteVariantSet(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_UPDATE_VARIANT_SET,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.UpdateVariantSetRequest,
              com.google.genomics.v1.VariantSet>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.UpdateVariantSetRequest request,
                io.grpc.stub.StreamObserver<com.google.genomics.v1.VariantSet> responseObserver) {
              serviceImpl.updateVariantSet(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_SEARCH_VARIANTS,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.SearchVariantsRequest,
              com.google.genomics.v1.SearchVariantsResponse>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.SearchVariantsRequest request,
                io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchVariantsResponse> responseObserver) {
              serviceImpl.searchVariants(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_CREATE_VARIANT,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.CreateVariantRequest,
              com.google.genomics.v1.Variant>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.CreateVariantRequest request,
                io.grpc.stub.StreamObserver<com.google.genomics.v1.Variant> responseObserver) {
              serviceImpl.createVariant(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_UPDATE_VARIANT,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.UpdateVariantRequest,
              com.google.genomics.v1.Variant>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.UpdateVariantRequest request,
                io.grpc.stub.StreamObserver<com.google.genomics.v1.Variant> responseObserver) {
              serviceImpl.updateVariant(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_DELETE_VARIANT,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.DeleteVariantRequest,
              com.google.protobuf.Empty>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.DeleteVariantRequest request,
                io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
              serviceImpl.deleteVariant(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_GET_VARIANT,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.GetVariantRequest,
              com.google.genomics.v1.Variant>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.GetVariantRequest request,
                io.grpc.stub.StreamObserver<com.google.genomics.v1.Variant> responseObserver) {
              serviceImpl.getVariant(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_MERGE_VARIANTS,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.MergeVariantsRequest,
              com.google.protobuf.Empty>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.MergeVariantsRequest request,
                io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
              serviceImpl.mergeVariants(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_SEARCH_CALL_SETS,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.SearchCallSetsRequest,
              com.google.genomics.v1.SearchCallSetsResponse>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.SearchCallSetsRequest request,
                io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchCallSetsResponse> responseObserver) {
              serviceImpl.searchCallSets(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_CREATE_CALL_SET,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.CreateCallSetRequest,
              com.google.genomics.v1.CallSet>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.CreateCallSetRequest request,
                io.grpc.stub.StreamObserver<com.google.genomics.v1.CallSet> responseObserver) {
              serviceImpl.createCallSet(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_UPDATE_CALL_SET,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.UpdateCallSetRequest,
              com.google.genomics.v1.CallSet>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.UpdateCallSetRequest request,
                io.grpc.stub.StreamObserver<com.google.genomics.v1.CallSet> responseObserver) {
              serviceImpl.updateCallSet(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_DELETE_CALL_SET,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.DeleteCallSetRequest,
              com.google.protobuf.Empty>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.DeleteCallSetRequest request,
                io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
              serviceImpl.deleteCallSet(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_GET_CALL_SET,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.GetCallSetRequest,
              com.google.genomics.v1.CallSet>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.GetCallSetRequest request,
                io.grpc.stub.StreamObserver<com.google.genomics.v1.CallSet> responseObserver) {
              serviceImpl.getCallSet(request, responseObserver);
            }
          })).build();
  }
}
