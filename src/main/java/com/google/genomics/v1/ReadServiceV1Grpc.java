package com.google.genomics.v1;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;

@javax.annotation.Generated("by gRPC proto compiler")
public class ReadServiceV1Grpc {

  // Static method descriptors that strictly reflect the proto.
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.ImportReadGroupSetsRequest,
      com.google.longrunning.Operation> METHOD_IMPORT_READ_GROUP_SETS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          "google.genomics.v1.ReadServiceV1", "ImportReadGroupSets",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.ImportReadGroupSetsRequest.parser()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.longrunning.Operation.parser()));
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.ExportReadGroupSetRequest,
      com.google.longrunning.Operation> METHOD_EXPORT_READ_GROUP_SET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          "google.genomics.v1.ReadServiceV1", "ExportReadGroupSet",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.ExportReadGroupSetRequest.parser()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.longrunning.Operation.parser()));
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.SearchReadGroupSetsRequest,
      com.google.genomics.v1.SearchReadGroupSetsResponse> METHOD_SEARCH_READ_GROUP_SETS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          "google.genomics.v1.ReadServiceV1", "SearchReadGroupSets",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.SearchReadGroupSetsRequest.parser()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.SearchReadGroupSetsResponse.parser()));
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.UpdateReadGroupSetRequest,
      com.google.genomics.v1.ReadGroupSet> METHOD_UPDATE_READ_GROUP_SET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          "google.genomics.v1.ReadServiceV1", "UpdateReadGroupSet",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.UpdateReadGroupSetRequest.parser()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.ReadGroupSet.parser()));
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.DeleteReadGroupSetRequest,
      com.google.protobuf.Empty> METHOD_DELETE_READ_GROUP_SET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          "google.genomics.v1.ReadServiceV1", "DeleteReadGroupSet",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.DeleteReadGroupSetRequest.parser()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.protobuf.Empty.parser()));
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.GetReadGroupSetRequest,
      com.google.genomics.v1.ReadGroupSet> METHOD_GET_READ_GROUP_SET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          "google.genomics.v1.ReadServiceV1", "GetReadGroupSet",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.GetReadGroupSetRequest.parser()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.ReadGroupSet.parser()));
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.ListCoverageBucketsRequest,
      com.google.genomics.v1.ListCoverageBucketsResponse> METHOD_LIST_COVERAGE_BUCKETS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          "google.genomics.v1.ReadServiceV1", "ListCoverageBuckets",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.ListCoverageBucketsRequest.parser()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.ListCoverageBucketsResponse.parser()));
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.SearchReadsRequest,
      com.google.genomics.v1.SearchReadsResponse> METHOD_SEARCH_READS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          "google.genomics.v1.ReadServiceV1", "SearchReads",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.SearchReadsRequest.parser()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.SearchReadsResponse.parser()));

  public static ReadServiceV1Stub newStub(io.grpc.Channel channel) {
    return new ReadServiceV1Stub(channel);
  }

  public static ReadServiceV1BlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ReadServiceV1BlockingStub(channel);
  }

  public static ReadServiceV1FutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ReadServiceV1FutureStub(channel);
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

  public static class ReadServiceV1Stub extends io.grpc.stub.AbstractStub<ReadServiceV1Stub>
      implements ReadServiceV1 {
    private ReadServiceV1Stub(io.grpc.Channel channel) {
      super(channel);
    }

    private ReadServiceV1Stub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ReadServiceV1Stub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ReadServiceV1Stub(channel, callOptions);
    }

    @java.lang.Override
    public void importReadGroupSets(com.google.genomics.v1.ImportReadGroupSetsRequest request,
        io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
      asyncUnaryCall(
          channel.newCall(METHOD_IMPORT_READ_GROUP_SETS, callOptions), request, responseObserver);
    }

    @java.lang.Override
    public void exportReadGroupSet(com.google.genomics.v1.ExportReadGroupSetRequest request,
        io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
      asyncUnaryCall(
          channel.newCall(METHOD_EXPORT_READ_GROUP_SET, callOptions), request, responseObserver);
    }

    @java.lang.Override
    public void searchReadGroupSets(com.google.genomics.v1.SearchReadGroupSetsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReadGroupSetsResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(METHOD_SEARCH_READ_GROUP_SETS, callOptions), request, responseObserver);
    }

    @java.lang.Override
    public void updateReadGroupSet(com.google.genomics.v1.UpdateReadGroupSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ReadGroupSet> responseObserver) {
      asyncUnaryCall(
          channel.newCall(METHOD_UPDATE_READ_GROUP_SET, callOptions), request, responseObserver);
    }

    @java.lang.Override
    public void deleteReadGroupSet(com.google.genomics.v1.DeleteReadGroupSetRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          channel.newCall(METHOD_DELETE_READ_GROUP_SET, callOptions), request, responseObserver);
    }

    @java.lang.Override
    public void getReadGroupSet(com.google.genomics.v1.GetReadGroupSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ReadGroupSet> responseObserver) {
      asyncUnaryCall(
          channel.newCall(METHOD_GET_READ_GROUP_SET, callOptions), request, responseObserver);
    }

    @java.lang.Override
    public void listCoverageBuckets(com.google.genomics.v1.ListCoverageBucketsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ListCoverageBucketsResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(METHOD_LIST_COVERAGE_BUCKETS, callOptions), request, responseObserver);
    }

    @java.lang.Override
    public void searchReads(com.google.genomics.v1.SearchReadsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReadsResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(METHOD_SEARCH_READS, callOptions), request, responseObserver);
    }
  }

  public static class ReadServiceV1BlockingStub extends io.grpc.stub.AbstractStub<ReadServiceV1BlockingStub>
      implements ReadServiceV1BlockingClient {
    private ReadServiceV1BlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ReadServiceV1BlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ReadServiceV1BlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ReadServiceV1BlockingStub(channel, callOptions);
    }

    @java.lang.Override
    public com.google.longrunning.Operation importReadGroupSets(com.google.genomics.v1.ImportReadGroupSetsRequest request) {
      return blockingUnaryCall(
          channel.newCall(METHOD_IMPORT_READ_GROUP_SETS, callOptions), request);
    }

    @java.lang.Override
    public com.google.longrunning.Operation exportReadGroupSet(com.google.genomics.v1.ExportReadGroupSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(METHOD_EXPORT_READ_GROUP_SET, callOptions), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.SearchReadGroupSetsResponse searchReadGroupSets(com.google.genomics.v1.SearchReadGroupSetsRequest request) {
      return blockingUnaryCall(
          channel.newCall(METHOD_SEARCH_READ_GROUP_SETS, callOptions), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.ReadGroupSet updateReadGroupSet(com.google.genomics.v1.UpdateReadGroupSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(METHOD_UPDATE_READ_GROUP_SET, callOptions), request);
    }

    @java.lang.Override
    public com.google.protobuf.Empty deleteReadGroupSet(com.google.genomics.v1.DeleteReadGroupSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(METHOD_DELETE_READ_GROUP_SET, callOptions), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.ReadGroupSet getReadGroupSet(com.google.genomics.v1.GetReadGroupSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(METHOD_GET_READ_GROUP_SET, callOptions), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.ListCoverageBucketsResponse listCoverageBuckets(com.google.genomics.v1.ListCoverageBucketsRequest request) {
      return blockingUnaryCall(
          channel.newCall(METHOD_LIST_COVERAGE_BUCKETS, callOptions), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.SearchReadsResponse searchReads(com.google.genomics.v1.SearchReadsRequest request) {
      return blockingUnaryCall(
          channel.newCall(METHOD_SEARCH_READS, callOptions), request);
    }
  }

  public static class ReadServiceV1FutureStub extends io.grpc.stub.AbstractStub<ReadServiceV1FutureStub>
      implements ReadServiceV1FutureClient {
    private ReadServiceV1FutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ReadServiceV1FutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ReadServiceV1FutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ReadServiceV1FutureStub(channel, callOptions);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.longrunning.Operation> importReadGroupSets(
        com.google.genomics.v1.ImportReadGroupSetsRequest request) {
      return futureUnaryCall(
          channel.newCall(METHOD_IMPORT_READ_GROUP_SETS, callOptions), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.longrunning.Operation> exportReadGroupSet(
        com.google.genomics.v1.ExportReadGroupSetRequest request) {
      return futureUnaryCall(
          channel.newCall(METHOD_EXPORT_READ_GROUP_SET, callOptions), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchReadGroupSetsResponse> searchReadGroupSets(
        com.google.genomics.v1.SearchReadGroupSetsRequest request) {
      return futureUnaryCall(
          channel.newCall(METHOD_SEARCH_READ_GROUP_SETS, callOptions), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ReadGroupSet> updateReadGroupSet(
        com.google.genomics.v1.UpdateReadGroupSetRequest request) {
      return futureUnaryCall(
          channel.newCall(METHOD_UPDATE_READ_GROUP_SET, callOptions), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteReadGroupSet(
        com.google.genomics.v1.DeleteReadGroupSetRequest request) {
      return futureUnaryCall(
          channel.newCall(METHOD_DELETE_READ_GROUP_SET, callOptions), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ReadGroupSet> getReadGroupSet(
        com.google.genomics.v1.GetReadGroupSetRequest request) {
      return futureUnaryCall(
          channel.newCall(METHOD_GET_READ_GROUP_SET, callOptions), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ListCoverageBucketsResponse> listCoverageBuckets(
        com.google.genomics.v1.ListCoverageBucketsRequest request) {
      return futureUnaryCall(
          channel.newCall(METHOD_LIST_COVERAGE_BUCKETS, callOptions), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchReadsResponse> searchReads(
        com.google.genomics.v1.SearchReadsRequest request) {
      return futureUnaryCall(
          channel.newCall(METHOD_SEARCH_READS, callOptions), request);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final ReadServiceV1 serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder("google.genomics.v1.ReadServiceV1")
      .addMethod(io.grpc.ServerMethodDefinition.create(
          METHOD_IMPORT_READ_GROUP_SETS,
          asyncUnaryCall(
            new io.grpc.stub.ServerCalls.UnaryMethod<
                com.google.genomics.v1.ImportReadGroupSetsRequest,
                com.google.longrunning.Operation>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.ImportReadGroupSetsRequest request,
                  io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
                serviceImpl.importReadGroupSets(request, responseObserver);
              }
            })))
      .addMethod(io.grpc.ServerMethodDefinition.create(
          METHOD_EXPORT_READ_GROUP_SET,
          asyncUnaryCall(
            new io.grpc.stub.ServerCalls.UnaryMethod<
                com.google.genomics.v1.ExportReadGroupSetRequest,
                com.google.longrunning.Operation>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.ExportReadGroupSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.longrunning.Operation> responseObserver) {
                serviceImpl.exportReadGroupSet(request, responseObserver);
              }
            })))
      .addMethod(io.grpc.ServerMethodDefinition.create(
          METHOD_SEARCH_READ_GROUP_SETS,
          asyncUnaryCall(
            new io.grpc.stub.ServerCalls.UnaryMethod<
                com.google.genomics.v1.SearchReadGroupSetsRequest,
                com.google.genomics.v1.SearchReadGroupSetsResponse>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.SearchReadGroupSetsRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReadGroupSetsResponse> responseObserver) {
                serviceImpl.searchReadGroupSets(request, responseObserver);
              }
            })))
      .addMethod(io.grpc.ServerMethodDefinition.create(
          METHOD_UPDATE_READ_GROUP_SET,
          asyncUnaryCall(
            new io.grpc.stub.ServerCalls.UnaryMethod<
                com.google.genomics.v1.UpdateReadGroupSetRequest,
                com.google.genomics.v1.ReadGroupSet>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.UpdateReadGroupSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.ReadGroupSet> responseObserver) {
                serviceImpl.updateReadGroupSet(request, responseObserver);
              }
            })))
      .addMethod(io.grpc.ServerMethodDefinition.create(
          METHOD_DELETE_READ_GROUP_SET,
          asyncUnaryCall(
            new io.grpc.stub.ServerCalls.UnaryMethod<
                com.google.genomics.v1.DeleteReadGroupSetRequest,
                com.google.protobuf.Empty>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.DeleteReadGroupSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
                serviceImpl.deleteReadGroupSet(request, responseObserver);
              }
            })))
      .addMethod(io.grpc.ServerMethodDefinition.create(
          METHOD_GET_READ_GROUP_SET,
          asyncUnaryCall(
            new io.grpc.stub.ServerCalls.UnaryMethod<
                com.google.genomics.v1.GetReadGroupSetRequest,
                com.google.genomics.v1.ReadGroupSet>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.GetReadGroupSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.ReadGroupSet> responseObserver) {
                serviceImpl.getReadGroupSet(request, responseObserver);
              }
            })))
      .addMethod(io.grpc.ServerMethodDefinition.create(
          METHOD_LIST_COVERAGE_BUCKETS,
          asyncUnaryCall(
            new io.grpc.stub.ServerCalls.UnaryMethod<
                com.google.genomics.v1.ListCoverageBucketsRequest,
                com.google.genomics.v1.ListCoverageBucketsResponse>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.ListCoverageBucketsRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.ListCoverageBucketsResponse> responseObserver) {
                serviceImpl.listCoverageBuckets(request, responseObserver);
              }
            })))
      .addMethod(io.grpc.ServerMethodDefinition.create(
          METHOD_SEARCH_READS,
          asyncUnaryCall(
            new io.grpc.stub.ServerCalls.UnaryMethod<
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
