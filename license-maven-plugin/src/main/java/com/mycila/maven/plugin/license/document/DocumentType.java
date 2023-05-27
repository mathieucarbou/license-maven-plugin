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

  /** The actionscript. */
  ACTIONSCRIPT("as", HeaderType.JAVADOC_STYLE),

  /** The ada body. */
  ADA_BODY("adb", HeaderType.DOUBLEDASHES_STYLE),

  /** The ada spec. */
  ADA_SPEC("ads", HeaderType.DOUBLEDASHES_STYLE),

  /** The ascii doc. */
  ASCII_DOC("adoc", HeaderType.ASCIIDOC_STYLE),

  /** The asp. */
  ASP("asp", HeaderType.ASP),

  /** The aspectj. */
  ASPECTJ("aj", HeaderType.JAVADOC_STYLE),

  /** The assembler. */
  ASSEMBLER("asm", HeaderType.SEMICOLON_STYLE),

  /** The c. */
  C("c", HeaderType.JAVADOC_STYLE),

  /** The clojure. */
  CLOJURE("clj", HeaderType.SEMICOLON_STYLE),

  /** The clojurescript. */
  CLOJURESCRIPT("cljs", HeaderType.SEMICOLON_STYLE),

  /** The coldfusion component. */
  COLDFUSION_COMPONENT("cfc", HeaderType.DYNASCRIPT3_STYLE),

  /** The coldfusion ml. */
  COLDFUSION_ML("cfm", HeaderType.DYNASCRIPT3_STYLE),

  /** The cpp. */
  CPP("cpp", HeaderType.JAVADOC_STYLE),

  /** The csharp. */
  CSHARP("cs", HeaderType.JAVADOC_STYLE),

  /** The css. */
  CSS("css", HeaderType.JAVADOC_STYLE),

  /** The delphi. */
  DELPHI("pas", HeaderType.BRACESSTAR_STYLE),

  /** The dockerfile. */
  DOCKERFILE("Dockerfile", HeaderType.SCRIPT_STYLE),

  /** The doxia apt. */
  DOXIA_APT("apt", HeaderType.DOUBLETILDE_STYLE),

  /** The doxia faq. */
  DOXIA_FAQ("fml", HeaderType.XML_STYLE),

  /** The dtd. */
  DTD("dtd", HeaderType.XML_STYLE),

  /** The editorconfig. */
  EDITORCONFIG(".editorconfig", HeaderType.SCRIPT_STYLE),

  /** The eiffel. */
  EIFFEL("e", HeaderType.DOUBLEDASHES_STYLE),

  /** The erlang. */
  ERLANG("erl", HeaderType.PERCENT3_STYLE),

  /** The erlang header. */
  ERLANG_HEADER("hrl", HeaderType.PERCENT3_STYLE),

  /** The fortran. */
  FORTRAN("f", HeaderType.EXCLAMATION_STYLE),

  /** The freemarker. */
  FREEMARKER("ftl", HeaderType.FTL),

  /** The groovy. */
  GROOVY("groovy", HeaderType.SLASHSTAR_STYLE),

  /** The gsp. */
  GSP("GSP", HeaderType.XML_STYLE),

  /** The h. */
  H("h", HeaderType.JAVADOC_STYLE),

  /** The haml. */
  HAML("haml", HeaderType.HAML_STYLE),

  /** The htm. */
  HTM("htm", HeaderType.XML_STYLE),

  /** The html. */
  HTML("html", HeaderType.XML_STYLE),

  /** The java. */
  JAVA("java", HeaderType.SLASHSTAR_STYLE),

  /** The javafx. */
  JAVAFX("fx", HeaderType.SLASHSTAR_STYLE),

  /** The javascript. */
  JAVASCRIPT("js", HeaderType.SLASHSTAR_STYLE),

  /** The jsp. */
  JSP("jsp", HeaderType.DYNASCRIPT_STYLE),

  /** The jspx. */
  JSPX("jspx", HeaderType.XML_STYLE),

  /** The kml. */
  KML("kml", HeaderType.XML_STYLE),

  /** The kotlin. */
  KOTLIN("kt", HeaderType.SLASHSTAR_STYLE),

  /** The lisp. */
  LISP("el", HeaderType.EXCLAMATION3_STYLE),

  /** The lua. */
  LUA("lua", HeaderType.LUA),

  /** The mustache. */
  MUSTACHE("mustache", HeaderType.MUSTACHE_STYLE),

  /** The mvel. */
  MVEL("mv", HeaderType.MVEL_STYLE),

  /** The mxml. */
  MXML("mxml", HeaderType.XML_STYLE),

  /** The perl. */
  PERL("pl", HeaderType.SCRIPT_STYLE),

  /** The perl module. */
  PERL_MODULE("pm", HeaderType.SCRIPT_STYLE),

  /** The php. */
  PHP("php", HeaderType.PHP),

  /** The pom. */
  POM("pom", HeaderType.XML_STYLE),

  /** The properties. */
  PROPERTIES("properties", HeaderType.SCRIPT_STYLE),

  /** The python. */
  PYTHON("py", HeaderType.SCRIPT_STYLE),

  /** The ruby. */
  RUBY("rb", HeaderType.SCRIPT_STYLE),

  /** The scala. */
  SCALA("scala", HeaderType.SLASHSTAR_STYLE),

  /** The scaml. */
  SCAML("scaml", HeaderType.HAML_STYLE),

  /** The scss. */
  SCSS("scss", HeaderType.JAVADOC_STYLE),

  /** The shell. */
  SHELL("sh", HeaderType.SCRIPT_STYLE),

  /** The spring factories. */
  SPRING_FACTORIES("spring.factories", HeaderType.SCRIPT_STYLE),

  /** The sql. */
  SQL("sql", HeaderType.DOUBLEDASHES_STYLE),

  /** The tagx. */
  TAGX("tagx", HeaderType.XML_STYLE),

  /** The tex class. */
  TEX_CLASS("cls", HeaderType.PERCENT_STYLE),

  /** The tex style. */
  TEX_STYLE("sty", HeaderType.PERCENT_STYLE),

  /** The tex. */
  TEX("tex", HeaderType.PERCENT_STYLE),

  /** The tld. */
  TLD("tld", HeaderType.XML_STYLE),

  /** The ts. */
  TS("ts", HeaderType.TRIPLESLASH_STYLE),

  /** The txt. */
  TXT("txt", HeaderType.TEXT),

  /** The unknown. */
  UNKNOWN("", HeaderType.UNKNOWN),

  /** The vb. */
  VB("bas", HeaderType.HAML_STYLE),

  /** The vba. */
  VBA("vba", HeaderType.APOSTROPHE_STYLE),

  /** The velocity. */
  VELOCITY("vm", HeaderType.SHARPSTAR_STYLE),

  /** The windows batch. */
  WINDOWS_BATCH("bat", HeaderType.BATCH),

  /** The windows shell. */
  WINDOWS_SHELL("cmd", HeaderType.BATCH),

  /** The wsdl. */
  WSDL("wsdl", HeaderType.XML_STYLE),

  /** The xhtml. */
  XHTML("xhtml", HeaderType.XML_STYLE),

  /** The xml. */
  XML("xml", HeaderType.XML_STYLE),

  /** The xsd. */
  XSD("xsd", HeaderType.XML_STYLE),

  /** The xsl. */
  XSL("xsl", HeaderType.XML_STYLE),

  /** The xslt. */
  XSLT("xslt", HeaderType.XML_STYLE),

  /** The yaml. */
  YAML("yaml", HeaderType.SCRIPT_STYLE),

  /** The yml. */
  YML("yml", HeaderType.SCRIPT_STYLE);

  ////////////////////////////////////

  /** The Constant MAPPING. */
  private static final Map<String, String> MAPPING = new LinkedHashMap<>(values().length);

  static {
    for (DocumentType type : values()) {
      MAPPING.put(type.getExtension(), type.getDefaultHeaderTypeName());
    }
  }

  /** The extension. */
  private final String extension;

  /** The default header type. */
  private final HeaderType defaultHeaderType;

  /**
   * Instantiates a new document type.
   *
   * @param extension the extension
   * @param defaultHeaderType the default header type
   */
  private DocumentType(String extension, HeaderType defaultHeaderType) {
    this.extension = extension;
    this.defaultHeaderType = defaultHeaderType;
  }

  /**
   * Gets the extension.
   *
   * @return the extension
   */
  public String getExtension() {
    return extension;
  }

  /**
   * Gets the default header type.
   *
   * @return the default header type
   */
  public HeaderType getDefaultHeaderType() {
    return defaultHeaderType;
  }

  /**
   * Gets the default header type name.
   *
   * @return the default header type name
   */
  public String getDefaultHeaderTypeName() {
    return defaultHeaderType.name().toLowerCase();
  }

  /**
   * Default mapping.
   *
   * @return the map
   */
  public static Map<String, String> defaultMapping() {
    return Collections.unmodifiableMap(MAPPING);
  }
}
