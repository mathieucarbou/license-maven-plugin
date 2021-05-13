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
package com.mycila.maven.plugin.license.dependencies;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.License;

public class LicensePolicyEnforcerResult {

	private final LicensePolicy policy;
	private final License license;
	private final Artifact artifact;
	private final LicensePolicy.Rule ruling;
	
	public LicensePolicyEnforcerResult(final LicensePolicy policy, final License license, final Artifact artifact, final LicensePolicy.Rule ruling) {
		this.policy = policy;
		this.license = license;
		this.artifact = artifact;
		this.ruling = ruling;
	}
	
	@Override
	public int hashCode() {
		return 11 * (policy.hashCode() + license.hashCode() + artifact.hashCode() + ruling.hashCode());
	}
	
	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		} else {
			return (other.hashCode() == hashCode());
		}
	}

	public LicensePolicy getPolicy() {
		return policy;
	}

	public Artifact getArtifact() {
		return artifact;
	}

	public Boolean isAllowed() {
		return ruling.getPredicate();
	}
	
	public LicensePolicy.Rule getRuling() {
		return ruling;
	}

	public License getLicense() {
		return license;
	}
	
	@Override
	public String toString() {
		return String.format("%s [%s] %s", getArtifact(), getLicense().getName(), getPolicy());
	}
}