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

import com.mycila.maven.plugin.license.document.Document;
import com.mycila.maven.plugin.license.document.DocumentFactory;
import com.mycila.maven.plugin.license.document.DocumentType;
import com.mycila.maven.plugin.license.header.AdditionalHeaderDefinition;
import com.mycila.maven.plugin.license.header.Header;
import com.mycila.maven.plugin.license.header.HeaderDefinition;
import com.mycila.maven.plugin.license.header.HeaderType;
import com.mycila.maven.plugin.license.util.Selection;
import com.mycila.maven.plugin.license.util.resource.ResourceFinder;
import com.mycila.xmltool.XMLDoc;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.mycila.maven.plugin.license.document.DocumentType.defaultMapping;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.deepToString;

/**
 * <b>Date:</b> 18-Feb-2008<br> <b>Author:</b> Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public abstract class AbstractLicenseMojo extends AbstractMojo {

    /**
     * The base directory, in which to search for project files.
     *
     * @parameter expression="${license.basedir}" default-value="${basedir}"
     * @required
     */
    protected File basedir;

    /**
     * Location of the header. It can be a relative path, absolute path, classpath resource, any URL. The plugin first check if the name specified is a relative file, then an absolute file, then in the claspath. If not found, it tries to construct a URL from the location
     *
     * @parameter expression="${license.header}"
     */
    protected String header;

    /**
     * Specifies additional header files to use when checking for the presence of a valid header in your sources.
     * <br>
     * When using format goal, this property will be used to detect all valid headers that don't need formatting.
     * <br>
     * When using remove goal, this property will be used to detect all valid headers that also must be removed.
     *
     * @parameter
     */
    protected String[] validHeaders = new String[0];

    /**
     * Allows the use of external header definitions files. These files are properties like files.
     *
     * @parameter
     */
    protected String[] headerDefinitions = new String[0];

    /**
     * HeadSections define special regions of a header that allow for dynamic substitution and validation
     *
     * @parameter
     */
    protected HeaderSection[] headerSections = new HeaderSection[0];

    /**
     * You can set here some properties that you want to use when reading the header file. You can use in your header file some properties like ${year}, ${owner} or whatever you want for the name. They will be replaced when the header file is read by those you specified in the command line, in the POM and in system environment.
     *
     * @parameter
     */
    protected Map<String, String> properties = new HashMap<>();

    /**
     * Specifies files, which are included in the check. By default, all files are included.
     *
     * @parameter
     */
    protected String[] includes = new String[0];

    /**
     * Specifies files, which are excluded in the check. By default, only the files matching the default exclude patterns are excluded.
     *
     * @parameter
     */
    protected String[] excludes = new String[0];

    /**
     * Specify the list of keywords to use to detect a header. A header must include all keywords to be valid.
     * By default, the word 'copyright' is used. Detection is done case insensitive.
     *
     * @parameter
     */
    protected String[] keywords = new String[]{"copyright"};

    /**
     * Specify if you want to use default exclusions besides the files you have excluded. Default exclusions exclude CVS and SVN folders, IDE descriptors and so on.
     *
     * @parameter expression="${license.useDefaultExcludes}" default-value="true"
     */
    protected boolean useDefaultExcludes = true;

    /**
     * You can set this flag to true if you want to check the headers for all modules of your project. Only used for multi-modules projects, to check for example the header licenses from the parent module for all sub modules
     *
     * @parameter expression="${license.aggregate}" default-value="false"
     */
    protected boolean aggregate = false;

    /**
     * Set mapping between document mapping and a supported type to use. This section is very useful when you want to customize the supported extensions. Is your project is using file extensions not supported by default by this plugin, you can add a mapping to attach the extension to an existing type of comment. The tag name is the new extension name to support, and the value is the name of the comment type to use.
     *
     * @parameter
     */
    protected Map<String, String> mapping = new HashMap<String, String>();

    /**
     * Whether to use the default mapping between file extensions and comment types, or only the one your provide
     *
     * @parameter expression="${license.useDefaultMapping}" default-value="true"
     */
    protected boolean useDefaultMapping = true;

    /**
     * Maven license plugin uses concurrency to check license headers. This factor is used to control the number
     * of threads used to check. The rule is:
     * <br>
     * {@code <nThreads> = <number of cores> *  concurrencyFactor}
     * <br>
     * The default is 1.5.
     *
     * @parameter expression="${license.concurrencyFactor}" default-value="1.5"
     */
    protected float concurrencyFactor = 1.5f;

    /**
     * Whether to skip the plugin execution
     *
     * @parameter expression="${license.skip}" default-value="false"
     */
    protected boolean skip = false;

    /**
     * If you do not want to see the list of file having a missing header, you can add the quiet flag that will shorten the output
     *
     * @parameter expression="${license.quiet}" default-value="false"
     */
    protected boolean quiet = false;

    /**
     * Set to true if you need a strict check against the headers. By default, the existence of
     * a header is verified by taking the top portion of a file and checking if it contains the
     * headers text, not considering special characters (spaces, tabs, ...)
     * <br>
     * We highly recommend to set this option to true. It is by default set to false for
     * backward compatibility
     *
     * @parameter expression="${license.strictCheck}" default-value="false"
     */
    protected boolean strictCheck = true;

    /**
     * Specify the encoding of your files. Default to the project source encoding property (project.build.sourceEncoding)
     *
     * @parameter expression="${license.encoding}" default-value="${project.build.sourceEncoding}"
     */
    protected String encoding = "UTF-8";

    /**
     * You can set this flag to false if you do not want the build to fail when some headers are missing.
     *
     * @parameter expression="${license.failIfMissing}" default-value="true"
     */
    protected boolean failIfMissing = true;

    /**
     * If dryRun is enabled, calls to license:format and license:remove will not overwrite the existing file but instead write the result to a new file with the same name but ending with `.licensed`
     *
     * @parameter expression="${license.dryRun}" default-value="false"
     */
    protected boolean dryRun = false;

    /**
     * Skip the formatting of files which already contain a detected header
     *
     * @parameter expression="${license.skipExistingHeaders}" default-value="false"
     */
    protected boolean skipExistingHeaders = false;

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project = new MavenProject();

    private ResourceFinder finder;

    @SuppressWarnings({"unchecked"})
    protected final void execute(final Callback callback) throws MojoExecutionException, MojoFailureException {
        if (!skip) {
            if (header == null) {
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

            final Header h = new Header(finder.findResource(this.header), encoding, mergeProperties(), headerSections);
            debug("Header %s:\n%s", h.getLocation(), h);

            if (this.validHeaders == null) this.validHeaders = new String[0];
            final List<Header> validHeaders = new ArrayList<Header>(this.validHeaders.length);
            for (String validHeader : this.validHeaders)
                validHeaders.add(new Header(finder.findResource(validHeader), encoding, mergeProperties(), headerSections));

            final DocumentFactory documentFactory = new DocumentFactory(basedir, buildMapping(), buildHeaderDefinitions(), encoding, keywords);

            int nThreads = (int) (Runtime.getRuntime().availableProcessors() * concurrencyFactor);
            ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
            CompletionService completionService = new ExecutorCompletionService(executorService);
            int count = 0;
            debug("Number of execution threads: %s", nThreads);

            try {
                for (final String file : listSelectedFiles()) {
                    completionService.submit(new Runnable() {
                        public void run() {
                            Document document = documentFactory.createDocuments(file);
                            debug("Selected file: %s [header style: %s]", document.getFile(), document.getHeaderDefinition());
                            if (document.isNotSupported()) {
                                warn("Unknown file extension: %s", document.getFile());
                            } else if (document.is(h)) {
                                debug("Skipping header file: %s", document.getFile());
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
                                if (!headerFound)
                                    callback.onHeaderNotFound(document, h);
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
                        if (cause instanceof Error)
                            throw (Error) cause;
                        if (cause instanceof MojoExecutionException)
                            throw (MojoExecutionException) cause;
                        if (cause instanceof MojoFailureException)
                            throw (MojoFailureException) cause;
                        if (cause instanceof RuntimeException)
                            throw (RuntimeException) cause;
                        throw new RuntimeException(cause.getMessage(), cause);
                    }
                }

            } finally {
                executorService.shutdownNow();
            }
        }
    }

    protected final Map<String, String> mergeProperties() {
        // first put systen environment
        Map<String, String> props = new HashMap<String, String>(System.getenv());
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

    protected final String[] listSelectedFiles() throws MojoFailureException {
        Selection selection = new Selection(basedir, includes, buildExcludes(), useDefaultExcludes);
        debug("From: %s", basedir);
        debug("Including: %s", deepToString(selection.getIncluded()));
        debug("Excluding: %s", deepToString(selection.getExcluded()));
        return selection.getSelectedFiles();
    }

    @SuppressWarnings({"unchecked"})
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

    protected final void info(String format, Object... params) {
        if (!quiet) {
            getLog().info(format(format, params));
        }
    }

    protected final void debug(String format, Object... params) {
        if (!quiet) {
            getLog().debug(format(format, params));
        }
    }

    protected final void warn(String format, Object... params) {
        if (!quiet) {
            getLog().warn(format(format, params));
        }
    }

    private Map<String, String> buildMapping() {
        Map<String, String> extensionMapping = useDefaultMapping ? new HashMap<String, String>(defaultMapping()) : new HashMap<String, String>();
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            extensionMapping.put(entry.getKey().toLowerCase(), entry.getValue().toLowerCase());
        }
        // force inclusion of unknow item to manage unknown files
        extensionMapping.put(DocumentType.UNKNOWN.getExtension(), DocumentType.UNKNOWN.getDefaultHeaderTypeName());
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

}
