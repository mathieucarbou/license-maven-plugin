# Comment Styles

The plugin needs to know how to wrap the license text in the appropriate comment syntax for each file type. This is controlled by **comment style mappings** — a mapping from file extension to a named comment style.

## Default Mappings

The following extensions are recognized out of the box:

| Extension(s) | Comment Style | Example |
|---|---|---|
| `.java`, `.groovy`, `.kt`, `.scala`, `.js`, `.gradle`, `.kts` | `SLASHSTAR_STYLE` | `/* ... */` |
| `.css`, `.cs`, `.as`, `.aj`, `.c`, `.h`, `.cpp`, `.scss` | `JAVADOC_STYLE` | `/** ... */` |
| `.xml`, `.html`, `.htm`, `.xhtml`, `.pom`, `.xsd`, `.xsl`, `.wsdl`, `.kml`, `.tld`, `.dtd`, `.jspx` | `XML_STYLE` | `<!-- ... -->` |
| `.properties`, `.sh`, `.py`, `.rb`, `.pl`, `.yml`, `.yaml`, `.Dockerfile`, `.editorconfig` | `SCRIPT_STYLE` | `# ...` |
| `.ts` | `TRIPLESLASH_STYLE` | `/// ...` |
| `.sql`, `.adb`, `.ads`, `.e` | `DOUBLEDASHES_STYLE` | `-- ...` |
| `.jsp` | `DYNASCRIPT_STYLE` | `<%-- ... --%>` |
| `.bat`, `.cmd` | `BATCH` | `@REM ...` |
| `.txt` | `TEXT` | `==== ... ====` |
| `.lua` | `LUA` | `--[[ ... ]]` |
| `.php` | `PHP` | `/* ... */` (after `<?php`) |
| `.asp` | `ASP` | `<% ' ... %>` |
| `.ftl` | `FTL` | `<#-- ... -->` |
| `.vm` | `SHARPSTAR_STYLE` | `#* ... *#` |
| `.haml`, `.scaml` | `HAML_STYLE` | `-# ...` |
| `.pas` | `BRACESSTAR_STYLE` | `{* ... *}` |
| `.bas`, `.vba` | `APOSTROPHE_STYLE` | `' ...` |
| `.asm` | `SEMICOLON_STYLE` | `; ...` |
| `.f` | `EXCLAMATION_STYLE` | `! ...` |
| `.tex`, `.cls`, `.sty` | `PERCENT_STYLE` | `% ...` |
| `.erl`, `.hrl` | `PERCENT3_STYLE` | `%%% ...` |
| `.el` | `EXCLAMATION3_STYLE` | `!!! ...` |
| `.apt` | `DOUBLETILDE_STYLE` | `~~ ...` |
| `.adoc` | `ASCIIDOC_STYLE` | `////  // ... ////` |
| `.proto` | `DOUBLESLASH_STYLE` | `// ...` |
| `.clj`, `.cljs` | `SEMICOLON_STYLE` | `; ...` |
| `.mv` | `MVEL_STYLE` | `@comment{ ... }` |
| `.mustache` | `MUSTACHE_STYLE` | `{{! ... }}` |

## All Comment Styles Reference

