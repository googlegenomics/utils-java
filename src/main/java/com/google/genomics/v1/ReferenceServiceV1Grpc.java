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
public class ReferenceServiceV1Grpc {

  private static final io.grpc.stub.Method<com.google.genomics.v1.SearchReferenceSetsRequest,
      com.google.genomics.v1.SearchReferenceSetsResponse> METHOD_SEARCH_REFERENCE_SETS =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "SearchReferenceSets",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.SearchReferenceSetsRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.SearchReferenceSetsResponse.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.GetReferenceSetRequest,
      com.google.genomics.v1.ReferenceSet> METHOD_GET_REFERENCE_SET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "GetReferenceSet",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.GetReferenceSetRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.ReferenceSet.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.SearchReferencesRequest,
      com.google.genomics.v1.SearchReferencesResponse> METHOD_SEARCH_REFERENCES =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "SearchReferences",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.SearchReferencesRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.SearchReferencesResponse.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.GetReferenceRequest,
      com.google.genomics.v1.Reference> METHOD_GET_REFERENCE =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "GetReference",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.GetReferenceRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.Reference.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.ListBasesRequest,
      com.google.genomics.v1.ListBasesResponse> METHOD_LIST_BASES =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "ListBases",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.ListBasesRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.ListBasesResponse.PARSER));

  public static ReferenceServiceV1Stub newStub(io.grpc.Channel channel) {
    return new ReferenceServiceV1Stub(channel, CONFIG);
  }

  public static ReferenceServiceV1BlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ReferenceServiceV1BlockingStub(channel, CONFIG);
  }

  public static ReferenceServiceV1FutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ReferenceServiceV1FutureStub(channel, CONFIG);
  }

  public static final ReferenceServiceV1ServiceDescriptor CONFIG =
      new ReferenceServiceV1ServiceDescriptor();

  @javax.annotation.concurrent.Immutable
  public static class ReferenceServiceV1ServiceDescriptor extends
      io.grpc.stub.AbstractServiceDescriptor<ReferenceServiceV1ServiceDescriptor> {
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.SearchReferenceSetsRequest,
        com.google.genomics.v1.SearchReferenceSetsResponse> searchReferenceSets;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.GetReferenceSetRequest,
        com.google.genomics.v1.ReferenceSet> getReferenceSet;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.SearchReferencesRequest,
        com.google.genomics.v1.SearchReferencesResponse> searchReferences;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.GetReferenceRequest,
        com.google.genomics.v1.Reference> getReference;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.ListBasesRequest,
        com.google.genomics.v1.ListBasesResponse> listBases;

    private ReferenceServiceV1ServiceDescriptor() {
      searchReferenceSets = createMethodDescriptor(
          "google.genomics.v1.ReferenceServiceV1", METHOD_SEARCH_REFERENCE_SETS);
      getReferenceSet = createMethodDescriptor(
          "google.genomics.v1.ReferenceServiceV1", METHOD_GET_REFERENCE_SET);
      searchReferences = createMethodDescriptor(
          "google.genomics.v1.ReferenceServiceV1", METHOD_SEARCH_REFERENCES);
      getReference = createMethodDescriptor(
          "google.genomics.v1.ReferenceServiceV1", METHOD_GET_REFERENCE);
      listBases = createMethodDescriptor(
          "google.genomics.v1.ReferenceServiceV1", METHOD_LIST_BASES);
    }

    @SuppressWarnings("unchecked")
    private ReferenceServiceV1ServiceDescriptor(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      searchReferenceSets = (io.grpc.MethodDescriptor<com.google.genomics.v1.SearchReferenceSetsRequest,
          com.google.genomics.v1.SearchReferenceSetsResponse>) methodMap.get(
          CONFIG.searchReferenceSets.getName());
      getReferenceSet = (io.grpc.MethodDescriptor<com.google.genomics.v1.GetReferenceSetRequest,
          com.google.genomics.v1.ReferenceSet>) methodMap.get(
          CONFIG.getReferenceSet.getName());
      searchReferences = (io.grpc.MethodDescriptor<com.google.genomics.v1.SearchReferencesRequest,
          com.google.genomics.v1.SearchReferencesResponse>) methodMap.get(
          CONFIG.searchReferences.getName());
      getReference = (io.grpc.MethodDescriptor<com.google.genomics.v1.GetReferenceRequest,
          com.google.genomics.v1.Reference>) methodMap.get(
          CONFIG.getReference.getName());
      listBases = (io.grpc.MethodDescriptor<com.google.genomics.v1.ListBasesRequest,
          com.google.genomics.v1.ListBasesResponse>) methodMap.get(
          CONFIG.listBases.getName());
    }

    @java.lang.Override
    protected ReferenceServiceV1ServiceDescriptor build(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      return new ReferenceServiceV1ServiceDescriptor(methodMap);
    }

    @java.lang.Override
    public com.google.common.collect.ImmutableList<io.grpc.MethodDescriptor<?, ?>> methods() {
      return com.google.common.collect.ImmutableList.<io.grpc.MethodDescriptor<?, ?>>of(
          searchReferenceSets,
          getReferenceSet,
          searchReferences,
          getReference,
          listBases);
    }
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

  public static class ReferenceServiceV1Stub extends
      io.grpc.stub.AbstractStub<ReferenceServiceV1Stub, ReferenceServiceV1ServiceDescriptor>
      implements ReferenceServiceV1 {
    private ReferenceServiceV1Stub(io.grpc.Channel channel,
        ReferenceServiceV1ServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected ReferenceServiceV1Stub build(io.grpc.Channel channel,
        ReferenceServiceV1ServiceDescriptor config) {
      return new ReferenceServiceV1Stub(channel, config);
    }

    @java.lang.Override
    public void searchReferenceSets(com.google.genomics.v1.SearchReferenceSetsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReferenceSetsResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.searchReferenceSets), request, responseObserver);
    }

    @java.lang.Override
    public void getReferenceSet(com.google.genomics.v1.GetReferenceSetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ReferenceSet> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.getReferenceSet), request, responseObserver);
    }

    @java.lang.Override
    public void searchReferences(com.google.genomics.v1.SearchReferencesRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReferencesResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.searchReferences), request, responseObserver);
    }

    @java.lang.Override
    public void getReference(com.google.genomics.v1.GetReferenceRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Reference> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.getReference), request, responseObserver);
    }

    @java.lang.Override
    public void listBases(com.google.genomics.v1.ListBasesRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ListBasesResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.listBases), request, responseObserver);
    }
  }

  public static class ReferenceServiceV1BlockingStub extends
      io.grpc.stub.AbstractStub<ReferenceServiceV1BlockingStub, ReferenceServiceV1ServiceDescriptor>
      implements ReferenceServiceV1BlockingClient {
    private ReferenceServiceV1BlockingStub(io.grpc.Channel channel,
        ReferenceServiceV1ServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected ReferenceServiceV1BlockingStub build(io.grpc.Channel channel,
        ReferenceServiceV1ServiceDescriptor config) {
      return new ReferenceServiceV1BlockingStub(channel, config);
    }

    @java.lang.Override
    public com.google.genomics.v1.SearchReferenceSetsResponse searchReferenceSets(com.google.genomics.v1.SearchReferenceSetsRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.searchReferenceSets), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.ReferenceSet getReferenceSet(com.google.genomics.v1.GetReferenceSetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.getReferenceSet), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.SearchReferencesResponse searchReferences(com.google.genomics.v1.SearchReferencesRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.searchReferences), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.Reference getReference(com.google.genomics.v1.GetReferenceRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.getReference), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.ListBasesResponse listBases(com.google.genomics.v1.ListBasesRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.listBases), request);
    }
  }

  public static class ReferenceServiceV1FutureStub extends
      io.grpc.stub.AbstractStub<ReferenceServiceV1FutureStub, ReferenceServiceV1ServiceDescriptor>
      implements ReferenceServiceV1FutureClient {
    private ReferenceServiceV1FutureStub(io.grpc.Channel channel,
        ReferenceServiceV1ServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected ReferenceServiceV1FutureStub build(io.grpc.Channel channel,
        ReferenceServiceV1ServiceDescriptor config) {
      return new ReferenceServiceV1FutureStub(channel, config);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchReferenceSetsResponse> searchReferenceSets(
        com.google.genomics.v1.SearchReferenceSetsRequest request) {
      return unaryFutureCall(
          channel.newCall(config.searchReferenceSets), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ReferenceSet> getReferenceSet(
        com.google.genomics.v1.GetReferenceSetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.getReferenceSet), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.SearchReferencesResponse> searchReferences(
        com.google.genomics.v1.SearchReferencesRequest request) {
      return unaryFutureCall(
          channel.newCall(config.searchReferences), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Reference> getReference(
        com.google.genomics.v1.GetReferenceRequest request) {
      return unaryFutureCall(
          channel.newCall(config.getReference), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ListBasesResponse> listBases(
        com.google.genomics.v1.ListBasesRequest request) {
      return unaryFutureCall(
          channel.newCall(config.listBases), request);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final ReferenceServiceV1 serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder("google.genomics.v1.ReferenceServiceV1")
      .addMethod(createMethodDefinition(
          METHOD_SEARCH_REFERENCE_SETS,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.SearchReferenceSetsRequest,
                com.google.genomics.v1.SearchReferenceSetsResponse>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.SearchReferenceSetsRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReferenceSetsResponse> responseObserver) {
                serviceImpl.searchReferenceSets(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_GET_REFERENCE_SET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.GetReferenceSetRequest,
                com.google.genomics.v1.ReferenceSet>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.GetReferenceSetRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.ReferenceSet> responseObserver) {
                serviceImpl.getReferenceSet(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_SEARCH_REFERENCES,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.SearchReferencesRequest,
                com.google.genomics.v1.SearchReferencesResponse>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.SearchReferencesRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.SearchReferencesResponse> responseObserver) {
                serviceImpl.searchReferences(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_GET_REFERENCE,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.GetReferenceRequest,
                com.google.genomics.v1.Reference>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.GetReferenceRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.Reference> responseObserver) {
                serviceImpl.getReference(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_LIST_BASES,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
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
