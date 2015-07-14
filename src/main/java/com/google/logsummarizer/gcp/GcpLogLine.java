package com.google.logsummarizer.gcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 *
 */
public class GcpLogLine {

  public String line = "";
  public String timestamp = "";
  public String jobId = "";
  public String machine = "";
  public String log = "";

  public GcpLogLine(String lineJSON) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.readTree(lineJSON);
    JsonNode n = node.get("textPayload");
    if (null!=n) {
      // format 1
      line = n.asText();
    } else {
      // format 2
      line = node.get("structPayload").get("message").asText();
    }
    timestamp = node.get("metadata").get("timestamp").asText();
    jobId = node.get("metadata").get("labels").get("dataflow.googleapis.com/job_id").asText();
    machine = node.get("insertId").asText().split("[|]")[2];
    log = node.get("log").asText();
  }

}
