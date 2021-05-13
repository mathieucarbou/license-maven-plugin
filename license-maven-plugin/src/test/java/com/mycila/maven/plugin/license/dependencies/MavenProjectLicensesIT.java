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
package com.mycila.maven.plugin.license.dependencies;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Files;

/**
 * We use {@link Verifier} here for mvn executions, mainly so:
 * a) we can verify a few cases that the invoker method make really difficult (I'd like to step-through with my IDE)
 * b) the test harness method requires creating a custom Artifact resolver, as the dependencGraphBuilder Component will
 *    not provide a usable bean, and we would need extensive mocking to override data for specific cases
 * c) it's a lot faster than maven-invoker-plugin
 * ... good overview of similar woes {@link https://khmarbaise.github.io/maven-it-extension/itf-documentation/background/background.html}
 * 
 * @author Royce Remer
 *
 */
public final class MavenProjectLicensesIT {
	
	File source;
	String target;
	String phase;
	Map<String, String> env;
	
	@Rule
	public TemporaryFolder workspace = new TemporaryFolder();
	final String sourcePrefix = "src/test/resources/config/";
	final String resource = "/pom.xml";
	
	@Before
	public void setUp() {
		// your maven may be on a different path, they'll append '/bin/mvn' to it
		System.setProperty("maven.home", "/usr/local");
		
		this.target = workspace.getRoot().getAbsolutePath();
		this.env = Collections.singletonMap("LICENSE_PLUGIN_VERSION", this.getClass().getPackage().getImplementationVersion());
		this.phase = "verify";
	}
	
	private boolean hasLogLine(final String logline) {
		Optional<Verifier> verifier;
		try {
			verifier = Optional.of(new Verifier(target));
		} catch (Exception ex) {
			// project didn't even build, this is a test or test resource error
			return false;
		}
		
		try {
			verifier.get().executeGoal(phase, env);
		} catch (VerificationException e) {
			// potential purposeful MojoExecutionException (hidden in stack)
			try {
				verifier.get().verifyTextInLog(logline);
				return true;
			} catch (VerificationException e1) {}
		}
		
		// legit test failure
		return false;
	}
	
	/**
	 * Helper method to sync test resources to a temporary folder for execution.
	 * @param dir - String relative path to {@link sourcePrefix} to copy from.
	 * @throws IOException
	 */
	private void syncTarget(final String dir) throws IOException {
		source = new File(sourcePrefix + dir + resource);
		final File syncTarget = new File(target + resource);
		Files.copy(source, syncTarget);
	}
		
    @Test
    public void test_null() throws IOException {
    	final String description = "A project with enforcement enabled but nothing in scope should find zero dependencies";
    	syncTarget("null");
    	
    	assertEquals(description, true, hasLogLine(MavenProjectLicenses.INFO_DEPS_DISCOVERED + ": 1"));
    }
    
    @Test
    public void test_deny() throws IOException {
    	final String description = "A project with enforcement enabled and dependencies in scope under default deny policy should fail.";
    	syncTarget("deny");
    	
    	assertEquals(description, true, hasLogLine(MavenProjectLicenses.WARN_POLICY_DENIED));
    }
    
    @Test
    public void test_approved() throws IOException {
    	final String description = "A project with allow policy and a single dependency should succeed.";
    	syncTarget("approve");
    	
    	assertEquals(description, true, hasLogLine(MavenProjectLicenses.INFO_DEPS_DISCOVERED + ": 1"));
    }
}