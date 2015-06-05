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
public class ReadServiceV1Grpc {

  private static final io.grpc.stub.Method<com.google.genomics.v1.ImportReadGroupSetsRequest,
      com.google.longrunning.Operation> METHOD_IMPORT_READ_GROUP_SETS =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "ImportReadGroupSets",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.ImportReadGroupSetsRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.longrunning.Operation.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.ExportReadGroupSetRequest,
      com.google.longrunning.Operation> METHOD_EXPORT_READ_GROUP_SET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "ExportReadGroupSet",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.ExportReadGroupSetRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.longrunning.Operation.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.SearchReadGroupSetsRequest,
      com.google.genomics.v1.SearchReadGroupSetsResponse> METHOD_SEARCH_READ_GROUP_SETS =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "SearchReadGroupSets",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.SearchReadGroupSetsRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.SearchReadGroupSetsResponse.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.UpdateReadGroupSetRequest,
      com.google.genomics.v1.ReadGroupSet> METHOD_UPDATE_READ_GROUP_SET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "UpdateReadGroupSet",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.UpdateReadGroupSetRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.ReadGroupSet.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.DeleteReadGroupSetRequest,
      com.google.protobuf.Empty> METHOD_DELETE_READ_GROUP_SET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "DeleteReadGroupSet",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.DeleteReadGroupSetRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.protobuf.Empty.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.GetReadGroupSetRequest,
      com.google.genomics.v1.ReadGroupSet> METHOD_GET_READ_GROUP_SET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "GetReadGroupSet",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.GetReadGroupSetRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.ReadGroupSet.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.ListCoverageBucketsRequest,
      com.google.genomics.v1.ListCoverageBucketsResponse> METHOD_LIST_COVERAGE_BUCKETS =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "ListCoverageBuckets",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.ListCoverageBucketsRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.ListCoverageBucketsResponse.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.SearchReadsRequest,
      com.google.genomics.v1.SearchReadsResponse> METHOD_SEARCH_READS =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "SearchReads",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.SearchReadsRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.SearchReadsResponse.PARSER));

  public static ReadServiceV1Stub newStub(io.grpc.Channel channel) {
    return new ReadServiceV1Stub(channel, CONFIG);
  }

  public static ReadServiceV1BlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ReadServiceV1BlockingStub(channel, CONFIG);
  }

  public static ReadServiceV1FutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ReadServiceV1FutureStub(channel, CONFIG);
  }

  public static final ReadServiceV1ServiceDescriptor CONFIG =
      new ReadServiceV1ServiceDescriptor();

  @javax.annotation.concurrent.Immutable
  public static class ReadServiceV1ServiceDescriptor extends
      io.grpc.stub.AbstractServiceDescriptor<ReadServiceV1ServiceDescriptor> {
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.ImportReadGroupSetsRequest,
        com.google.longrunning.Operation> importReadGroupSets;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.ExportReadGroupSetRequest,
        com.google.longrunning.Operation> exportReadGroupSet;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.SearchReadGroupSetsRequest,
        com.google.genomics.v1.SearchReadGroupSetsResponse> searchReadGroupSets;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.UpdateReadGroupSetRequest,
        com.google.genomics.v1.ReadGroupSet> updateReadGroupSet;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.DeleteReadGroupSetRequest,
        com.google.protobuf.Empty> deleteReadGroupSet;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.GetReadGroupSetRequest,
        com.google.genomics.v1.ReadGroupSet> getReadGroupSet;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.ListCoverageBucketsRequest,
        com.google.genomics.v1.ListCoverageBucketsResponse> listCoverageBuckets;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.SearchReadsRequest,
        com.google.genomics.v1.SearchReadsResponse> searchReads;

    private ReadServiceV1ServiceDescriptor() {
      importReadGroupSets = createMethodDescriptor(
          "google.genomics.v1.ReadServiceV1", METHOD_IMPORT_READ_GROUP_SETS);
      exportReadGroupSet = createMethodDescriptor(
          "google.genomics.v1.ReadServiceV1", METHOD_EXPORT_READ_GROUP_SET);
      searchReadGroupSets = createMethodDescriptor(
          "google.genomics.v1.ReadServiceV1", METHOD_SEARCH_READ_GROUP_SETS);
      updateReadGroupSet = createMethodDescriptor(
          "google.genomics.v1.ReadServiceV1", METHOD_UPDATE_READ_GROUP_SET);
      deleteReadGroupSet = createMethodDescriptor(
          "google.genomics.v1.ReadServiceV1", METHOD_DELETE_READ_GROUP_SET);
      getReadGroupSet = createMethodDescriptor(
          "google.genomics.v1.ReadServiceV1", METHOD_GET_READ_GROUP_SET);
      listCoverageBuckets = createMethodDescriptor(
          "google.genomics.v1.ReadServiceV1", METHOD_LIST_COVERAGE_BUCKETS);
      searchReads = createMethodDescriptor(
          "google.genomics.v1.ReadServiceV1", METHOD_SEARCH_READS);
    }

    @SuppressWarnings("unchecked")
    private ReadServiceV1ServiceDescriptor(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      importReadGroupSets = (io.grpc.MethodDescriptor<com.google.genomics.v1.ImportReadGroupSetsRequest,
          com.google.longrunning.Operation>) methodMap.get(
          CONFIG.importReadGroupSets.getName());
      exportReadGroupSet = (io.grpc.MethodDescriptor<com.google.genomics.v1.ExportReadGroupSetRequest,
          com.google.longrunning.Operation>) methodMap.get(
          CONFIG.exportReadGroupSet.getName());
      searchReadGroupSets = (io.grpc.MethodDescriptor<com.google.genomics.v1.SearchReadGroupSetsRequest,
          com.google.genomics.v1.SearchReadGroupSetsResponse>) methodMap.get(
          CONFIG.searchReadGroupSets.getName());
      updateReadGroupSet = (io.grpc.MethodDescriptor<com.google.genomics.v1.UpdateReadGroupSetRequest,
          com.google.genomics.v1.ReadGroupSet>) methodMap.get(
          CONFIG.updateReadGroupSet.getName());
      deleteReadGroupSet = (io.grpc.MethodDescriptor<com.google.genomics.v1.DeleteReadGroupSetRequest,
          com.google.protobuf.Empty>) methodMap.get(
          CONFIG.deleteReadGroupSet.getName());
      getReadGroupSet = (io.grpc.MethodDescriptor<com.google.genomics.v1.GetReadGroupSetRequest,
          com.google.genomics.v1.ReadGroupSet>) methodMap.get(
          CONFIG.getReadGroupSet.getName());
      listCoverageBuckets = (io.grpc.MethodDescriptor<com.google.genomics.v1.ListCoverageBucketsRequest,
          com.google.genomics.v1.ListCoverageBucketsResponse>) methodMap.get(
          CONFIG.listCoverageBuckets.getName());
      searchReads = (io.grpc.MethodDescriptor<com.google.genomics.v1.SearchReadsRequest,
          com.google.genomics.v1.SearchReadsResponse>) methodMap.get(
          CONFIG.searchReads.getName());
    }

    @java.lang.Override
    protected ReadServiceV1ServiceDescriptor build(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      return new ReadServiceV1ServiceDescriptor(methodMap);
    }

    @java.lang.Override
    public com.google.common.collect.ImmutableList<io.grpc.MethodDescriptor<?, ?>> methods() {
      return com.google.common.collect.ImmutableList.<io.grpc.MethodDescriptor<?, ?>>of(
          importReadGroupSets,
          exportReadGroupSet,
          searchReadGroupSets,
          updateReadGroupSet,
          deleteReadGroupSet,
          getReadGroupSet,
          listCoverageBuckets,
          searchReads);
    }
  }

  public static interface ReadServiceV1 {

    public void importReadGroupSets(com.google.genomics.v1.ImportReadGroupSetsRequest request,
        io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver);

    public void exportReadGroupSet(com.google.genomics.v1.ExportReadGroupSetRequest request,
        io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver);

    public void searchReadGroupSets(com.google.genomics.v1.SearchReadGroupSetsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReadGroupSetsResponse> responseObserver);

    public void updateReadGroupSet(com.google.genomics.v1.UpdateReadGroupSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ReadGroupSet> responseObserver);

    public void deleteReadGroupSet(com.google.genomics.v1.DeleteReadGroupSetRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver);

    public void getReadGroupSet(com.google.genomics.v1.GetReadGroupSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ReadGroupSet> responseObserver);

    public void listCoverageBuckets(com.google.genomics.v1.ListCoverageBucketsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ListCoverageBucketsResponse> responseObserver);

    public void searchReads(com.google.genomics.v1.SearchReadsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReadsResponse> responseObserver);
  }

  public static interface ReadServiceV1BlockingClient {

    public com.google.longrunning.Operation importReadGroupSets(com.google.genomics.v1.ImportReadGroupSetsRequest request);

    public com.google.longrunning.Operation exportReadGroupSet(com.google.genomics.v1.ExportReadGroupSetRequest request);

    public com.google.genomics.v1.SearchReadGroupSetsResponse searchReadGroupSets(com.google.genomics.v1.SearchReadGroupSetsRequest request);

    public com.google.genomics.v1.ReadGroupSet updateReadGroupSet(com.google.genomics.v1.UpdateReadGroupSetRequest request);

    public com.google.protobuf.Empty deleteReadGroupSet(com.google.genomics.v1.DeleteReadGroupSetRequest request);

    public com.google.genomics.v1.ReadGroupSet getReadGroupSet(com.google.genomics.v1.GetReadGroupSetRequest request);

    public com.google.genomics.v1.ListCoverageBucketsResponse listCoverageBuckets(com.google.genomics.v1.ListCoverageBucketsRequest request);

    public com.google.genomics.v1.SearchReadsResponse searchReads(com.google.genomics.v1.SearchReadsRequest request);
  }

  public static interface ReadServiceV1FutureClient {

    public com.google.common.util.concurrent.ListenableFuture<com.google.longrunning.Operation> importReadGroupSets(
        com.google.genomics.v1.ImportReadGroupSetsRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.longrunning.Operation> exportReadGroupSet(
        com.google.genomics.v1.ExportReadGroupSetRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchReadGroupSetsResponse> searchReadGroupSets(
        com.google.genomics.v1.SearchReadGroupSetsRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ReadGroupSet> updateReadGroupSet(
        com.google.genomics.v1.UpdateReadGroupSetRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteReadGroupSet(
        com.google.genomics.v1.DeleteReadGroupSetRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ReadGroupSet> getReadGroupSet(
        com.google.genomics.v1.GetReadGroupSetRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ListCoverageBucketsResponse> listCoverageBuckets(
        com.google.genomics.v1.ListCoverageBucketsRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchReadsResponse> searchReads(
        com.google.genomics.v1.SearchReadsRequest request);
  }

  public static class ReadServiceV1Stub extends
      io.grpc.stub.AbstractStub<ReadServiceV1Stub, ReadServiceV1ServiceDescriptor>
      implements ReadServiceV1 {
    private ReadServiceV1Stub(io.grpc.Channel channel,
        ReadServiceV1ServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected ReadServiceV1Stub build(io.grpc.Channel channel,
        ReadServiceV1ServiceDescriptor config) {
      return new ReadServiceV1Stub(channel, config);
    }

    @java.lang.Override
    public void importReadGroupSets(com.google.genomics.v1.ImportReadGroupSetsRequest request,
        io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.importReadGroupSets), request, responseObserver);
    }

    @java.lang.Override
    public void exportReadGroupSet(com.google.genomics.v1.ExportReadGroupSetRequest request,
        io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.exportReadGroupSet), request, responseObserver);
    }

    @java.lang.Override
    public void searchReadGroupSets(com.google.genomics.v1.SearchReadGroupSetsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReadGroupSetsResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.searchReadGroupSets), request, responseObserver);
    }

    @java.lang.Override
    public void updateReadGroupSet(com.google.genomics.v1.UpdateReadGroupSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ReadGroupSet> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.updateReadGroupSet), request, responseObserver);
    }

    @java.lang.Override
    public void deleteReadGroupSet(com.google.genomics.v1.DeleteReadGroupSetRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.deleteReadGroupSet), request, responseObserver);
    }

    @java.lang.Override
    public void getReadGroupSet(com.google.genomics.v1.GetReadGroupSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ReadGroupSet> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.getReadGroupSet), request, responseObserver);
    }

    @java.lang.Override
    public void listCoverageBuckets(com.google.genomics.v1.ListCoverageBucketsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ListCoverageBucketsResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.listCoverageBuckets), request, responseObserver);
    }

    @java.lang.Override
    public void searchReads(com.google.genomics.v1.SearchReadsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReadsResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.searchReads), request, responseObserver);
    }
  }

  public static class ReadServiceV1BlockingStub extends
      io.grpc.stub.AbstractStub<ReadServiceV1BlockingStub, ReadServiceV1ServiceDescriptor>
      implements ReadServiceV1BlockingClient {
    private ReadServiceV1BlockingStub(io.grpc.Channel channel,
        ReadServiceV1ServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected ReadServiceV1BlockingStub build(io.grpc.Channel channel,
        ReadServiceV1ServiceDescriptor config) {
      return new ReadServiceV1BlockingStub(channel, config);
    }

    @java.lang.Override
    public com.google.longrunning.Operation importReadGroupSets(com.google.genomics.v1.ImportReadGroupSetsRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.importReadGroupSets), request);
    }

    @java.lang.Override
    public com.google.longrunning.Operation exportReadGroupSet(com.google.genomics.v1.ExportReadGroupSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.exportReadGroupSet), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.SearchReadGroupSetsResponse searchReadGroupSets(com.google.genomics.v1.SearchReadGroupSetsRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.searchReadGroupSets), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.ReadGroupSet updateReadGroupSet(com.google.genomics.v1.UpdateReadGroupSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.updateReadGroupSet), request);
    }

    @java.lang.Override
    public com.google.protobuf.Empty deleteReadGroupSet(com.google.genomics.v1.DeleteReadGroupSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.deleteReadGroupSet), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.ReadGroupSet getReadGroupSet(com.google.genomics.v1.GetReadGroupSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.getReadGroupSet), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.ListCoverageBucketsResponse listCoverageBuckets(com.google.genomics.v1.ListCoverageBucketsRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.listCoverageBuckets), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.SearchReadsResponse searchReads(com.google.genomics.v1.SearchReadsRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.searchReads), request);
    }
  }

  public static class ReadServiceV1FutureStub extends
      io.grpc.stub.AbstractStub<ReadServiceV1FutureStub, ReadServiceV1ServiceDescriptor>
      implements ReadServiceV1FutureClient {
    private ReadServiceV1FutureStub(io.grpc.Channel channel,
        ReadServiceV1ServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected ReadServiceV1FutureStub build(io.grpc.Channel channel,
        ReadServiceV1ServiceDescriptor config) {
      return new ReadServiceV1FutureStub(channel, config);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.longrunning.Operation> importReadGroupSets(
        com.google.genomics.v1.ImportReadGroupSetsRequest request) {
      return unaryFutureCall(
          channel.newCall(config.importReadGroupSets), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.longrunning.Operation> exportReadGroupSet(
        com.google.genomics.v1.ExportReadGroupSetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.exportReadGroupSet), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchReadGroupSetsResponse> searchReadGroupSets(
        com.google.genomics.v1.SearchReadGroupSetsRequest request) {
      return unaryFutureCall(
          channel.newCall(config.searchReadGroupSets), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ReadGroupSet> updateReadGroupSet(
        com.google.genomics.v1.UpdateReadGroupSetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.updateReadGroupSet), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteReadGroupSet(
        com.google.genomics.v1.DeleteReadGroupSetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.deleteReadGroupSet), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ReadGroupSet> getReadGroupSet(
        com.google.genomics.v1.GetReadGroupSetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.getReadGroupSet), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ListCoverageBucketsResponse> listCoverageBuckets(
        com.google.genomics.v1.ListCoverageBucketsRequest request) {
      return unaryFutureCall(
          channel.newCall(config.listCoverageBuckets), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchReadsResponse> searchReads(
        com.google.genomics.v1.SearchReadsRequest request) {
      return unaryFutureCall(
          channel.newCall(config.searchReads), request);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final ReadServiceV1 serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder("google.genomics.v1.ReadServiceV1")
      .addMethod(createMethodDefinition(
          METHOD_IMPORT_READ_GROUP_SETS,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.ImportReadGroupSetsRequest,
                com.google.longrunning.Operation>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.ImportReadGroupSetsRequest request,
                  io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
                serviceImpl.importReadGroupSets(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_EXPORT_READ_GROUP_SET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.ExportReadGroupSetRequest,
                com.google.longrunning.Operation>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.ExportReadGroupSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
                serviceImpl.exportReadGroupSet(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_SEARCH_READ_GROUP_SETS,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.SearchReadGroupSetsRequest,
                com.google.genomics.v1.SearchReadGroupSetsResponse>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.SearchReadGroupSetsRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReadGroupSetsResponse> responseObserver) {
                serviceImpl.searchReadGroupSets(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_UPDATE_READ_GROUP_SET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.UpdateReadGroupSetRequest,
                com.google.genomics.v1.ReadGroupSet>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.UpdateReadGroupSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.ReadGroupSet> responseObserver) {
                serviceImpl.updateReadGroupSet(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_DELETE_READ_GROUP_SET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.DeleteReadGroupSetRequest,
                com.google.protobuf.Empty>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.DeleteReadGroupSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
                serviceImpl.deleteReadGroupSet(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_GET_READ_GROUP_SET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.GetReadGroupSetRequest,
                com.google.genomics.v1.ReadGroupSet>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.GetReadGroupSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.ReadGroupSet> responseObserver) {
                serviceImpl.getReadGroupSet(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_LIST_COVERAGE_BUCKETS,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.ListCoverageBucketsRequest,
                com.google.genomics.v1.ListCoverageBucketsResponse>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.ListCoverageBucketsRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.ListCoverageBucketsResponse> responseObserver) {
                serviceImpl.listCoverageBuckets(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_SEARCH_READS,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.SearchReadsRequest,
                com.google.genomics.v1.SearchReadsResponse>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.SearchReadsRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReadsResponse> responseObserver) {
                serviceImpl.searchReads(request, responseObserver);
              }
            }))).build();
  }
}
