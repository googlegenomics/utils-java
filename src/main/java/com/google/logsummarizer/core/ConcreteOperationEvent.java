package com.google.logsummarizer.core;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConcreteOperationEvent implements OperationEvent {
  public final Date when;
  public final String what;
  public final HashMap<String,Object> attributes;

  public ConcreteOperationEvent(Date when, String what) {
    this.when = when;
    this.what = what;
    attributes = new HashMap<String, Object>();
  }

  /** When we started on this operation */
  public Date getTime() {
    return when;
  }
  /** What to call this interval on the GUI */
  public String getLabel() {
    return what;
  }
  /** Optionally, we can have key/value attributes associated with this operation.
   * These can be used in the GUI for filtering or highlighting.
   * This method should return a read-only map.
   */
  public Map<String,Object> getAttributes() {
    return Collections.unmodifiableMap(attributes);
  }

}
