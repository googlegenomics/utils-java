package com.google.logsummarizer.core;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * Prints a summary table of how often each operation is called and how long it lasts.
 */
public class Summarize {

  private List<OperationInterval> intervals;

  private static class Record {
    long count;
    long totalSeconds;
    LocalDateTime begin, end;
    public void update(OperationInterval op) {
      count++;
      totalSeconds += Duration.between(op.getStart(), op.getEnd()).getSeconds();
      if (begin==null || op.getStart().isBefore(begin)) begin = op.getStart();
      if (end==null || end.isBefore(op.getEnd())) end = op.getEnd();
    }
    public void print(String title) {
      System.out.println(title+": ");
      long s= totalSeconds;
      String prettyDur = String.format("%dh%02dm%02ds", s / 3600, (s % 3600) / 60, (s % 60));
      s = Duration.between(begin, end).getSeconds();
      String prettySpan = String.format("%dh%02dm%02ds", s / 3600, (s % 3600) / 60, (s % 60));
      System.out.println("     " + prettySpan+" (span),     " + prettyDur + " (total),      " + count + " #,   " + (double) totalSeconds / (double) count + " s each");
    }
  }


  public Summarize(List<OperationInterval> intervals) {
    this.intervals = intervals;
  }

  /**
   * Prints a summary table of how often each operation is called and how long it lasts.
   */
  public void printTable() {
    HashMap<String,Record> stats = new HashMap<String, Record>();
    Record overall = new Record();
    for (OperationInterval op : intervals) {
      Record r;
      if (stats.containsKey(op.getGroup())) {
        r = stats.get(op.getGroup());
      } else {
        r = new Record();
      }
      r.update(op);
      stats.put(op.getGroup(), r);
      overall.update(op);
    }
    for (String k : stats.keySet()) {
      stats.get(k).print(k);
    }
    overall.print("OVERALL");
  }

}