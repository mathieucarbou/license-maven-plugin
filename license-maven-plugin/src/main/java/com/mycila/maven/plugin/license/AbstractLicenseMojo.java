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
package com.mycila.maven.plugin.license;

import com.mycila.maven.plugin.license.dependencies.LicenseMessage;
import com.mycila.maven.plugin.license.dependencies.LicensePolicy;
import com.mycila.maven.plugin.license.document.Document;
import com.mycila.maven.plugin.license.document.DocumentFactory;
import com.mycila.maven.plugin.license.document.DocumentPropertiesLoader;
import com.mycila.maven.plugin.license.document.DocumentType;
import com.mycila.maven.plugin.license.header.AdditionalHeaderDefinition;
import com.mycila.maven.plugin.license.header.Header;
import com.mycila.maven.plugin.license.header.HeaderDefinition;
import com.mycila.maven.plugin.license.header.HeaderSource;
import com.mycila.maven.plugin.license.header.HeaderType;
import com.mycila.maven.plugin.license.util.FileUtils;
import com.mycila.maven.plugin.license.util.LazyMap;
import com.mycila.maven.plugin.license.util.Selection;
import com.mycila.maven.plugin.license.util.resource.ResourceFinder;
import com.mycila.xmltool.XMLDoc;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Organization;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.inject.Inject;

import static com.mycila.maven.plugin.license.document.DocumentType.defaultMapping;
import static com.mycila.maven.plugin.license.util.FileUtils.asPath;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.deepToString;

public abstract class AbstractLicenseMojo extends AbstractMojo {

  private static final String[] DEFAULT_KEYWORDS = {"copyright"};

  @Parameter(property = "license.workspace", alias = "workspace")
  public WorkSpace workspace = new WorkSpace();


  @Parameter(property = "license.licenseSets", alias = "licenseSets")
  public LicenseSet[] licenseSets;

  /**
   * The base directory, in which to search for project files.
   * <p>
   * This is named `defaultBaseDirectory` as it will be used as the default
   * value for the base directory. This default value can be overridden
   * in each LicenseSet by setting {@link LicenseSet#basedir}.
   *
   * @deprecated use {@link WorkSpace#basedir}
   */
  @Deprecated
  @Parameter(property = "license.basedir", defaultValue = "${project.basedir}", alias = "basedir", required = true)
  public File legacyDefaultBasedir;

  /**
   * Location of the header. It can be a relative path, absolute path,
   * classpath resource, any URL. The plugin first check if the name specified
   * is a relative file, then an absolute file, then in the classpath. If not
   * found, it tries to construct a URL from the location.
   *
   * @deprecated use {@link LicenseSet#header}
   */
  @Deprecated
  @Parameter(property = "license.header", alias = "header")
  public String legacyConfigHeader;

  /**
   * Header, as text, directly in pom file. Using a CDATA section is strongly recommended.
   *
   * @deprecated use {@link LicenseSet#inlineHeader}
   */
  @Deprecated
  @Parameter(property = "license.inlineHeader", alias = "inlineHeader")
  public String legacyConfigInlineHeader;

  /**
   * Specifies additional header files to use when checking for the presence
   * of a valid header in your sources.
   * <br>
   * When using format goal, this property will be used to detect all valid
   * headers that don't need formatting.
   * <br>
   * When using remove goal, this property will be used to detect all valid
   * headers that also must be removed.
   *
   * @deprecated use {@link LicenseSet#validHeaders}
   */
  @Deprecated
  @Parameter(alias = "validHeaders")
  public String[] legacyConfigValidHeaders = new String[0];

  /**
   * Alternative to `header`, `inlineHeader`, or `validHeaders`
   * for use when code is multi-licensed.
   * Whilst you could create a concatenated header yourself,
   * a cleaner approach may be to specify more than one header
   * and have them concatenated together by the plugin. This
   * allows you to maintain each distinct license header in
   * its own file and combined them in different ways.
   *
   * @deprecated use {@link LicenseSet#multi}
   */
  @Deprecated
  @Parameter
  public Multi legacyConfigMulti;

