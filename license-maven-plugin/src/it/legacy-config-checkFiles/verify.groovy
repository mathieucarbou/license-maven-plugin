import java.nio.file.Files
import java.nio.file.Path

import static org.junit.jupiter.api.Assertions.assertEquals


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

final Path unformattedJavaFile1 = base.resolve("src/main/java/com/mycilla/it/Unformatted1.java.bak")
if (!Files.exists(unformattedJavaFile1)) {
  System.err.println(unformattedJavaFile1.getFileName() + " file is missing (should have been created by setup.groovy).")
  return false
}

final Path unformattedJavaFile2 = base.resolve("src/main/java/com/mycilla/it/Unformatted2.java.bak")
if (!Files.exists(unformattedJavaFile2)) {
  System.err.println(unformattedJavaFile2.getFileName() + " file is missing (should have been created by setup.groovy).")
  return false
}

final Path formattedJavaFile1 = base.resolve("src/main/java/com/mycilla/it/Unformatted1.java")
if (!Files.exists(formattedJavaFile1)) {
  System.err.println(formattedJavaFile1.getFileName() + " file is missing.")
  return false
}


final Path formattedJavaFile2 = base.resolve("src/main/java/com/mycilla/it/Unformatted2.java")
if (!Files.exists(formattedJavaFile2)) {
  System.err.println(formattedJavaFile2.getFileName() + " file is missing.")
  return false
}

assertEquals(new String(Files.readAllBytes(formattedJavaFile1)), new String(Files.readAllBytes(unformattedJavaFile1)))

final StringBuilder expected = new StringBuilder();
expected.append("/*\n");
license.withReader { reader ->
  while ((line = reader.readLine()) != null) {
    expected.append(" * ").append(line).append('\n')
  }
}
expected.append(" */\n")
expected.append(new String(Files.readAllBytes(unformattedJavaFile2)))

final String actual = new String(Files.readAllBytes(formattedJavaFile2))

assertEquals(expected.toString(), actual)

return true
