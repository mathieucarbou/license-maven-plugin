/*
 * Copyright (C) 2008-2021 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class for executing on the command line and returning the result of
 * execution.
 */
public final class ExecutingCommand {

  private static final boolean IS_WINDOWS = System.getProperty("os.name").startsWith("Windows");
  private static final boolean IS_SOLARIS = !IS_WINDOWS && System.getProperty("os.name").startsWith("SunOS")
      || System.getProperty("os.name").startsWith("Solaris");
  private static final String[] DEFAULT_ENV = getDefaultEnv();

  private ExecutingCommand() {
  }

  private static String[] getDefaultEnv() {
    if (IS_WINDOWS) {
      return new String[] { "LANGUAGE=C" };
    } else {
      return new String[] { "LC_ALL=C" };
    }
  }

  /**
   * Executes a command on the native command line and returns the result. This is
   * a convenience method to call {@link java.lang.Runtime#exec(String)} and
   * capture the resulting output in a list of Strings. On Windows, built-in
   * commands not associated with an executable program may require
   * {@code cmd.exe /c} to be prepended to the command.
   *
   * @param cmdToRun
   *          Command to run
   * @return A list of Strings representing the result of the command, or empty
   *         list if the command failed
   */
  public static List<String> runNative(String cmdToRun) {
    String[] cmd = cmdToRun.split(" ");
    return runNative(cmd);
  }

  /**
   * Executes a command on the native command line and returns the result line by
   * line. This is a convenience method to call
   * {@link java.lang.Runtime#exec(String[])} and capture the resulting output in
   * a list of Strings. On Windows, built-in commands not associated with an
   * executable program may require the strings {@code cmd.exe} and {@code /c} to
   * be prepended to the array.
   *
   * @param cmdToRunWithArgs
   *          Command to run and args, in an array
   * @return A list of Strings representing the result of the command, or empty
   *         list if the command failed
   */
  public static List<String> runNative(String[] cmdToRunWithArgs) {
    return runNative(cmdToRunWithArgs, DEFAULT_ENV);
  }

  /**
   * Executes a command on the native command line and returns the result line by
   * line. This is a convenience method to call
   * {@link java.lang.Runtime#exec(String[])} and capture the resulting output in
   * a list of Strings. On Windows, built-in commands not associated with an
   * executable program may require the strings {@code cmd.exe} and {@code /c} to
   * be prepended to the array.
   *
   * @param cmdToRunWithArgs
   *          Command to run and args, in an array
   * @param envp
   *          array of strings, each element of which has environment variable
   *          settings in the format name=value, or null if the subprocess should
   *          inherit the environment of the current process.
   * @return A list of Strings representing the result of the command, or empty
   *         list if the command failed
   */
  public static List<String> runNative(String[] cmdToRunWithArgs, String[] envp) {
    Process p = null;
    try {
      p = Runtime.getRuntime().exec(cmdToRunWithArgs, envp);
      return getProcessOutput(p, cmdToRunWithArgs);
    } catch (SecurityException | IOException e) {
      return Collections.emptyList();
    } finally {
      // Ensure all resources are released if exec fails
      if (p != null) {
        // Windows and Solaris don't close descriptors on destroy,
        // so we must handle separately
        if (IS_WINDOWS || IS_SOLARIS) {
          try {
            p.getOutputStream().close();
          } catch (IOException e) {
            // do nothing on failure
          }
          try {
            p.getInputStream().close();
          } catch (IOException e) {
            // do nothing on failure
          }
          try {
            p.getErrorStream().close();
          } catch (IOException e) {
            // do nothing on failure
          }
        }
        p.destroy();
      }
    }
  }

  private static List<String> getProcessOutput(Process p, String[] cmd) {
    ArrayList<String> sa = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(p.getInputStream(), Charset.defaultCharset()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        sa.add(line);
      }
      p.waitFor();
    } catch (IOException e) {
      return Collections.emptyList();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
    return sa;
  }
}
