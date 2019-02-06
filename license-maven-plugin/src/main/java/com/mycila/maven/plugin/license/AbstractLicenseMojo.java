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
package com.mycila.maven.plugin.license;

import com.mycila.maven.plugin.license.document.Document;
import com.mycila.maven.plugin.license.document.DocumentFactory;
import com.mycila.maven.plugin.license.document.DocumentPropertiesLoader;
import com.mycila.maven.plugin.license.document.DocumentType;
import com.mycila.maven.plugin.license.header.AdditionalHeaderDefinition;
import com.mycila.maven.plugin.license.header.Header;
import com.mycila.maven.plugin.license.header.HeaderDefinition;
import com.mycila.maven.plugin.license.header.HeaderSource;
import com.mycila.maven.plugin.license.header.HeaderType;
import com.mycila.maven.plugin.license.util.Selection;
import com.mycila.maven.plugin.license.util.resource.ResourceFinder;
import com.mycila.xmltool.XMLDoc;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mycila.maven.plugin.license.document.DocumentType.defaultMapping;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.deepToString;

/**
 * <b>Date:</b> 18-Feb-2008<br> <b>Author:</b> Mathieu Carbou
 * (mathieu.carbou@gmail.com)
 */
public abstract class AbstractLicenseMojo extends AbstractMojo {

    /**
     * The base directory, in which to search for project files.
     */
    @Parameter(property = "license.basedir", defaultValue = "${basedir}", required = true)
    public File basedir;

    /**
     * Location of the header. It can be a relative path, absolute path,
     * classpath resource, any URL. The plugin first check if the name specified
     * is a relative file, then an absolute file, then in the classpath. If not
     * found, it tries to construct a URL from the location.
     */
    @Parameter(property = "license.header")
    public String header;

    /**
     * Header, as text, directly in pom file. Using a CDATA section is strongly recommended.
     */
    @Parameter(property = "license.inlineHeader")
    public String inlineHeader;

    /**
     * Specifies additional header files to use when checking for the presence
     * of a valid header in your sources.
     * <br>
     * When using format goal, this property will be used to detect all valid
     * headers that don't need formatting.
     * <br>
     * When using remove goal, this property will be used to detect all valid
     * headers that also must be removed.
     */
    @Parameter
    public String[] validHeaders = new String[0];

    /**
     * Allows the use of external header definitions files. These files are
     * properties like files.
     */
    @Parameter
    public String[] headerDefinitions = new String[0];

    /**
     * HeadSections define special regions of a header that allow for dynamic
     * substitution and validation
     */
    @Parameter
    public HeaderSection[] headerSections = new HeaderSection[0];

    /**
     * You can set here some properties that you want to use when reading the
     * header file. You can use in your header file some properties like
     * ${year}, ${owner} or whatever you want for the name. They will be
     * replaced when the header file is read by those you specified in the
     * command line, in the POM and in system environment.
     */
    @Parameter
    public Map<String, String> properties = new HashMap<String, String>();

    /**
     * Specifies files, which are included in the check. By default, all files
     * are included.
     */
    @Parameter
    public String[] includes = new String[0];

    /**
     * Specifies files, which are excluded in the check. By default, only the
     * files matching the default exclude patterns are excluded.
     */
    @Parameter
    public String[] excludes = new String[0];

    /**
     * Specify the list of keywords to use to detect a header. A header must
     * include all keywords to be valid. By default, the word 'copyright' is
     * used. Detection is done case insensitive.
     */
    @Parameter
    public String[] keywords = new String[]{"copyright"};

    /**
     * Specify if you want to use default exclusions besides the files you have
     * excluded. Default exclusions exclude CVS and SVN folders, IDE descriptors
     * and so on.
     */
    @Parameter(property = "license.useDefaultExcludes", defaultValue = "true")
    public boolean useDefaultExcludes = true;

    /**
     * You can set this flag to true if you want to check the headers for all
     * modules of your project. Only used for multi-modules projects, to check
     * for example the header licenses from the parent module for all sub
     * modules.
     */
    @Parameter(property = "license.aggregate", defaultValue = "false")
    public boolean aggregate = false;

    /**
     * Set mapping between document mapping and a supported type to use. This
     * section is very useful when you want to customize the supported
     * extensions. If your project is using file extensions not supported by
     * default by this plugin, you can add a mapping to attach the extension to
     * an existing type of comment. The tag name is the new extension name to
     * support, and the value is the name of the comment type to use.
     */
    @Parameter
    public LinkedHashMap<String, String> mapping = new LinkedHashMap<String, String>();

    /**
     * Whether to use the default mapping between file extensions and comment
     * types, or only the one your provide.
     */
    @Parameter(property = "license.useDefaultMapping", defaultValue = "true")
    public boolean useDefaultMapping = true;

