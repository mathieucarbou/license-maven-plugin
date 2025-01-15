package com.mycila.maven.plugin.license;

import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WorkSpaceTest {
    @Test
    void test_workspace_basedir_check() throws MojoExecutionException, MojoFailureException {
        final WorkSpace workspace = new WorkSpace();
        workspace.basedir = new File("src/test/resources/check/modules");

        final LicenseSet licenseSet = new LicenseSet();
        licenseSet.basedir = new File("src/test/resources/check/issue76");
        licenseSet.header = "src/test/resources/test-header1.txt";

        final LicenseSet[] licenseSets = {
                licenseSet
        };

        final LicenseCheckMojo check = new LicenseCheckMojo();
        check.licenseSets = licenseSets;
        check.project = new MavenProjectStub();

        check.workspace = workspace;
        check.licenseSets = licenseSets;

        final MockedLog logger = new MockedLog();
        check.setLog(new DefaultLog(logger));
        MojoExecutionException thrown = assertThrows(MojoExecutionException.class, () -> check.execute());
        assertTrue(thrown.getMessage().startsWith("LicenseSet basedir parameter"));
    }

    @Test
    public void test_multipleLicenseSets() throws Exception {
        final LicenseSet licenseSet1 = new LicenseSet();
        licenseSet1.basedir = new File("src/test/resources/check/strict");
        licenseSet1.header = "src/test/resources/test-header1-diff.txt";

        final LicenseSet licenseSet2 = new LicenseSet();
        licenseSet2.basedir = new File("src/test/resources/check/issue76");
        licenseSet2.header = "src/test/resources/test-header1.txt";

        final LicenseSet licenseSetWithoutBaseDir = new LicenseSet();
        licenseSetWithoutBaseDir.header = "test-header1.txt";

        final LicenseSet[] licenseSets = {
                licenseSet1,
                licenseSet2,
                licenseSetWithoutBaseDir
        };

        final LicenseCheckMojo check = new LicenseCheckMojo();
        check.licenseSets = licenseSets;
        check.project = new MavenProjectStub();
        check.strictCheck = false;
        check.workspace = new WorkSpace();
        check.workspace.basedir = new File("src/test/resources");
        final MockedLog logger = new MockedLog();
        check.setLog(new DefaultLog(logger));
        check.execute();

        final String log = logger.getContent();
        final String fileFromFirstSet = new File("src/test/resources/check/strict/space.java").getCanonicalPath()
                .replace('\\', '/');
        final String fileFromSecondSet = new File("src/test/resources/check/issue76/after.xml").getCanonicalPath()
                .replace('\\', '/');
        final String fileFromDefaultBaseDirSet = new File("src/test/resources/unknown/header.txt").getCanonicalPath()
                .replace('\\', '/');

        Assertions.assertTrue(log.contains("Header OK in: " + fileFromFirstSet));
        Assertions.assertTrue(log.contains("Header OK in: " + fileFromSecondSet));
        Assertions.assertTrue(log.contains("Header OK in: " + fileFromDefaultBaseDirSet));
    }
}
