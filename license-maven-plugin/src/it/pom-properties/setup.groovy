import java.nio.file.Files
import java.nio.file.Path

final Path base = basedir.toPath()

if (!Files.exists(base) || !Files.isDirectory(base)) {
  System.err.println("base directory is missing.")
  return false
}

final Path expectedJavaFile = base.resolve("src/main/java/com/mycilla/it/Unformatted1.java.expected")
if (!Files.exists(expectedJavaFile)) {
  System.err.println("Unformatted1.java.expected file is missing.")
  return false
}

final Path unformattedJavaFile = base.resolve("src/main/java/com/mycilla/it/Unformatted1.java")
if (!Files.exists(unformattedJavaFile)) {
  System.err.println("Unformatted1.java file is missing.")
  return false
}

return true;