    /**
     * Maven license plugin uses concurrency to check license headers. This
     * factor is used to control the number of threads used to check. The rule
     * is:
     * <br> {@code <nThreads> = <number of cores> *  concurrencyFactor}
     * <br>
     * The default is 1.5.
     */
    @Parameter(property = "license.concurrencyFactor", defaultValue = "1.5")
    public float concurrencyFactor = 1.5f;


    /**
     * Maven license plugin uses concurrency to check license headers. With this
     * option the number of threads used to check can be specified. If given
     * it take precedence over <code>concurrencyFactor</code>
     *
     * The default is 0 which implies that the default for <code>concurrencyFactor</code>
     * is used.
     */
    @Parameter(property = "license.nThreads", defaultValue = "0")
    public int nThreads;

    /**
     * Whether to skip the plugin execution
     */
    @Parameter(property = "license.skip", defaultValue = "false")
    public boolean skip = false;

    /**
     * If you do not want to see the list of file having a missing header, you
     * can add the quiet flag that will shorten the output
     */
    @Parameter(property = "license.quiet", defaultValue = "false")
    public boolean quiet = false;

    /**
     * Set to true if you need a strict check against the headers. By default,
     * the existence of a header is verified by taking the top portion of a file
     * and checking if it contains the headers text, not considering special
     * characters (spaces, tabs, ...).
     * <br>
     * We highly recommend to keep this option set to {@code true}.
     */
    @Parameter(property = "license.strictCheck", defaultValue = "true")
    public boolean strictCheck = true;

    /**
     * Specify the encoding of your files. Default to the project source
     * encoding property (project.build.sourceEncoding).
     */
    @Parameter(property = "license.encoding", defaultValue = "${project.build.sourceEncoding}")
    public String encoding = "UTF-8";

    /**
     * You can set this flag to false if you do not want the build to fail when
     * some headers are missing.
     */
    @Parameter(property = "license.failIfMissing", defaultValue = "true")
    public boolean failIfMissing = true;

    /**
     * You can leave this flag on {@code false} if you do not want the build to
     * fail for files that do not have an implicit or explicit comment style
     * definition. Setting this explicitly to {@code true} is a safe way to make
     * sure that the effective file type mappings cover all files included from
     * your project.
     * <p>
     * Default is {@code false} for backwards compatibility reasons.
     *
     * @since 2.8
     */
    @Parameter(property = "license.failIfUnknown", defaultValue = "false")
    public boolean failIfUnknown = false;

    /**
     * If dryRun is enabled, calls to license:format and license:remove will not
     * overwrite the existing file but instead write the result to a new file
     * with the same name but ending with `.licensed`.
     */
    @Parameter(property = "license.dryRun", defaultValue = "false")
    public boolean dryRun = false;

    /**
     * Skip the formatting of files which already contain a detected header.
     */
    @Parameter(property = "license.skipExistingHeaders", defaultValue = "false")
    public boolean skipExistingHeaders = false;

    @Component
    public MavenProject project;

    /**
     * Maven settings.
     */
    @Component
    private Settings settings;
    /**
     * The decrypter for passwords.
     */
    @Component
    private SettingsDecrypter settingsDecrypter;

    private ResourceFinder finder;

    protected abstract class AbstractCallback implements Callback {

        /**
         * Related to {@link #failIfUnknown}.
         */
        private final Collection<File> unknownFiles = new ConcurrentLinkedQueue<File>();

        @Override
        public void onUnknownFile(Document document, Header header) {
            warn("Unknown file extension: %s", document.getFilePath());
            unknownFiles.add(document.getFile());
        }

        public void checkUnknown() throws MojoExecutionException {
            if (!unknownFiles.isEmpty()) {
                String msg = "Unable to find a comment style definition for some "
                        + "files. You may want to add a custom mapping for the relevant file extensions.";
                if (failIfUnknown) {
                    throw new MojoExecutionException(msg);
                }
                getLog().warn(msg);
            }
        }

    }

