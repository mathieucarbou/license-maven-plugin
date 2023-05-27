/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license.document;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

/**
 * Copy and simplification of Spring's PropertyPlaceholderHelper at
 * https://github.com/spring-projects/spring-framework/blob/master/spring-core/src/main/java/org/springframework/util/PropertyPlaceholderHelper.java
 * <p>
 * Utility class for working with Strings that have placeholder values in them. A placeholder takes the form
 * {@code ${name}}. Using {@code PropertyPlaceholderHelper} these placeholders can be substituted for
 * user-supplied values. <p> Values for substitution can be supplied using a {@link Properties} instance or
 * using a {@link Function}.
 *
 * @since 3.0
 */
class PropertyPlaceholderResolver {

  private final String placeholderPrefix = "${";
  private final String placeholderSuffix = "}";

  /**
   * Replace placeholders.
   *
   * @param value the value
   * @param properties the properties
   * @return the string
   */
  public String replacePlaceholders(String value, final Map<String, String> properties) {
    return replacePlaceholders(value, properties::get);
  }

  private String replacePlaceholders(String value, Function<String, String> placeholderResolver) {
    return parseStringValue(value, placeholderResolver, new HashSet<>());
  }

  private String parseStringValue(String value, Function<String, String> placeholderResolver,
                                  Set<String> visitedPlaceholders) {
    StringBuilder result = new StringBuilder(value);
    int startIndex = value.indexOf(this.placeholderPrefix);
    while (startIndex != -1) {
      int endIndex = findPlaceholderEndIndex(result, startIndex);
      if (endIndex != -1) {
        String placeholder = result.substring(startIndex + this.placeholderPrefix.length(), endIndex);
        String originalPlaceholder = placeholder;
        if (!visitedPlaceholders.add(originalPlaceholder)) {
          throw new IllegalArgumentException("Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
        }
        // Recursive invocation, parsing placeholders contained in the placeholder key.
        placeholder = parseStringValue(placeholder, placeholderResolver, visitedPlaceholders);
        // Now obtain the value for the fully resolved key...
        String propVal = placeholderResolver.apply(placeholder);
        String valueSeparator = ":";
        if (propVal == null) {
          int separatorIndex = placeholder.indexOf(valueSeparator);
          if (separatorIndex != -1) {
            String actualPlaceholder = placeholder.substring(0, separatorIndex);
            String defaultValue = placeholder.substring(separatorIndex + valueSeparator.length());
            propVal = placeholderResolver.apply(actualPlaceholder);
            if (propVal == null) {
              propVal = defaultValue;
            }
          }
        }
        if (propVal != null) {
          // Recursive invocation, parsing placeholders contained in the
          // previously resolved placeholder value.
          propVal = parseStringValue(propVal, placeholderResolver, visitedPlaceholders);
          result.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);
          startIndex = result.indexOf(this.placeholderPrefix, startIndex + propVal.length());
        } else {
          // Proceed with unprocessed value.
          startIndex = result.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
        }
        visitedPlaceholders.remove(originalPlaceholder);
      } else {
        startIndex = -1;
      }
    }

    return result.toString();
  }

  private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
    int index = startIndex + this.placeholderPrefix.length();
    int withinNestedPlaceholder = 0;
    while (index < buf.length()) {
      String simplePrefix = "{";
      if (substringMatch(buf, index, this.placeholderSuffix)) {
        if (withinNestedPlaceholder > 0) {
          withinNestedPlaceholder--;
          index = index + this.placeholderSuffix.length();
        } else {
          return index;
        }
      } else if (substringMatch(buf, index, simplePrefix)) {
        withinNestedPlaceholder++;
        index = index + simplePrefix.length();
      } else {
        index++;
      }
    }
    return -1;
  }

  private static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
    if (index + substring.length() > str.length()) {
      return false;
    }
    for (int i = 0; i < substring.length(); i++) {
      if (str.charAt(index + i) != substring.charAt(i)) {
        return false;
      }
    }
    return true;
  }
}