  /**
   * Allows the use of external header definitions files. These files are
   * properties like files.
   * <p>
   * This is named `defaultHeaderDefinitions` as it will be used as the default
   * value for the header definitions. This default value can be overridden
   * in each LicenseSet by setting {@link LicenseSet#headerDefinitions}  or
   * {@link LicenseSet#inlineHeaderStyles} and is overridden by {@link #defaultInlineHeaderStyles}.
   */
  @Parameter(alias = "headerDefinitions")
  public String[] defaultHeaderDefinitions = new String[0];

  /**
   * Allows the use of inline header definitions.
   * <p>
   * This is named `defaultInlineHeaderStyles` as it will be used as the default
   * value for the header definitions.
   * <p>
   * This default value can be overridden
   * in each LicenseSet by setting {@link LicenseSet#headerDefinitions} or {@link LicenseSet#inlineHeaderStyles}.
   * <p>
   * Inline styles overrides those read from file
   */
  @Parameter
  public HeaderStyle[] defaultInlineHeaderStyles = new HeaderStyle[0];

  /**
   * HeadSections define special regions of a header that allow for dynamic
   * substitution and validation.
   *
   * @deprecated use {@link LicenseSet#headerSections}
   */
  @Deprecated
  @Parameter(alias = "headerSections")
  public HeaderSection[] legacyConfigHeaderSections = new HeaderSection[0];

  /**
   * You can set here some properties that you want to use when reading the
   * header file. You can use in your header file some properties like
   * ${year}, ${owner} or whatever you want for the name. They will be
   * replaced when the header file is read by those you specified in the
   * command line, in the POM and in system environment.
   * <p>
   * This is named `defaultProperties` as it will be used as the default
   * value for the properties. This default value can be overridden
   * in each LicenseSet by setting {@link LicenseSet#properties}.
   */
  @Parameter(alias = "properties")
  public Map<String, String> defaultProperties = new HashMap<>();

  /**
   * Specifies files, which are included in the check. By default, all files
   * are included.
   *
   * @deprecated use {@link LicenseSet#includes}
   */
  @Deprecated
  @Parameter(alias = "includes", property = "license.includes")
  public String[] legacyConfigIncludes = new String[0];

  /**
   * Specifies files, which are excluded in the check. By default, only the
   * files matching the default exclude patterns are excluded.
   *
   * @deprecated use {@link LicenseSet#excludes}
   */
  @Deprecated
  @Parameter(alias = "excludes", property = "license.excludes")
  public String[] legacyConfigExcludes = new String[0];

  /**
   * Specify the list of keywords to use to detect a header. A header must
   * include all keywords to be valid. By default, the word 'copyright' is
   * used. Detection is done case insensitive.
   *
   * @deprecated use {@link LicenseSet#keywords}
   */
  @Deprecated
  @Parameter(alias = "keywords")
  public String[] legacyConfigKeywords = DEFAULT_KEYWORDS;

  /**
   * Specify if you want to use default exclusions besides the files you have
   * excluded. Default exclusions exclude CVS and SVN folders, IDE descriptors
   * and so on.
   * <p>
   * This is named `defaultUseDefaultExcludes` as it will be used as the default
   * value for whether to use default excludes. This default value can be overridden
   * in each LicenseSet by setting {@link LicenseSet#useDefaultExcludes}.
   */
  @Parameter(property = "license.useDefaultExcludes", defaultValue = "true", alias = "useDefaultExcludes")
  public boolean defaultUseDefaultExcludes = true;

  /**
   * You can set this flag to true if you want to check the headers for all
   * modules of your project. Only used for multi-modules projects, to check
   * for example the header licenses from the parent module for all sub
   * modules.
   */
  @Parameter(property = "license.aggregate", defaultValue = "false")
  public boolean aggregate;

  /**
   * Set mapping between document mapping and a supported type to use. This
   * section is very useful when you want to customize the supported
   * extensions. If your project is using file extensions not supported by
   * default by this plugin, you can add a mapping to attach the extension to
   * an existing type of comment. The tag name is the new extension name to
   * support, and the value is the name of the comment type to use.
   */
  @Parameter
  public Map<String, String> mapping = new LinkedHashMap<>();

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
   * <p>
   * The default is 0 which implies that the default for <code>concurrencyFactor</code>
   * is used.
   */
  @Parameter(property = "license.nThreads", defaultValue = "0")
  public int nThreads;

