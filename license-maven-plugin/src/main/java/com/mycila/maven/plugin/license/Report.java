/*
 * Copyright (C) 2008-2023 Mycila (mathieu.carbou@gmail.com)
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mycila.xmltool.XMLDoc;
import com.mycila.xmltool.XMLTag;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Report {

  enum Action {CHECK, FORMAT, REMOVE}

  enum Result {
    /**
     * For check: header is OK
     */
    PRESENT,

    /**
     * For check: means the file does not contain a header
     */
    MISSING,

    /**
     * For format or remove when no operation were done
     */
    NOOP,

    /**
     * For format, when header is added
     */
    ADDED,

    /**
     * For format, when header is replaced
     */
    REPLACED,

    /**
     * For remove, when header is removed
     */
    REMOVED,

    /**
     * For any actions: means the file extension is unknown
     */
    UNKNOWN,
  }

  private final long timestamp;
  private final MavenProject project;
  private final String format;
  private final Action action;
  private final boolean skipped;
  private final Map<String, Result> results = new ConcurrentHashMap<>();
  private final Path basePath;

  public Report(String format, Action action, MavenProject project, Clock clock, boolean skip) {
    this.format = format;
    this.action = action;
    this.project = project;
    this.skipped = skip;
    this.basePath = project.getBasedir().toPath().toAbsolutePath();
    this.timestamp = clock.millis();
  }

  void add(File file, Result result) {
    if (!skipped) {
      results.put(basePath.relativize(file.getAbsoluteFile().toPath()).toString(), result);
    }
  }

  public void exportTo(File reportLocation) {
    if (!skipped && reportLocation != null) {

      //noinspection ResultOfMethodCallIgnored
      reportLocation.getParentFile().mkdirs();

      String format = this.format;
      if (format == null) {
        final int p = reportLocation.getName().lastIndexOf('.');
        format = p >= 0 ? reportLocation.getName().substring(p + 1) : null;
        if (format == null) {
          throw new IllegalStateException("Report format ('xml' or 'json') needs to be specified in the plugin configuration because it cannot be determined from the report extension");
        }
      }
      format = format.toLowerCase(Locale.ROOT);

      switch (format) {

        case "xml": {
          XMLTag files = XMLDoc.newDocument(true)

              .addRoot("licensePluginReport")
              .addAttribute("timestamp", Long.toString(timestamp))
              .addAttribute("goal", action.name())

              .addTag("module")
              .addAttribute("groupId", project == null ? "unknown" : project.getArtifact().getGroupId())
              .addAttribute("artifactId", project == null ? "unknown" : project.getArtifact().getArtifactId())
              .addAttribute("version", project == null ? "unknown" : project.getArtifact().getVersion())
              .gotoRoot()

              .addTag("files");

          results.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> files.addTag("file")
              .addAttribute("path", e.getKey())
              .addAttribute("result", e.getValue().name())
              .gotoParent());

          try {
            Files.write(reportLocation.toPath(), files.gotoRoot().toBytes("UTF-8"));
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
          break;
        }
        case "json": {
          JsonObject module = new JsonObject();
          module.add("groupId", new JsonPrimitive(project == null ? "unknown" : project.getArtifact().getGroupId()));
          module.add("artifactId", new JsonPrimitive(project == null ? "unknown" : project.getArtifact().getArtifactId()));
          module.add("version", new JsonPrimitive(project == null ? "unknown" : project.getArtifact().getVersion()));

          JsonObject root = new JsonObject();
          root.add("timestamp", new JsonPrimitive(Long.toString(timestamp)));
          root.add("goal", new JsonPrimitive(action.name()));
          root.add("module", module);
          root.add("files", results.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(e -> {
            JsonObject file = new JsonObject();
            file.add("path", new JsonPrimitive(e.getKey()));
            file.add("result", new JsonPrimitive(e.getValue().name()));
            return file;
          }).reduce(new JsonArray(), (files, file) -> {
            files.add(file);
            return files;
          }, (files1, files2) -> {
            files1.addAll(files2);
            return files1;
          }));

          try {
            final Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
            Files.write(reportLocation.toPath(), gson.toJson(root).getBytes(UTF_8));
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
          break;
        }

        default:
          throw new IllegalArgumentException("Invalid report format: '" + format + "'");
      }
    }
  }
}
