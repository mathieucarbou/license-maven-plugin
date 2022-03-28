package com.mycila.maven.plugin.license.header;

import static com.mycila.maven.plugin.license.Multi.DEFAULT_SEPARATOR;

import com.mycila.maven.plugin.license.Multi;
import com.mycila.maven.plugin.license.util.resource.ResourceFinder;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HeaderSourceTest {

  private ResourceFinder finder = new ResourceFinder(Paths.get("src", "test", "resources"));

  @Test
  void testOfInline() {
    HeaderSource actual = HeaderSource.of(null, "inline header", "single-line-header.txt", "utf-8", finder);
    Assertions.assertEquals("inline header", actual.getContent());
    Assertions.assertEquals(true, actual.isInline());
  }

  @Test
  void testOfInlineOnly() {
    HeaderSource actual = HeaderSource.of(null, "inline header", null, null, null);
    Assertions.assertEquals("inline header", actual.getContent());
    Assertions.assertEquals(true, actual.isInline());
  }

  @Test
  void testOfUrl() {
    HeaderSource actual = HeaderSource.of(null, "", "single-line-header.txt", "utf-8", finder);
    Assertions.assertEquals("just a one line header file for copyright", actual.getContent());
    Assertions.assertEquals(false, actual.isInline());
  }

  @Test
  void testOfUrlOnly() {
    HeaderSource actual = HeaderSource.of(null, null, "single-line-header.txt", "utf-8", finder);
    Assertions.assertEquals("just a one line header file for copyright", actual.getContent());
    Assertions.assertEquals(false, actual.isInline());
  }

  @Test
  void testOfNulls() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      HeaderSource.of(null, null, null, "utf-8", finder);
    });
  }

  @Test
  void testOfEmptyAndNull() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      HeaderSource.of(null, "", null, "utf-8", finder);
    });
  }

  @Test
  void testOfMultiInlineSingle() {
    final Multi multi = new Multi();
    multi.setInlineHeaders(new String[]{"multi inline header 1"});
    final HeaderSource actual = HeaderSource.of(multi, "inline header", "single-line-header.txt", "utf-8", finder);
    Assertions.assertEquals("multi inline header 1", actual.getContent());
    Assertions.assertEquals(true, actual.isInline());
  }

  @Test
  void testOfMultiInlineDouble() {
    final Multi multi = new Multi();
    multi.setInlineHeaders(new String[]{"multi inline header 1", "multi inline header 2"});
    final HeaderSource actual = HeaderSource.of(multi, "inline header", "single-line-header.txt", "utf-8", finder);

    final String expected = "multi inline header 1" + "\n\n"
        + DEFAULT_SEPARATOR + "\n\n" + "multi inline header 2";

    Assertions.assertEquals(expected, actual.getContent());
    Assertions.assertEquals(true, actual.isInline());
  }

  @Test
  void testOfMultiInlineDoubleCustomSeparator() {
    final Multi multi = new Multi();
    multi.setInlineHeaders(new String[]{"multi inline header 1", "multi inline header 2"});
    multi.setSeparators(new String[]{"###"});
    final HeaderSource actual = HeaderSource.of(multi, "inline header", "single-line-header.txt", "utf-8", finder);

    final String expected = "multi inline header 1" + "\n\n"
        + "###" + "\n\n" + "multi inline header 2";

    Assertions.assertEquals(expected, actual.getContent());
    Assertions.assertEquals(true, actual.isInline());
  }

  @Test
  void testOfMultiInlineTriple() {
    final Multi multi = new Multi();
    multi.setInlineHeaders(new String[]{"multi inline header 1", "multi inline header 2", "multi inline header 3"});
    final HeaderSource actual = HeaderSource.of(multi, "inline header", "single-line-header.txt", "utf-8", finder);

    final String expected = "multi inline header 1" + "\n\n"
        + DEFAULT_SEPARATOR + "\n\n" + "multi inline header 2" + "\n\n"
        + DEFAULT_SEPARATOR + "\n\n" + "multi inline header 3";

    Assertions.assertEquals(expected, actual.getContent());
    Assertions.assertEquals(true, actual.isInline());
  }

  @Test
  void testOfMultiInlineTripleCustomSeparator() {
    final Multi multi = new Multi();
    multi.setInlineHeaders(new String[]{"multi inline header 1", "multi inline header 2", "multi inline header 3"});
    multi.setSeparators(new String[]{"###"});
    final HeaderSource actual = HeaderSource.of(multi, "inline header", "single-line-header.txt", "utf-8", finder);

    final String expected = "multi inline header 1" + "\n\n"
        + "###" + "\n\n" + "multi inline header 2" + "\n\n"
        + "###" + "\n\n" + "multi inline header 3";

    Assertions.assertEquals(expected, actual.getContent());
    Assertions.assertEquals(true, actual.isInline());
  }

  @Test
  void testOfMultiInlineTripleCustomSeparators() {
    final Multi multi = new Multi();
    multi.setInlineHeaders(new String[]{"multi inline header 1", "multi inline header 2", "multi inline header 3"});
    multi.setSeparators(new String[]{"###", "*****"});
    final HeaderSource actual = HeaderSource.of(multi, "inline header", "single-line-header.txt", "utf-8", finder);

    final String expected = "multi inline header 1" + "\n\n"
        + "###" + "\n\n" + "multi inline header 2" + "\n\n"
        + "*****" + "\n\n" + "multi inline header 3";

    Assertions.assertEquals(expected, actual.getContent());
    Assertions.assertEquals(true, actual.isInline());
  }

  @Test
  void testOfMultiUrlSingle() {
    final Multi multi = new Multi();
    multi.setHeaders(new String[]{"single-line-header.txt"});
    final HeaderSource actual = HeaderSource.of(multi, null, null, "utf-8", finder);
    Assertions.assertEquals("just a one line header file for copyright", actual.getContent());
    Assertions.assertEquals(false, actual.isInline());
  }

  @Test
  void testOfMultiUrlDouble() {
    final Multi multi = new Multi();
    multi.setHeaders(new String[]{"single-line-header.txt", "alternative-single-line-header.txt"});
    final HeaderSource actual = HeaderSource.of(multi, null, null, "utf-8", finder);

    final String expected = "just a one line header file for copyright" + "\n\n"
        + DEFAULT_SEPARATOR + "\n\n" + "alternative one line header file for copyright";

    Assertions.assertEquals(expected, actual.getContent());
    Assertions.assertEquals(false, actual.isInline());
  }

  @Test
  void testOfMultiUrlDoubleCustomSeparator() {
    final Multi multi = new Multi();
    multi.setHeaders(new String[]{"single-line-header.txt", "alternative-single-line-header.txt"});
    multi.setSeparators(new String[]{"###"});
    final HeaderSource actual = HeaderSource.of(multi, null, null, "utf-8", finder);

    final String expected = "just a one line header file for copyright" + "\n\n"
        + "###" + "\n\n" + "alternative one line header file for copyright";

    Assertions.assertEquals(expected, actual.getContent());
    Assertions.assertEquals(false, actual.isInline());
  }

  @Test
  void testOfMultiUrlTriple() {
    final Multi multi = new Multi();
    multi.setHeaders(new String[]{"single-line-header.txt", "alternative-single-line-header.txt", "single-line-header.txt"});
    final HeaderSource actual = HeaderSource.of(multi, null, null, "utf-8", finder);

    final String expected = "just a one line header file for copyright" + "\n\n"
        + DEFAULT_SEPARATOR + "\n\n" + "alternative one line header file for copyright" + "\n\n"
        + DEFAULT_SEPARATOR + "\n\n" + "just a one line header file for copyright";

    Assertions.assertEquals(expected, actual.getContent());
    Assertions.assertEquals(false, actual.isInline());
  }

  @Test
  void testOfMultiUrlTripleCustomSeparator() {
    final Multi multi = new Multi();
    multi.setHeaders(new String[]{"single-line-header.txt", "alternative-single-line-header.txt", "single-line-header.txt"});
    multi.setSeparators(new String[]{"###"});
    final HeaderSource actual = HeaderSource.of(multi, null, null, "utf-8", finder);

    final String expected = "just a one line header file for copyright" + "\n\n"
        + "###" + "\n\n" + "alternative one line header file for copyright" + "\n\n"
        + "###" + "\n\n" + "just a one line header file for copyright";

    Assertions.assertEquals(expected, actual.getContent());
    Assertions.assertEquals(false, actual.isInline());
  }

  @Test
  void testOfMultiUrlTripleCustomSeparators() {
    final Multi multi = new Multi();
    multi.setHeaders(new String[]{"single-line-header.txt", "alternative-single-line-header.txt", "single-line-header.txt"});
    multi.setSeparators(new String[]{"###", "*****"});
    final HeaderSource actual = HeaderSource.of(multi, null, null, "utf-8", finder);

    final String expected = "just a one line header file for copyright" + "\n\n"
        + "###" + "\n\n" + "alternative one line header file for copyright" + "\n\n"
        + "*****" + "\n\n" + "just a one line header file for copyright";

    Assertions.assertEquals(expected, actual.getContent());
    Assertions.assertEquals(false, actual.isInline());
  }

  @Test
  void testOfMultiNulls() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      HeaderSource.of(new Multi(), null, null, "utf-8", finder);
    });
  }

  @Test
  void testOfMultiEmptyAndNull() {
    final Multi multi = new Multi();
    multi.setInlineHeaders(new String[0]);
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      HeaderSource.of(multi, "", null, "utf-8", finder);
    });
  }
}
