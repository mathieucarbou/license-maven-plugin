package com.mycila.maven.plugin.license;

import com.google.common.base.Strings;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class AbstractLicenseMojoTest {

  @Test
  void test_StarEncryptMethodWorksAsExpected() {
    Assertions.assertThat(AbstractLicenseMojo.starEncrypt("dummy")).isEqualTo("*****");

    Assertions.assertThat(AbstractLicenseMojo.starEncrypt(null)).isNull();

    String generatedPassword = UUID.randomUUID().toString();
    Assertions.assertThat(AbstractLicenseMojo.starEncrypt(generatedPassword)).isEqualTo(Strings.repeat("*", 5));
  }
}
