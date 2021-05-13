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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.License;

import com.mycila.maven.plugin.license.dependencies.LicensePolicy.Rule;

/**
 * Aggregate license policy enforcement with default enforcer bindings based on {@link LicensePolicy.Type}.
 * 
 * Rules are applied in the following order:
 * 1) defaultPolicy: unless overridden via setDefaultPolicy, this will DENY all artifacts.
 * 2) APPROVE policies: any policy in the Set which have {@link LicensePolicy.Rule.APPROVE}
 * 3) DENY policies: any policy in the Set which have {@link LIcensePolicy.Rule.DENY}
 * 
 * @author Royce Remer
 *
 */
@SuppressWarnings("rawtypes")
public class AggregateLicensePolicyEnforcer {
	private final Set<LicensePolicy> policies;
	private LicensePolicyEnforcer defaultPolicy;
	private Set<LicensePolicyEnforcer> enforcers;
	
	public AggregateLicensePolicyEnforcer(final Set<LicensePolicy> policies) {
		this.policies = policies;
		this.defaultPolicy = new DefaultLicensePolicyEnforcer();
		this.enforcers = policies.stream().map(policy -> initPolicyEnforcer(policy)).collect(Collectors.toSet());
	}
	
	/**
	 * Initialize an {@LicensePolicyEnforcer} implementation based on its {@link LicensePolicy.Type}.
	 * 
	 * @param policy - a single license policy which needs enforcement.
	 * @return
	 */
	private static LicensePolicyEnforcer<?> initPolicyEnforcer(final LicensePolicy policy) {
		switch (policy.getType()) {
		case LICENSE_NAME:
			return new LicenseNameLicensePolicyEnforcer(policy);
		case ARTIFACT_PATTERN:
			return new ArtifactLicensePolicyEnforcer(policy);
		case LICENSE_URL:
			return new LicenseURLLicensePolicyEnforcer(policy);
		default:
			return new DefaultLicensePolicyEnforcer();
		}
	}
	
	/**
	 * Get a Set of policy enforces that have a given rule (approve/deny) and type (artifact/license).
	 * 
	 * @param rule - the {@link LicensePolicy.Rule} to filter all enforcers by.
	 * @return
	 */
	private Set<LicensePolicyEnforcer> getEnforcers(final LicensePolicy.Rule rule) {
		return enforcers.stream()
				.filter(e -> e.getPolicy().getRule() == rule)
				.collect(Collectors.toSet());
	}
	
	/**
	 * Helper method for taking a single iteration of license to set of artifacts, and applying a policy enforcer.
	 * 
	 * @param license
	 * @param artifacts
	 * @param enforcer
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<Artifact, LicensePolicyEnforcerResult> apply(final License license, final Set<Artifact> artifacts, final LicensePolicyEnforcer enforcer) {
		final Map<Artifact, LicensePolicyEnforcerResult> results = new HashMap<Artifact, LicensePolicyEnforcerResult>();
		
		final LicensePolicy.Rule filter = enforcer.getPolicy().getRule();
		
		artifacts.forEach(artifact -> {
			LicensePolicy.Rule ruling = LicensePolicy.Rule.DENY;
			if (enforcer.getType() == License.class) {
				ruling = LicensePolicy.Rule.valueOf(enforcer.apply(license));
			} else if (enforcer.getType() == Artifact.class) {
				ruling = LicensePolicy.Rule.valueOf(enforcer.apply(artifact));
			}
			results.put(artifact, new LicensePolicyEnforcerResult(enforcer.getPolicy(), license, artifact, ruling));
		});
		
		// if this was an APPROVE rule, only return approvals. If a DENY rule, only return denials
		return results.entrySet().stream()
				.filter(result -> filter.equals(result.getValue().getRuling()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}
	
	/**
	 * Helper method for taking a full map of License:Set<Artifact> and building a rulings map from a policy enforcer.
	 * 
	 * @param licenseMap
	 * @param enforcer
	 * @return
	 */
	private Map<Artifact, LicensePolicyEnforcerResult> apply(final Map<License, Set<Artifact>> licenseMap, final LicensePolicyEnforcer enforcer) {
		final Map<Artifact, LicensePolicyEnforcerResult> results = new HashMap<Artifact, LicensePolicyEnforcerResult>();
		
		licenseMap.forEach((license, artifactSet) -> {
			results.putAll(apply(license, artifactSet, enforcer));
		});
		return results;
	}
	
	
	/**
	 * Take a map of {@link License} keys and the Set of {@link Artifact} attributed to them,
	 * applying the internal set of {@link LicensePolicyEnforcer} implementations on them,
	 * and returning a mapping of Artifact keys to the boolean enforcement decision made.
	 * 
	 * @param licenseMap - the underlying LicenseMap interface types
	 * @return final policy decision map on each artifact
	 */
	@SuppressWarnings("unchecked")
	public Map<Artifact, LicensePolicyEnforcerResult> apply(final Map<License, Set<Artifact>> licenseMap) {
		final Map<Artifact, LicensePolicyEnforcerResult> results = new HashMap<Artifact, LicensePolicyEnforcerResult>();
		
		// apply the default policy to all artifacts, populating the map
		licenseMap.entrySet().stream().forEach(entry -> {
			License license = entry.getKey();
			entry.getValue().forEach(
					artifact -> results.putIfAbsent(artifact, new LicensePolicyEnforcerResult(defaultPolicy.getPolicy(),
							license, artifact, LicensePolicy.Rule.valueOf(defaultPolicy.apply(artifact)))));
		});

		// apply approval rules, updating the map
		getEnforcers(LicensePolicy.Rule.APPROVE).forEach(enforcer -> {
			results.putAll(apply(licenseMap, enforcer));
		});
		
		// apply deny rules, updating the map
		getEnforcers(LicensePolicy.Rule.DENY).forEach(enforcer -> {
			results.putAll(apply(licenseMap, enforcer));
		});

		return results;
	}
	
	/**
	 * Take an {@link LicenseMap} implementation, getting its licenseMap and
	 * applying the internal set of {@link LicensePolicyEnforcer} implementations on them,
	 * and returning a mapping of Artifact keys to the boolean enforcement decision made.
	 * 
	 * @param licenseMap - a Consumer<LicenseMap>
	 * @return final policy decision map on each artifact
	 */
	public Map<Artifact, LicensePolicyEnforcerResult> apply(final LicenseMap licenseMap) {
		return apply(licenseMap.getLicenseMap());
	}
	
	public void setEnforcers(final Set<LicensePolicyEnforcer> enforcers) {
		this.enforcers = enforcers;
	}

	public Set<LicensePolicyEnforcer> getEnforcers() {
		return enforcers;
	}

	public Set<LicensePolicy> getPolicies() {
		return policies;
	}

    public LicensePolicyEnforcer<?> getDefaultPolicy() {
		return defaultPolicy;
	}

	public void setDefaultPolicy(final LicensePolicyEnforcer defaultPolicy) {
		this.defaultPolicy = defaultPolicy;
	}
}