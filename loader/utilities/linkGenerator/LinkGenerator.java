package loader.utilities.linkGenerator;

import loader.utilities.linkGenerator.link.VersionedLink;
import loader.utilities.linkGenerator.link.LinkExtension;
import loader.utilities.linkGenerator.link.Link;
import loader.utilities.pomReader.supportedTagsClasses.artifact.Artifact;
import loader.utilities.pomReader.supportedTagsClasses.artifact.VersionedArtifact;
import loader.utilities.version.Version;

import java.net.URI;
import java.net.URISyntaxException;

public class LinkGenerator {

    private final String MAVEN_LINK = "https://repo1.maven.org/maven2/{groupId}/{artifactId}/{version}/{artifactId}-{version}.{extension}";
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


    public VersionedLink generateLink(VersionedArtifact versionedArtifact, LinkExtension extension) {
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
            System.out.println(link);
            URI uri = new URI(link);
            System.out.println(uri);
            return new VersionedLink(versionedArtifact, uri, extension);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Link generateMetadataLink(Artifact dependency) {
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
