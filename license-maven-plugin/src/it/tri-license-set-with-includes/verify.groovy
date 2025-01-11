import java.nio.file.Files
import java.nio.file.Path

import static org.junit.jupiter.api.Assertions.assertEquals


final Path base = basedir.toPath()

if (!Files.exists(base) || !Files.isDirectory(base)) {
  System.err.println("base directory is missing.")
  return false
}

for (int i = 1; i <= 3; i++) {

  final Path license = base.resolve("mock-license-" + i + ".txt")
  if (!Files.exists(license)) {
    System.err.println(license.getFileName() + " file is missing.")
    return false
  }

  final String filename = "Unformatted" + i + ".java"

  final Path unformattedJavaFile = base.resolve("src/main/java/com/mycila/it/" + filename + ".bak")
  if (!Files.exists(unformattedJavaFile)) {
    System.err.println(unformattedJavaFile.getFileName() + " file is missing (should have been created by setup.groovy).")
    return false
  }

  final Path formattedJavaFile = base.resolve("src/main/java/com/mycila/it/" + filename)
  if (!Files.exists(formattedJavaFile)) {
    System.err.println(formattedJavaFile.getFileName() + " file is missing.")
    return false
  }

  final StringBuilder expected = new StringBuilder();
  if (i == 2) {
    expected.append("/*\n");
    license.withReader { reader ->
      while ((line = reader.readLine()) != null) {
        expected.append(" * ").append(line).append('\n')
      }
    }
    expected.append(" */\n")
  }

  expected.append(new String(Files.readAllBytes(unformattedJavaFile)))

  final String actual = new String(Files.readAllBytes(formattedJavaFile))

  assertEquals(expected.toString(), actual)
}

return true