  /** Whether to skip the plugin execution. */
  @Parameter(property = "license.skip", defaultValue = "false")
  public boolean skip;

  /**
   * Determination of the year and author of the first commit and last change year
   * of a file requires a full git or svn history. By default the plugin will log
   * warning when using these properties on a shallow or sparse repository. If you
   * are certain the repository depth will permit accurate determination of these
   * values, you can disable this check.
   */
  @Parameter(property = "license.warnIfShallow", defaultValue = "true")
  public boolean warnIfShallow = true;

  /** If you do not want to see the list of file having a missing header, you can add the quiet flag that will shorten the output. */
  @Parameter(property = "license.quiet", defaultValue = "false")
  public boolean quiet;

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
  public boolean failIfUnknown;

  /**
   * If dryRun is enabled, calls to license:format and license:remove will not
   * overwrite the existing file but instead write the result to a new file
   * with the same name but ending with `.licensed`.
   */
  @Parameter(property = "license.dryRun", defaultValue = "false")
  public boolean dryRun;

  /**
   * Skip the formatting of files which already contain a detected header.
   */
  @Parameter(property = "license.skipExistingHeaders", defaultValue = "false")
  public boolean skipExistingHeaders;

  /**
   * When enforcing licenses on dependencies, exclude all but these scopes.
   */
  @Parameter(property = "license.dependencies.scope", required = true, defaultValue = "runtime")
  protected List<String> dependencyScopes;

  /**
   * Whether to enforce license.dependencies.allow list.
   */
  @Parameter(property = "license.dependencies.enforce", required = true, defaultValue = "false")
  protected boolean dependencyEnforce;

  /**
   * Block of {@link LicensePolicy} configuration for enforcing license adherence in dependencies.
   */
  @Parameter(property = "license.dependencies.policies")
  protected Set<LicensePolicy> dependencyPolicies;

  /**
   * Exception message prefix to display when an artifact is denied by one of the license policies.
   */
  @Parameter(property = "license.dependencies.exceptionMessage", required = true, defaultValue = LicenseMessage.WARN_POLICY_DENIED)
  protected String dependencyExceptionMessage;


  @Parameter(defaultValue = "${project}", required = true)
  protected MavenProject project;

  /**
   * Maven settings.
   */
  @Parameter(defaultValue = "${settings}", readonly = true)
  private Settings settings;
  /**
   * The decrypter for passwords.
   */
  @Inject
  private SettingsDecrypter settingsDecrypter;

  @Inject
  protected DependencyGraphBuilder dependencyGraphBuilder;

  @Inject
  protected ProjectBuilder projectBuilder;

  @Parameter(defaultValue = "${session}")
  public MavenSession session;

  /**
   * The location where to write the report of the plugin execution (file processed, action taken, etc).
   * <p>
   * "PRESENT" means the file has a header (check goal)
   * <p>
   * "MISSING" means the header is missing (check goal)
   * <p>
   * "NOOP" means no action were performed (remove or format goal)
   * <p>
   * "ADDED" means a header was added (format goal)
   * <p>
   * "REPLACED" means a header was replaced (format goal)
   * <p>
   * "REMOVED" means a header was removed (format goal)
   * <p>
   * "UNKNOWN" means that the file extension is unknown
   * <p>
   * Activated by default.
   */
  @Parameter(property = "license.report.location", defaultValue = "${project.reporting.outputDirectory}/license-plugin-report.xml")
  public File reportLocation;

  /**
   * Format of the report.
   * <p>
   * Can be (case-insensitive): 'xml', 'json'.
   * <p>
   * Default is XML.
   */
  @Parameter(property = "license.report.format")
  public String reportFormat;

  /**
   * Skip the report generation. Default: false
   */
  @Parameter(property = "license.report.skip", defaultValue = "false")
  public boolean reportSkipped;

  @Parameter(property = "license.prohibitLegacyUse", defaultValue = "false")
  public boolean prohibitLegacyUse;

  protected Clock clock = Clock.systemUTC();
  protected Report report;

  protected abstract class AbstractCallback implements Callback {

