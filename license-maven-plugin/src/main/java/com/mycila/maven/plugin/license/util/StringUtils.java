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
package com.mycila.maven.plugin.license.util;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static String rtrim(java.lang.String s) {
        int i;
        for (i = s.length() - 1; i >= 0; i--) {
            if (Character.isWhitespace(s.charAt(i))) {
                continue;
            }
            break;
        }
        return s.substring(0, i + 1);
    }

    public static String padRight(String s, int len) {
        if (s == null || s.length() >= len) {
            return s;
        }

        StringBuilder sb = new StringBuilder(len);
        sb.append(s);
        for (int i = s.length(); i < len; i++) {
          sb.append(' ');
        }
        return sb.toString();
    }
}
