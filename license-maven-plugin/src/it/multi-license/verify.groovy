import java.nio.file.Files
import java.nio.file.Path

import static org.junit.jupiter.api.Assertions.assertEquals


final Path base = basedir.toPath()

if (!Files.exists(base) || !Files.isDirectory(base)) {
  System.err.println("base directory is missing.")
  return false
}

final String preamble = "This product is multi-licensed under X, Y, Z licenses."

separators = [
    "*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#**#*#*#*#*#*#*",
    "===================================================================="
]

final StringBuilder expected = new StringBuilder()
expected.append("/*\n")
expected.append(" * ").append(preamble).append("\n")
expected.append(" *\n")


for (int i = 1; i <= 3; i++) {

  final Path license = base.resolve("mock-license-" + i + ".txt")
  if (!Files.exists(license)) {
    System.err.println(license.getFileName() + " file is missing.")
    return false
  }

  license.withReader { reader ->
    while ((line = reader.readLine()) != null) {
      expected.append(" * ").append(line).append('\n')
    }
  }

  if (i < 3) {
    final String separator = separators[i - 1]
    expected.append(" *\n")
    expected.append(" * ").append(separator).append('\n')
    expected.append(" *\n")
  }
}

final Path unformattedJavaFile = base.resolve("src/main/java/com/mycilla/it/Unformatted1.java.bak")
if (!Files.exists(unformattedJavaFile)) {
  System.err.println(unformattedJavaFile.getFileName() + " file is missing (should have been created by setup.groovy).")
  return false
}

expected.append(" */\n")
expected.append(new String(Files.readAllBytes(unformattedJavaFile)))

final Path formattedJavaFile = base.resolve("src/main/java/com/mycilla/it/Unformatted1.java")
if (!Files.exists(formattedJavaFile)) {
  System.err.println(formattedJavaFile.getFileName() + " file is missing.")
  return false
}

final String actual = new String(Files.readAllBytes(formattedJavaFile))

assertEquals(expected.toString(), actual)

return true