| Style Name | First Line | Before Each Line | End Line | Description |
|---|---|---|---|---|
| `SLASHSTAR_STYLE` | `/*` | ` * ` | ` */` | JavaScript/Java block comment |
| `JAVADOC_STYLE` | `/**` | ` * ` | ` */` | Javadoc-style comment |
| `JAVAPKG_STYLE` | (after package) `/*-` | ` * ` | ` */` | Javadoc, skips first `package` line |
| `XML_STYLE` | `<!--` | `    ` | `-->` | XML/HTML comment |
| `XML_PER_LINE` | — | `<!-- ` | — (suffix ` -->`) | One XML comment per line |
| `SCRIPT_STYLE` | `#` | `# ` | `#` | Shell/Python/Ruby/YAML |
| `HAML_STYLE` | `-#` | `-# ` | `-#` | Haml/Scaml |
| `DOUBLESLASH_STYLE` | `//` | `// ` | `//` | Double-slash (e.g., Protobuf) |
| `SINGLE_LINE_DOUBLESLASH_STYLE` | — | `// ` | — | Single-line double-slash |
| `TRIPLESLASH_STYLE` | `///` | `/// ` | `///` | TypeScript triple-slash |
| `DOUBLEDASHES_STYLE` | `--` | `-- ` | `--` | SQL/Ada |
| `BRACESSTAR_STYLE` | `{*` | ` * ` | ` *}` | Delphi/Pascal |
| `SHARPSTAR_STYLE` | `#*` | ` * ` | ` *#` | Velocity |
| `DOUBLETILDE_STYLE` | `~~` | `~~ ` | `~~` | APT/Doxia |
| `DYNASCRIPT_STYLE` | `<%--` | `    ` | `--%>` | JSP |
| `DYNASCRIPT3_STYLE` | `<!---` | `    ` | `--->` | ColdFusion |
| `PERCENT_STYLE` | — | `% ` | — | TeX/LaTeX |
| `PERCENT3_STYLE` | `%%%` | `%%% ` | `%%%` | Erlang |
| `EXCLAMATION_STYLE` | `!` | `! ` | `!` | Fortran |
| `EXCLAMATION3_STYLE` | `!!!` | `!!! ` | `!!!` | Lisp/Emacs |
| `SEMICOLON_STYLE` | `;` | `; ` | `;` | Assembler/Clojure |
| `APOSTROPHE_STYLE` | `'` | `' ` | `'` | VisualBasic |
| `BATCH` | `@REM` | `@REM ` | `@REM` | Windows batch |
| `TEXT` | `====` | `    ` | `====` | Plain text |
| `LUA` | `--[[` | `    ` | `]]` | Lua |
| `ASP` | `<%` | `' ` | `%>` | ASP |
| `PHP` | `/*` | ` * ` | ` */` | PHP (inserted after `<?php`) |
| `FTL` | `<#--` | `    ` | `-->` | FreeMarker |
| `FTL_ALT` | `[#--` | `    ` | `--]` | FreeMarker (alternative syntax) |
| `ASCIIDOC_STYLE` | `////` | `  // ` | `////` | AsciiDoc |
| `MVEL_STYLE` | `@comment{` | `  ` | `}` | MVEL |
| `MUSTACHE_STYLE` | `{{!` | `    ` | `}}` | Mustache |

## Custom Mappings

### Map an Extension to an Existing Style

```xml
<configuration>
  <mapping>
    <jwc>XML_STYLE</jwc>
    <application>XML_STYLE</application>
    <vm>SHARPSTAR_STYLE</vm>
  </mapping>
</configuration>
```

The tag name is the file extension; the value is the style name (case-insensitive).

### Compound Extensions

Compound extensions like `apt.vm` are supported. The order in the `<mapping>` section matters — earlier entries take precedence:

```xml
<mapping>
  <!-- Check apt.vm before vm -->
  <apt.vm>DOUBLETILDE_STYLE</apt.vm>
  <vm>SHARPSTAR_STYLE</vm>
</mapping>
```

### Java Packages Style

When source files contain a `package` declaration, the license header should come *after* it. Use `JAVAPKG_STYLE`:

```xml
<mapping>
  <java>JAVAPKG_STYLE</java>
</mapping>
```

This produces:

```java
package com.example;
/*-
 * Copyright (C) 2024 Acme Corp
 * ...
 */
```

### Disable Default Mappings

To use *only* your custom mappings and ignore all built-in defaults:

```xml
<configuration>
  <useDefaultMapping>false</useDefaultMapping>
  <mapping>
    <java>SLASHSTAR_STYLE</java>
    <xml>XML_STYLE</xml>
  </mapping>
</configuration>
```
