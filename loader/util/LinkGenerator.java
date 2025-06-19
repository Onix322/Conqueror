package loader.util;

import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LinkGenerator {

    private final String MAVEN_LINK = "https://repo1.maven.org/maven2/{groupId}/{artifactId}/{version}/{artifactId}-{version}.{extension}";
    private final String MAVEN_METADATA_LINK = "https://repo1.maven.org/maven2/{groupId}/{artifactId}/maven-metadata.xml";
    public final String POM_EXTENSION = "pom";
    public final String JAR_EXTENSION = "jar";
    private final PomReader pomReader;
    private final ConnectionManager connectionManager;

    public LinkGenerator(PomReader pomReader, ConnectionManager connectionManager) {
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
                .replace("{artifactId}", dependency.getArtifactId());
        URI uri = URI.create(link);
        return new MetadataLink(dependency, uri);
    }

    public String generateLink(Dependency dependency, String extension) {
        String groupId = this.refactorGroupId(dependency);
        String artifactId = dependency.getArtifactId();
        String version = dependency.getVersion();

        if (version == null) {
            version = this.handleNullVersion(dependency);
        }
        return MAVEN_LINK.replace("{groupId}", groupId)
                .replace("{artifactId}", artifactId)
                .replace("{version}", version)
                .replace("{extension}", extension);
    }

    private String handleNullVersion(Dependency dependency) {
        MetadataLink metadataLink = this.generateMetadataLink(dependency);
        return this.handleURL(metadataLink);
    }

    public String handleURL(MetadataLink metadataLink) {
        try (InputStream inputStream = this.connectionManager.open(metadataLink.getLink().toURL())) {
            Document document = this.pomReader.readStream(inputStream);
            Map<String, String> versions = this.pomReader.extractTags("version", document);
            return versions.get("version");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<String> generateJar(List<Dependency> dependencies) {
        List<String> links = new ArrayList<>();
        for (Dependency dependency : dependencies) {
            links.add(this.generateLink(dependency, JAR_EXTENSION));
        }
        return links;
    }

    public List<String> generatePom(List<Dependency> dependencies) {
        List<String> links = new ArrayList<>();
        for (Dependency dependency : dependencies) {
            links.add(this.generateLink(dependency, POM_EXTENSION));
        }
        return links;
    }

    public String refactorGroupId(Dependency dependency) {
        return dependency.getGroupId().replace('.', '/');
    }
}
