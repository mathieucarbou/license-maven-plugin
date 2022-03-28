package com.mycila.maven.plugin.license;

import com.google.common.base.Strings;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Matthieu Brouillard
 */
class AbstractLicenseMojoTest {

  @Test
  void test_StarEncryptMethodWorksAsExpected() {
    Assertions.assertThat(AbstractLicenseMojo.starEncrypt("dummy")).isEqualTo("*****");

    Assertions.assertThat(AbstractLicenseMojo.starEncrypt(null)).isNull();

    String generatedPassword = UUID.randomUUID().toString();
    Assertions.assertThat(AbstractLicenseMojo.starEncrypt(generatedPassword)).isEqualTo(Strings.repeat("*", generatedPassword.length()));
  }
}
