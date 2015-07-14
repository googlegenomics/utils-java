package com.google.logsummarizer.core;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * An interval representing some operation that took some amount of time.
 */
public interface OperationEvent {
  /** When */
  public LocalDateTime getTime();
  /** What */
  public String getLabel();
  /** Optionally, we can have key/value attributes associated with this operation.
   * These can be used in the GUI for filtering or highlighting.
   * This method should return a read-only map.
   */
  public Map<String,Object> getAttributes();
}
