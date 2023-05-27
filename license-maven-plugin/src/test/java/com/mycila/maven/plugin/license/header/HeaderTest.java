/**
 * Copyright (C) 2008 Mycila (mathieu.carbou@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.maven.plugin.license.header;

import com.mycila.maven.plugin.license.HeaderSection;
import com.mycila.maven.plugin.license.header.HeaderSource.UrlHeaderSource;
import com.mycila.maven.plugin.license.util.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class HeaderTest {
  @Test
  void test() throws Exception {
    Header header = new Header(new UrlHeaderSource(getClass().getResource("/test-header1.txt"), "UTF-8"), null);
    Assertions.assertEquals(13, header.getLineCount());
    Assertions.assertTrue(header.asOneLineString().contains("${year}"));
    Assertions.assertTrue(header.getLocation().isFromUrl(getClass().getResource("/test-header1.txt")));

    //FileUtils.write(new File("src/test/resources/test-header2.txt"), header.buildForDefinition(HeaderType.ASP.getDefinition(), false));

    final File file = new File("src/test/resources/test-header2.txt");
    final String content = FileUtils.read(file, System.getProperty("file.encoding"));
    Assertions.assertEquals(content, header.buildForDefinition(HeaderType.ASP.getDefinition(), content.indexOf("\n") == -1));
  }

  @Test
  void testHeaderSections() throws IOException {

    HeaderSection section = new HeaderSection();
    section.setKey("COPYRIGHT_SECTION");
    section.setEnsureMatch("Copyright \\(C\\) \\d{4} .*");
    section.setDefaultValue("Copyright (C) 2011 http://code.google.com/p/maven-license-plugin/");

    HeaderSection[] sections = {section};

    Header header = new Header(new UrlHeaderSource(getClass().getResource("/test-header5.txt"), "UTF-8"), sections);

    HeaderDefinition headerDefinition = HeaderType.JAVADOC_STYLE.getDefinition();

    StringBuilder h1 = new StringBuilder();
    h1.append("/**\n");
    h1.append(" * Copyright (C) 2011 Some Guy\n");
    h1.append(" *\n");
    h1.append(" * My License\n");
    h1.append(" */\n");
    Assertions.assertTrue(header.isMatchForText(h1.toString(), headerDefinition, true));

    /**
     * This potential header should fail because the date is invalid
     */
    StringBuilder h2 = new StringBuilder();
    h2.append("/**\n");
    h2.append(" * Copyright (C) 201 Some Guy\n");
    h2.append(" *\n");
    h2.append(" * My License\n");
    h2.append(" */\n");
    Assertions.assertFalse(header.isMatchForText(h2.toString(), headerDefinition, true));

    /**
     * This potential header should match, even with multiple lines
     */
    StringBuilder h3 = new StringBuilder();
    h3.append("/**\n");
    h3.append(" * Copyright (C) 2011 Some Guy\n");
    h3.append(" * Copyright (C) 2011 Some Other Guy\n");
    h3.append(" *\n");
    h3.append(" * My License\n");
    h3.append(" */\n");
    Assertions.assertTrue(header.isMatchForText(h3.toString(), headerDefinition, true));

    /**
     * Assert that the header, rendered with default values, looks correct
     */
    StringBuilder h4 = new StringBuilder();
    h4.append("/**\n");
    h4.append(" * Copyright (C) 2011 http://code.google.com/p/maven-license-plugin/\n");
    h4.append(" *\n");
    h4.append(" * My License\n");
    h4.append(" */\n");
    String headerText = header.applyDefinitionAndSections(headerDefinition, true);
    Assertions.assertEquals(headerText, h4.toString());
  }

  @Test
  void testHeaderSectionsWithAmbiguousSeparation() throws IOException {

    HeaderSection sectionA = new HeaderSection();
    sectionA.setKey("COPYRIGHT_SECTION");
    sectionA.setDefaultValue("Copyright (C) 2011");

    HeaderSection sectionB = new HeaderSection();
    sectionB.setKey("NAME_SECTION");
    sectionB.setEnsureMatch("\\w+");
    sectionB.setDefaultValue("SomeGuy");

    HeaderSection[] sections = {sectionA, sectionB};

    Header header = new Header(new UrlHeaderSource(getClass().getResource("/test-header6.txt"), "UTF-8"), sections);

    HeaderDefinition headerDefinition = HeaderType.JAVADOC_STYLE.getDefinition();

    /**
     * This is a test of matching because the " " between COPYRIGHT_SECTION
     * and NAME_SECTION in the header could potentially be matched in
     * multiple places, but only one will result in a valid match that
     * allows the NAME_SECTION validation regex to match correctly. This is
     * admittedly a totally contrived example, but you never know what users
     * might do in practice.
     */
    StringBuilder h1 = new StringBuilder();
    h1.append("/**\n");
    h1.append(" * Copyright (C) 2011 SomeGuy\n");
    h1.append(" *\n");
    h1.append(" * My License\n");
    h1.append(" */\n");
    Assertions.assertTrue(header.isMatchForText(h1.toString(), headerDefinition, true));

    /**
     * This potential header should fail because there is no space in there to match
     */
    StringBuilder h2 = new StringBuilder();
    h2.append("/**\n");
    h2.append(" * Copyright(C)2011SomeGuy\n");
    h2.append(" *\n");
    h2.append(" * My License\n");
    h2.append(" */\n");
    Assertions.assertFalse(header.isMatchForText(h2.toString(), headerDefinition, true));

    /**
     * Assert that the header, rendered with default values, looks correct
     */
    StringBuilder h3 = new StringBuilder();
    h3.append("/**\n");
    h3.append(" * Copyright (C) 2011 SomeGuy\n");
    h3.append(" *\n");
    h3.append(" * My License\n");
    h3.append(" */\n");
    String headerText = header.applyDefinitionAndSections(headerDefinition, true);
    Assertions.assertEquals(headerText, h3.toString());
  }

  @Test
  void testHeaderSectionsWithMultiLineMatch() throws IOException {

    HeaderSection section = new HeaderSection();
    section.setKey("COPYRIGHT_SECTION");
    section.setEnsureMatch("Copyright \\(C\\) \\d{4}\nName: .*");
    section.setMultiLineMatch(true);
    section.setDefaultValue("Copyright (C) 2011\nName: http://code.google.com/p/maven-license-plugin/");

    HeaderSection[] sections = {section};

    Header header = new Header(new UrlHeaderSource(getClass().getResource("/test-header5.txt"), "UTF-8"), sections);

    HeaderDefinition headerDefinition = HeaderType.JAVADOC_STYLE.getDefinition();

    StringBuilder h1 = new StringBuilder();
    h1.append("/**\n");
    h1.append(" * Copyright (C) 2011\n");
    h1.append(" * Name: Some Guy\n");
    h1.append(" *\n");
    h1.append(" * My License\n");
    h1.append(" */\n");
    Assertions.assertTrue(header.isMatchForText(h1.toString(), headerDefinition, true));

    /**
     * This potential header should fail because "Name" => "NamE"
     */
    StringBuilder h2 = new StringBuilder();
    h2.append("/**\n");
    h2.append(" * Copyright (C) 2011\n");
    h2.append(" * NamE: Some Guy\n");
    h2.append(" *\n");
    h2.append(" * My License\n");
    h2.append(" */\n");
    Assertions.assertFalse(header.isMatchForText(h2.toString(), headerDefinition, true));

    /**
     * Assert that the header, rendered with default values, looks correct
     */
    StringBuilder h3 = new StringBuilder();
    h3.append("/**\n");
    h3.append(" * Copyright (C) 2011\n");
    h3.append(" * Name: http://code.google.com/p/maven-license-plugin/\n");
    h3.append(" *\n");
    h3.append(" * My License\n");
    h3.append(" */\n");
    String headerText = header.applyDefinitionAndSections(headerDefinition, true);
    Assertions.assertEquals(headerText, h3.toString());
  }
}
