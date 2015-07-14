package com.google.logsummarizer.core;

import java.awt.*;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * List of OperationInterval -> a pretty picture
 */
public class Render {

  List<OperationInterval> intervals;
  List<Integer> ys;

  Color[] palette = new Color[]{
      new Color(0,188,212),
      new Color(33,150,243),
      new Color(139,195,74),
      new Color(255,152,0),
      new Color(103,58,183)
  };

  public Render(List<OperationInterval> intervals) {
    this.intervals = intervals;
    Collections.sort(this.intervals, new Comparator<OperationInterval>() {
      public int compare(OperationInterval o1, OperationInterval o2) {
        return o1.getStart().compareTo(o2.getStart());
      }
    });
    ys = IntervalLayout.computeYCoordinate(intervals);
  }

  private String getAttribute(OperationInterval iv, String attrib) {
    if ("label".equals(attrib)) return iv.getLabel();
    Map<String, Object> attributes = iv.getAttributes();
    if (!attributes.containsKey(attrib)) {
      return null;
    } else {
      return (String)attributes.get(attrib);
    }
  }

  /**
   * Each interval is represented by a box. They're laid out so they do not overlap.
   */
  public void toSvg(PrintStream out) {
    long barHeight = 20;
    long barVSpacing= 25;
    // color 0 goes to those that don't have that attribute.
    // then attributes get colors 1..n, round robin.
    String colorBy="label";
    HashMap<String,Integer> groupColor = new HashMap<String, Integer>();
    out.println("<?xml version=\"1.0\"?><!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
    out.println("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"100%\" height=\"100%\" style=\"overflow-x:scroll; overflow-y:scroll;\">");
    if (intervals.size()>0) {
      long lmargin = intervals.get(0).getStart().getTime()/1000;
      for (int i = 0; i < intervals.size(); i++) {
        OperationInterval iv = intervals.get(i);
        Color bg;
        String colorKey = getAttribute(iv,colorBy);
        if (null==colorKey) {
          bg = getColor(0);
        } else {
          if (!groupColor.containsKey(colorKey)) {
            groupColor.put(colorKey, groupColor.size());
          }
          bg = getColor(1+groupColor.get(colorKey));
        }

        long x = iv.getStart().getTime()/1000-lmargin;
        long y = ys.get(i) * barVSpacing;
        // if something starts and ends at the same time, we still give it a width of 1.
        long w = (iv.getEnd().getTime() - iv.getStart().getTime())/1000+1;
        out.println("<rect x=\"" + x + "\" y=\"" + y + "\" width=\"" + w + "\" height=\"" + barHeight + "\""
            + " style=\"fill:rgb("+bg.getRed()+","+bg.getGreen()+","+bg.getBlue()+");stroke-width:2;stroke:rgb(0,0,0)\"><title>"
            + iv.getLabel()
            + "</title></rect>");
        Color fg = bg.brighter();
        /*
        List<? extends OperationEvent> steps = iv.getSteps();
        if (null!=steps) {
          x = iv.getStart().toEpochSecond(ZoneOffset.UTC)-lmargin;
          for (OperationEvent step : steps) {
            long x2 = step.getTime().toEpochSecond(ZoneOffset.UTC)-lmargin;
            w = (x2-x);
            long radius = barHeight/2;
            out.println("<rect x=\"" + x + "\" y=\"" + y + "\" width=\"" + w + "\" height=\"" + barHeight + "\""
                + " rx=\"" + radius + "\" ry=\"" + radius + "\""
                +" style=\"fill:rgb("+fg.getRed()+","+fg.getGreen()+","+fg.getBlue()+");stroke-width:1;stroke:rgb(0,0,0)\"><title>"
                + step.getLabel()
                + "</title></rect>");
            x = x2;
          }
        }
        */
        Stack<OperationInterval> sub = new Stack<OperationInterval>();
        for (OperationInterval op : iv.getSubOperations()) sub.push(op);
        while (!sub.isEmpty()) {
          OperationInterval op = sub.pop();
          for (OperationInterval sop : op.getSubOperations()) sub.push(sop);
          long x2 = op.getStart().getTime()/1000-lmargin;
          // if something starts and ends at the same time, we still give it a width of 1.
          long w2 = (op.getEnd().getTime() - op.getStart().getTime())/1000+1;
          // clip to parent boundaries
          long right = x2+w2;
          if (x2<x) x2=x;
          if (right>x+w) right=x+w;
          w2 = (right-x2);
          long radius = barHeight/2;
          out.println("<rect x=\"" + x2 + "\" y=\"" + y + "\" width=\"" + w2 + "\" height=\"" + barHeight + "\""
              + " rx=\"" + radius + "\" ry=\"" + radius + "\""
              + " style=\"fill:rgb("+fg.getRed()+","+fg.getGreen()+","+fg.getBlue()+");stroke-width:2;stroke:rgb(0,0,0)\"><title>"
              + op.getLabel()
              + "</title></rect>");
        }

      }
    }
    out.println("</svg>");
  }

  private Color getColor(int index) {
    return palette[index%palette.length];
  }

}
