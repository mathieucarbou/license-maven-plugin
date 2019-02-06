/*
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
package com.mycila.maven.plugin.license.svn;

import com.mycila.maven.plugin.license.AbstractLicenseMojo;
import com.mycila.maven.plugin.license.Credentials;
import com.mycila.maven.plugin.license.PropertiesProvider;
import com.mycila.maven.plugin.license.document.Document;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * An implementation of {@link PropertiesProvider} that uses SVN to retrieve
 * year information of last modification of files.
 *
 * @author Matthieu Brouillard
 */
public class SVNPropertiesProvider implements PropertiesProvider {
    ThreadLocal<SVNClientManager> svnClientThreadLocal = new ThreadLocal<SVNClientManager>();
    ThreadLocal<SimpleDateFormat> sdfTimestampThreadLocal = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
        }
    };

    public static final String SVN_COPYRIGHT_LASTCHANGE_YEAR_KEY = "license.svn.lastchange.year";
    public static final String SVN_COPYRIGHT_LASTCHANGE_DATE_KEY = "license.svn.lastchange.date";
    public static final String SVN_COPYRIGHT_LASTCHANGE_TIMESTAMP_KEY = "license.svn.lastchange.timestamp";
    public static final String SVN_COPYRIGHT_LASTCHANGE_REVISION_KEY = "license.svn.lastchange.revision";
    public static final String SVN_COPYRIGHT_YEARS_RANGE_KEY = "license.svn.years.range";
    
    public static final String INCEPTION_YEAR_KEY = "project.inceptionYear";

    public static final String SVN_SERVER_ID_PLUGIN_KEY = "license.svn.serverId";

    /**
     * Provides information on the given document. The information is put in the
     * returned map using: SVN_COPYRIGHT_LAST_YEAR_KEY the year of the latest
     * change detected for the given document file.
     *
     * @param mojo the current license maven plugin
     * @param currentProperties the default properties (without any plugin contributions)
     * @param document the document corresponding to the file for which we want to add properties
     * @return a non null Map containing the added entries
     */
    @Override
    public Map<String, String> getAdditionalProperties(final AbstractLicenseMojo mojo, Properties currentProperties, Document document) {
        final Map<String, String> newProperties = new HashMap<String, String>();
        final File documentFile = document.getFile();
        
        initThreadLocalObjects(mojo, currentProperties.getProperty(SVN_SERVER_ID_PLUGIN_KEY));
        
        SVNClientManager clientManager = svnClientThreadLocal.get();
        
        final String inceptionYear = currentProperties.getProperty(INCEPTION_YEAR_KEY);

        ISVNLogEntryHandler lastChangeDateLogEntryHandler = new ISVNLogEntryHandler() {
            @Override
            public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
                GregorianCalendar c = new GregorianCalendar();
                c.setTime(logEntry.getDate());
                
                final String timestamp = sdfTimestampThreadLocal.get().format(logEntry.getDate());
                final String year = timestamp.substring(0,4);
                
                newProperties.put(SVN_COPYRIGHT_LASTCHANGE_TIMESTAMP_KEY, timestamp);
                newProperties.put(SVN_COPYRIGHT_LASTCHANGE_DATE_KEY, timestamp.substring(0,8));
                newProperties.put(SVN_COPYRIGHT_LASTCHANGE_REVISION_KEY, ""+logEntry.getRevision());
                newProperties.put(SVN_COPYRIGHT_LASTCHANGE_YEAR_KEY, year);
                
                mojo.getLog().debug("found " + logEntry.getDate() + " as last modified date for file: " + documentFile);
                if (year.equals(inceptionYear) || inceptionYear == null) {
                    newProperties.put(SVN_COPYRIGHT_YEARS_RANGE_KEY, year);
                } else {
                    // a real range can be created
                    newProperties.put(SVN_COPYRIGHT_YEARS_RANGE_KEY, inceptionYear + "-" + year);
                }
            }
        };

        try {
            clientManager.getLogClient().doLog(new File[]{documentFile}, SVNRevision.HEAD, SVNRevision.create(0), true, true, 1, lastChangeDateLogEntryHandler);
        } catch (SVNException ex) {
            IllegalStateException ise = new IllegalStateException("cannot query SVN latest date information for file: " + documentFile, ex);
            throw ise;
        }

        return newProperties;
    }

    private void setSVNClientManager(AbstractLicenseMojo mojo, String svnServerID) {
        SVNClientManager c = svnClientThreadLocal.get();
        if (c == null) {
            Credentials svnCredentials = mojo.findCredentials(svnServerID);

            if (svnCredentials == null) {
                c = SVNClientManager.newInstance(new DefaultSVNOptions());
            } else {
                c = SVNClientManager.newInstance(new DefaultSVNOptions(), svnCredentials.getLogin(), svnCredentials.getPassword());
            }
            
            svnClientThreadLocal.set(c);
        }
    }

    private void initThreadLocalObjects(AbstractLicenseMojo mojo, String serverID) {
        setSVNClientManager(mojo, serverID);
    }
}
