package com.google.logsummarizer.gcp;

import com.google.logsummarizer.core.ConcreteOperationInterval;
import com.google.logsummarizer.core.OperationInterval;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Reads the log of a bunny-enabled application running on Dataflow.
 * (that's the "worker-stdout" or "worker" log).
 */
public class DataflowParser {

  static SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'H:m:s'Z'");

  public static List<OperationInterval> parse(String fname, String jobId)  throws Exception {

    final String bunny = "=[**]=";

    BufferedReader r = new BufferedReader(new FileReader(fname));
    String lineJson;
    ObjectMapper mapper = new ObjectMapper();
    HashMap<String,ConcreteOperationInterval> intervals = new HashMap<String, ConcreteOperationInterval>();
    ArrayList<OperationInterval> ret = new ArrayList<OperationInterval>();

    while ((lineJson=r.readLine())!=null) {
      try {
        GcpLogLine lineParsed = new GcpLogLine(lineJson);
        if (jobId!=null && !lineParsed.jobId.equals(jobId)) {
          continue;
        }
        String line = lineParsed.line;
        String[] p = line.split(" ");
        int bunnyIndex = -1;
        for (int i = 0; i < p.length; i++) {
          if (p[i].equals(bunny)) {
            bunnyIndex = i;
            break;
          }
        }
        if (bunnyIndex < 0) continue;
        String command = p[bunnyIndex + 1];
        if (p.length <= bunnyIndex + 2) {
          // bunny syntax error
          System.out.println("bunny syntax error (missing id) at line: "+lineJson);
          continue;
        }
        String id = p[bunnyIndex + 2];
        Date when = lineParsed.tryParseTimestamp();
        if (command.equals("START")) {
          StringBuilder label = new StringBuilder();
          for (int i = bunnyIndex + 3; i < p.length; i++) {
            // hack to remove the "[blah blah blah] classname" that gets added to the line.
            if (p[i].startsWith("[")) break;
            label.append(p[i]);
            if (i + 1 < p.length) label.append(" ");
          }
          // coded like this because we may see END before START
          // (if they happen on the same second)
          ConcreteOperationInterval interval = intervals.get(id);
          if (null==interval) {
            interval = new ConcreteOperationInterval();
            intervals.put(id, interval);
          }
          interval.id = id;
          interval.label = label.toString();
          interval.group = interval.label;
          String timestamp = lineParsed.timestamp;
          interval.begin = when;
        }
        if (command.equals("END")) {
          // coded like this because we may see END before START
          // (if they happen on the same second)
          ConcreteOperationInterval interval = intervals.get(id);
          if (null==interval) {
            interval = new ConcreteOperationInterval();
            intervals.put(id, interval);
          }
          String timestamp = lineParsed.timestamp;
          interval.end = when;
          interval.attributes.put("machine", lineParsed.machine);
          interval.attributes.put("log", lineParsed.log);
        }
        if (command.equals("STEPEND")) {
          StringBuilder label = new StringBuilder();
          for (int i = bunnyIndex + 3; i < p.length; i++) {
            label.append(p[i]);
            if (i + 1 < p.length) label.append(" ");
          }
          ConcreteOperationInterval interval = intervals.get(id);
          if (null==interval) {
            interval = new ConcreteOperationInterval();
            intervals.put(id, interval);
          }
          String timestamp = lineParsed.timestamp;
          Date start = interval.getLatestInnerPoint();
          if (null==start) {
            // stepend happens at the same second as start and comes first in the log.
            start = when;
          }
            ConcreteOperationInterval subOp = new ConcreteOperationInterval();
          subOp.begin = start;
          subOp.end = when;
          subOp.label = label.toString();
          subOp.attributes.put("machine", lineParsed.machine);
          subOp.attributes.put("log", lineParsed.log);
          interval.sub.add(subOp);
        }
      } catch (Exception x) {
        System.out.println("Parsing error: "+x.getMessage()+", continuing. The line was: "+lineJson);
      }
    }
    for (ConcreteOperationInterval interval : intervals.values()) {
      if (interval.begin!=null && interval.end!=null) ret.add(interval);
    }
    return ret;
  }
}

