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
public class DatasetServiceV1Grpc {

  private static final io.grpc.stub.Method<com.google.genomics.v1.ListDatasetsRequest,
      com.google.genomics.v1.ListDatasetsResponse> METHOD_LIST_DATASETS =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "ListDatasets",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.ListDatasetsRequest.PARSER),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.ListDatasetsResponse.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.CreateDatasetRequest,
      com.google.genomics.v1.Dataset> METHOD_CREATE_DATASET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "CreateDataset",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.CreateDatasetRequest.PARSER),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.Dataset.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.GetDatasetRequest,
      com.google.genomics.v1.Dataset> METHOD_GET_DATASET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "GetDataset",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.GetDatasetRequest.PARSER),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.Dataset.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.UpdateDatasetRequest,
      com.google.genomics.v1.Dataset> METHOD_UPDATE_DATASET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "UpdateDataset",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.UpdateDatasetRequest.PARSER),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.Dataset.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.DeleteDatasetRequest,
      com.google.protobuf.Empty> METHOD_DELETE_DATASET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "DeleteDataset",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.DeleteDatasetRequest.PARSER),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.protobuf.Empty.PARSER));
  private static final io.grpc.stub.Method<com.google.genomics.v1.UndeleteDatasetRequest,
      com.google.genomics.v1.Dataset> METHOD_UNDELETE_DATASET =
      io.grpc.stub.Method.create(
          io.grpc.MethodType.UNARY, "UndeleteDataset",
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.UndeleteDatasetRequest.PARSER),
          io.grpc.protobuf.ProtoUtils.marshaller(com.google.genomics.v1.Dataset.PARSER));

  public static DatasetServiceV1Stub newStub(io.grpc.Channel channel) {
    return new DatasetServiceV1Stub(channel, CONFIG);
  }

  public static DatasetServiceV1BlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new DatasetServiceV1BlockingStub(channel, CONFIG);
  }

  public static DatasetServiceV1FutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new DatasetServiceV1FutureStub(channel, CONFIG);
  }

  public static final DatasetServiceV1ServiceDescriptor CONFIG =
      new DatasetServiceV1ServiceDescriptor();

  @javax.annotation.concurrent.Immutable
  public static class DatasetServiceV1ServiceDescriptor extends
      io.grpc.stub.AbstractServiceDescriptor<DatasetServiceV1ServiceDescriptor> {
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.ListDatasetsRequest,
        com.google.genomics.v1.ListDatasetsResponse> listDatasets;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.CreateDatasetRequest,
        com.google.genomics.v1.Dataset> createDataset;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.GetDatasetRequest,
        com.google.genomics.v1.Dataset> getDataset;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.UpdateDatasetRequest,
        com.google.genomics.v1.Dataset> updateDataset;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.DeleteDatasetRequest,
        com.google.protobuf.Empty> deleteDataset;
    public final io.grpc.MethodDescriptor<com.google.genomics.v1.UndeleteDatasetRequest,
        com.google.genomics.v1.Dataset> undeleteDataset;

    private DatasetServiceV1ServiceDescriptor() {
      listDatasets = createMethodDescriptor(
          "google.genomics.v1.DatasetServiceV1", METHOD_LIST_DATASETS);
      createDataset = createMethodDescriptor(
          "google.genomics.v1.DatasetServiceV1", METHOD_CREATE_DATASET);
      getDataset = createMethodDescriptor(
          "google.genomics.v1.DatasetServiceV1", METHOD_GET_DATASET);
      updateDataset = createMethodDescriptor(
          "google.genomics.v1.DatasetServiceV1", METHOD_UPDATE_DATASET);
      deleteDataset = createMethodDescriptor(
          "google.genomics.v1.DatasetServiceV1", METHOD_DELETE_DATASET);
      undeleteDataset = createMethodDescriptor(
          "google.genomics.v1.DatasetServiceV1", METHOD_UNDELETE_DATASET);
    }

    @SuppressWarnings("unchecked")
    private DatasetServiceV1ServiceDescriptor(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      listDatasets = (io.grpc.MethodDescriptor<com.google.genomics.v1.ListDatasetsRequest,
          com.google.genomics.v1.ListDatasetsResponse>) methodMap.get(
          CONFIG.listDatasets.getName());
      createDataset = (io.grpc.MethodDescriptor<com.google.genomics.v1.CreateDatasetRequest,
          com.google.genomics.v1.Dataset>) methodMap.get(
          CONFIG.createDataset.getName());
      getDataset = (io.grpc.MethodDescriptor<com.google.genomics.v1.GetDatasetRequest,
          com.google.genomics.v1.Dataset>) methodMap.get(
          CONFIG.getDataset.getName());
      updateDataset = (io.grpc.MethodDescriptor<com.google.genomics.v1.UpdateDatasetRequest,
          com.google.genomics.v1.Dataset>) methodMap.get(
          CONFIG.updateDataset.getName());
      deleteDataset = (io.grpc.MethodDescriptor<com.google.genomics.v1.DeleteDatasetRequest,
          com.google.protobuf.Empty>) methodMap.get(
          CONFIG.deleteDataset.getName());
      undeleteDataset = (io.grpc.MethodDescriptor<com.google.genomics.v1.UndeleteDatasetRequest,
          com.google.genomics.v1.Dataset>) methodMap.get(
          CONFIG.undeleteDataset.getName());
    }

    @java.lang.Override
    protected DatasetServiceV1ServiceDescriptor build(
        java.util.Map<java.lang.String, io.grpc.MethodDescriptor<?, ?>> methodMap) {
      return new DatasetServiceV1ServiceDescriptor(methodMap);
    }

    @java.lang.Override
    public com.google.common.collect.ImmutableList<io.grpc.MethodDescriptor<?, ?>> methods() {
      return com.google.common.collect.ImmutableList.<io.grpc.MethodDescriptor<?, ?>>of(
          listDatasets,
          createDataset,
          getDataset,
          updateDataset,
          deleteDataset,
          undeleteDataset);
    }
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

  public static class DatasetServiceV1Stub extends
      io.grpc.stub.AbstractStub<DatasetServiceV1Stub, DatasetServiceV1ServiceDescriptor>
      implements DatasetServiceV1 {
    private DatasetServiceV1Stub(io.grpc.Channel channel,
        DatasetServiceV1ServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected DatasetServiceV1Stub build(io.grpc.Channel channel,
        DatasetServiceV1ServiceDescriptor config) {
      return new DatasetServiceV1Stub(channel, config);
    }

    @java.lang.Override
    public void listDatasets(com.google.genomics.v1.ListDatasetsRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.ListDatasetsResponse> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.listDatasets), request, responseObserver);
    }

    @java.lang.Override
    public void createDataset(com.google.genomics.v1.CreateDatasetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.createDataset), request, responseObserver);
    }

    @java.lang.Override
    public void getDataset(com.google.genomics.v1.GetDatasetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.getDataset), request, responseObserver);
    }

    @java.lang.Override
    public void updateDataset(com.google.genomics.v1.UpdateDatasetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.updateDataset), request, responseObserver);
    }

    @java.lang.Override
    public void deleteDataset(com.google.genomics.v1.DeleteDatasetRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.deleteDataset), request, responseObserver);
    }

    @java.lang.Override
    public void undeleteDataset(com.google.genomics.v1.UndeleteDatasetRequest request,
        io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver) {
      asyncUnaryCall(
          channel.newCall(config.undeleteDataset), request, responseObserver);
    }
  }

  public static class DatasetServiceV1BlockingStub extends
      io.grpc.stub.AbstractStub<DatasetServiceV1BlockingStub, DatasetServiceV1ServiceDescriptor>
      implements DatasetServiceV1BlockingClient {
    private DatasetServiceV1BlockingStub(io.grpc.Channel channel,
        DatasetServiceV1ServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected DatasetServiceV1BlockingStub build(io.grpc.Channel channel,
        DatasetServiceV1ServiceDescriptor config) {
      return new DatasetServiceV1BlockingStub(channel, config);
    }

    @java.lang.Override
    public com.google.genomics.v1.ListDatasetsResponse listDatasets(com.google.genomics.v1.ListDatasetsRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.listDatasets), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.Dataset createDataset(com.google.genomics.v1.CreateDatasetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.createDataset), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.Dataset getDataset(com.google.genomics.v1.GetDatasetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.getDataset), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.Dataset updateDataset(com.google.genomics.v1.UpdateDatasetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.updateDataset), request);
    }

    @java.lang.Override
    public com.google.protobuf.Empty deleteDataset(com.google.genomics.v1.DeleteDatasetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.deleteDataset), request);
    }

    @java.lang.Override
    public com.google.genomics.v1.Dataset undeleteDataset(com.google.genomics.v1.UndeleteDatasetRequest request) {
      return blockingUnaryCall(
          channel.newCall(config.undeleteDataset), request);
    }
  }

  public static class DatasetServiceV1FutureStub extends
      io.grpc.stub.AbstractStub<DatasetServiceV1FutureStub, DatasetServiceV1ServiceDescriptor>
      implements DatasetServiceV1FutureClient {
    private DatasetServiceV1FutureStub(io.grpc.Channel channel,
        DatasetServiceV1ServiceDescriptor config) {
      super(channel, config);
    }

    @java.lang.Override
    protected DatasetServiceV1FutureStub build(io.grpc.Channel channel,
        DatasetServiceV1ServiceDescriptor config) {
      return new DatasetServiceV1FutureStub(channel, config);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.ListDatasetsResponse> listDatasets(
        com.google.genomics.v1.ListDatasetsRequest request) {
      return unaryFutureCall(
          channel.newCall(config.listDatasets), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Dataset> createDataset(
        com.google.genomics.v1.CreateDatasetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.createDataset), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Dataset> getDataset(
        com.google.genomics.v1.GetDatasetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.getDataset), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Dataset> updateDataset(
        com.google.genomics.v1.UpdateDatasetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.updateDataset), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> deleteDataset(
        com.google.genomics.v1.DeleteDatasetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.deleteDataset), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.google.genomics.v1.Dataset> undeleteDataset(
        com.google.genomics.v1.UndeleteDatasetRequest request) {
      return unaryFutureCall(
          channel.newCall(config.undeleteDataset), request);
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final DatasetServiceV1 serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder("google.genomics.v1.DatasetServiceV1")
      .addMethod(createMethodDefinition(
          METHOD_LIST_DATASETS,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.ListDatasetsRequest,
                com.google.genomics.v1.ListDatasetsResponse>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.ListDatasetsRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.ListDatasetsResponse> responseObserver) {
                serviceImpl.listDatasets(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_CREATE_DATASET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.CreateDatasetRequest,
                com.google.genomics.v1.Dataset>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.CreateDatasetRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver) {
                serviceImpl.createDataset(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_GET_DATASET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.GetDatasetRequest,
                com.google.genomics.v1.Dataset>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.GetDatasetRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver) {
                serviceImpl.getDataset(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_UPDATE_DATASET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.UpdateDatasetRequest,
                com.google.genomics.v1.Dataset>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.UpdateDatasetRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver) {
                serviceImpl.updateDataset(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_DELETE_DATASET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.DeleteDatasetRequest,
                com.google.protobuf.Empty>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.DeleteDatasetRequest request,
                  io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
                serviceImpl.deleteDataset(request, responseObserver);
              }
            })))
      .addMethod(createMethodDefinition(
          METHOD_UNDELETE_DATASET,
          asyncUnaryRequestCall(
            new io.grpc.stub.ServerCalls.UnaryRequestMethod<
                com.google.genomics.v1.UndeleteDatasetRequest,
                com.google.genomics.v1.Dataset>() {
              @java.lang.Override
              public void invoke(
                  com.google.genomics.v1.UndeleteDatasetRequest request,
                  io.grpc.stub.StreamObserver<com.google.genomics.v1.Dataset> responseObserver) {
                serviceImpl.undeleteDataset(request, responseObserver);
              }
            }))).build();
  }
}
