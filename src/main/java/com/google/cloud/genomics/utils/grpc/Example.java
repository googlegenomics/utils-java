package com.google.cloud.genomics.utils.grpc;

import io.grpc.ManagedChannel;

import java.util.Iterator;

import com.google.genomics.v1.ReferenceServiceV1Grpc;
import com.google.genomics.v1.ReferenceServiceV1Grpc.ReferenceServiceV1BlockingStub;
import com.google.genomics.v1.ReferenceSet;
import com.google.genomics.v1.SearchReferenceSetsRequest;
import com.google.genomics.v1.SearchReferenceSetsResponse;
import com.google.genomics.v1.StreamVariantsRequest;
import com.google.genomics.v1.StreamVariantsResponse;
import com.google.genomics.v1.StreamingVariantServiceGrpc;
import com.google.genomics.v1.StreamingVariantServiceGrpc.StreamingVariantServiceBlockingStub;

public class Example {

  public static void main(String[] args) throws Exception {
    ManagedChannel channel = GenomicsChannel.fromDefaultCreds();

    // Regular RPC example: list all reference set assembly ids.
    ReferenceServiceV1BlockingStub refStub =
        ReferenceServiceV1Grpc.newBlockingStub(channel);
    SearchReferenceSetsRequest request =
        SearchReferenceSetsRequest.newBuilder().build();
    SearchReferenceSetsResponse response = refStub.searchReferenceSets(request);
    for (ReferenceSet rs : response.getReferenceSetsList()) {
      System.out.println(rs.getAssemblyId());
    }

    // Streaming RPC example: request the variants within BRCA1 for the Platinum Genomes variant set.
    StreamingVariantServiceBlockingStub varStub =
        StreamingVariantServiceGrpc.newBlockingStub(channel);
    StreamVariantsRequest varRequest = StreamVariantsRequest.newBuilder()
        .setVariantSetId("3049512673186936334")
        .setReferenceName("chr17")
        .setStart(41196311)
        .setEnd(41277499)
        .build();

    try {
      Iterator<StreamVariantsResponse> iter = varStub.streamVariants(varRequest);
      while (iter.hasNext()) {
        StreamVariantsResponse varResponse = iter.next();
        System.out.println("Response:");
        System.out.println(varResponse.toString());
        System.out.println();
      }
      System.out.println("Done");
    } finally {
      channel.shutdownNow();
    }
  }
}
