import java.nio.file.Files
import java.nio.file.Path

final Path base = basedir.toPath()

if (!Files.exists(base) || !Files.isDirectory(base)) {
    System.err.println("base directory is missing.")
    return false
}

final List<String> ALL_FILES = Arrays.asList("Unformatted1.java", "Unformatted2.java")

for (final String filename : ALL_FILES) {
    final Path unformattedJavaFile = base.resolve("src/main/java/com/mycilla/it/" + filename)
    if (!Files.exists(unformattedJavaFile)) {
        System.err.println(filename + " file is missing.")
        return false
    }

    Files.copy(unformattedJavaFile, unformattedJavaFile.resolveSibling(filename + ".bak"))
}

return true;