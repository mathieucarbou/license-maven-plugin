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

final Path expectedJavaFile = base.resolve("src/main/java/com/mycilla/it/Unformatted1.java.expected")
if (!Files.exists(expectedJavaFile)) {
  System.err.println(expectedJavaFile.getFileName() + " file is missing.")
  return false
}

final Path formattedJavaFile = base.resolve("src/main/java/com/mycilla/it/Unformatted1.java")
if (!Files.exists(formattedJavaFile)) {
  System.err.println(formattedJavaFile.getFileName() + " file is missing.")
  return false
}
final String expected = new String(Files.readAllBytes(expectedJavaFile))
final String actual = new String(Files.readAllBytes(formattedJavaFile))

assertEquals(expected, actual)

return true