    @SuppressWarnings({"unchecked"})
    public final void execute(final Callback callback) throws MojoExecutionException, MojoFailureException {
        if (!skip) {
            if (header == null && (this.inlineHeader == null || this.inlineHeader.isEmpty())) {
                warn("No header file specified to check for license");
                return;
            }
            if (!strictCheck) {
                warn("Property 'strictCheck' is not enabled. Please consider adding <strictCheck>true</strictCheck> in your pom.xml file.");
                warn("See http://mycila.github.io/license-maven-plugin for more information.");
            }

            finder = new ResourceFinder(basedir);
            try {
                finder.setCompileClassPath(project.getCompileClasspathElements());
            } catch (DependencyResolutionRequiredException e) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
            finder.setPluginClassPath(getClass().getClassLoader());

            final HeaderSource headerSource = HeaderSource.of(this.inlineHeader, this.header, this.encoding, this.finder);
            final Header h = new Header(headerSource, headerSections);
            debug("Header: %s", h.getLocation());

            if (this.validHeaders == null) {
                this.validHeaders = new String[0];
            }
            final List<Header> validHeaders = new ArrayList<Header>(this.validHeaders.length);
            for (String validHeader : this.validHeaders) {
                final HeaderSource validHeaderSource = HeaderSource.of(null, validHeader, this.encoding, this.finder);
                validHeaders.add(new Header(validHeaderSource, headerSections));
            }

            final List<PropertiesProvider> propertiesProviders = new LinkedList<PropertiesProvider>();
            for (PropertiesProvider provider : ServiceLoader.load(PropertiesProvider.class, Thread.currentThread().getContextClassLoader())) {
                propertiesProviders.add(provider);
            }
            final DocumentPropertiesLoader propertiesLoader = new DocumentPropertiesLoader() {
                @Override
                public Properties load(Document document) {
                    Properties props = new Properties();

                    for (Map.Entry<String, String> entry : mergeProperties(document).entrySet()) {
                        if (entry.getValue() != null) {
                            props.setProperty(entry.getKey(), entry.getValue());
                        } else {
                            props.remove(entry.getKey());
                        }
                    }
                    for (PropertiesProvider provider : propertiesProviders) {
                        try {
                            final Map<String, String> providerProperties = provider.getAdditionalProperties(AbstractLicenseMojo.this, props, document);
                            if (getLog().isDebugEnabled()) {
                                getLog().debug("provider: " + provider.getClass() + " brought new properties\n" + providerProperties);
                            }
                            for (Map.Entry<String, String> entry : providerProperties.entrySet()) {
                                if (entry.getValue() != null) {
                                    props.setProperty(entry.getKey(), entry.getValue());
                                } else {
                                    props.remove(entry.getKey());
                                }
                            }
                        } catch (Exception e) {
                            getLog().warn("failure occured while calling " + provider.getClass(), e);
                        }
                    }
                    return props;
                }
            };

            final DocumentFactory documentFactory = new DocumentFactory(basedir, buildMapping(), buildHeaderDefinitions(), encoding, keywords, propertiesLoader);

            int nThreads = getNumberOfExecutorThreads();
            ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
            CompletionService completionService = new ExecutorCompletionService(executorService);
            int count = 0;
            debug("Number of execution threads: %s", nThreads);

            try {
                for (final String file : listSelectedFiles()) {
                    completionService.submit(new Runnable() {
                        @Override
                        public void run() {
                            Document document = documentFactory.createDocuments(file);
                            debug("Selected file: %s [header style: %s]", document.getFilePath(), document.getHeaderDefinition());
                            if (document.isNotSupported()) {
                                callback.onUnknownFile(document, h);
                            } else if (document.is(h)) {
                                debug("Skipping header file: %s", document.getFilePath());
                            } else if (document.hasHeader(h, strictCheck)) {
                                callback.onExistingHeader(document, h);
                            } else {
                                boolean headerFound = false;
                                for (Header validHeader : validHeaders) {
                                    if (headerFound = document.hasHeader(validHeader, strictCheck)) {
                                        callback.onExistingHeader(document, h);
                                        break;
                                    }
                                }
                                if (!headerFound) {
                                    callback.onHeaderNotFound(document, h);
                                }
                            }
                        }
                    }, null);
                    count++;
                }

                while (count-- > 0) {
                    try {
                        completionService.take().get();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (ExecutionException e) {
                        Throwable cause = e.getCause();
                        if (cause instanceof Error) {
                            throw (Error) cause;
                        }
                        if (cause instanceof MojoExecutionException) {
                            throw (MojoExecutionException) cause;
                        }
                        if (cause instanceof MojoFailureException) {
                            throw (MojoFailureException) cause;
                        }
                        if (cause instanceof RuntimeException) {
                            throw (RuntimeException) cause;
                        }
                        throw new RuntimeException(cause.getMessage(), cause);
                    }
                }

            } finally {
                executorService.shutdownNow();
            }
        }
    }

    private int getNumberOfExecutorThreads() {
        return nThreads > 0 ?
            nThreads :
            Math.max(1, (int) (Runtime.getRuntime().availableProcessors() * concurrencyFactor));
    }

    private Map<String, String> mergeProperties(Document document) {
        // first put systen environment
        Map<String, String> props = new LinkedHashMap<String, String>(System.getenv());
        // then add ${project.XYZ} properties
        props.put("project.groupId", project.getGroupId());
        props.put("project.artifactId", project.getArtifactId());
        props.put("project.version", project.getVersion());
        props.put("project.name", project.getName());
        props.put("project.description", project.getDescription());
        props.put("project.inceptionYear", project.getInceptionYear());
        props.put("project.url", project.getUrl());
        // then add per document properties
        props.put("file.name", document.getFile().getName());
        // we override by properties in the POM
        if (this.properties != null) {
            props.putAll(this.properties);
        }
        // then we override by java system properties (command-line -D...)
        for (String key : System.getProperties().stringPropertyNames()) {
            props.put(key, System.getProperty(key));
        }
        return props;
    }

    private String[] listSelectedFiles() {
        Selection selection = new Selection(basedir, includes, buildExcludes(), useDefaultExcludes);
        debug("From: %s", basedir);
        debug("Including: %s", deepToString(selection.getIncluded()));
        debug("Excluding: %s", deepToString(selection.getExcluded()));
        return selection.getSelectedFiles();
    }

    private String[] buildExcludes() {
        List<String> ex = new ArrayList<String>();
        ex.addAll(asList(this.excludes));
        if (project != null && project.getModules() != null && !aggregate) {
            for (String module : (List<String>) project.getModules()) {
                ex.add(module + "/**");
            }
        }
        return ex.toArray(new String[ex.size()]);
    }

    public final void info(String format, Object... params) {
        if (!quiet) {
            getLog().info(format(format, params));
        }
    }

    public final void debug(String format, Object... params) {
        if (!quiet) {
            getLog().debug(format(format, params));
        }
    }

    public final void warn(String format, Object... params) {
        if (!quiet) {
            getLog().warn(format(format, params));
        }
    }

    private Map<String, String> buildMapping() {
        Map<String, String> extensionMapping = new LinkedHashMap<String, String>();
        // force inclusion of unknow item to manage unknown files
        extensionMapping.put(DocumentType.UNKNOWN.getExtension(), DocumentType.UNKNOWN.getDefaultHeaderTypeName());
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            extensionMapping.put(entry.getKey().toLowerCase(), entry.getValue().toLowerCase());
        }
        if (useDefaultMapping) {
            for (Map.Entry<String, String> entry : defaultMapping().entrySet()) {
                if (!extensionMapping.containsKey(entry.getKey())) {
                    extensionMapping.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return extensionMapping;
    }

    private Map<String, HeaderDefinition> buildHeaderDefinitions() throws MojoFailureException {
        // like mappings, first get default definitions
        final Map<String, HeaderDefinition> headers = new HashMap<String, HeaderDefinition>(HeaderType.defaultDefinitions());
        // and then override them with those provided in properties file
        for (String resource : headerDefinitions) {
            try {
                InputSource source = new InputSource(finder.findResource(resource).openStream());
                source.setEncoding(encoding);
                final AdditionalHeaderDefinition fileDefinitions = new AdditionalHeaderDefinition(XMLDoc.from(source, true));
                final Map<String, HeaderDefinition> map = fileDefinitions.getDefinitions();
                debug("%d header definitions loaded from '%s'", map.size(), resource);
                headers.putAll(map);
            } catch (IOException ex) {
                throw new MojoFailureException("Error reading header definition: " + resource, ex);
            }
        }
        // force inclusion of unknow item to manage unknown files
        headers.put(HeaderType.UNKNOWN.getDefinition().getType(), HeaderType.UNKNOWN.getDefinition());
        return headers;
    }

    /**
     * Returns the list of servers with decrypted passwords.
     *
     * @return list of servers with decrypted passwords.
     */
    List<Server> getDecryptedServers() {
        final SettingsDecryptionRequest settingsDecryptionRequest = new DefaultSettingsDecryptionRequest();
        settingsDecryptionRequest.setServers(settings.getServers());
        final SettingsDecryptionResult decrypt = settingsDecrypter.decrypt(settingsDecryptionRequest);
        return decrypt.getServers();
    }

    /**
     * Retrieves the credentials for the given server or null if none could be
     * found
     *
     * @param serverID
     * @return
     */
    public Credentials findCredentials(String serverID) {
        List<Server> decryptedServers = getDecryptedServers();

        for (Server ds : decryptedServers) {
            if (ds.getId().equals(serverID)) {
                getLog().debug("credentials have been found for server: " + serverID + ", login:" + ds.getUsername() + ", password:" + starEncrypt(ds.getPassword()));
                return new Credentials(ds.getUsername(), ds.getPassword());
            }
        }

        getLog().debug("no credentials found for server: " + serverID);
        return null;
    }

    static String starEncrypt(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll(".", "*");
    }
}
