package com.mycila.maven.plugin.license.header;

import java.nio.file.Paths;

import com.mycila.maven.plugin.license.Multi;
import org.junit.Assert;
import org.junit.Test;

import com.mycila.maven.plugin.license.util.resource.ResourceFinder;

import static com.mycila.maven.plugin.license.Multi.DEFAULT_SEPARATOR;

public class HeaderSourceTest {
    
    private ResourceFinder finder = new ResourceFinder(Paths.get("src", "test", "resources"));

    @Test
    public void testOfInline() {
        HeaderSource actual = HeaderSource.of(null, "inline header", "single-line-header.txt", "utf-8", finder);
        Assert.assertEquals("inline header", actual.getContent());
        Assert.assertEquals(true, actual.isInline());
    }

    @Test
    public void testOfInlineOnly() {
        HeaderSource actual = HeaderSource.of(null, "inline header", null, null, null);
        Assert.assertEquals("inline header", actual.getContent());
        Assert.assertEquals(true, actual.isInline());
    }

    @Test
    public void testOfUrl() {
        HeaderSource actual = HeaderSource.of(null, "", "single-line-header.txt", "utf-8", finder);
        Assert.assertEquals("just a one line header file for copyright", actual.getContent());
        Assert.assertEquals(false, actual.isInline());
    }

