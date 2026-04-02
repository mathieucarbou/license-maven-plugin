# License Templates

The plugin ships with ready-to-use templates for the most common open-source licenses. Each template uses [property placeholders](properties.md) that are resolved at runtime.

## Using a Built-in Template

Reference templates by their classpath path:

```xml
<header>com/mycila/maven/plugin/license/templates/APACHE-2.txt</header>
```

## Available Templates

| Template File | License |
|---|---|
| `AGPL-3.txt` | GNU Affero General Public License v3 |
| `APACHE-2.txt` | Apache License 2.0 |
| `APACHE-2-noemail.txt` | Apache License 2.0 (without email placeholder) |
| `Artistic-2.0.txt` | Artistic License 2.0 |
| `BSD-2.txt` | BSD 2-Clause License |
| `BSD-3.txt` | BSD 3-Clause License |
| `BSD-4.txt` | BSD 4-Clause License |
| `BUSL-11.txt` | Business Source License 1.1 |
| `CC0-1.0.txt` | Creative Commons Zero v1.0 Universal |
| `CDDL-1.0.txt` | Common Development and Distribution License 1.0 |
| `COMMONS-CLAUSE-1.txt` | Commons Clause License Condition v1.0 |
| `CPAL.txt` | Common Public Attribution License 1.0 |
| `EPL-1.txt` | Eclipse Public License 1.0 |
| `EPL-2.txt` | Eclipse Public License 2.0 |
| `EUPL-1.txt` | European Union Public License 1.0 |
| `EUPL-1.1.txt` | European Union Public License 1.1 |
| `EUPL-1.2.txt` | European Union Public License 1.2 |
| `GPL-2-ONLY.txt` | GNU General Public License v2 only |
| `GPL-2.txt` | GNU General Public License v2 or later |
| `GPL-3-ONLY.txt` | GNU General Public License v3 only |
| `GPL-3.txt` | GNU General Public License v3 or later |
| `ISC.txt` | ISC License |
| `LGPL-21-ONLY.txt` | GNU Lesser General Public License v2.1 only |
| `LGPL-21.txt` | GNU Lesser General Public License v2.1 or later |
| `LGPL-3-ONLY.txt` | GNU Lesser General Public License v3 only |
| `LGPL-3.txt` | GNU Lesser General Public License v3 or later |
| `MirOS.txt` | MirOS License |
| `MIT.txt` | MIT License |
| `MPL-1.txt` | Mozilla Public License 1.0 |
| `MPL-2.txt` | Mozilla Public License 2.0 |
| `SSPL-1.txt` | Server Side Public License v1 |
| `UNLICENSE.txt` | The Unlicense |
| `WTFPL.txt` | Do What The F*ck You Want To Public License |

All templates are [available on GitHub](https://github.com/mathieucarbou/license-maven-plugin/tree/master/license-maven-plugin/src/main/resources/com/mycila/maven/plugin/license/templates).

## Template Placeholders

Templates use `${property}` placeholders. Common ones used across the built-in templates:

| Placeholder | Description |
|---|---|
| `${year}` | Current year (or the year from POM `inceptionYear`) |
| `${owner}` | Set via `<properties><owner>…</owner></properties>` |
| `${email}` | Set via `<properties><email>…</email></properties>` |
| `${project.name}` | Maven `<name>` from the POM |
| `${project.description}` | Maven `<description>` from the POM |

See [Properties & Placeholders](properties.md) for the full reference.

## Custom Header Files

You can provide your own header template — any file accessible on the filesystem, classpath, or via URL:

```xml
<!-- Classpath resource (from plugin dependencies) -->
<header>com/example/headers/my-header.txt</header>

<!-- URL -->
<header>https://example.com/license-header.txt</header>
```

### Example: Apache 2.0 Template

```
Copyright © ${year} ${owner} (${email})

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

### Example: MIT Template

```
The MIT License
Copyright © ${year} ${owner}

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
...
```
