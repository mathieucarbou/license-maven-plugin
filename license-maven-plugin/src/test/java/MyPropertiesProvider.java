import com.mycila.maven.plugin.license.AbstractLicenseMojo;
import com.mycila.maven.plugin.license.PropertiesProvider;
import com.mycila.maven.plugin.license.document.Document;

import java.util.HashMap;
import java.util.Map;

public final class MyPropertiesProvider implements PropertiesProvider {

  @Override
  public void init(AbstractLicenseMojo mojo, Map<String, String> currentProperties) {
  }

  @Override
  public void close() {
  }

  @Override
  public Map<String, String> adjustProperties(AbstractLicenseMojo mojo,
                                              Map<String, String> currentProperties, Document document) {
    Map<String, String> map = new HashMap<String, String>();
    map.put("my-custom-property", "my-custom-value");
    return map;
  }
}
