package com.google.logsummarizer.core;

import java.util.Date;
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
    Date begin, end;
    public void update(OperationInterval op) {
      count++;
      totalSeconds += (op.getEnd().getTime()-op.getStart().getTime())/1000;
      if (begin==null || op.getStart().compareTo(begin)<0) begin = op.getStart();
      if (end==null || end.compareTo(op.getEnd())<0) end = op.getEnd();
    }
    public void print(String title) {
      if (count==0) return;
      System.out.println(title+": ");
      long s= totalSeconds;
      String prettyDur = String.format("%dh%02dm%02ds", s / 3600, (s % 3600) / 60, (s % 60));
      s = (end.getTime()-begin.getTime())/1000;
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