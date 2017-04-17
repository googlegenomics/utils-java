package com.google.logsummarizer.textloggers;

import com.google.logsummarizer.core.ConcreteOperationInterval;
import com.google.logsummarizer.core.OperationInterval;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Reads the log of a bunny-enabled application running on Dataflow.
 * (that's the "worker-stdout" log).
 */
public class Log4jParser {

  static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
  static SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy.MM.dd-H:m:s'.'S");

  public static List<OperationInterval> parse(String fname)  throws Exception {

    final String bunny = "=[**]=";

    BufferedReader r = new BufferedReader(new FileReader(fname));
    String line;
    HashMap<String,ConcreteOperationInterval> intervals = new HashMap<String, ConcreteOperationInterval>();
    ArrayList<OperationInterval> ret = new ArrayList<OperationInterval>();

    String today = dateFormat.format(new Date());

    while ((line=r.readLine())!=null) {
      try {
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
          System.out.println("bunny syntax error (missing id) at line: "+line);
          continue;
        }
        String timestamp = p[0];

        Date when = timestampFormat.parse(today+"-"+timestamp);
        String id = p[bunnyIndex + 2];
        if (command.equals("START")) {
          StringBuilder label = new StringBuilder();
          for (int i = bunnyIndex + 3; i < p.length; i++) {
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
          interval.begin = when;
          interval.attributes.put("log", fname);
          interval.attributes.put("class", p[3]);
        }
        if (command.equals("END")) {
          // coded like this because we may see END before START
          // (if they happen on the same second)
          ConcreteOperationInterval interval = intervals.get(id);
          if (null==interval) {
            interval = new ConcreteOperationInterval();
            intervals.put(id, interval);
          }
          interval.end = when;
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
          Date start = interval.getLatestInnerPoint();
          if (null==start) {
            // stepend happens at the same second as start and comes first in the log.
            start = when;
          }
            ConcreteOperationInterval subOp = new ConcreteOperationInterval();
          subOp.begin = start;
          subOp.end = when;
          subOp.label = label.toString();
          interval.sub.add(subOp);
        }
      } catch (Exception x) {
        System.out.println("Parsing error: "+x.getMessage()+", continuing. The line was: "+line);
      }
    }
    for (ConcreteOperationInterval interval : intervals.values()) {
      if (interval.begin!=null && interval.end!=null) ret.add(interval);
    }
    return ret;
  }
}

