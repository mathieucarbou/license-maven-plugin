/*
 * Copyright (C) 2008-2022 Mycila (mathieu.carbou@gmail.com)
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
import com.mycila.maven.plugin.license.header.Header;
import java.io.File;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Remove the specified header from source files
 *
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
@Mojo(name = "remove", threadSafe = true)
public final class LicenseRemoveMojo extends AbstractLicenseMojo {

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    report = new Report(reportFormat, Report.Action.REMOVE, project, clock, reportSkipped);

    if (!skip) {
      getLog().info("Removing license headers...");
    }

    AbstractCallback callback = new AbstractCallback() {

      @Override
      public void onHeaderNotFound(Document document, Header header) {
        debug("Header was not found in: %s (But keep trying to find another header to remove)", document.getFile());
        remove(document);
      }

      @Override
      public void onExistingHeader(Document document, Header header) {
        info("Removing license header from: %s", document.getFile());
        remove(document);
      }

    };

    execute(callback);

    callback.checkUnknown();
  }

  private void remove(Document document) {
    document.parseHeader();
    if (document.headerDetected()) {
      document.removeHeader();
      if (!dryRun) {
        document.save();
      } else {
        String name = document.getFile().getName() + ".licensed";
        File copy = new File(document.getFile().getParentFile(), name);
        info("Result saved to: %s", copy);
        document.saveTo(copy);
      }
      report.add(document.getFile(), Report.Result.REMOVED);
    } else {
      report.add(document.getFile(), Report.Result.NOOP);
    }
  }

}
