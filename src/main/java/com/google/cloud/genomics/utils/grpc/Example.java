package com.google.cloud.genomics.utils.grpc;

import java.io.FileNotFoundException;
import java.util.Iterator;

import com.google.cloud.genomics.utils.GenomicsFactory;
import com.google.cloud.genomics.utils.GenomicsFactory.Builder;
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
    final String clientSecretsJson = "client_secrets.json";
    GenomicsFactory.OfflineAuth auth = null;
    try {
      Builder builder =
          GenomicsFactory.builder("gRPCExample");
      auth = builder.build().getOfflineAuthFromClientSecretsFile(clientSecretsJson);
    } catch (FileNotFoundException fex) {
      System.out.println("Expecting to find " + clientSecretsJson +
          " file in " + 
          "this directory and use it for authentication.\n" +
          "Please make sure your project is whitelisted for gRPC access and\n" +
          "generate and download JSON key for your service account.\n" +
          "You can do that in API & Auth section of the Developer Console.");
      return;
    }
    
    GenomicsChannel channel = GenomicsChannel.fromOfflineAuth(auth);

    // Regular RPC example: list all reference set assembly ids.
    ReferenceServiceV1BlockingStub refStub =
        ReferenceServiceV1Grpc.newBlockingStub(channel.getChannel());
    SearchReferenceSetsRequest request =
        SearchReferenceSetsRequest.newBuilder().build();
    SearchReferenceSetsResponse response = refStub.searchReferenceSets(request);
    for (ReferenceSet rs : response.getReferenceSetsList()) {
      System.out.println(rs.getAssemblyId());
    }

    // Streaming RPC example: request the variants within BRCA1 for the Platinum Genomes variant set.
    StreamingVariantServiceBlockingStub varStub =
        StreamingVariantServiceGrpc.newBlockingStub(channel.getChannel());
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
