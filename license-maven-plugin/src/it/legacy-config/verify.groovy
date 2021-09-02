import java.nio.file.Files
import java.nio.file.Path

import static org.junit.Assert.assertEquals


final Path base = basedir.toPath()

if (!Files.exists(base) || !Files.isDirectory(base)) {
    System.err.println("base directory is missing.")
    return false
}

final Path license = base.resolve("mock-license.txt")
if (!Files.exists(license)) {
    System.err.println("license file is missing.")
    return false
}

final Path unformattedJavaFile = base.resolve("src/main/java/com/mycilla/it/Unformatted1.java.bak")
if (!Files.exists(unformattedJavaFile)) {
    System.err.println(unformattedJavaFile.getFileName() + " file is missing (should have been created by setup.groovy).")
    return false
}

final Path formattedJavaFile = base.resolve("src/main/java/com/mycilla/it/Unformatted1.java")
if (!Files.exists(formattedJavaFile)) {
    System.err.println(formattedJavaFile.getFileName() + " file is missing.")
    return false
}

final StringBuilder expected = new StringBuilder();
expected.append("/*\n");
license.withReader { reader ->
    while ((line = reader.readLine()) != null) {
        expected.append(" * ").append(line).append('\n')
    }
}
expected.append(" */\n")
expected.append(new String(Files.readAllBytes(unformattedJavaFile)))

final String actual = new String(Files.readAllBytes(formattedJavaFile))

assertEquals(expected.toString(), actual)

return true
