package com.mycila.maven.plugin.license;

import com.google.common.base.Strings;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


/**
 * @author Matthieu Brouillard
 */
public class AbstractLicenseMojoTest {
    @Test
    public void test_StarEncryptMethodWorksAsExpected() {
        assertThat(AbstractLicenseMojo.starEncrypt("dummy"), is("*****"));
        
        assertThat(AbstractLicenseMojo.starEncrypt(null), nullValue());
        
        String generatedPassword = UUID.randomUUID().toString();
        assertThat(AbstractLicenseMojo.starEncrypt(generatedPassword), is(Strings.repeat("*", generatedPassword.length())));
    }
}
