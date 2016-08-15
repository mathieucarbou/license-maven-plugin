package com.mycila.maven.plugin.license.header;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.mycila.maven.plugin.license.util.resource.ResourceFinder;

public class HeaderSourceTest {
    
    private ResourceFinder finder = new ResourceFinder(new File("src/test/resources"));

    @Test
    public void testOfInline() {
        HeaderSource actual = HeaderSource.of("inline header", "single-line-header.txt", "utf-8", finder);
        Assert.assertEquals("inline header", actual.getContent());
        Assert.assertEquals(true, actual.isInline());
    }

    @Test
    public void testOfInlineOnly() {
        HeaderSource actual = HeaderSource.of("inline header", null, null, null);
        Assert.assertEquals("inline header", actual.getContent());
        Assert.assertEquals(true, actual.isInline());
    }

    @Test
    public void testOfUrl() {
        HeaderSource actual = HeaderSource.of(null, "single-line-header.txt", "utf-8", finder);
        Assert.assertEquals("just a one line header file for copyright", actual.getContent());
        Assert.assertEquals(false, actual.isInline());
        
        actual = HeaderSource.of("", "single-line-header.txt", "utf-8", finder);
        Assert.assertEquals("just a one line header file for copyright", actual.getContent());
        Assert.assertEquals(false, actual.isInline());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOfNulls() {
        HeaderSource.of(null, null, "utf-8", finder);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOfEmptyAndNull() {
        HeaderSource.of("", null, "utf-8", finder);
    }

}