    @Test
    public void testOfUrlOnly() {
        HeaderSource actual = HeaderSource.of(null, null, "single-line-header.txt", "utf-8", finder);
        Assert.assertEquals("just a one line header file for copyright", actual.getContent());
        Assert.assertEquals(false, actual.isInline());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOfNulls() {
        HeaderSource.of(null, null, null, "utf-8", finder);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOfEmptyAndNull() {
        HeaderSource.of(null, "", null, "utf-8", finder);
    }

    @Test
    public void testOfMultiInlineSingle() {
        final Multi multi = new Multi();
        multi.setInlineHeaders(new String[]{"multi inline header 1"});
        final HeaderSource actual = HeaderSource.of(multi, "inline header", "single-line-header.txt", "utf-8", finder);
        Assert.assertEquals("multi inline header 1", actual.getContent());
        Assert.assertEquals(true, actual.isInline());
    }

    @Test
    public void testOfMultiInlineDouble() {
        final Multi multi = new Multi();
        multi.setInlineHeaders(new String[] {"multi inline header 1", "multi inline header 2"});
        final HeaderSource actual = HeaderSource.of(multi, "inline header", "single-line-header.txt", "utf-8", finder);

        final String expected = "multi inline header 1" + "\n\n"
                + DEFAULT_SEPARATOR + "\n\n" + "multi inline header 2";

        Assert.assertEquals(expected, actual.getContent());
        Assert.assertEquals(true, actual.isInline());
    }

    @Test
    public void testOfMultiInlineDoubleCustomSeparator() {
        final Multi multi = new Multi();
        multi.setInlineHeaders(new String[] {"multi inline header 1", "multi inline header 2"});
        multi.setSeparators(new String[]{ "###"});
        final HeaderSource actual = HeaderSource.of(multi, "inline header", "single-line-header.txt", "utf-8", finder);

        final String expected = "multi inline header 1" + "\n\n"
                + "###" + "\n\n" + "multi inline header 2";

        Assert.assertEquals(expected, actual.getContent());
        Assert.assertEquals(true, actual.isInline());
    }

    @Test
    public void testOfMultiInlineTriple() {
        final Multi multi = new Multi();
        multi.setInlineHeaders(new String[] {"multi inline header 1", "multi inline header 2", "multi inline header 3"});
        final HeaderSource actual = HeaderSource.of(multi, "inline header", "single-line-header.txt", "utf-8", finder);

        final String expected = "multi inline header 1" + "\n\n"
                + DEFAULT_SEPARATOR + "\n\n" + "multi inline header 2" + "\n\n"
                + DEFAULT_SEPARATOR + "\n\n" + "multi inline header 3";

        Assert.assertEquals(expected, actual.getContent());
        Assert.assertEquals(true, actual.isInline());
    }

    @Test
    public void testOfMultiInlineTripleCustomSeparator() {
        final Multi multi = new Multi();
        multi.setInlineHeaders(new String[] {"multi inline header 1", "multi inline header 2", "multi inline header 3"});
        multi.setSeparators(new String[]{ "###" });
        final HeaderSource actual = HeaderSource.of(multi, "inline header", "single-line-header.txt", "utf-8", finder);

        final String expected = "multi inline header 1" + "\n\n"
                + "###" + "\n\n" + "multi inline header 2" + "\n\n"
                + "###" + "\n\n" + "multi inline header 3";

        Assert.assertEquals(expected, actual.getContent());
        Assert.assertEquals(true, actual.isInline());
    }

    @Test
    public void testOfMultiInlineTripleCustomSeparators() {
        final Multi multi = new Multi();
        multi.setInlineHeaders(new String[] {"multi inline header 1", "multi inline header 2", "multi inline header 3"});
        multi.setSeparators(new String[]{ "###", "*****" });
        final HeaderSource actual = HeaderSource.of(multi, "inline header", "single-line-header.txt", "utf-8", finder);

        final String expected = "multi inline header 1" + "\n\n"
                + "###" + "\n\n" + "multi inline header 2" + "\n\n"
                + "*****" + "\n\n" + "multi inline header 3";

        Assert.assertEquals(expected, actual.getContent());
        Assert.assertEquals(true, actual.isInline());
    }

    @Test
    public void testOfMultiUrlSingle() {
        final Multi multi = new Multi();
        multi.setHeaders(new String[] {"single-line-header.txt"});
        final HeaderSource actual = HeaderSource.of(multi, null, null, "utf-8", finder);
        Assert.assertEquals("just a one line header file for copyright", actual.getContent());
        Assert.assertEquals(false, actual.isInline());
    }

    @Test
    public void testOfMultiUrlDouble() {
        final Multi multi = new Multi();
        multi.setHeaders(new String[] {"single-line-header.txt", "alternative-single-line-header.txt"});
        final HeaderSource actual = HeaderSource.of(multi, null, null, "utf-8", finder);

        final String expected = "just a one line header file for copyright" + "\n\n"
                + DEFAULT_SEPARATOR + "\n\n" + "alternative one line header file for copyright";

        Assert.assertEquals(expected, actual.getContent());
        Assert.assertEquals(false, actual.isInline());
    }

    @Test
    public void testOfMultiUrlDoubleCustomSeparator() {
        final Multi multi = new Multi();
        multi.setHeaders(new String[] {"single-line-header.txt", "alternative-single-line-header.txt"});
        multi.setSeparators(new String[]{ "###"});
        final HeaderSource actual = HeaderSource.of(multi, null, null, "utf-8", finder);

        final String expected = "just a one line header file for copyright" + "\n\n"
                + "###" + "\n\n" + "alternative one line header file for copyright";

        Assert.assertEquals(expected, actual.getContent());
        Assert.assertEquals(false, actual.isInline());
    }

    @Test
    public void testOfMultiUrlTriple() {
        final Multi multi = new Multi();
        multi.setHeaders(new String[] {"single-line-header.txt", "alternative-single-line-header.txt", "single-line-header.txt"});
        final HeaderSource actual = HeaderSource.of(multi, null, null, "utf-8", finder);

        final String expected = "just a one line header file for copyright" + "\n\n"
                + DEFAULT_SEPARATOR + "\n\n" + "alternative one line header file for copyright" + "\n\n"
                + DEFAULT_SEPARATOR + "\n\n" + "just a one line header file for copyright";

        Assert.assertEquals(expected, actual.getContent());
        Assert.assertEquals(false, actual.isInline());
    }

    @Test
    public void testOfMultiUrlTripleCustomSeparator() {
        final Multi multi = new Multi();
        multi.setHeaders(new String[] {"single-line-header.txt", "alternative-single-line-header.txt", "single-line-header.txt"});
        multi.setSeparators(new String[]{ "###" });
        final HeaderSource actual = HeaderSource.of(multi, null, null, "utf-8", finder);

        final String expected = "just a one line header file for copyright" + "\n\n"
                + "###" + "\n\n" + "alternative one line header file for copyright" + "\n\n"
                + "###" + "\n\n" + "just a one line header file for copyright";

        Assert.assertEquals(expected, actual.getContent());
        Assert.assertEquals(false, actual.isInline());
    }

    @Test
    public void testOfMultiUrlTripleCustomSeparators() {
        final Multi multi = new Multi();
        multi.setHeaders(new String[] {"single-line-header.txt", "alternative-single-line-header.txt", "single-line-header.txt"});
        multi.setSeparators(new String[]{ "###", "*****" });
        final HeaderSource actual = HeaderSource.of(multi, null, null, "utf-8", finder);

        final String expected = "just a one line header file for copyright" + "\n\n"
                + "###" + "\n\n" + "alternative one line header file for copyright" + "\n\n"
                + "*****" + "\n\n" + "just a one line header file for copyright";

        Assert.assertEquals(expected, actual.getContent());
        Assert.assertEquals(false, actual.isInline());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOfMultiNulls() {
        HeaderSource.of(new Multi(), null, null, "utf-8", finder);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOfMultiEmptyAndNull() {
        final Multi multi = new Multi();
        multi.setInlineHeaders(new String[0]);
        HeaderSource.of(multi, "", null, "utf-8", finder);
    }
}
