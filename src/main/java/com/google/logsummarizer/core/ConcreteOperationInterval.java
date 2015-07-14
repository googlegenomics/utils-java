package com.google.logsummarizer.core;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ConcreteOperationInterval implements OperationInterval {
  public LocalDateTime begin, end;
  public String label;
  public String group;
  public HashMap<String,Object> attributes;
  public String id;
  public ArrayList<ConcreteOperationInterval> sub;

  public ConcreteOperationInterval() {
    attributes = new HashMap<String, Object>();
    sub = new ArrayList<ConcreteOperationInterval>();
  }

  /** When we started on this operation */
  public LocalDateTime getStart() {
    return begin;
  }
  /** When we were done with this operation */
  public LocalDateTime getEnd() {
    return end;
  }
  /** What to call this interval on the GUI */
  public String getLabel() {
    return label;
  }
  public String getGroup()  { return group; }

  public Map<String,Object> getAttributes() {
    return Collections.unmodifiableMap(attributes);
  }

  public List<? extends OperationInterval> getSubOperations() {
    return sub;
  }

  public List<? extends OperationEvent> getEvents() {
    // not yet supported
    return null;
  }

  // helper
  public LocalDateTime getLatestInnerPoint() {
    if (sub.isEmpty()) return begin;
    return sub.get(sub.size()-1).end;
  }

}
