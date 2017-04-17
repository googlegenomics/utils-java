package com.google.logsummarizer.core;

import java.util.Date;
import java.util.Map;

/**
 * An event representing some particular point during an operation's execution
 */
public interface OperationEvent {
  /** When */
  public Date getTime();
  /** What */
  public String getLabel();
  /** Optionally, we can have key/value attributes associated with this operation.
   * These can be used in the GUI for filtering or highlighting.
   * This method should return a read-only map.
   */
  public Map<String,Object> getAttributes();
}
