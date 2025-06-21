package loader.utilities;

import loader.objects.Dependency;
import loader.objects.link.Link;
import loader.objects.link.LinkExtension;
import loader.objects.link.MetadataLink;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class LinkGenerator {

    private final String MAVEN_LINK = "https://repo1.maven.org/maven2/{groupId}/{artifactId}/{version}/{artifactId}-{version}.{extension}";
    private final String MAVEN_METADATA_LINK = "https://repo1.maven.org/maven2/{groupId}/{artifactId}/maven-metadata.{extension}";
    private final PomReader pomReader;
    private final ConnectionManager connectionManager;

    private LinkGenerator(PomReader pomReader, ConnectionManager connectionManager) {
        this.pomReader = pomReader;
        this.connectionManager = connectionManager;
    }

    private static class Holder {
        private static LinkGenerator INSTANCE = null;
    }

    public static synchronized void init(PomReader pomReader, ConnectionManager connectionManager) {
        if (LinkGenerator.Holder.INSTANCE == null) {
            LinkGenerator.Holder.INSTANCE = new LinkGenerator(pomReader, connectionManager);
        }
    }

    public static LinkGenerator getInstance() {
        if (LinkGenerator.Holder.INSTANCE == null) {
            throw new IllegalStateException("LinkGenerator is not initialized. Use LinkGenerator.init().");
        }
        return LinkGenerator.Holder.INSTANCE;
    }

    public MetadataLink generateMetadataLink(Dependency dependency) {
        String groupId = this.refactorGroupId(dependency);
        String link = MAVEN_METADATA_LINK.replace("{groupId}", groupId)
                .replace("{artifactId}", dependency.getArtifactId())
                .replace("{extension}", LinkExtension.XML.getValue());
        URI uri = URI.create(link);
        return new MetadataLink(dependency, uri, LinkExtension.XML);
    }

    public Link generateLink(Dependency dependency, LinkExtension extension) {
        String groupId = this.refactorGroupId(dependency);
        String artifactId = dependency.getArtifactId();
        String version = dependency.getVersion();

        if (version == null) {
            version = this.handleNullVersion(dependency);
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

    private String handleNullVersion(Dependency dependency) {
        MetadataLink metadataLink = this.generateMetadataLink(dependency);
        return this.handleURL(metadataLink);
    }

    public String handleURL(MetadataLink metadataLink) {
        try (InputStream inputStream = this.connectionManager.open(metadataLink.getUri().toURL())) {
            Document document = this.pomReader.readStream(inputStream);
            Map<String, String> versions = this.pomReader.extractTags("version", document);
            String version = versions.get("version");
            metadataLink.getDependency().setVersion(version);
            return version;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String refactorGroupId(Dependency dependency) {
        return dependency.getGroupId().replace('.', '/');
    }
}
