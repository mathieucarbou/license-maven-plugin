import com.mycila.maven.plugin.license.AbstractLicenseMojo;
import com.mycila.maven.plugin.license.PropertiesProvider;
import com.mycila.maven.plugin.license.document.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2013-08-27
 */
public final class MyPropertiesProvider implements PropertiesProvider {
    @Override
    public Map<String, String> getAdditionalProperties(AbstractLicenseMojo mojo, Properties currentProperties, Document document) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("my-custom-property", "my-custom-value");
        return map;
    }
}
