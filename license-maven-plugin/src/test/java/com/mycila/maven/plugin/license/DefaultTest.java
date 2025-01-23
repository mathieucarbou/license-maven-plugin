package com.mycila.maven.plugin.license;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTest {
  @Test
  void test_Jenkinsfile_is_not_a_default_exclude() {
    assertThat(Arrays.asList(Default.EXCLUDES)).doesNotContain("Jenkinsfile");
  }
}
