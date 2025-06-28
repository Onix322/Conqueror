package loader.utilities;

import loader.objects.Dependency;
import loader.objects.link.Link;
import loader.objects.link.LinkExtension;
import loader.objects.link.MetadataLink;

import java.net.URI;
import java.net.URISyntaxException;

public class LinkGenerator {

    private final String MAVEN_LINK = "https://repo1.maven.org/maven2/{groupId}/{artifactId}/{version}/{artifactId}-{version}.{extension}";
    private final String MAVEN_METADATA_LINK = "https://repo1.maven.org/maven2/{groupId}/{artifactId}/maven-metadata.{extension}";
    private final VersionHandler versionHandler;

    private LinkGenerator(VersionHandler versionHandler) {
        this.versionHandler = versionHandler;
    }

    private static class Holder {
        private static LinkGenerator INSTANCE = null;
    }

    public static synchronized void init(VersionHandler versionHandler) {
        if (LinkGenerator.Holder.INSTANCE == null) {
            LinkGenerator.Holder.INSTANCE = new LinkGenerator(versionHandler);
        }
    }

    public static LinkGenerator getInstance() {
        if (LinkGenerator.Holder.INSTANCE == null) {
            throw new IllegalStateException("LinkGenerator is not initialized. Use LinkGenerator.init().");
        }
        return LinkGenerator.Holder.INSTANCE;
    }

    public Link generateLink(Dependency dependency, LinkExtension extension) {
        String groupId = this.refactorGroupId(dependency);
        String artifactId = dependency.getArtifactId();
        String version = dependency.getVersion();

        if ((version == null) || (version.matches("([\\[(]).+"))) {
            MetadataLink metadataLink = this.generateMetadataLink(dependency);
            version = this.versionHandler.handleVersion(metadataLink)
                    .getVersion();
        }

        try {
            String link = MAVEN_LINK.replace("{groupId}", groupId)
                    .replace("{artifactId}", artifactId)
                    .replace("{version}", version)
                    .replace("{extension}", extension.getValue());
            URI uri = new URI(link);
            return new Link(dependency, uri, extension);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public MetadataLink generateMetadataLink(Dependency dependency) {
        String groupId = this.refactorGroupId(dependency);
        String link = MAVEN_METADATA_LINK.replace("{groupId}", groupId)
                .replace("{artifactId}", dependency.getArtifactId())
                .replace("{extension}", LinkExtension.XML.getValue());
        URI uri = URI.create(link);
        return new MetadataLink(dependency, uri, LinkExtension.XML);
    }

    public String refactorGroupId(Dependency dependency) {
        return dependency.getGroupId().replace('.', '/');
    }
}
