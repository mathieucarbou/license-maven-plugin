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
package com.mycila.maven.plugin.license.document;

import com.mycila.maven.plugin.license.header.HeaderDefinition;

import java.io.File;
import java.util.Map;

/**
 * <b>Date:</b> 14-Feb-2008<br>
 * <b>Author:</b> Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class DocumentFactory {
    private final Map<String, String> mapping;
    private final Map<String, HeaderDefinition> definitions;
    private final File basedir;
    private final String encoding;
    private final String[] keywords;
    private final DocumentPropertiesLoader documentPropertiesLoader;

    public DocumentFactory(File basedir, Map<String, String> mapping, Map<String, HeaderDefinition> definitions, String encoding, String[] keywords, DocumentPropertiesLoader documentPropertiesLoader) {
        this.mapping = mapping;
        this.definitions = definitions;
        this.basedir = basedir;
        this.encoding = encoding;
        this.keywords = keywords.clone();
        this.documentPropertiesLoader = documentPropertiesLoader;
    }

    public Document createDocuments(String file) {
        return getWrapper(file);
    }

    private Document getWrapper(String file) {
        String headerType = mapping.get("");
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            if (file.toLowerCase().endsWith("." + entry.getKey().toLowerCase())) {
                headerType = entry.getValue().toLowerCase();
                break;
            }
        }
        return new Document(new File(basedir, file), definitions.get(headerType), encoding, keywords, documentPropertiesLoader);
    }

}
