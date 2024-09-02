/*
 * Copyright (C) 2008-2024 Mycila (mathieu.carbou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license.svn;

import com.mycila.maven.plugin.license.AbstractLicenseMojo;
import com.mycila.maven.plugin.license.Credentials;
import com.mycila.maven.plugin.license.PropertiesProvider;
import com.mycila.maven.plugin.license.document.Document;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An implementation of {@link PropertiesProvider} that uses SVN to retrieve
 * year information of last modification of files.
 */
public class SVNPropertiesProvider implements PropertiesProvider {

  public static final String SVN_COPYRIGHT_LASTCHANGE_YEAR_KEY = "license.svn.lastchange.year";
  public static final String SVN_COPYRIGHT_LASTCHANGE_DATE_KEY = "license.svn.lastchange.date";
  public static final String SVN_COPYRIGHT_LASTCHANGE_TIMESTAMP_KEY = "license.svn.lastchange.timestamp";
  public static final String SVN_COPYRIGHT_LASTCHANGE_REVISION_KEY = "license.svn.lastchange.revision";
  public static final String SVN_COPYRIGHT_YEARS_RANGE_KEY = "license.svn.years.range";
  public static final String INCEPTION_YEAR_KEY = "project.inceptionYear";
  public static final String SVN_SERVER_ID_PLUGIN_KEY = "license.svn.serverId";

  private Credentials svnCredentials;

  private final AtomicBoolean warnedIfShallow = new AtomicBoolean();
  private final Queue<SVNClientManager> clients = new ConcurrentLinkedQueue<>();
  private final ThreadLocal<SimpleDateFormat> sdfTimestampThreadLocal = ThreadLocal.withInitial(
      () -> new SimpleDateFormat("yyyyMMdd-HH:mm:ss"));
  private final ThreadLocal<SVNClientManager> svnClientThreadLocal = ThreadLocal.withInitial(() -> {
    SVNClientManager svnClientManager = svnCredentials == null ?
        SVNClientManager.newInstance(new DefaultSVNOptions()) :
        SVNClientManager.newInstance(new DefaultSVNOptions(), svnCredentials.getLogin(),
            svnCredentials.getPassword());
    clients.offer(svnClientManager);
    return svnClientManager;
  });

  @Override
  public void init(AbstractLicenseMojo mojo, Map<String, String> currentProperties) {
    svnCredentials = mojo.findCredentials(currentProperties.get(SVN_SERVER_ID_PLUGIN_KEY));
  }

  @Override
  public void close() {
    while (!clients.isEmpty()) {
      clients.poll().dispose();
    }
  }

  /**
   * Provides information on the given document. The information is put in the returned map using:
   * SVN_COPYRIGHT_LAST_YEAR_KEY the year of the latest change detected for the given document
   * file.
   *
   * @param mojo              the current license maven plugin
   * @param currentProperties the default properties (without any plugin contributions)
   * @param document          the document corresponding to the file for which we want to add
   *                          properties
   * @return a non null Map containing the added entries
   */
  @Override
  public Map<String, String> adjustProperties(final AbstractLicenseMojo mojo,
                                              Map<String, String> currentProperties, Document document) {
    final Map<String, String> newProperties = new HashMap<>();
    final File documentFile = document.getFile();
    final SVNClientManager svnClientManager = svnClientThreadLocal.get();

    ISVNLogEntryHandler lastChangeDateLogEntryHandler = new ISVNLogEntryHandler() {
      @Override
      public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(logEntry.getDate());

        final String timestamp = sdfTimestampThreadLocal.get().format(logEntry.getDate());
        final String year = timestamp.substring(0, 4);
        final String inceptionYear = currentProperties.get(INCEPTION_YEAR_KEY);

        newProperties.put(SVN_COPYRIGHT_LASTCHANGE_TIMESTAMP_KEY, timestamp);
        newProperties.put(SVN_COPYRIGHT_LASTCHANGE_DATE_KEY, timestamp.substring(0, 8));
        newProperties.put(SVN_COPYRIGHT_LASTCHANGE_REVISION_KEY, "" + logEntry.getRevision());
        newProperties.put(SVN_COPYRIGHT_LASTCHANGE_YEAR_KEY, year);

        if (mojo.getLog().isDebugEnabled()) {
          mojo.getLog().debug("found " + logEntry.getDate() + " as last modified date for file: " + documentFile);
        }

        if (year.equals(inceptionYear) || inceptionYear == null) {
          newProperties.put(SVN_COPYRIGHT_YEARS_RANGE_KEY, year);
        } else {
          // a real range can be created
          newProperties.put(SVN_COPYRIGHT_YEARS_RANGE_KEY, inceptionYear + "-" + year);
        }
      }
    };

    try {
      // One-time warning for shallow repo
      if (mojo.warnIfShallow && !warnedIfShallow.get()) {
        SVNInfo info = svnClientManager.getWCClient().doInfo(documentFile, SVNRevision.HEAD);
        if (info.getDepth() != SVNDepth.INFINITY && warnedIfShallow.compareAndSet(false, true)) {
          mojo.warn(
              "Sparse svn repository detected. Year property values may not be accurate.");
        }
      }

      svnClientManager.getLogClient()
          .doLog(new File[]{documentFile}, SVNRevision.HEAD, SVNRevision.create(0), true, true, 1,
              lastChangeDateLogEntryHandler);
    } catch (SVNException e) {
      throw new IllegalStateException("cannot query SVN latest date information for file: " + documentFile, e);
    }

    return newProperties;
  }
}
