package com.google.logsummarizer.gcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Parses the JSON lines in the GCP logs, and interprets common fields.
 */
public class GcpLogLine {

  static SimpleDateFormat GcpTimestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'H:m:s'Z'");

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

  public Date parseTimestamp() throws ParseException {
    GcpTimestampFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return GcpTimestampFormat.parse(timestamp);
  }

  public Date tryParseTimestamp() {
    try {
      return parseTimestamp();
    } catch (Exception x) {
      // indicate parsing error.
      return null;
    }
  }

}
