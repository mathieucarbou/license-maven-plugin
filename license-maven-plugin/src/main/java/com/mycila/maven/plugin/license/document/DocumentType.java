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
package com.mycila.maven.plugin.license.document;

import com.mycila.maven.plugin.license.header.HeaderType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <b>Date:</b> 16-Feb-2008<br>
 * <b>Author:</b> Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public enum DocumentType {
  ////////// Document types ordered alphabetically //////////

  ACTIONSCRIPT("as", HeaderType.JAVADOC_STYLE),
  ADA_BODY("adb", HeaderType.DOUBLEDASHES_STYLE),
  ADA_SPEC("ads", HeaderType.DOUBLEDASHES_STYLE),
  ASCII_DOC("adoc", HeaderType.ASCIIDOC_STYLE),
  ASP("asp", HeaderType.ASP),
  ASPECTJ("aj", HeaderType.JAVADOC_STYLE),
  ASSEMBLER("asm", HeaderType.SEMICOLON_STYLE),
  C("c", HeaderType.JAVADOC_STYLE),
  CLOJURE("clj", HeaderType.SEMICOLON_STYLE),
  CLOJURESCRIPT("cljs", HeaderType.SEMICOLON_STYLE),
  COLDFUSION_COMPONENT("cfc", HeaderType.DYNASCRIPT3_STYLE),
  COLDFUSION_ML("cfm", HeaderType.DYNASCRIPT3_STYLE),
  CPP("cpp", HeaderType.JAVADOC_STYLE),
  CSHARP("cs", HeaderType.JAVADOC_STYLE),
  CSS("css", HeaderType.JAVADOC_STYLE),
  DELPHI("pas", HeaderType.BRACESSTAR_STYLE),
  DOCKERFILE("Dockerfile", HeaderType.SCRIPT_STYLE),
  DOXIA_APT("apt", HeaderType.DOUBLETILDE_STYLE),
  DOXIA_FAQ("fml", HeaderType.XML_STYLE),
  DTD("dtd", HeaderType.XML_STYLE),
  EDITORCONFIG(".editorconfig", HeaderType.SCRIPT_STYLE),
  EIFFEL("e", HeaderType.DOUBLEDASHES_STYLE),
  ERLANG("erl", HeaderType.PERCENT3_STYLE),
  ERLANG_HEADER("hrl", HeaderType.PERCENT3_STYLE),
  FORTRAN("f", HeaderType.EXCLAMATION_STYLE),
  FREEMARKER("ftl", HeaderType.FTL),
  GROOVY("groovy", HeaderType.SLASHSTAR_STYLE),
  GSP("GSP", HeaderType.XML_STYLE),
  H("h", HeaderType.JAVADOC_STYLE),
  HAML("haml", HeaderType.HAML_STYLE),
  HTM("htm", HeaderType.XML_STYLE),
  HTML("html", HeaderType.XML_STYLE),
  JAVA("java", HeaderType.SLASHSTAR_STYLE),
  JAVAFX("fx", HeaderType.SLASHSTAR_STYLE),
  JAVASCRIPT("js", HeaderType.SLASHSTAR_STYLE),
  JSP("jsp", HeaderType.DYNASCRIPT_STYLE),
  JSPX("jspx", HeaderType.XML_STYLE),
  KML("kml", HeaderType.XML_STYLE),
  KOTLIN("kt", HeaderType.SLASHSTAR_STYLE),
  LISP("el", HeaderType.EXCLAMATION3_STYLE),
  LUA("lua", HeaderType.LUA),
  MUSTACHE("mustache", HeaderType.MUSTACHE_STYLE),
  MVEL("mv", HeaderType.MVEL_STYLE),
  MXML("mxml", HeaderType.XML_STYLE),
  PERL("pl", HeaderType.SCRIPT_STYLE),
  PERL_MODULE("pm", HeaderType.SCRIPT_STYLE),
  PHP("php", HeaderType.PHP),
  POM("pom", HeaderType.XML_STYLE),
  PROPERTIES("properties", HeaderType.SCRIPT_STYLE),
  PYTHON("py", HeaderType.SCRIPT_STYLE),
  RUBY("rb", HeaderType.SCRIPT_STYLE),
  SCALA("scala", HeaderType.SLASHSTAR_STYLE),
  SCAML("scaml", HeaderType.HAML_STYLE),
  SCSS("scss", HeaderType.JAVADOC_STYLE),
  SHELL("sh", HeaderType.SCRIPT_STYLE),
  SPRING_FACTORIES("spring.factories", HeaderType.SCRIPT_STYLE),
  SQL("sql", HeaderType.DOUBLEDASHES_STYLE),
  TAGX("tagx", HeaderType.XML_STYLE),
  TEX_CLASS("cls", HeaderType.PERCENT_STYLE),
  TEX_STYLE("sty", HeaderType.PERCENT_STYLE),
  TEX("tex", HeaderType.PERCENT_STYLE),
  TLD("tld", HeaderType.XML_STYLE),
  TS("ts", HeaderType.TRIPLESLASH_STYLE),
  TXT("txt", HeaderType.TEXT),
  UNKNOWN("", HeaderType.UNKNOWN),
  VB("bas", HeaderType.HAML_STYLE),
  VBA("vba", HeaderType.APOSTROPHE_STYLE),
  VELOCITY("vm", HeaderType.SHARPSTAR_STYLE),
  WINDOWS_BATCH("bat", HeaderType.BATCH),
  WINDOWS_SHELL("cmd", HeaderType.BATCH),
  WSDL("wsdl", HeaderType.XML_STYLE),
  XHTML("xhtml", HeaderType.XML_STYLE),
  XML("xml", HeaderType.XML_STYLE),
  XSD("xsd", HeaderType.XML_STYLE),
  XSL("xsl", HeaderType.XML_STYLE),
  XSLT("xslt", HeaderType.XML_STYLE),
  YAML("yaml", HeaderType.SCRIPT_STYLE),
  YML("yml", HeaderType.SCRIPT_STYLE);

  ////////////////////////////////////

  private static final Map<String, String> MAPPING = new LinkedHashMap<String, String>(values().length);

  static {
    for (DocumentType type : values()) {
      MAPPING.put(type.getExtension(), type.getDefaultHeaderTypeName());
    }
  }

  private final String extension;
  private final HeaderType defaultHeaderType;

  private DocumentType(String extension, HeaderType defaultHeaderType) {
    this.extension = extension;
    this.defaultHeaderType = defaultHeaderType;
  }

  public String getExtension() {
    return extension;
  }

  public HeaderType getDefaultHeaderType() {
    return defaultHeaderType;
  }

  public String getDefaultHeaderTypeName() {
    return defaultHeaderType.name().toLowerCase();
  }

  public static Map<String, String> defaultMapping() {
    return Collections.unmodifiableMap(MAPPING);
  }
}
