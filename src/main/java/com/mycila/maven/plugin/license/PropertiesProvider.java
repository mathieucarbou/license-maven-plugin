package com.mycila.maven.plugin.license;

import com.mycila.maven.plugin.license.document.Document;

import java.util.Map;
import java.util.Properties;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2013-08-27
 */
public interface PropertiesProvider {
    Map<String, String> getAdditionalProperties(AbstractLicenseMojo mojo, Properties currentProperties, Document document);
}