    /**
     * Related to {@link #failIfUnknown}.
     */
    private final Collection<File> unknownFiles = new ConcurrentLinkedQueue<>();

    @Override
    public void onUnknownFile(Document document, Header header) {
      warn("Unknown file extension: %s", document.getFilePath());
      unknownFiles.add(document.getFile());
      report.add(document.getFile(), Report.Result.UNKNOWN);
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

  protected final void execute(final Callback callback) throws MojoExecutionException, MojoFailureException {
    if (skip) {
        getLog().info("License Plugin is Skipped");
    } else {
      if (prohibitLegacyUse && detectLegacyUse()) {
        throw new MojoExecutionException("Use of legacy parameters has been prohibited by configuration.");
      }

      // make default base dir canonical
      workspace.basedir = getCanonicalFile(firstNonNull(workspace.basedir, legacyDefaultBasedir), "license.workspace.basedir");

      // collect all the license sets together
      final LicenseSet[] allLicenseSets;

      // if we abandon the legacy config this contiguous block can be removed
      final LicenseSet legacyLicenseSet = convertLegacyConfigToLicenseSet();

      if (workspace.basedir != null) {
        if (legacyLicenseSet != null && legacyLicenseSet.basedir != null) {
          if (!FileUtils.isSubfolder(legacyLicenseSet.basedir, workspace.basedir)) {
            throw new MojoExecutionException("Legacy basedir parameter should be a subfolder of the workspace basedir.");
          }
        }
        for (LicenseSet licenseSet : licenseSets) {
          if (licenseSet.basedir != null) {
            if (!FileUtils.isSubfolder(licenseSet.basedir, workspace.basedir)) {
              throw new MojoExecutionException(String.format("LicenseSet basedir parameter [%s] should be a subfolder of the workspace basedir.", licenseSet.basedir.getPath()));
            }
          }
        }
      }

      if (legacyLicenseSet != null) {
        if (licenseSets == null) {
          allLicenseSets = new LicenseSet[]{legacyLicenseSet};
        } else {
          allLicenseSets = Arrays.copyOf(licenseSets, licenseSets.length + 1);
          allLicenseSets[licenseSets.length] = legacyLicenseSet;
        }
      } else {
        allLicenseSets = licenseSets;
      }

      // execute
      executeForLicenseSets(allLicenseSets, callback);

      report.exportTo(reportLocation);
    }
  }

  private File getCanonicalFile(final File file, final String description) throws MojoFailureException {
    if (file == null) {
      return null;
    }
    try {
      return file.getCanonicalFile();
    } catch (final IOException e) {
      throw new MojoFailureException("Could not get canonical path of " + description, e);
    }
  }

  private void executeForLicenseSets(final LicenseSet[] licenseSets, final Callback callback) throws MojoFailureException, MojoExecutionException {
    if (licenseSets == null || licenseSets.length == 0) {
      warn("At least one licenseSet must be specified");
      return;
    }

    // need to perform validation first
    for (int i = 0; i < licenseSets.length; i++) {
      final LicenseSet licenseSet = licenseSets[i];
      if (!hasHeader(licenseSet)) {
        warn("No header file specified to check for license in licenseSet: " + i);
        return;
      }
      // make licenseSet baseDir canonical
      licenseSet.basedir = this.getCanonicalFile(licenseSet.basedir, "licenseSet[" + i + "].basedir");
    }
    if (!strictCheck) {
      warn("Property 'strictCheck' is not enabled. Please consider adding <strictCheck>true</strictCheck> in your pom.xml file.");
      warn("See https://mathieu.carbou.me/license-maven-plugin for more information.");
    }

    // then execute each license set
    for (final LicenseSet licenseSet : licenseSets) {
      executeForLicenseSet(licenseSet, callback);
    }
  }

  private boolean detectLegacyUse() {
    return legacyDefaultBasedir != null
            || legacyConfigHeader != null
            || legacyConfigInlineHeader != null
            || (legacyConfigValidHeaders != null && legacyConfigValidHeaders.length > 0)
            || legacyConfigMulti != null
            || (legacyConfigHeaderSections != null && legacyConfigHeaderSections.length > 0)
            || (legacyConfigIncludes != null && legacyConfigIncludes.length > 0)
            || (legacyConfigExcludes != null && legacyConfigExcludes.length > 0)
            || (legacyConfigKeywords != null && !Arrays.equals(legacyConfigKeywords, DEFAULT_KEYWORDS));
  }

  private LicenseSet convertLegacyConfigToLicenseSet() {
    if (legacyConfigHeader == null && (this.legacyConfigInlineHeader == null || this.legacyConfigInlineHeader.isEmpty())) {
      return null;
    }

    final LicenseSet legacyLicenseSet = new LicenseSet();
    legacyLicenseSet.header = legacyConfigHeader;
    legacyLicenseSet.inlineHeader = legacyConfigInlineHeader;
    legacyLicenseSet.validHeaders = legacyConfigValidHeaders;
    legacyLicenseSet.multi = legacyConfigMulti;
    legacyLicenseSet.headerSections = legacyConfigHeaderSections;
    legacyLicenseSet.includes = legacyConfigIncludes;
    legacyLicenseSet.excludes = legacyConfigExcludes;
    legacyLicenseSet.keywords = legacyConfigKeywords;
    legacyLicenseSet.basedir = legacyDefaultBasedir;
    return legacyLicenseSet;
  }

  private void executeForLicenseSet(final LicenseSet licenseSet, final Callback callback) throws MojoExecutionException, MojoFailureException {
    final ResourceFinder finder = new ResourceFinder(firstNonNull(asPath(licenseSet.basedir), asPath(workspace.basedir)));
    try {
      finder.setCompileClassPath(project.getCompileClasspathElements());
    } catch (DependencyResolutionRequiredException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
    finder.setPluginClassPath(getClass().getClassLoader());

    final HeaderSource headerSource = HeaderSource.of(licenseSet.multi, licenseSet.inlineHeader, licenseSet.header, Charset.forName(this.encoding), finder);
    final Header header = new Header(headerSource, licenseSet.headerSections);
    debug("Header: %s", header.getLocation());

    if (licenseSet.validHeaders == null) {
      licenseSet.validHeaders = new String[0];
    }
    final List<Header> validHeaders = new ArrayList<>(licenseSet.validHeaders.length);
    for (final String validHeader : licenseSet.validHeaders) {
      final HeaderSource validHeaderSource = HeaderSource.of(null, null, validHeader, Charset.forName(this.encoding),
          finder);
      validHeaders.add(new Header(validHeaderSource, licenseSet.headerSections));
    }

    Map<String, String> globalProperties = getDefaultProperties();

    // we override by properties in the licenseSet
    if (licenseSet.properties != null) {
      for (Map.Entry<String, String> entry : licenseSet.properties.entrySet()) {
        if (!System.getProperties().contains(entry.getKey())) {
          globalProperties.put(entry.getKey(), entry.getValue());
        }
      }
    }

    if (getLog().isDebugEnabled()) {
      getLog().debug(
          "global properties:\n - " + globalProperties.entrySet().stream().map(Objects::toString)
              .collect(Collectors.joining("\n - ")));
    }

    final List<PropertiesProvider> propertiesProviders = new LinkedList<>();
    int threads = getNumberOfExecutorThreads();
    ExecutorService executorService = Executors.newFixedThreadPool(threads);

    try {

      for (final PropertiesProvider provider : ServiceLoader.load(PropertiesProvider.class,
          Thread.currentThread().getContextClassLoader())) {
        provider.init(this, globalProperties);
        propertiesProviders.add(provider);
      }

      final DocumentPropertiesLoader perDocumentProperties = document -> {

        // then add per document properties
        LazyMap<String, String> perDoc = new LazyMap<>(key -> {
          return Objects.equals(key, "file.name") ? document.getFile().getName() : globalProperties.get(key);
        });

        Map<String, String> readOnly = Collections.unmodifiableMap(perDoc);

        for (final PropertiesProvider provider : propertiesProviders) {
          try {
            final Map<String, String> adjustments = provider.adjustProperties(
                AbstractLicenseMojo.this, readOnly, document);
            if (getLog().isDebugEnabled()) {
              getLog().debug("provider: " + provider.getClass() + " adjusted these properties:\n"
                  + adjustments);
            }
            for (String key : adjustments.keySet()) {
              perDoc.putSupplier(key, () -> adjustments.get(key));
            }
          } catch (Exception e) {
            if (getLog().isWarnEnabled()) {
              getLog().warn("failure occurred while calling " + provider.getClass(), e);
            }
          }
        }

        if (getLog().isDebugEnabled()) {
          getLog().debug("properties for " + document + ":\n - " + perDoc.entrySet().stream()
              .map(Objects::toString).collect(Collectors.joining("\n - ")));
        }

        return perDoc;
      };

      final DocumentFactory documentFactory = new DocumentFactory(
          firstNonNull(licenseSet.basedir, workspace.basedir), buildMapping(),
          buildHeaderDefinitions(licenseSet, finder), Charset.forName(encoding), licenseSet.keywords,
          perDocumentProperties);

      CompletionService<?> completionService = new ExecutorCompletionService<>(executorService);
      int count = 0;
      debug("Number of execution threads: %s", threads);

      for (final String file : listSelectedFiles(licenseSet)) {
        completionService.submit(() -> {
          Document document = documentFactory.createDocuments(file);
          debug("Selected file: %s [header style: %s]", document.getFilePath(),
              document.getHeaderDefinition());
          if (document.isNotSupported()) {
            callback.onUnknownFile(document, header);
          } else if (document.is(header)) {
            debug("Skipping header file: %s", document.getFilePath());
          } else if (document.hasHeader(header, strictCheck)) {
            callback.onExistingHeader(document, header);
          } else {
            boolean headerFound = false;
            for (final Header validHeader : validHeaders) {
              headerFound = document.hasHeader(validHeader, strictCheck);
              if (headerFound) {
                callback.onExistingHeader(document, header);
                break;
              }
            }
            if (!headerFound) {
              callback.onHeaderNotFound(document, header);
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
      propertiesProviders.forEach(PropertiesProvider::close);
    }
  }

  private boolean hasHeader(final LicenseSet licenseSet) {
    return
        (licenseSet.multi != null
            && ((licenseSet.multi.headers != null && licenseSet.multi.headers.length > 0)
            || (licenseSet.multi.inlineHeaders != null && licenseSet.multi.inlineHeaders.length > 0 && !licenseSet.multi.inlineHeaders[0].isEmpty()))
        ) || (licenseSet.header != null || (licenseSet.inlineHeader != null && !licenseSet.inlineHeader.isEmpty()));
  }

  private int getNumberOfExecutorThreads() {
    return nThreads > 0 ?
        nThreads :
        Math.max(1, (int) (Runtime.getRuntime().availableProcessors() * concurrencyFactor));
  }

  private Map<String, String> getDefaultProperties() {
    // first put system environment
    Map<String, String> props = new TreeMap<>(
        System.getenv()); // treemap just to have nice debug logs
    // then add ${project.XYZ} properties
    props.put("project.groupId", project.getGroupId());
    props.put("project.artifactId", project.getArtifactId());
    props.put("project.version", project.getVersion());
    props.put("project.name", project.getName());
    props.put("project.description", project.getDescription());
    props.put("project.inceptionYear", project.getInceptionYear());
    props.put("year", project.getInceptionYear()); // maintains backward compatibility
    props.put("project.url", project.getUrl());
    Organization org = project.getOrganization();
    if (org != null) {
      props.put("owner", org.getName()); // maintains backward compatibility
      props.put("project.organization.name", org.getName());
      props.put("project.organization.url", org.getUrl());
    }

    // we override by properties in the POM
    if (this.defaultProperties != null) {
      props.putAll(this.defaultProperties);
    }

    // then we override by java system properties (command-line -D...)
    for (String key : System.getProperties().stringPropertyNames()) {
      props.put(key, System.getProperty(key));
    }

    return props;
  }

  private String[] listSelectedFiles(final LicenseSet licenseSet) {
    final boolean useDefaultExcludes = (licenseSet.useDefaultExcludes != null ? licenseSet.useDefaultExcludes : defaultUseDefaultExcludes);
    final Selection selection = new Selection(
        firstNonNull(licenseSet.basedir, workspace.basedir), licenseSet.includes, buildExcludes(licenseSet), useDefaultExcludes,
        getLog());
    debug("From: %s", firstNonNull(licenseSet.basedir, workspace.basedir));
    debug("Including: %s", deepToString(selection.getIncluded()));
    debug("Excluding: %s", deepToString(selection.getExcluded()));
    return selection.getSelectedFiles();
  }

  private String[] buildExcludes(final LicenseSet licenseSet) {
    List<String> ex = new ArrayList<>();
    ex.addAll(asList(licenseSet.excludes));
    if (project != null && project.getModules() != null && !aggregate) {
      for (String module : project.getModules()) {
        ex.add(module + "/**");
      }
    }
    return ex.toArray(new String[ex.size()]);
  }

  public final void info(String format, Object... params) {
    if (!quiet && getLog().isInfoEnabled()) {
      getLog().info(format(format, params));
    }
  }

  public final void debug(String format, Object... params) {
    if (!quiet && getLog().isDebugEnabled()) {
      getLog().debug(format(format, params));
    }
  }

  public final void warn(String format, Object... params) {
    if (!quiet && getLog().isWarnEnabled()) {
      getLog().warn(format(format, params));
    }
  }

  private Map<String, String> buildMapping() {
    Map<String, String> extensionMapping = new LinkedHashMap<>();
    // force inclusion of unknown item to manage unknown files
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

  private Map<String, HeaderDefinition> buildHeaderDefinitions(final LicenseSet licenseSet, final ResourceFinder finder) throws MojoFailureException {
    // like mappings, first get default definitions
    final Map<String, HeaderDefinition> headers = new HashMap<>(HeaderType.defaultDefinitions());

    // and then override them with those provided in base config
    for (final String headerDefiniton : defaultHeaderDefinitions) {
      headers.putAll(loadHeaderDefinition(headerDefiniton, finder));
    }

    // then override by inline default styles
    for (HeaderStyle defaultInlineHeaderStyle : defaultInlineHeaderStyles) {
      headers.put(defaultInlineHeaderStyle.name, defaultInlineHeaderStyle.toHeaderDefinition());
    }

    // and then override them with those provided in licenseSet config
    for (final String headerDefiniton : licenseSet.headerDefinitions) {
      headers.putAll(loadHeaderDefinition(headerDefiniton, finder));
    }

    for (HeaderStyle inlineHeaderStyle : licenseSet.inlineHeaderStyles) {
      headers.put(inlineHeaderStyle.name, inlineHeaderStyle.toHeaderDefinition());
    }

    // force inclusion of unknown item to manage unknown files
    headers.put(HeaderType.UNKNOWN.getDefinition().getType(), HeaderType.UNKNOWN.getDefinition());
    return headers;
  }

  private Map<String, HeaderDefinition> loadHeaderDefinition(final String headerDefinition, final ResourceFinder finder) throws MojoFailureException {
    try {
      final InputSource source = new InputSource(finder.findResource(headerDefinition).openStream());
      source.setEncoding(encoding);
      final AdditionalHeaderDefinition fileDefinitions = new AdditionalHeaderDefinition(XMLDoc.from(source, true));
      final Map<String, HeaderDefinition> map = fileDefinitions.getDefinitions();
      debug("%d header definitions loaded from '%s'", map.size(), headerDefinition);
      return map;
    } catch (final IOException ex) {
      throw new MojoFailureException("Error reading header definition: " + headerDefinition, ex);
    }
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
   * found.
   *
   * @param serverID
   * @return
   */
  public Credentials findCredentials(String serverID) {
    if (serverID == null) {
      return null;
    }

    List<Server> decryptedServers = getDecryptedServers();

    for (Server ds : decryptedServers) {
      if (ds.getId().equals(serverID)) {
        if (getLog().isDebugEnabled()) {
          getLog().debug(
              "credentials have been found for server: " + serverID + ", login:" + ds.getUsername());
        }
        return new Credentials(ds.getUsername(), ds.getPassword());
      }
    }

    if (getLog().isDebugEnabled()) {
        getLog().debug("no credentials found for server: " + serverID);
    }
    return null;
  }

  private static <T> T firstNonNull(final T t1, final T t2) {
    return t1 == null ? t2 : t1;
  }
}
