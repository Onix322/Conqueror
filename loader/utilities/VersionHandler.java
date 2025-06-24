package loader.utilities;

import loader.objects.Dependency;
import loader.objects.NodeAttributes;
import loader.objects.link.LinkExtension;
import loader.objects.link.MetadataLink;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionHandler {

    private final PomReader pomReader;
    private final ConnectionManager connectionManager;
    private final VersionComparator versionComparator;

    public VersionHandler(PomReader pomReader, ConnectionManager connectionManager, VersionComparator versionComparator) {
        this.pomReader = pomReader;
        this.connectionManager = connectionManager;
        this.versionComparator = versionComparator;
    }

    private static class Holder {
        private static VersionHandler INSTANCE = null;
    }

    public static synchronized void init(PomReader pomReader, ConnectionManager connectionManager, VersionComparator versionComparator) {
        if (VersionHandler.Holder.INSTANCE == null) {
            VersionHandler.Holder.INSTANCE = new VersionHandler(pomReader, connectionManager, versionComparator);
        }
    }

    public static VersionHandler getInstance() {
        if (VersionHandler.Holder.INSTANCE == null) {
            throw new IllegalStateException("VersionHandler is not initialized. Use VersionHandler.init().");
        }
        return VersionHandler.Holder.INSTANCE;
    }

    public Dependency handleVersion(MetadataLink metadataLink) {
        String version = this.handleURL(metadataLink);
        metadataLink.getDependency().setVersion(version);
        return metadataLink.getDependency();
    }

    private String handleURL(MetadataLink metadataLink) {
        try (InputStream inputStream = connectionManager.open(metadataLink.getUri().toURL())) {
            Document document = pomReader.readStream(inputStream);
            Map<String, String> versions = pomReader.extractTagsAsMap("version", document);
            String version = versions.get("version");
            metadataLink.getDependency().setVersion(version);
            return version;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String handleInterval(String version, PomReader pomReader, MetadataLink metadataLink) {

        Pattern pattern = Pattern.compile("^([\\[(])\\s*(\\d+(?:\\.\\d+)*|)\\s*(?:,\\s*(\\d+(?:\\.\\d+)*|))?\\s*([])])$");
        Matcher matcher = pattern.matcher(version);
        if (!matcher.matches()) {
            return version;
        }

        String[] versionSplit = version.split(",");

        var first = firstPartOfInterval(versionSplit[0]);
        var second = secondPartOfInterval(versionSplit[1]);

        System.out.println(first);
        System.out.println(second);
        Document document = pomReader.readString(metadataLink.getUri().toString());
        NodeAttributes metadata = pomReader.extract(document, "metadata").getFirst();
        NodeAttributes versioning = pomReader.extract(metadata.getNode(), "versioning").getFirst();
        NodeAttributes versions = pomReader.extract(versioning.getNode(), "versions").getFirst();
        List<NodeAttributes> versionList = pomReader.extract(versions.getNode(), "version");

        versionList.forEach(v -> System.out.println(v.getNode().getTextContent()));

        System.out.println(versionComparator.compare(first, second));
        return "";
    }

    private Map.Entry<Integer[], VersionIntervalDirection> firstPartOfInterval(String firstPart) {
        String version = firstPart.substring(1);
        String[] split = version.split("\\.");
        Integer[] integers = this.parse(split);
        var direction = VersionIntervalDirection.getDirection(firstPart.substring(0, 1));
        return Map.entry(integers, direction);
    }

    private Map.Entry<Integer[], VersionIntervalDirection> secondPartOfInterval(String secondPart) {
        String version = secondPart.substring(0, secondPart.length() - 1);
        String[] split = version.split("\\.");
        Integer[] integers = this.parse(split);
        var direction = VersionIntervalDirection.getDirection(secondPart.substring(secondPart.length() - 1));
        return Map.entry(integers, direction);
    }

    public Integer[] parse(String[] numbersLike) {
        return Arrays.stream(numbersLike).map(Integer::parseInt)
                .toArray(Integer[]::new);
    }

    public static void main(String[] args) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            PomReader.init(documentBuilder);
            PomReader pomReader = PomReader.getInstance();
            ConnectionManager connectionManager = ConnectionManager.getInstance();
            VersionComparator.init();
            VersionComparator versionComparator = VersionComparator.getInstance();

            VersionHandler.init(pomReader, connectionManager, versionComparator);
            VersionHandler versionHandler = VersionHandler.getInstance();

            URI uri = new URI("https://repo1.maven.org/maven2/junit/junit/maven-metadata.xml");
            MetadataLink metadataLink = new MetadataLink(null, uri, LinkExtension.XML);
            versionHandler.handleInterval("[4.13.2.2,4.13.2)", PomReader.getInstance(), metadataLink);

        } catch (URISyntaxException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
