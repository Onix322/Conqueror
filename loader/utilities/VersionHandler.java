package loader.utilities;

import loader.objects.Dependency;
import loader.objects.link.LinkExtension;
import loader.objects.link.MetadataLink;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionHandler {

    public VersionHandler() {
    }

    private static class Holder {
        private static VersionHandler INSTANCE = null;
    }

    public static synchronized void init() {
        if (VersionHandler.Holder.INSTANCE == null) {
            VersionHandler.Holder.INSTANCE = new VersionHandler();
        }
    }

    public static VersionHandler getInstance() {
        if (VersionHandler.Holder.INSTANCE == null) {
            throw new IllegalStateException("VersionHandler is not initialized. Use VersionHandler.init().");
        }
        return VersionHandler.Holder.INSTANCE;
    }

    public Dependency handleVersion(MetadataLink metadataLink, PomReader pomReader, ConnectionManager connectionManager) {
        String version = this.handleURL(metadataLink, pomReader, connectionManager);
        metadataLink.getDependency().setVersion(version);
        return metadataLink.getDependency();
    }

    private String handleURL(MetadataLink metadataLink, PomReader pomReader, ConnectionManager connectionManager) {
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

//    public String handleVersionInterval(String version){
//        return handleInterval(version);
//    }

    /**
     * [ -> '>='
     * ] -> '<='
     * ( -> '>'
     * ) -> '<'
     * none of them means '=' -> output = input
     */
    public static String handleInterval(String version, PomReader pomReader, MetadataLink metadataLink) {

        Pattern pattern = Pattern.compile("^([\\[(])\\s*(\\d+(?:\\.\\d+)*|)\\s*(?:,\\s*(\\d+(?:\\.\\d+)*|))?\\s*([])])$");
        Matcher matcher = pattern.matcher(version);
        if (!matcher.matches()) {
            return version;
        }

        String[] versionSplit = version.split(",");

        return "";
    }

    private static Map.Entry<String, VersionIntervalDirection> firstPartOfInterval(String firstPart) {
        String version = firstPart.substring(1);
        var direction = VersionIntervalDirection.getDirection(firstPart.substring(0, 1));
        return Map.entry(version, direction);
    }

    private static Map.Entry<String, VersionIntervalDirection> secondPartOfInterval(String secondPart) {
        String version = secondPart.substring(0, secondPart.length() - 1);
        var direction = VersionIntervalDirection.getDirection(secondPart.substring(secondPart.length() - 1));
        return Map.entry(version, direction);
    }

    private static String compare(Map.Entry<String, VersionIntervalDirection> v1, Map.Entry<String, VersionIntervalDirection> v2, List<Map.Entry<String, String>> versions) {

        return "";
    }

    public static void main(String[] args) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            PomReader.init(documentBuilder);

            URI uri = new URI("https://repo1.maven.org/maven2/junit/junit/maven-metadata.xml");
            MetadataLink metadataLink = new MetadataLink(null, uri, LinkExtension.XML);
            handleInterval("[4.13.1,4.13.2)", PomReader.getInstance(), metadataLink);
        } catch (URISyntaxException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
