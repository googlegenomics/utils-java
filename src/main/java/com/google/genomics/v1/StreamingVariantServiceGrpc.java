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
public class StreamingVariantServiceGrpc {

  private static final io.grpc.stub.Method<com.google.genomics.v1.StreamVariantsRequest,
      com.google.genomics.v1.StreamVariantsResponse> METHOD_STREAM_VARIANTS =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.SERVER_STREAMING, "StreamVariants",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.StreamVariantsRequest.PARSER),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.StreamVariantsResponse.PARSER));

  public static StreamingVariantServiceStub newStub(io.grpc.Channel channel) {
    return new StreamingVariantServiceStub(channel, CONFIG);
  }

  public static StreamingVariantServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new StreamingVariantServiceBlockingStub(channel, CONFIG);
  }

  public static StreamingVariantServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new StreamingVariantServiceFutureStub(channel, CONFIG);
  }

  public static final StreamingVariantServiceServiceDescriptor CONFIG =
      new StreamingVariantServiceServiceDescriptor();

  @javax.annotation.concurrent.Immutable
  public static class StreamingVariantServiceServiceDescriptor extends
      io.grpc.stub.AbstractServiceDescriptor<StreamingVariantServiceServiceDescriptor> {
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.StreamVariantsRequest,
        com.google.genomics.v1.StreamVariantsResponse> streamVariants;

    private StreamingVariantServiceServiceDescriptor() {
      streamVariants = createMethodDescriptor(
          "google.genomics.v1.StreamingVariantService", METHOD_STREAM_VARIANTS);
    }

    @SuppressWarnings("unchecked")
    private StreamingVariantServiceServiceDescriptor(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      streamVariants = (io.grpc.MethodDescriptor<com.google.genomics.v1.StreamVariantsRequest,
          com.google.genomics.v1.StreamVariantsResponse>) methodMap.get(
          CONFIG.streamVariants.getName());
    }

    @java.lang.Override
    protected StreamingVariantServiceServiceDescriptor build(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      return new StreamingVariantServiceServiceDescriptor(methodMap);
    }

    @java.lang.Override
    public com.google.common.collect.ImmutableList<io.grpc.MethodDescriptor<?, ?>> methods() {
      return com.google.common.collect.ImmutableList.<io.grpc.MethodDescriptor<?, ?>>of(
          streamVariants);
    }
  }

  public static interface StreamingVariantService {

    public void streamVariants(com.google.genomics.v1.StreamVariantsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.StreamVariantsResponse> responseObserver);
  }

  public static interface StreamingVariantServiceBlockingClient {

    public java.util.Iterator<com.google.genomics.v1.StreamVariantsResponse> streamVariants(
        com.google.genomics.v1.StreamVariantsRequest request);
  }

  public static interface StreamingVariantServiceFutureClient {
  }

  public static class StreamingVariantServiceStub extends
      io.grpc.stub.AbstractStub<StreamingVariantServiceStub, StreamingVariantServiceServiceDescriptor>
      implements StreamingVariantService {
    private StreamingVariantServiceStub(io.grpc.Channel channel,
        StreamingVariantServiceServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected StreamingVariantServiceStub build(io.grpc.Channel channel,
        StreamingVariantServiceServiceDescriptor config) {
      return new StreamingVariantServiceStub(channel, config);
    }

    @java.lang.Override
    public void streamVariants(com.google.genomics.v1.StreamVariantsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.StreamVariantsResponse> responseObserver) {
      asyncServerStreamingCall(
          channel.newCall(config.streamVariants), request, responseObserver);
    }
  }

  public static class StreamingVariantServiceBlockingStub extends
      io.grpc.stub.AbstractStub<StreamingVariantServiceBlockingStub, StreamingVariantServiceServiceDescriptor>
      implements StreamingVariantServiceBlockingClient {
    private StreamingVariantServiceBlockingStub(io.grpc.Channel channel,
        StreamingVariantServiceServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected StreamingVariantServiceBlockingStub build(io.grpc.Channel channel,
        StreamingVariantServiceServiceDescriptor config) {
      return new StreamingVariantServiceBlockingStub(channel, config);
    }

    @java.lang.Override
    public java.util.Iterator<com.google.genomics.v1.StreamVariantsResponse> streamVariants(
        com.google.genomics.v1.StreamVariantsRequest request) {
      return blockingServerStreamingCall(
          channel.newCall(config.streamVariants), request);
    }
  }

  public static class StreamingVariantServiceFutureStub extends
      io.grpc.stub.AbstractStub<StreamingVariantServiceFutureStub, StreamingVariantServiceServiceDescriptor>
      implements StreamingVariantServiceFutureClient {
    private StreamingVariantServiceFutureStub(io.grpc.Channel channel,
        StreamingVariantServiceServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected StreamingVariantServiceFutureStub build(io.grpc.Channel channel,
        StreamingVariantServiceServiceDescriptor config) {
      return new StreamingVariantServiceFutureStub(channel, config);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final StreamingVariantService serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder("google.genomics.v1.StreamingVariantService")
      .addMethod(createMethodDefinition(
          METHOD_STREAM_VARIANTS,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.StreamVariantsRequest,
                com.google.genomics.v1.StreamVariantsResponse>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.StreamVariantsRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.StreamVariantsResponse> responseObserver) {
                serviceImpl.streamVariants(request, responseObserver);
              }
            }))).build();
  }
}
