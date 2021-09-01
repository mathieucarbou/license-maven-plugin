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
package com.mycila.maven.plugin.license;

import com.mycila.maven.plugin.license.dependencies.AggregateLicensePolicyEnforcer;
import com.mycila.maven.plugin.license.dependencies.LicenseMap;
import com.mycila.maven.plugin.license.dependencies.LicensePolicy;
import com.mycila.maven.plugin.license.dependencies.LicensePolicyEnforcerResult;
import com.mycila.maven.plugin.license.dependencies.MavenProjectLicenses;
import com.mycila.maven.plugin.license.document.Document;
import com.mycila.maven.plugin.license.header.Header;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.License;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Check if the source files of the project have a valid license header
 *
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
@Mojo(name = "check", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public final class LicenseCheckMojo extends AbstractLicenseMojo {

    @Parameter(property = "license.errorMessage", defaultValue = "Some files do not have the expected license header")
    public String errorMessage = "Some files do not have the expected license header";

    public final Collection<File> missingHeaders = new ConcurrentLinkedQueue<File>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if(!skip) {
            getLog().info("Checking licenses...");
        }
        missingHeaders.clear();

        AbstractCallback callback = new AbstractCallback() {
            @Override
            public void onHeaderNotFound(Document document, Header header) {
                if (skipExistingHeaders) {
                    document.parseHeader();
                    if (document.headerDetected()) {
                        debug("Existing header in: %s", document.getFilePath());
                        return;
                    }
                }
                warn("Missing header in: %s", document.getFilePath());
                missingHeaders.add(document.getFile());
            }

            @Override
            public void onExistingHeader(Document document, Header header) {
                debug("Header OK in: %s", document.getFilePath());
            }
        };

        if(dependencyEnforce) {
        	//TODO(rremer) config-driven factory of the LicenseMap implementation	
		    final LicenseMap licenseMap = new MavenProjectLicenses(session, project, dependencyGraphBuilder, projectBuilder, dependencyScopes, getLog());
		    final AggregateLicensePolicyEnforcer enforcer = new AggregateLicensePolicyEnforcer(dependencyPolicies);
		    final Map<Artifact, LicensePolicyEnforcerResult> licenseResult = enforcer.apply(licenseMap);
		    final Set<LicensePolicyEnforcerResult> deniedLicenseResult = licenseResult.values().stream()
		    		.filter(result -> result.getRuling().equals(LicensePolicy.Rule.DENY))
		    		.collect(Collectors.toSet());
		    
		    if (deniedLicenseResult.size() != 0) {
		    	final StringBuilder licenseExceptionMsg = new StringBuilder(dependencyExceptionMessage);
		    	deniedLicenseResult.stream().forEach(result -> {
		    		licenseExceptionMsg.append(System.lineSeparator());
		    		licenseExceptionMsg.append(result);
		    	});
		    	throw new MojoExecutionException(licenseExceptionMsg.toString());
		    }
        }

        execute(callback);

        if (!missingHeaders.isEmpty()) {
            if (failIfMissing) {
                throw new MojoExecutionException(errorMessage);
            }
            getLog().warn(errorMessage);
        }

        if(!skip) {
            callback.checkUnknown();
        }
    }

}
