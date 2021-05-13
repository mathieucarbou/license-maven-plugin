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

import java.util.Collections;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.shared.artifact.filter.StrictPatternIncludesArtifactFilter;

/**
 * Make policy decisions on a {@link Artifact} based on an {@link ArtifactFilter}.
 * 
 * @author Royce Remer
 *
 */
public class ArtifactLicensePolicyEnforcer extends AbstractLicensePolicyEnforcer<Artifact> {
	private ArtifactFilter filter;

	public ArtifactLicensePolicyEnforcer(final LicensePolicy policy, final ArtifactFilter filter) {
		super(policy);
		this.filter = filter;
	}
	
	public ArtifactLicensePolicyEnforcer(final LicensePolicy policy) {
		super(policy);
		this.filter = new StrictPatternIncludesArtifactFilter(Collections.singletonList(policy.getValue()));
	}

	@Override
	public boolean apply(final Artifact target) {
		final boolean matches = filter.include(target);
		return getPolicy().getRule().isAllowed(matches);
	}

	@Override
	public Class<?> getType() {
		return Artifact.class;
	}
}