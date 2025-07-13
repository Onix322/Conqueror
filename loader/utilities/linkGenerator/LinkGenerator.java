package loader.utilities.linkGenerator;

import loader.utilities.linkGenerator.link.Link;
import loader.utilities.linkGenerator.link.LinkExtension;
import loader.utilities.linkGenerator.link.VersionedLink;
import loader.utilities.pomReader.supportedTagsClasses.artifact.Artifact;
import loader.utilities.pomReader.supportedTagsClasses.artifact.VersionedArtifact;
import loader.utilities.version.Version;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Utility class for generating Maven repository links for artifacts and versioned artifacts.
 * Provides methods to create links for metadata and specific artifact versions.
 * Implements a thread-safe singleton pattern.
 */
public class LinkGenerator {

    /* The base URL for Maven Central repository.
     * This URL is used to construct links for artifacts and their metadata.
     * The placeholders {groupId}, {artifactId}, {version}, and {extension} will be replaced with actual values.
     *
     * Example:
     * For an artifact with groupId "com.example", artifactId "my-artifact", version "1.0.0", and extension "jar",
     * the generated link will be:
     * https://repo1.maven.org/maven2/com/example/my-artifact/1.0.0/my-artifact-1.0.0.jar
    * */
    private final String MAVEN_LINK = "https://repo1.maven.org/maven2/{groupId}/{artifactId}/{version}/{artifactId}-{version}.{extension}";
    /* The base URL for Maven metadata.
     * This URL is used to construct links for artifact metadata files.
     * The placeholders {groupId}, {artifactId}, and {extension} will be replaced with actual values.
     *
     * Example:
     * For an artifact with groupId "com.example", artifactId "my-artifact", and extension "xml",
     * the generated link will be:
     * https://repo1.maven.org/maven2/com/example/my-artifact/maven-metadata.xml
    * */
    private final String MAVEN_METADATA_LINK = "https://repo1.maven.org/maven2/{groupId}/{artifactId}/maven-metadata.{extension}";

    private LinkGenerator() {
    }

    private static class Holder {
        private static LinkGenerator INSTANCE = null;
    }

    public static synchronized void init() {
        if (LinkGenerator.Holder.INSTANCE == null) {
            LinkGenerator.Holder.INSTANCE = new LinkGenerator();
        }
    }

    public static LinkGenerator getInstance() {
        if (LinkGenerator.Holder.INSTANCE == null) {
            throw new IllegalStateException("LinkGenerator is not initialized. Use LinkGenerator.init().");
        }
        return LinkGenerator.Holder.INSTANCE;
    }

    /**
         * Generates a versioned link for a given versioned artifact and file extension.
         *
         * @param versionedArtifact the artifact with version information; must not be null
         * @param extension the file extension for the artifact (e.g., JAR, POM); must not be null
         * @return a VersionedLink representing the constructed URI for the artifact
         * @throws IllegalArgumentException if any parameter is null
         * @throws RuntimeException if the generated URI is invalid
         */
    public VersionedLink generateVersionedLink(VersionedArtifact versionedArtifact, LinkExtension extension) {
        if (versionedArtifact == null || extension == null) {
            throw new IllegalArgumentException("Parameters: groupId, artifactId, version, extension cannot be null or empty");
        }
        String groupId = versionedArtifact.getGroupId()
                .replace('.', '/');
        String artifactId = versionedArtifact.getArtifactId();
        Version version = versionedArtifact.getVersion();
        try {
            String link = MAVEN_LINK.replace("{groupId}", groupId)
                    .replace("{artifactId}", artifactId)
                    .replace("{version}", version.asString())
                    .replace("{extension}", extension.getValue());
            URI uri = new URI(link);
            return new VersionedLink(versionedArtifact, uri, extension);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a link for the metadata of a given artifact.
     *
     * @param dependency the artifact for which to generate the metadata link; must not be null
     * @return a Link representing the constructed URI for the artifact's metadata
     * @throws IllegalArgumentException if the dependency is null
     */
    public Link generateLink(Artifact dependency) {
        if(dependency == null) {
            throw new IllegalArgumentException("Parameter: dependency cannot be null or empty");
        }
        String groupId = this.refactorGroupId(dependency);
        String link = MAVEN_METADATA_LINK.replace("{groupId}", groupId)
                .replace("{artifactId}", dependency.getArtifactId())
                .replace("{extension}", LinkExtension.XML.getValue());
        URI uri = URI.create(link);
        return new Link(dependency, uri, LinkExtension.XML);
    }

    public String refactorGroupId(Artifact dependency) {
        return dependency.getGroupId().replace('.', '/');
    }

}
