package loader.utilities;


import loader.utilities.linkGenerator.link.VersionedLink;
import loader.utilities.pomReader.supportedTagsClasses.artifact.VersionedArtifact;
import configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    public boolean verifyExistence(VersionedLink versionedLink) {
        VersionedArtifact dependency = versionedLink.getArtifact();
        return this.verifyExistence(dependency);
    }

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
