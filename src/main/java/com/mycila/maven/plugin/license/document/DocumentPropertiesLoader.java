package com.mycila.maven.plugin.license.document;

import java.util.Properties;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2013-08-27
 */
public interface DocumentPropertiesLoader {
    Properties load(Document d);
}
