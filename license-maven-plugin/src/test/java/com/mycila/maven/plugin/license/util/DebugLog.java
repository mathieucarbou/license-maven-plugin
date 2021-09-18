package com.mycila.maven.plugin.license.util;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

/**
 * @author Mathieu Carbou
 */
public class DebugLog extends SystemStreamLog implements Log {
  @Override
  public boolean isDebugEnabled() {
    return true;
  }
}
