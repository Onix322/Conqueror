package framework.src.boot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

/**
 *  Boot class is responsible for managing the boot of the app when is started
 *  And extract all needed files for the app to run properly.
 */
public class Boot {

    public static void boot(String jarPath, String dirToExtract, String targetDir, boolean force) {

        try {
            Path target = Path.of(targetDir);

            if (Files.exists(target) && Files.isDirectory(target) && Files.list(target).findAny().isPresent()) {
                if (!force) {
                    System.out.println("Target directory already exists and is not empty. Skipping extraction.");
                    return;
                } else {
                    System.out.println("Target directory exists. Force flag enabled, re-extracting...");
                    deleteDirectoryRecursively(target);
                }
            }

            String javaHome = System.getProperty("java.home");
            if (javaHome == null) {
                throw new IllegalStateException("JAVA_HOME is not set. Please configure your JDK path.");
            }

            System.out.println("Extracting '" + dirToExtract + "' from " + jarPath + "...");

            ProcessBuilder pb = new ProcessBuilder(
                    "jar",
                    "-xf",
                    jarPath
            );
            pb.inheritIO();

            int exitCode = pb.start().waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Extraction failed with exit code " + exitCode);
            }

            Files.createDirectories(target.getParent());
            Files.move(Path.of(dirToExtract), target, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Extraction and move completed successfully.");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Boot process failed", e);
        }
    }

    private static void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to delete " + p, e);
                        }
                    });
        }
    }
}
