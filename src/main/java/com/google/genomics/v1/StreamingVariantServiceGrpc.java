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
public class StreamingVariantServiceGrpc {

  // Static method descriptors that strictly reflect the proto.
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.StreamVariantsRequest,
      com.google.genomics.v1.StreamVariantsResponse> METHOD_STREAM_VARIANTS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING,
          "google.genomics.v1.StreamingVariantService", "StreamVariants",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.StreamVariantsRequest.parser()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.StreamVariantsResponse.parser()));

  public static StreamingVariantServiceStub newStub(io.grpc.Channel channel) {
    return new StreamingVariantServiceStub(channel);
  }

  public static StreamingVariantServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new StreamingVariantServiceBlockingStub(channel);
  }

  public static StreamingVariantServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new StreamingVariantServiceFutureStub(channel);
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

  public static class StreamingVariantServiceStub extends io.grpc.stub.AbstractStub<StreamingVariantServiceStub>
      implements StreamingVariantService {
    private StreamingVariantServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private StreamingVariantServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StreamingVariantServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new StreamingVariantServiceStub(channel, callOptions);
    }

    @java.lang.Override
    public void streamVariants(com.google.genomics.v1.StreamVariantsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.StreamVariantsResponse> responseObserver) {
      asyncServerStreamingCall(
          channel.newCall(METHOD_STREAM_VARIANTS, callOptions), request, responseObserver);
    }
  }

  public static class StreamingVariantServiceBlockingStub extends io.grpc.stub.AbstractStub<StreamingVariantServiceBlockingStub>
      implements StreamingVariantServiceBlockingClient {
    private StreamingVariantServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private StreamingVariantServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StreamingVariantServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new StreamingVariantServiceBlockingStub(channel, callOptions);
    }

    @java.lang.Override
    public java.util.Iterator<com.google.genomics.v1.StreamVariantsResponse> streamVariants(
        com.google.genomics.v1.StreamVariantsRequest request) {
      return blockingServerStreamingCall(
          channel.newCall(METHOD_STREAM_VARIANTS, callOptions), request);
    }
  }

  public static class StreamingVariantServiceFutureStub extends io.grpc.stub.AbstractStub<StreamingVariantServiceFutureStub>
      implements StreamingVariantServiceFutureClient {
    private StreamingVariantServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private StreamingVariantServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StreamingVariantServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new StreamingVariantServiceFutureStub(channel, callOptions);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final StreamingVariantService serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder("google.genomics.v1.StreamingVariantService")
      .addMethod(io.grpc.ServerMethodDefinition.create(
          METHOD_STREAM_VARIANTS,
          asyncServerStreamingCall(
            new io.grpc.stub.ServerCalls.ServerStreamingMethod<
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
