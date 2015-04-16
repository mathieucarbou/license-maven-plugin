package com.mycila.maven.plugin.license;

import com.google.common.base.Strings;
import java.util.UUID;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


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
