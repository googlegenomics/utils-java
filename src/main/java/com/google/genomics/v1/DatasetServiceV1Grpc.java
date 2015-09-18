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
public class DatasetServiceV1Grpc {

  private DatasetServiceV1Grpc() {}

  public static final String SERVICE_NAME = "google.genomics.v1.DatasetServiceV1";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.ListDatasetsRequest,
      com.google.genomics.v1.ListDatasetsResponse> METHOD_LIST_DATASETS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.DatasetServiceV1", "ListDatasets"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.ListDatasetsRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.ListDatasetsResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.CreateDatasetRequest,
      com.google.genomics.v1.Dataset> METHOD_CREATE_DATASET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.DatasetServiceV1", "CreateDataset"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.CreateDatasetRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.Dataset.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.GetDatasetRequest,
      com.google.genomics.v1.Dataset> METHOD_GET_DATASET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.DatasetServiceV1", "GetDataset"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.GetDatasetRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.Dataset.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.UpdateDatasetRequest,
      com.google.genomics.v1.Dataset> METHOD_UPDATE_DATASET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.DatasetServiceV1", "UpdateDataset"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.UpdateDatasetRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.Dataset.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.DeleteDatasetRequest,
      com.google.protobuf.Empty> METHOD_DELETE_DATASET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.DatasetServiceV1", "DeleteDataset"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.DeleteDatasetRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.protobuf.Empty.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.google.genomics.v1.UndeleteDatasetRequest,
      com.google.genomics.v1.Dataset> METHOD_UNDELETE_DATASET =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "google.genomics.v1.DatasetServiceV1", "UndeleteDataset"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.UndeleteDatasetRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.Dataset.getDefaultInstance()));

  public static DatasetServiceV1Stub newStub(io.grpc.Channel channel) {
    return new DatasetServiceV1Stub(channel);
  }

  public static DatasetServiceV1BlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new DatasetServiceV1BlockingStub(channel);
  }

  public static DatasetServiceV1FutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new DatasetServiceV1FutureStub(channel);
  }

  public static interface DatasetServiceV1 {

    public void listDatasets(com.google.genomics.v1.ListDatasetsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ListDatasetsResponse> responseObserver);

    public void createDataset(com.google.genomics.v1.CreateDatasetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver);

    public void getDataset(com.google.genomics.v1.GetDatasetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver);

    public void updateDataset(com.google.genomics.v1.UpdateDatasetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver);

    public void deleteDataset(com.google.genomics.v1.DeleteDatasetRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver);

    public void undeleteDataset(com.google.genomics.v1.UndeleteDatasetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver);
  }

  public static interface DatasetServiceV1BlockingClient {

    public com.google.genomics.v1.ListDatasetsResponse listDatasets(com.google.genomics.v1.ListDatasetsRequest request);

    public com.google.genomics.v1.Dataset createDataset(com.google.genomics.v1.CreateDatasetRequest request);

    public com.google.genomics.v1.Dataset getDataset(com.google.genomics.v1.GetDatasetRequest request);

    public com.google.genomics.v1.Dataset updateDataset(com.google.genomics.v1.UpdateDatasetRequest request);

    public com.google.protobuf.Empty deleteDataset(com.google.genomics.v1.DeleteDatasetRequest request);

    public com.google.genomics.v1.Dataset undeleteDataset(com.google.genomics.v1.UndeleteDatasetRequest request);
  }

  public static interface DatasetServiceV1FutureClient {

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ListDatasetsResponse> listDatasets(
        com.google.genomics.v1.ListDatasetsRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Dataset> createDataset(
        com.google.genomics.v1.CreateDatasetRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Dataset> getDataset(
        com.google.genomics.v1.GetDatasetRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Dataset> updateDataset(
        com.google.genomics.v1.UpdateDatasetRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteDataset(
        com.google.genomics.v1.DeleteDatasetRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Dataset> undeleteDataset(
        com.google.genomics.v1.UndeleteDatasetRequest request);
  }

  public static class DatasetServiceV1Stub extends io.grpc.stub.AbstractStub<DatasetServiceV1Stub>
      implements DatasetServiceV1 {
    private DatasetServiceV1Stub(io.grpc.Channel channel) {
      super(channel);
    }

    private DatasetServiceV1Stub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DatasetServiceV1Stub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DatasetServiceV1Stub(channel, callOptions);
    }

    @java.lang.Override
    public void listDatasets(com.google.genomics.v1.ListDatasetsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ListDatasetsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_LIST_DATASETS, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void createDataset(com.google.genomics.v1.CreateDatasetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CREATE_DATASET, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void getDataset(com.google.genomics.v1.GetDatasetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_DATASET, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void updateDataset(com.google.genomics.v1.UpdateDatasetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_UPDATE_DATASET, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void deleteDataset(com.google.genomics.v1.DeleteDatasetRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_DELETE_DATASET, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void undeleteDataset(com.google.genomics.v1.UndeleteDatasetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_UNDELETE_DATASET, getCallOptions()), request, responseObserver);
    }
  }

  public static class DatasetServiceV1BlockingStub extends io.grpc.stub.AbstractStub<DatasetServiceV1BlockingStub>
      implements DatasetServiceV1BlockingClient {
    private DatasetServiceV1BlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DatasetServiceV1BlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DatasetServiceV1BlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DatasetServiceV1BlockingStub(channel, callOptions);
    }

    @java.lang.Override
    public com.google.genomics.v1.ListDatasetsResponse listDatasets(com.google.genomics.v1.ListDatasetsRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_LIST_DATASETS, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.Dataset createDataset(com.google.genomics.v1.CreateDatasetRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_CREATE_DATASET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.Dataset getDataset(com.google.genomics.v1.GetDatasetRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_GET_DATASET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.Dataset updateDataset(com.google.genomics.v1.UpdateDatasetRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_UPDATE_DATASET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.protobuf.Empty deleteDataset(com.google.genomics.v1.DeleteDatasetRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_DELETE_DATASET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.Dataset undeleteDataset(com.google.genomics.v1.UndeleteDatasetRequest request) {
      return blockingUnaryCall(
          getChannel().newCall(METHOD_UNDELETE_DATASET, getCallOptions()), request);
    }
  }

  public static class DatasetServiceV1FutureStub extends io.grpc.stub.AbstractStub<DatasetServiceV1FutureStub>
      implements DatasetServiceV1FutureClient {
    private DatasetServiceV1FutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DatasetServiceV1FutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DatasetServiceV1FutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DatasetServiceV1FutureStub(channel, callOptions);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ListDatasetsResponse> listDatasets(
        com.google.genomics.v1.ListDatasetsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_LIST_DATASETS, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Dataset> createDataset(
        com.google.genomics.v1.CreateDatasetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CREATE_DATASET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Dataset> getDataset(
        com.google.genomics.v1.GetDatasetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_DATASET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Dataset> updateDataset(
        com.google.genomics.v1.UpdateDatasetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_UPDATE_DATASET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteDataset(
        com.google.genomics.v1.DeleteDatasetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_DELETE_DATASET, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Dataset> undeleteDataset(
        com.google.genomics.v1.UndeleteDatasetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_UNDELETE_DATASET, getCallOptions()), request);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final DatasetServiceV1 serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder(SERVICE_NAME)
      .addMethod(
        METHOD_LIST_DATASETS,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.ListDatasetsRequest,
              com.google.genomics.v1.ListDatasetsResponse>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.ListDatasetsRequest request,
                io.grpc.stub.StreamObserver<com.google.genomics.v1.ListDatasetsResponse> responseObserver) {
              serviceImpl.listDatasets(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_CREATE_DATASET,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.CreateDatasetRequest,
              com.google.genomics.v1.Dataset>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.CreateDatasetRequest request,
                io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver) {
              serviceImpl.createDataset(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_GET_DATASET,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.GetDatasetRequest,
              com.google.genomics.v1.Dataset>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.GetDatasetRequest request,
                io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver) {
              serviceImpl.getDataset(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_UPDATE_DATASET,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.UpdateDatasetRequest,
              com.google.genomics.v1.Dataset>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.UpdateDatasetRequest request,
                io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver) {
              serviceImpl.updateDataset(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_DELETE_DATASET,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.DeleteDatasetRequest,
              com.google.protobuf.Empty>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.DeleteDatasetRequest request,
                io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
              serviceImpl.deleteDataset(request, responseObserver);
            }
          }))
      .addMethod(
        METHOD_UNDELETE_DATASET,
        asyncUnaryCall(
          new io.grpc.stub.ServerCalls.UnaryMethod<
              com.google.genomics.v1.UndeleteDatasetRequest,
              com.google.genomics.v1.Dataset>() {
            @java.lang.Override
            public void invoke(
                com.google.genomics.v1.UndeleteDatasetRequest request,
                io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver) {
              serviceImpl.undeleteDataset(request, responseObserver);
            }
          })).build();
  }
}
