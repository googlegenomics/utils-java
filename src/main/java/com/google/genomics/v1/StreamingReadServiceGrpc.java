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
public class StreamingReadServiceGrpc {

  // Static method descriptors that strictly reflect the proto.
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.StreamReadsRequest,
      com.google.genomics.v1.StreamReadsResponse> METHOD_STREAM_READS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING,
          "google.genomics.v1.StreamingReadService", "StreamReads",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.StreamReadsRequest.parser()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.StreamReadsResponse.parser()));

  public static StreamingReadServiceStub newStub(io.grpc.Channel channel) {
    return new StreamingReadServiceStub(channel);
  }

  public static StreamingReadServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new StreamingReadServiceBlockingStub(channel);
  }

  public static StreamingReadServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new StreamingReadServiceFutureStub(channel);
  }

  public static interface StreamingReadService {

    public void streamReads(com.google.genomics.v1.StreamReadsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.StreamReadsResponse> responseObserver);
  }

  public static interface StreamingReadServiceBlockingClient {

    public java.util.Iterator<com.google.genomics.v1.StreamReadsResponse> streamReads(
        com.google.genomics.v1.StreamReadsRequest request);
  }

  public static interface StreamingReadServiceFutureClient {
  }

  public static class StreamingReadServiceStub extends io.grpc.stub.AbstractStub<StreamingReadServiceStub>
      implements StreamingReadService {
    private StreamingReadServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private StreamingReadServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StreamingReadServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new StreamingReadServiceStub(channel, callOptions);
    }

    @java.lang.Override
    public void streamReads(com.google.genomics.v1.StreamReadsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.StreamReadsResponse> responseObserver) {
      asyncServerStreamingCall(
          channel.newCall(METHOD_STREAM_READS, callOptions), request, responseObserver);
    }
  }

  public static class StreamingReadServiceBlockingStub extends io.grpc.stub.AbstractStub<StreamingReadServiceBlockingStub>
      implements StreamingReadServiceBlockingClient {
    private StreamingReadServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private StreamingReadServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StreamingReadServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new StreamingReadServiceBlockingStub(channel, callOptions);
    }

    @java.lang.Override
    public java.util.Iterator<com.google.genomics.v1.StreamReadsResponse> streamReads(
        com.google.genomics.v1.StreamReadsRequest request) {
      return blockingServerStreamingCall(
          channel.newCall(METHOD_STREAM_READS, callOptions), request);
    }
  }

  public static class StreamingReadServiceFutureStub extends io.grpc.stub.AbstractStub<StreamingReadServiceFutureStub>
      implements StreamingReadServiceFutureClient {
    private StreamingReadServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private StreamingReadServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StreamingReadServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new StreamingReadServiceFutureStub(channel, callOptions);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final StreamingReadService serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder("google.genomics.v1.StreamingReadService")
      .addMethod(io.grpc.ServerMethodDefinition.create(
          METHOD_STREAM_READS,
          asyncServerStreamingCall(
            new io.grpc.stub.ServerCalls.ServerStreamingMethod<
                com.google.genomics.v1.StreamReadsRequest,
                com.google.genomics.v1.StreamReadsResponse>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.StreamReadsRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.StreamReadsResponse> responseObserver) {
                serviceImpl.streamReads(request, responseObserver);
              }
            }))).build();
  }
}
