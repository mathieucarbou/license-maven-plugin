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
package com.mycila.maven.plugin.license.dependencies;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.Optional;

/**
 * A policy decision based on some matcher/value and type. Different policy
 * enforcers should take this class as a constructor argument.
 */
public class LicensePolicy {

  /**
   * The Enum Type.
   */
  public enum Type {
    /** The license name. */
    LICENSE_NAME,

    /** The license url. */
    LICENSE_URL,

    /** The artifact pattern. */
    ARTIFACT_PATTERN;
  }

  /**
   * The Enum Rule.
   */
  public enum Rule {

    /** The approve. */
    APPROVE(true),

    /** The deny. */
    DENY(false);

    boolean allowed;

    Rule(final boolean allowed) {
      this.allowed = allowed;
    }

    /**
     * Get a boolean form of a rule.
     *
     * @return the predicate
     */
    public boolean getPredicate() {
      return allowed;
    }

    /**
     * Simple policy decision based on whether a matcher succeeded.
     *
     * @param matched - boolean result of some matching operation.
     * @return true, if is allowed
     */
    public boolean isAllowed(final boolean matched) {
      return matched == allowed;
    }

    /**
     * Value of.
     *
     * @param allowed the allowed
     * @return the rule
     */
    public static Rule valueOf(final boolean allowed) {
      if (allowed) {
        return APPROVE;
      } else {
        return DENY;
      }
    }
  }

  @Parameter
  private Type type;
  @Parameter
  private Rule rule;
  @Parameter
  private String value;

  /**
   * Instantiates a new license policy.
   */
  // only here for plexus container injection by maven
  public LicensePolicy() {
  }

  /**
   * Instantiates a new license policy.
   *
   * @param rule the rule
   * @param type the type
   * @param value the value
   */
  public LicensePolicy(final Rule rule, final Type type, final String value) {
    this.setRule(rule);
    this.setType(type);
    this.setValue(value);
  }

  @Override
  public int hashCode() {
    return 11 * (rule.hashCode() + type.hashCode() + value.hashCode());
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null) {
      return false;
    } else {
      return (other.hashCode() == hashCode());
    }
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * Gets the rule.
   *
   * @return the rule
   */
  public Rule getRule() {
    return rule;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public Type getType() {
    return type;
  }

  private void setType(Type type) {
    this.type = type;
  }

  private void setRule(Rule rule) {
    this.rule = Optional.ofNullable(rule).orElse(Rule.DENY);
  }

  private void setValue(String value) {
    this.value = Optional.ofNullable(value).orElse("");
  }

  @Override
  public String toString() {
    return String.format("%s:%s:%s", getType(), getRule(), getValue());
  }
}
