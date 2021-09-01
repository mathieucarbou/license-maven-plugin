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

import org.apache.maven.model.License;

/**
 * Make policy decisions on a {@link License} based on the license URL.
 * 
 * @author Royce Remer
 *
 */
public class LicenseURLLicensePolicyEnforcer extends AbstractLicensePolicyEnforcer<License> {

	public LicenseURLLicensePolicyEnforcer(final LicensePolicy policy) {
		super(policy);
	}

	@Override
	public boolean apply(final License target) {
		final Boolean matches = getPolicy().getValue().equals(target.getUrl());
		return getPolicy().getRule().isAllowed(matches);
	}

	@Override
	public Class<?> getType() {
		return License.class;
	}
}