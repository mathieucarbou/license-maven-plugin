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

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.model.License;

/**
 * Utility class for test setup methods related to this package.
 * 
 * @author Royce Remer
 *
 */
class ArtifactLicensePolicyEnforcerTestBase {

	/**
	 * Helper method for easy {@link Artifact} generation.
	 * @param gav - String with at least two separators of groupId:artifactId:type:artifactVersion.
	 * @return
	 */
	protected Artifact getArtifact(final String gav) {
		final String[] gavParts = gav.split(":");
		return new DefaultArtifact(gavParts[0], gavParts[1], gavParts[3], "runtime", gavParts[2], "", new DefaultArtifactHandler());
		
	}
	
	/**
	 * Helper method to get a license with set URL.
	 * 
	 * @param name
	 * @return
	 */
	protected License getLicense(final URL url) {
		final License license = new License();
		license.setUrl(url.toString());;
		return license;
	}
	
	/**
	 * Helper method to get a license with set name.
	 * 
	 * @param name
	 * @return
	 */
	protected License getLicense(final String name) {
		final License license = new License();
		if (!"".equals(name)) {
			license.setName(name);
		}
		return license;
	}
	
	/**
	 * Helper class for tracking test data related to license enforcement.
	 * 
	 * @author Royce Remer
	 *
	 */
	protected class LicenseMapData implements LicenseMap {
		Map<Artifact, LicensePolicyEnforcerResult> expected;
		Map<License, Set<Artifact>> licenseMap;
		
		LicenseMapData() {
			expected = new HashMap<Artifact, LicensePolicyEnforcerResult>();
			licenseMap = new HashMap<License, Set<Artifact>>();
		}
		
		void put(final String artifactCoordinates, final String licenseName, final LicensePolicy policy, final Boolean allowed) {
			final Artifact artifact = getArtifact(artifactCoordinates);
			final License license = getLicense(licenseName);
			final Set<Artifact> artifacts = Optional.ofNullable(licenseMap.get(license)).orElse(new HashSet<Artifact>());
			artifacts.add(artifact);
			licenseMap.put(license, artifacts);
			expected.put(artifact, new LicensePolicyEnforcerResult(policy, license, artifact, LicensePolicy.Rule.valueOf(allowed)));
		}
		
		public Map<Artifact, LicensePolicyEnforcerResult> getExpected() {
			return expected;
		}
		
		@Override
		public Map<License, Set<Artifact>> getLicenseMap(){
			return licenseMap;
		}
	}
	
}