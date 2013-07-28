/**
 * Copyright (C) 2008 http://code.google.com/p/maven-license-plugin/
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

package com.google.code.mojo.license.header;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class HeaderTypeTest {
    @Test
    public void test() throws Exception {
        assertEquals(HeaderType.ASP.getDefinition().getType(), "asp");
        assertEquals(HeaderType.defaultDefinitions().size(), 25);
        System.out.println(HeaderType.defaultDefinitions().keySet());
    }
}
