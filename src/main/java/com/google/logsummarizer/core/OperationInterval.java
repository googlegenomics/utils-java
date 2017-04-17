package com.google.logsummarizer.core;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * An interval representing some operation that took some amount of time.
 */
public interface OperationInterval {
  /** When we started on this operation */
  public Date getStart();
  /** When we were done with this operation */
  public Date getEnd();
  /** What to call this interval on the GUI */
  public String getLabel();
  /** For the statistics, we aggregate together all the intervals that belong to the same group.
   */
  public String getGroup();
  /** Optionally, we can have key/value attributes associated with this operation.
   * These can be used in the GUI for filtering or highlighting.
   * This method should return a read-only map.
   */
  public Map<String,Object> getAttributes();
  // later we'll add intermediate events

  /**
   * An optional list of point events that happen during the operation.
   * The returned list shouldn't be modified.
   */
  public List<? extends OperationEvent> getEvents();

  /**
   * An optional list of sub-operations that happen during the operation.
   * The returned list shouldn't be modified.
   */
  public List<? extends OperationInterval> getSubOperations();

}
