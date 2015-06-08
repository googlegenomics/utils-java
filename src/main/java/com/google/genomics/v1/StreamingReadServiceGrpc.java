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
public class StreamingReadServiceGrpc {

  private static final io.grpc.stub.Method<com.google.genomics.v1.StreamReadsRequest,
      com.google.genomics.v1.StreamReadsResponse> METHOD_STREAM_READS =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.SERVER_STREAMING, "StreamReads",
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.StreamReadsRequest.PARSER),
          io.grpc.proto.ProtoUtils.marshaller(com.google.genomics.v1.StreamReadsResponse.PARSER));

  public static StreamingReadServiceStub newStub(io.grpc.Channel channel) {
    return new StreamingReadServiceStub(channel, CONFIG);
  }

  public static StreamingReadServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new StreamingReadServiceBlockingStub(channel, CONFIG);
  }

  public static StreamingReadServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new StreamingReadServiceFutureStub(channel, CONFIG);
  }

  public static final StreamingReadServiceServiceDescriptor CONFIG =
      new StreamingReadServiceServiceDescriptor();

  @javax.annotation.concurrent.Immutable
  public static class StreamingReadServiceServiceDescriptor extends
      io.grpc.stub.AbstractServiceDescriptor<StreamingReadServiceServiceDescriptor> {
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.StreamReadsRequest,
        com.google.genomics.v1.StreamReadsResponse> streamReads;

    private StreamingReadServiceServiceDescriptor() {
      streamReads = createMethodDescriptor(
          "google.genomics.v1.StreamingReadService", METHOD_STREAM_READS);
    }

    @SuppressWarnings("unchecked")
    private StreamingReadServiceServiceDescriptor(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      streamReads = (io.grpc.MethodDescriptor<com.google.genomics.v1.StreamReadsRequest,
          com.google.genomics.v1.StreamReadsResponse>) methodMap.get(
          CONFIG.streamReads.getName());
    }

    @java.lang.Override
    protected StreamingReadServiceServiceDescriptor build(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      return new StreamingReadServiceServiceDescriptor(methodMap);
    }

    @java.lang.Override
    public com.google.common.collect.ImmutableList<io.grpc.MethodDescriptor<?, ?>> methods() {
      return com.google.common.collect.ImmutableList.<io.grpc.MethodDescriptor<?, ?>>of(
          streamReads);
    }
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

  public static class StreamingReadServiceStub extends
      io.grpc.stub.AbstractStub<StreamingReadServiceStub, StreamingReadServiceServiceDescriptor>
      implements StreamingReadService {
    private StreamingReadServiceStub(io.grpc.Channel channel,
        StreamingReadServiceServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected StreamingReadServiceStub build(io.grpc.Channel channel,
        StreamingReadServiceServiceDescriptor config) {
      return new StreamingReadServiceStub(channel, config);
    }

    @java.lang.Override
    public void streamReads(com.google.genomics.v1.StreamReadsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.StreamReadsResponse> responseObserver) {
      asyncServerStreamingCall(
          channel.newCall(config.streamReads), request, responseObserver);
    }
  }

  public static class StreamingReadServiceBlockingStub extends
      io.grpc.stub.AbstractStub<StreamingReadServiceBlockingStub, StreamingReadServiceServiceDescriptor>
      implements StreamingReadServiceBlockingClient {
    private StreamingReadServiceBlockingStub(io.grpc.Channel channel,
        StreamingReadServiceServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected StreamingReadServiceBlockingStub build(io.grpc.Channel channel,
        StreamingReadServiceServiceDescriptor config) {
      return new StreamingReadServiceBlockingStub(channel, config);
    }

    @java.lang.Override
    public java.util.Iterator<com.google.genomics.v1.StreamReadsResponse> streamReads(
        com.google.genomics.v1.StreamReadsRequest request) {
      return blockingServerStreamingCall(
          channel.newCall(config.streamReads), request);
    }
  }

  public static class StreamingReadServiceFutureStub extends
      io.grpc.stub.AbstractStub<StreamingReadServiceFutureStub, StreamingReadServiceServiceDescriptor>
      implements StreamingReadServiceFutureClient {
    private StreamingReadServiceFutureStub(io.grpc.Channel channel,
        StreamingReadServiceServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected StreamingReadServiceFutureStub build(io.grpc.Channel channel,
        StreamingReadServiceServiceDescriptor config) {
      return new StreamingReadServiceFutureStub(channel, config);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final StreamingReadService serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder("google.genomics.v1.StreamingReadService")
      .addMethod(createMethodDefinition(
          METHOD_STREAM_READS,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
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
