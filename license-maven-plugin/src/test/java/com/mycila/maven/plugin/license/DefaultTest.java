package com.mycila.maven.plugin.license;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

class DefaultTest {
  @Test
  void test_Jenkinsfile_is_not_a_default_exclude() {
    assertThat(Arrays.asList(Default.EXCLUDES), not(CoreMatchers.<String>hasItems(containsString("Jenkinsfile"))));
  }
}
