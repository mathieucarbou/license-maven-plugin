/**
 * Copyright (C) 2008 Mycila (mathieu.carbou@gmail.com)
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
package com.mycila.maven.plugin.license;

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
final class MockedLog extends AbstractLogger {

    private final StringWriter sw = new StringWriter();
    private final PrintWriter pw = new PrintWriter(sw);
    private int logEntries;

    public MockedLog() {
        super(LEVEL_DISABLED, "MockedLogger");
    }

    @Override
    public void debug(String message, Throwable throwable) {
        log(message, throwable);
    }

    @Override
    public void info(String message, Throwable throwable) {
        log(message, throwable);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        log(message, throwable);
    }

    @Override
    public void error(String message, Throwable throwable) {
        log(message, throwable);
    }

    @Override
    public void fatalError(String message, Throwable throwable) {
        log(message, throwable);
    }

    @Override
    public Logger getChildLogger(String name) {
        return this;
    }

    private void log(String message, Throwable throwable) {
        logEntries++;
        pw.print(message);
        if(throwable != null) {
            throwable.printStackTrace(pw);
        }
    }

    public int getLogEntries() {
        return logEntries;
    }

    public void clear() {
        logEntries = 0;
        sw.getBuffer().setLength(0);
    }

    public String getContent() {
        return sw.toString();
    }
}
