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
package com.mycila.maven.plugin.license.header;

import com.mycila.xmltool.CallBack;
import com.mycila.xmltool.XMLTag;

import java.util.HashMap;
import java.util.Map;

/**
 * The class <code>AdditionalHeaderDefinition</code> is used to collect header definitions declared in an "external" XML
 * document configuration.
 * <p/>
 * The XML document must respect the following XML schema: <code>
 * <pre> &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;xs:schema attributeFormDefault="unqualified" elementFormDefault="qualified"
 *          xmlns:xs="http://www.w3.org/2001/XMLSchema"&gt;
 *   &lt;xs:element name="additionalHeaders" type="additionalHeadersType"/&gt;
 *   &lt;xs:complexType name="additionalHeadersType"&gt;
 *       &lt;xs:sequence&gt;
 *           &lt;xs:annotation&gt;
 *               &lt;xs:documentation&gt;
 *               Replace typeName tag name with the name of the type of header definition you are defining.
 *               &lt;/xs:documentation&gt;
 *           &lt;/xs:annotation&gt;
 *           &lt;xs:element type="textType" name="typeName"/&gt;
 *       &lt;/xs:sequence&gt;
 *   &lt;/xs:complexType&gt;
 *   &lt;xs:complexType name="textType"&gt;
 *       &lt;xs:sequence&gt;
 *          &lt;xs:annotation&gt;
 *              &lt;xs:documentation&gt;
 *               Do not forget that you can use CDATA format as element value.
 *              &lt;/xs:documentation&gt;
 *           &lt;/xs:annotation&gt;
 *           &lt;xs:element type="xs:string" name="firstLine" minOccurs="1" maxOccurs="1"/&gt;
 *           &lt;xs:element type="xs:string" name="beforeEachLine" minOccurs="1" maxOccurs="1"/&gt;
 *           &lt;xs:element type="xs:string" name="endLine" minOccurs="1" maxOccurs="1"/&gt;
 *           &lt;xs:element type="xs:string" name="skipLine" minOccurs="0" maxOccurs="1"/&gt;
 *           &lt;xs:element type="xs:string" name="firstLineDetectionPattern" minOccurs="1" maxOccurs="1"/&gt;
 *           &lt;xs:element type="xs:string" name="lastLineDetectionPattern" minOccurs="1" maxOccurs="1"/&gt;
 *       &lt;/xs:sequence&gt;
 *   &lt;/xs:complexType&gt;
 *  &lt;/xs:schema&gt;
 * </pre>
 * </code>
 *
 * @author Cedric Pronzato
 */
public final class AdditionalHeaderDefinition {
    private final Map<String, HeaderDefinition> definitions = new HashMap<String, HeaderDefinition>();

    /**
     * Construct an <code>AdditionalHeaderDefinition<code> object using the given XML document as header definitions
     * input.
     *
     * @param doc The XML definition to read.
     */
    public AdditionalHeaderDefinition(XMLTag doc) {
        if (doc == null) {
            throw new IllegalArgumentException("The header definition XML document cannot be null");
        }
        doc.gotoRoot().forEachChild(new CallBack() {
            @Override
            public void execute(XMLTag doc) {
                final String type = doc.getCurrentTagName().toLowerCase();
                HeaderDefinition definition = definitions.get(type);
                if (definition == null) {
                    definition = new HeaderDefinition(type);
                    definitions.put(type, definition);
                }
                doc.forEachChild(new FeedProperty(definition));
                definition.validate();
            }
        });
    }

    /**
     * Returns the header definitions declared by the external header definition as a map using the header type name as
     * key.
     *
     * @return The header definitions declared.
     */
    public Map<String, HeaderDefinition> getDefinitions() {
        return definitions;
    }

    private static final class FeedProperty implements CallBack {
        private final HeaderDefinition definition;

        private FeedProperty(HeaderDefinition definition) {
            this.definition = definition;
        }

        @Override
        public void execute(XMLTag xmlDocument) {
            String value = xmlDocument.getText();
            if ("".equals(value)) // value can't be null
            {
                value = xmlDocument.getCDATA();
            }
            definition.setPropertyFromString(xmlDocument.getCurrentTagName(), value);
        }
    }
}
