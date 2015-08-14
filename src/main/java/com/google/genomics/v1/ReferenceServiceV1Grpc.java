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
public class ReferenceServiceV1Grpc {

  // Static method descriptors that strictly reflect the proto.
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.SearchReferenceSetsRequest,
      com.google.genomics.v1.SearchReferenceSetsResponse> METHOD_SEARCH_REFERENCE_SETS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          "google.genomics.v1.ReferenceServiceV1", "SearchReferenceSets",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.SearchReferenceSetsRequest.parser()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.SearchReferenceSetsResponse.parser()));
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.GetReferenceSetRequest,
      com.google.genomics.v1.ReferenceSet> METHOD_GET_REFERENCE_SET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          "google.genomics.v1.ReferenceServiceV1", "GetReferenceSet",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.GetReferenceSetRequest.parser()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.ReferenceSet.parser()));
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.SearchReferencesRequest,
      com.google.genomics.v1.SearchReferencesResponse> METHOD_SEARCH_REFERENCES =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          "google.genomics.v1.ReferenceServiceV1", "SearchReferences",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.SearchReferencesRequest.parser()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.SearchReferencesResponse.parser()));
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.GetReferenceRequest,
      com.google.genomics.v1.Reference> METHOD_GET_REFERENCE =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          "google.genomics.v1.ReferenceServiceV1", "GetReference",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.GetReferenceRequest.parser()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.Reference.parser()));
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.ListBasesRequest,
      com.google.genomics.v1.ListBasesResponse> METHOD_LIST_BASES =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          "google.genomics.v1.ReferenceServiceV1", "ListBases",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.ListBasesRequest.parser()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.ListBasesResponse.parser()));

  public static ReferenceServiceV1Stub newStub(io.grpc.Channel channel) {
    return new ReferenceServiceV1Stub(channel);
  }

  public static ReferenceServiceV1BlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ReferenceServiceV1BlockingStub(channel);
  }

  public static ReferenceServiceV1FutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ReferenceServiceV1FutureStub(channel);
  }

  public static interface ReferenceServiceV1 {

    public void searchReferenceSets(com.google.genomics.v1.SearchReferenceSetsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReferenceSetsResponse> responseObserver);

    public void getReferenceSet(com.google.genomics.v1.GetReferenceSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ReferenceSet> responseObserver);

    public void searchReferences(com.google.genomics.v1.SearchReferencesRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReferencesResponse> responseObserver);

    public void getReference(com.google.genomics.v1.GetReferenceRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Reference> responseObserver);

    public void listBases(com.google.genomics.v1.ListBasesRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ListBasesResponse> responseObserver);
  }

  public static interface ReferenceServiceV1BlockingClient {

    public com.google.genomics.v1.SearchReferenceSetsResponse searchReferenceSets(com.google.genomics.v1.SearchReferenceSetsRequest request);

    public com.google.genomics.v1.ReferenceSet getReferenceSet(com.google.genomics.v1.GetReferenceSetRequest request);

    public com.google.genomics.v1.SearchReferencesResponse searchReferences(com.google.genomics.v1.SearchReferencesRequest request);

    public com.google.genomics.v1.Reference getReference(com.google.genomics.v1.GetReferenceRequest request);

    public com.google.genomics.v1.ListBasesResponse listBases(com.google.genomics.v1.ListBasesRequest request);
  }

  public static interface ReferenceServiceV1FutureClient {

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchReferenceSetsResponse> searchReferenceSets(
        com.google.genomics.v1.SearchReferenceSetsRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ReferenceSet> getReferenceSet(
        com.google.genomics.v1.GetReferenceSetRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchReferencesResponse> searchReferences(
        com.google.genomics.v1.SearchReferencesRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Reference> getReference(
        com.google.genomics.v1.GetReferenceRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ListBasesResponse> listBases(
        com.google.genomics.v1.ListBasesRequest request);
  }

  public static class ReferenceServiceV1Stub extends io.grpc.stub.AbstractStub<ReferenceServiceV1Stub>
      implements ReferenceServiceV1 {
    private ReferenceServiceV1Stub(io.grpc.Channel channel) {
      super(channel);
    }

    private ReferenceServiceV1Stub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ReferenceServiceV1Stub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ReferenceServiceV1Stub(channel, callOptions);
    }

    @java.lang.Override
    public void searchReferenceSets(com.google.genomics.v1.SearchReferenceSetsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReferenceSetsResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(METHOD_SEARCH_REFERENCE_SETS, callOptions), request, responseObserver);
    }

    @java.lang.Override
    public void getReferenceSet(com.google.genomics.v1.GetReferenceSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ReferenceSet> responseObserver) {
      asyncUnaryCall(
          channel.newCall(METHOD_GET_REFERENCE_SET, callOptions), request, responseObserver);
    }

    @java.lang.Override
    public void searchReferences(com.google.genomics.v1.SearchReferencesRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReferencesResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(METHOD_SEARCH_REFERENCES, callOptions), request, responseObserver);
    }

    @java.lang.Override
    public void getReference(com.google.genomics.v1.GetReferenceRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Reference> responseObserver) {
      asyncUnaryCall(
          channel.newCall(METHOD_GET_REFERENCE, callOptions), request, responseObserver);
    }

    @java.lang.Override
    public void listBases(com.google.genomics.v1.ListBasesRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ListBasesResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(METHOD_LIST_BASES, callOptions), request, responseObserver);
    }
  }

  public static class ReferenceServiceV1BlockingStub extends io.grpc.stub.AbstractStub<ReferenceServiceV1BlockingStub>
      implements ReferenceServiceV1BlockingClient {
    private ReferenceServiceV1BlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ReferenceServiceV1BlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ReferenceServiceV1BlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ReferenceServiceV1BlockingStub(channel, callOptions);
    }

    @java.lang.Override
    public com.google.genomics.v1.SearchReferenceSetsResponse searchReferenceSets(com.google.genomics.v1.SearchReferenceSetsRequest request) {
      return blockingUnaryCall(
          channel.newCall(METHOD_SEARCH_REFERENCE_SETS, callOptions), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.ReferenceSet getReferenceSet(com.google.genomics.v1.GetReferenceSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(METHOD_GET_REFERENCE_SET, callOptions), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.SearchReferencesResponse searchReferences(com.google.genomics.v1.SearchReferencesRequest request) {
      return blockingUnaryCall(
          channel.newCall(METHOD_SEARCH_REFERENCES, callOptions), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.Reference getReference(com.google.genomics.v1.GetReferenceRequest request) {
      return blockingUnaryCall(
          channel.newCall(METHOD_GET_REFERENCE, callOptions), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.ListBasesResponse listBases(com.google.genomics.v1.ListBasesRequest request) {
      return blockingUnaryCall(
          channel.newCall(METHOD_LIST_BASES, callOptions), request);
    }
  }

  public static class ReferenceServiceV1FutureStub extends io.grpc.stub.AbstractStub<ReferenceServiceV1FutureStub>
      implements ReferenceServiceV1FutureClient {
    private ReferenceServiceV1FutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ReferenceServiceV1FutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ReferenceServiceV1FutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ReferenceServiceV1FutureStub(channel, callOptions);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchReferenceSetsResponse> searchReferenceSets(
        com.google.genomics.v1.SearchReferenceSetsRequest request) {
      return futureUnaryCall(
          channel.newCall(METHOD_SEARCH_REFERENCE_SETS, callOptions), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ReferenceSet> getReferenceSet(
        com.google.genomics.v1.GetReferenceSetRequest request) {
      return futureUnaryCall(
          channel.newCall(METHOD_GET_REFERENCE_SET, callOptions), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchReferencesResponse> searchReferences(
        com.google.genomics.v1.SearchReferencesRequest request) {
      return futureUnaryCall(
          channel.newCall(METHOD_SEARCH_REFERENCES, callOptions), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Reference> getReference(
        com.google.genomics.v1.GetReferenceRequest request) {
      return futureUnaryCall(
          channel.newCall(METHOD_GET_REFERENCE, callOptions), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ListBasesResponse> listBases(
        com.google.genomics.v1.ListBasesRequest request) {
      return futureUnaryCall(
          channel.newCall(METHOD_LIST_BASES, callOptions), request);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final ReferenceServiceV1 serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder("google.genomics.v1.ReferenceServiceV1")
      .addMethod(io.grpc.ServerMethodDefinition.create(
          METHOD_SEARCH_REFERENCE_SETS,
          asyncUnaryCall(
            new io.grpc.stub.ServerCalls.UnaryMethod<
                com.google.genomics.v1.SearchReferenceSetsRequest,
                com.google.genomics.v1.SearchReferenceSetsResponse>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.SearchReferenceSetsRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReferenceSetsResponse> responseObserver) {
                serviceImpl.searchReferenceSets(request, responseObserver);
              }
            })))
      .addMethod(io.grpc.ServerMethodDefinition.create(
          METHOD_GET_REFERENCE_SET,
          asyncUnaryCall(
            new io.grpc.stub.ServerCalls.UnaryMethod<
                com.google.genomics.v1.GetReferenceSetRequest,
                com.google.genomics.v1.ReferenceSet>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.GetReferenceSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.ReferenceSet> responseObserver) {
                serviceImpl.getReferenceSet(request, responseObserver);
              }
            })))
      .addMethod(io.grpc.ServerMethodDefinition.create(
          METHOD_SEARCH_REFERENCES,
          asyncUnaryCall(
            new io.grpc.stub.ServerCalls.UnaryMethod<
                com.google.genomics.v1.SearchReferencesRequest,
                com.google.genomics.v1.SearchReferencesResponse>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.SearchReferencesRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReferencesResponse> responseObserver) {
                serviceImpl.searchReferences(request, responseObserver);
              }
            })))
      .addMethod(io.grpc.ServerMethodDefinition.create(
          METHOD_GET_REFERENCE,
          asyncUnaryCall(
            new io.grpc.stub.ServerCalls.UnaryMethod<
                com.google.genomics.v1.GetReferenceRequest,
                com.google.genomics.v1.Reference>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.GetReferenceRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.Reference> responseObserver) {
                serviceImpl.getReference(request, responseObserver);
              }
            })))
      .addMethod(io.grpc.ServerMethodDefinition.create(
          METHOD_LIST_BASES,
          asyncUnaryCall(
            new io.grpc.stub.ServerCalls.UnaryMethod<
                com.google.genomics.v1.ListBasesRequest,
                com.google.genomics.v1.ListBasesResponse>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.ListBasesRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.ListBasesResponse> responseObserver) {
                serviceImpl.listBases(request, responseObserver);
              }
            }))).build();
  }
}
