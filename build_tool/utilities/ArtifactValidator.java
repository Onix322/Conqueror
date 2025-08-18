package build_tool.utilities;


import build_tool.utilities.linkGenerator.link.VersionedLink;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.VersionedArtifact;
import configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ArtifactValidator is a singleton class responsible for validating the existence of artifacts
 * in a specified directory based on the configuration provided.
 * It reads the location of the classes from the configuration and checks if the specified
 * artifacts exist in that location.
 */
public class ArtifactValidator {

    private final File classesFile;

    private ArtifactValidator(Configuration configuration) {
        this.classesFile = this.readConfig(configuration);
    }

    private static class Holder {
        private static ArtifactValidator INSTANCE = null;
    }

    public static synchronized void init(Configuration configuration) {
        if (ArtifactValidator.Holder.INSTANCE == null) {
            ArtifactValidator.Holder.INSTANCE = new ArtifactValidator(configuration);
        }
    }

    public static ArtifactValidator getInstance() {
        if (ArtifactValidator.Holder.INSTANCE == null) {
            throw new IllegalStateException("ArtifactValidator is not initialized. Use ArtifactValidator.init().");
        }
        return ArtifactValidator.Holder.INSTANCE;
    }

    /**
     * Verifies the existence of a VersionedLink artifact in the specified classes directory.
     *
     * @param versionedLink the VersionedLink to verify
     * @return true if the artifact exists, false otherwise
     */
    public boolean verifyExistence(VersionedLink versionedLink) {
        VersionedArtifact dependency = versionedLink.getArtifact();
        return this.verifyExistence(dependency);
    }

    /**
     * Verifies the existence of a VersionedArtifact in the specified classes directory.
     *
     * @param dependency the VersionedArtifact to verify
     * @return true if the artifact exists, false otherwise
     */
    public boolean verifyExistence(VersionedArtifact dependency) {
        Path path = Path.of(classesFile.getPath()
                + '\\'
                + dependency.getArtifactId()
                + '-'
                + dependency.getVersion().asString()
                + ".jar");

        try {
            return Files.exists(path.toFile().getCanonicalFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the configuration to get the location of the classes directory.
     *
     * @param configuration the Configuration object containing properties
     * @return a File object representing the classes directory
     */
    private File readConfig(Configuration configuration) {
        String classesLocation = configuration.readProperty("dependencies.location");
        try {
            URI uri = new URI(classesLocation);
            return new File(uri.getRawPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
