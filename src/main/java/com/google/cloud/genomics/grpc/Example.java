package com.google.cloud.genomics.grpc;

import com.google.genomics.v1.ReferenceServiceV1Grpc;
import com.google.genomics.v1.ReferenceServiceV1Grpc.ReferenceServiceV1BlockingStub;
import com.google.genomics.v1.ReferenceSet;
import com.google.genomics.v1.SearchReferenceSetsRequest;
import com.google.genomics.v1.SearchReferenceSetsResponse;
import com.google.genomics.v1.StreamVariantsRequest;
import com.google.genomics.v1.StreamVariantsResponse;
import com.google.genomics.v1.StreamingVariantServiceGrpc;
import com.google.genomics.v1.StreamingVariantServiceGrpc.StreamingVariantServiceBlockingStub;

import io.grpc.Channel;
import java.util.Iterator;

public class Example {
  public static void main(String[] args) throws Exception {
    Channel channel = Channels.fromDefaultCreds();

    // Regular RPC
    ReferenceServiceV1BlockingStub refStub =
        ReferenceServiceV1Grpc.newBlockingStub(channel);
    SearchReferenceSetsRequest request =
        SearchReferenceSetsRequest.newBuilder().build();
    SearchReferenceSetsResponse response = refStub.searchReferenceSets(request);
    for (ReferenceSet rs : response.getReferenceSetsList()) {
      System.out.println(rs.getId());
    }

    // Streaming RPC
    StreamingVariantServiceBlockingStub varStub =
        StreamingVariantServiceGrpc.newBlockingStub(channel);
    StreamVariantsRequest varRequest = StreamVariantsRequest.newBuilder()
        .setVariantSetId("10473108253681171589")
        .setReferenceName("17")
        .build();
    Iterator<StreamVariantsResponse> iter = varStub.streamVariants(varRequest);
    while (iter.hasNext()) {
      StreamVariantsResponse varResponse = iter.next();
      System.out.println("Response:");
      System.out.println(varResponse.toString());
      System.out.println();
    }
    System.out.println("Done");
  }
}
