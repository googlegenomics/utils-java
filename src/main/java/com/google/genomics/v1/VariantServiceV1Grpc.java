package com.google.genomics.v1;

import static io.grpc.stub.Calls.createMethodDescriptor;
import static io.grpc.stub.Calls.asyncUnaryCall;
import static io.grpc.stub.Calls.asyncServerStreamingCall;
import static io.grpc.stub.Calls.asyncClientStreamingCall;
import static io.grpc.stub.Calls.duplexStreamingCall;
import static io.grpc.stub.Calls.blockingUnaryCall;
import static io.grpc.stub.Calls.blockingServerStreamingCall;
import static io.grpc.stub.Calls.unaryFutureCall;
import static io.grpc.stub.ServerCalls.createMethodDefinition;
import static io.grpc.stub.ServerCalls.asyncUnaryRequestCall;
import static io.grpc.stub.ServerCalls.asyncStreamingRequestCall;

@javax.annotation.Generated("by gRPC proto compiler")
public class VariantServiceV1Grpc {

  private static final io.grpc.stub.Method<com.google.genomics.v1.ImportVariantsRequest,
      com.google.longrunning.Operation> METHOD_IMPORT_VARIANTS =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "ImportVariants",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.ImportVariantsRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.longrunning.Operation.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.ExportVariantSetRequest,
      com.google.longrunning.Operation> METHOD_EXPORT_VARIANT_SET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "ExportVariantSet",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.ExportVariantSetRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.longrunning.Operation.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.GetVariantSetRequest,
      com.google.genomics.v1.VariantSet> METHOD_GET_VARIANT_SET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "GetVariantSet",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.GetVariantSetRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.VariantSet.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.SearchVariantSetsRequest,
      com.google.genomics.v1.SearchVariantSetsResponse> METHOD_SEARCH_VARIANT_SETS =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "SearchVariantSets",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.SearchVariantSetsRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.SearchVariantSetsResponse.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.DeleteVariantSetRequest,
      com.google.protobuf.Empty> METHOD_DELETE_VARIANT_SET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "DeleteVariantSet",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.DeleteVariantSetRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.protobuf.Empty.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.UpdateVariantSetRequest,
      com.google.genomics.v1.VariantSet> METHOD_UPDATE_VARIANT_SET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "UpdateVariantSet",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.UpdateVariantSetRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.VariantSet.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.SearchVariantsRequest,
      com.google.genomics.v1.SearchVariantsResponse> METHOD_SEARCH_VARIANTS =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "SearchVariants",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.SearchVariantsRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.SearchVariantsResponse.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.CreateVariantRequest,
      com.google.genomics.v1.Variant> METHOD_CREATE_VARIANT =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "CreateVariant",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.CreateVariantRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.Variant.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.UpdateVariantRequest,
      com.google.genomics.v1.Variant> METHOD_UPDATE_VARIANT =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "UpdateVariant",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.UpdateVariantRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.Variant.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.DeleteVariantRequest,
      com.google.protobuf.Empty> METHOD_DELETE_VARIANT =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "DeleteVariant",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.DeleteVariantRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.protobuf.Empty.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.GetVariantRequest,
      com.google.genomics.v1.Variant> METHOD_GET_VARIANT =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "GetVariant",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.GetVariantRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.Variant.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.MergeVariantsRequest,
      com.google.protobuf.Empty> METHOD_MERGE_VARIANTS =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "MergeVariants",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.MergeVariantsRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.protobuf.Empty.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.SearchCallSetsRequest,
      com.google.genomics.v1.SearchCallSetsResponse> METHOD_SEARCH_CALL_SETS =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "SearchCallSets",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.SearchCallSetsRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.SearchCallSetsResponse.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.CreateCallSetRequest,
      com.google.genomics.v1.CallSet> METHOD_CREATE_CALL_SET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "CreateCallSet",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.CreateCallSetRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.CallSet.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.UpdateCallSetRequest,
      com.google.genomics.v1.CallSet> METHOD_UPDATE_CALL_SET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "UpdateCallSet",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.UpdateCallSetRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.CallSet.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.DeleteCallSetRequest,
      com.google.protobuf.Empty> METHOD_DELETE_CALL_SET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "DeleteCallSet",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.DeleteCallSetRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.protobuf.Empty.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.GetCallSetRequest,
      com.google.genomics.v1.CallSet> METHOD_GET_CALL_SET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "GetCallSet",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.GetCallSetRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.CallSet.PARSER));

  public static VariantServiceV1Stub newStub(io.grpc.Channel channel) {
    return new VariantServiceV1Stub(channel, CONFIG);
  }

  public static VariantServiceV1BlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new VariantServiceV1BlockingStub(channel, CONFIG);
  }

  public static VariantServiceV1FutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new VariantServiceV1FutureStub(channel, CONFIG);
  }

  public static final VariantServiceV1ServiceDescriptor CONFIG =
      new VariantServiceV1ServiceDescriptor();

  @javax.annotation.concurrent.Immutable
  public static class VariantServiceV1ServiceDescriptor extends
      io.grpc.stub.AbstractServiceDescriptor<VariantServiceV1ServiceDescriptor> {
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.ImportVariantsRequest,
        com.google.longrunning.Operation> importVariants;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.ExportVariantSetRequest,
        com.google.longrunning.Operation> exportVariantSet;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.GetVariantSetRequest,
        com.google.genomics.v1.VariantSet> getVariantSet;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.SearchVariantSetsRequest,
        com.google.genomics.v1.SearchVariantSetsResponse> searchVariantSets;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.DeleteVariantSetRequest,
        com.google.protobuf.Empty> deleteVariantSet;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.UpdateVariantSetRequest,
        com.google.genomics.v1.VariantSet> updateVariantSet;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.SearchVariantsRequest,
        com.google.genomics.v1.SearchVariantsResponse> searchVariants;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.CreateVariantRequest,
        com.google.genomics.v1.Variant> createVariant;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.UpdateVariantRequest,
        com.google.genomics.v1.Variant> updateVariant;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.DeleteVariantRequest,
        com.google.protobuf.Empty> deleteVariant;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.GetVariantRequest,
        com.google.genomics.v1.Variant> getVariant;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.MergeVariantsRequest,
        com.google.protobuf.Empty> mergeVariants;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.SearchCallSetsRequest,
        com.google.genomics.v1.SearchCallSetsResponse> searchCallSets;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.CreateCallSetRequest,
        com.google.genomics.v1.CallSet> createCallSet;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.UpdateCallSetRequest,
        com.google.genomics.v1.CallSet> updateCallSet;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.DeleteCallSetRequest,
        com.google.protobuf.Empty> deleteCallSet;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.GetCallSetRequest,
        com.google.genomics.v1.CallSet> getCallSet;

    private VariantServiceV1ServiceDescriptor() {
      importVariants = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_IMPORT_VARIANTS);
      exportVariantSet = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_EXPORT_VARIANT_SET);
      getVariantSet = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_GET_VARIANT_SET);
      searchVariantSets = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_SEARCH_VARIANT_SETS);
      deleteVariantSet = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_DELETE_VARIANT_SET);
      updateVariantSet = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_UPDATE_VARIANT_SET);
      searchVariants = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_SEARCH_VARIANTS);
      createVariant = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_CREATE_VARIANT);
      updateVariant = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_UPDATE_VARIANT);
      deleteVariant = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_DELETE_VARIANT);
      getVariant = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_GET_VARIANT);
      mergeVariants = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_MERGE_VARIANTS);
      searchCallSets = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_SEARCH_CALL_SETS);
      createCallSet = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_CREATE_CALL_SET);
      updateCallSet = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_UPDATE_CALL_SET);
      deleteCallSet = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_DELETE_CALL_SET);
      getCallSet = createMethodDescriptor(
          "google.genomics.v1.VariantServiceV1", METHOD_GET_CALL_SET);
    }

    @SuppressWarnings("unchecked")
    private VariantServiceV1ServiceDescriptor(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      importVariants = (io.grpc.MethodDescriptor<com.google.genomics.v1.ImportVariantsRequest,
          com.google.longrunning.Operation>) methodMap.get(
          CONFIG.importVariants.getName());
      exportVariantSet = (io.grpc.MethodDescriptor<com.google.genomics.v1.ExportVariantSetRequest,
          com.google.longrunning.Operation>) methodMap.get(
          CONFIG.exportVariantSet.getName());
      getVariantSet = (io.grpc.MethodDescriptor<com.google.genomics.v1.GetVariantSetRequest,
          com.google.genomics.v1.VariantSet>) methodMap.get(
          CONFIG.getVariantSet.getName());
      searchVariantSets = (io.grpc.MethodDescriptor<com.google.genomics.v1.SearchVariantSetsRequest,
          com.google.genomics.v1.SearchVariantSetsResponse>) methodMap.get(
          CONFIG.searchVariantSets.getName());
      deleteVariantSet = (io.grpc.MethodDescriptor<com.google.genomics.v1.DeleteVariantSetRequest,
          com.google.protobuf.Empty>) methodMap.get(
          CONFIG.deleteVariantSet.getName());
      updateVariantSet = (io.grpc.MethodDescriptor<com.google.genomics.v1.UpdateVariantSetRequest,
          com.google.genomics.v1.VariantSet>) methodMap.get(
          CONFIG.updateVariantSet.getName());
      searchVariants = (io.grpc.MethodDescriptor<com.google.genomics.v1.SearchVariantsRequest,
          com.google.genomics.v1.SearchVariantsResponse>) methodMap.get(
          CONFIG.searchVariants.getName());
      createVariant = (io.grpc.MethodDescriptor<com.google.genomics.v1.CreateVariantRequest,
          com.google.genomics.v1.Variant>) methodMap.get(
          CONFIG.createVariant.getName());
      updateVariant = (io.grpc.MethodDescriptor<com.google.genomics.v1.UpdateVariantRequest,
          com.google.genomics.v1.Variant>) methodMap.get(
          CONFIG.updateVariant.getName());
      deleteVariant = (io.grpc.MethodDescriptor<com.google.genomics.v1.DeleteVariantRequest,
          com.google.protobuf.Empty>) methodMap.get(
          CONFIG.deleteVariant.getName());
      getVariant = (io.grpc.MethodDescriptor<com.google.genomics.v1.GetVariantRequest,
          com.google.genomics.v1.Variant>) methodMap.get(
          CONFIG.getVariant.getName());
      mergeVariants = (io.grpc.MethodDescriptor<com.google.genomics.v1.MergeVariantsRequest,
          com.google.protobuf.Empty>) methodMap.get(
          CONFIG.mergeVariants.getName());
      searchCallSets = (io.grpc.MethodDescriptor<com.google.genomics.v1.SearchCallSetsRequest,
          com.google.genomics.v1.SearchCallSetsResponse>) methodMap.get(
          CONFIG.searchCallSets.getName());
      createCallSet = (io.grpc.MethodDescriptor<com.google.genomics.v1.CreateCallSetRequest,
          com.google.genomics.v1.CallSet>) methodMap.get(
          CONFIG.createCallSet.getName());
      updateCallSet = (io.grpc.MethodDescriptor<com.google.genomics.v1.UpdateCallSetRequest,
          com.google.genomics.v1.CallSet>) methodMap.get(
          CONFIG.updateCallSet.getName());
      deleteCallSet = (io.grpc.MethodDescriptor<com.google.genomics.v1.DeleteCallSetRequest,
          com.google.protobuf.Empty>) methodMap.get(
          CONFIG.deleteCallSet.getName());
      getCallSet = (io.grpc.MethodDescriptor<com.google.genomics.v1.GetCallSetRequest,
          com.google.genomics.v1.CallSet>) methodMap.get(
          CONFIG.getCallSet.getName());
    }

    @java.lang.Override
    protected VariantServiceV1ServiceDescriptor build(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      return new VariantServiceV1ServiceDescriptor(methodMap);
    }

    @java.lang.Override
    public com.google.common.collect.ImmutableList<io.grpc.MethodDescriptor<?, ?>> methods() {
      return com.google.common.collect.ImmutableList.<io.grpc.MethodDescriptor<?, ?>>of(
          importVariants,
          exportVariantSet,
          getVariantSet,
          searchVariantSets,
          deleteVariantSet,
          updateVariantSet,
          searchVariants,
          createVariant,
          updateVariant,
          deleteVariant,
          getVariant,
          mergeVariants,
          searchCallSets,
          createCallSet,
          updateCallSet,
          deleteCallSet,
          getCallSet);
    }
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

  public static class VariantServiceV1Stub extends
      io.grpc.stub.AbstractStub<VariantServiceV1Stub, VariantServiceV1ServiceDescriptor>
      implements VariantServiceV1 {
    private VariantServiceV1Stub(io.grpc.Channel channel,
        VariantServiceV1ServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected VariantServiceV1Stub build(io.grpc.Channel channel,
        VariantServiceV1ServiceDescriptor config) {
      return new VariantServiceV1Stub(channel, config);
    }

    @java.lang.Override
    public void importVariants(com.google.genomics.v1.ImportVariantsRequest request,
        io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.importVariants), request, responseObserver);
    }

    @java.lang.Override
    public void exportVariantSet(com.google.genomics.v1.ExportVariantSetRequest request,
        io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.exportVariantSet), request, responseObserver);
    }

    @java.lang.Override
    public void getVariantSet(com.google.genomics.v1.GetVariantSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.VariantSet> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.getVariantSet), request, responseObserver);
    }

    @java.lang.Override
    public void searchVariantSets(com.google.genomics.v1.SearchVariantSetsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchVariantSetsResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.searchVariantSets), request, responseObserver);
    }

    @java.lang.Override
    public void deleteVariantSet(com.google.genomics.v1.DeleteVariantSetRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.deleteVariantSet), request, responseObserver);
    }

    @java.lang.Override
    public void updateVariantSet(com.google.genomics.v1.UpdateVariantSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.VariantSet> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.updateVariantSet), request, responseObserver);
    }

    @java.lang.Override
    public void searchVariants(com.google.genomics.v1.SearchVariantsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchVariantsResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.searchVariants), request, responseObserver);
    }

    @java.lang.Override
    public void createVariant(com.google.genomics.v1.CreateVariantRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Variant> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.createVariant), request, responseObserver);
    }

    @java.lang.Override
    public void updateVariant(com.google.genomics.v1.UpdateVariantRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Variant> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.updateVariant), request, responseObserver);
    }

    @java.lang.Override
    public void deleteVariant(com.google.genomics.v1.DeleteVariantRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.deleteVariant), request, responseObserver);
    }

    @java.lang.Override
    public void getVariant(com.google.genomics.v1.GetVariantRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Variant> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.getVariant), request, responseObserver);
    }

    @java.lang.Override
    public void mergeVariants(com.google.genomics.v1.MergeVariantsRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.mergeVariants), request, responseObserver);
    }

    @java.lang.Override
    public void searchCallSets(com.google.genomics.v1.SearchCallSetsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchCallSetsResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.searchCallSets), request, responseObserver);
    }

    @java.lang.Override
    public void createCallSet(com.google.genomics.v1.CreateCallSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.CallSet> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.createCallSet), request, responseObserver);
    }

    @java.lang.Override
    public void updateCallSet(com.google.genomics.v1.UpdateCallSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.CallSet> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.updateCallSet), request, responseObserver);
    }

    @java.lang.Override
    public void deleteCallSet(com.google.genomics.v1.DeleteCallSetRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.deleteCallSet), request, responseObserver);
    }

    @java.lang.Override
    public void getCallSet(com.google.genomics.v1.GetCallSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.CallSet> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.getCallSet), request, responseObserver);
    }
  }

  public static class VariantServiceV1BlockingStub extends
      io.grpc.stub.AbstractStub<VariantServiceV1BlockingStub, VariantServiceV1ServiceDescriptor>
      implements VariantServiceV1BlockingClient {
    private VariantServiceV1BlockingStub(io.grpc.Channel channel,
        VariantServiceV1ServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected VariantServiceV1BlockingStub build(io.grpc.Channel channel,
        VariantServiceV1ServiceDescriptor config) {
      return new VariantServiceV1BlockingStub(channel, config);
    }

    @java.lang.Override
    public com.google.longrunning.Operation importVariants(com.google.genomics.v1.ImportVariantsRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.importVariants), request);
    }

    @java.lang.Override
    public com.google.longrunning.Operation exportVariantSet(com.google.genomics.v1.ExportVariantSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.exportVariantSet), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.VariantSet getVariantSet(com.google.genomics.v1.GetVariantSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.getVariantSet), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.SearchVariantSetsResponse searchVariantSets(com.google.genomics.v1.SearchVariantSetsRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.searchVariantSets), request);
    }

    @java.lang.Override
    public com.google.protobuf.Empty deleteVariantSet(com.google.genomics.v1.DeleteVariantSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.deleteVariantSet), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.VariantSet updateVariantSet(com.google.genomics.v1.UpdateVariantSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.updateVariantSet), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.SearchVariantsResponse searchVariants(com.google.genomics.v1.SearchVariantsRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.searchVariants), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.Variant createVariant(com.google.genomics.v1.CreateVariantRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.createVariant), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.Variant updateVariant(com.google.genomics.v1.UpdateVariantRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.updateVariant), request);
    }

    @java.lang.Override
    public com.google.protobuf.Empty deleteVariant(com.google.genomics.v1.DeleteVariantRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.deleteVariant), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.Variant getVariant(com.google.genomics.v1.GetVariantRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.getVariant), request);
    }

    @java.lang.Override
    public com.google.protobuf.Empty mergeVariants(com.google.genomics.v1.MergeVariantsRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.mergeVariants), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.SearchCallSetsResponse searchCallSets(com.google.genomics.v1.SearchCallSetsRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.searchCallSets), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.CallSet createCallSet(com.google.genomics.v1.CreateCallSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.createCallSet), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.CallSet updateCallSet(com.google.genomics.v1.UpdateCallSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.updateCallSet), request);
    }

    @java.lang.Override
    public com.google.protobuf.Empty deleteCallSet(com.google.genomics.v1.DeleteCallSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.deleteCallSet), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.CallSet getCallSet(com.google.genomics.v1.GetCallSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.getCallSet), request);
    }
  }

  public static class VariantServiceV1FutureStub extends
      io.grpc.stub.AbstractStub<VariantServiceV1FutureStub, VariantServiceV1ServiceDescriptor>
      implements VariantServiceV1FutureClient {
    private VariantServiceV1FutureStub(io.grpc.Channel channel,
        VariantServiceV1ServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected VariantServiceV1FutureStub build(io.grpc.Channel channel,
        VariantServiceV1ServiceDescriptor config) {
      return new VariantServiceV1FutureStub(channel, config);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.longrunning.Operation> importVariants(
        com.google.genomics.v1.ImportVariantsRequest request) {
      return unaryFutureCall(
          channel.newCall(config.importVariants), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.longrunning.Operation> exportVariantSet(
        com.google.genomics.v1.ExportVariantSetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.exportVariantSet), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.VariantSet> getVariantSet(
        com.google.genomics.v1.GetVariantSetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.getVariantSet), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchVariantSetsResponse> searchVariantSets(
        com.google.genomics.v1.SearchVariantSetsRequest request) {
      return unaryFutureCall(
          channel.newCall(config.searchVariantSets), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteVariantSet(
        com.google.genomics.v1.DeleteVariantSetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.deleteVariantSet), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.VariantSet> updateVariantSet(
        com.google.genomics.v1.UpdateVariantSetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.updateVariantSet), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchVariantsResponse> searchVariants(
        com.google.genomics.v1.SearchVariantsRequest request) {
      return unaryFutureCall(
          channel.newCall(config.searchVariants), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Variant> createVariant(
        com.google.genomics.v1.CreateVariantRequest request) {
      return unaryFutureCall(
          channel.newCall(config.createVariant), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Variant> updateVariant(
        com.google.genomics.v1.UpdateVariantRequest request) {
      return unaryFutureCall(
          channel.newCall(config.updateVariant), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteVariant(
        com.google.genomics.v1.DeleteVariantRequest request) {
      return unaryFutureCall(
          channel.newCall(config.deleteVariant), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Variant> getVariant(
        com.google.genomics.v1.GetVariantRequest request) {
      return unaryFutureCall(
          channel.newCall(config.getVariant), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> mergeVariants(
        com.google.genomics.v1.MergeVariantsRequest request) {
      return unaryFutureCall(
          channel.newCall(config.mergeVariants), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchCallSetsResponse> searchCallSets(
        com.google.genomics.v1.SearchCallSetsRequest request) {
      return unaryFutureCall(
          channel.newCall(config.searchCallSets), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.CallSet> createCallSet(
        com.google.genomics.v1.CreateCallSetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.createCallSet), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.CallSet> updateCallSet(
        com.google.genomics.v1.UpdateCallSetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.updateCallSet), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteCallSet(
        com.google.genomics.v1.DeleteCallSetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.deleteCallSet), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.CallSet> getCallSet(
        com.google.genomics.v1.GetCallSetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.getCallSet), request);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final VariantServiceV1 serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder("google.genomics.v1.VariantServiceV1")
      .addMethod(createMethodDefinition(
          METHOD_IMPORT_VARIANTS,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.ImportVariantsRequest,
                com.google.longrunning.Operation>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.ImportVariantsRequest request,
                  io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
                serviceImpl.importVariants(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_EXPORT_VARIANT_SET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.ExportVariantSetRequest,
                com.google.longrunning.Operation>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.ExportVariantSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
                serviceImpl.exportVariantSet(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_GET_VARIANT_SET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.GetVariantSetRequest,
                com.google.genomics.v1.VariantSet>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.GetVariantSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.VariantSet> responseObserver) {
                serviceImpl.getVariantSet(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_SEARCH_VARIANT_SETS,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.SearchVariantSetsRequest,
                com.google.genomics.v1.SearchVariantSetsResponse>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.SearchVariantSetsRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchVariantSetsResponse> responseObserver) {
                serviceImpl.searchVariantSets(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_DELETE_VARIANT_SET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.DeleteVariantSetRequest,
                com.google.protobuf.Empty>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.DeleteVariantSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
                serviceImpl.deleteVariantSet(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_UPDATE_VARIANT_SET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.UpdateVariantSetRequest,
                com.google.genomics.v1.VariantSet>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.UpdateVariantSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.VariantSet> responseObserver) {
                serviceImpl.updateVariantSet(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_SEARCH_VARIANTS,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.SearchVariantsRequest,
                com.google.genomics.v1.SearchVariantsResponse>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.SearchVariantsRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchVariantsResponse> responseObserver) {
                serviceImpl.searchVariants(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_CREATE_VARIANT,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.CreateVariantRequest,
                com.google.genomics.v1.Variant>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.CreateVariantRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.Variant> responseObserver) {
                serviceImpl.createVariant(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_UPDATE_VARIANT,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.UpdateVariantRequest,
                com.google.genomics.v1.Variant>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.UpdateVariantRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.Variant> responseObserver) {
                serviceImpl.updateVariant(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_DELETE_VARIANT,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.DeleteVariantRequest,
                com.google.protobuf.Empty>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.DeleteVariantRequest request,
                  io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
                serviceImpl.deleteVariant(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_GET_VARIANT,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.GetVariantRequest,
                com.google.genomics.v1.Variant>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.GetVariantRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.Variant> responseObserver) {
                serviceImpl.getVariant(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_MERGE_VARIANTS,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.MergeVariantsRequest,
                com.google.protobuf.Empty>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.MergeVariantsRequest request,
                  io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
                serviceImpl.mergeVariants(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_SEARCH_CALL_SETS,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.SearchCallSetsRequest,
                com.google.genomics.v1.SearchCallSetsResponse>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.SearchCallSetsRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchCallSetsResponse> responseObserver) {
                serviceImpl.searchCallSets(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_CREATE_CALL_SET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.CreateCallSetRequest,
                com.google.genomics.v1.CallSet>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.CreateCallSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.CallSet> responseObserver) {
                serviceImpl.createCallSet(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_UPDATE_CALL_SET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.UpdateCallSetRequest,
                com.google.genomics.v1.CallSet>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.UpdateCallSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.CallSet> responseObserver) {
                serviceImpl.updateCallSet(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_DELETE_CALL_SET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.DeleteCallSetRequest,
                com.google.protobuf.Empty>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.DeleteCallSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
                serviceImpl.deleteCallSet(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_GET_CALL_SET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.GetCallSetRequest,
                com.google.genomics.v1.CallSet>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.GetCallSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.CallSet> responseObserver) {
                serviceImpl.getCallSet(request, responseObserver);
              }
            }))).build();
  }
}
