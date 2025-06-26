package loader.utilities;

import loader.objects.Dependency;
import loader.objects.link.LinkExtension;
import loader.objects.link.MetadataLink;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VersionHandler {

    private final PomReader pomReader;
    private final ConnectionManager connectionManager;
    private final VersionParser versionParser;
    private final XmlNavigator xmlNavigator;

    public VersionHandler(PomReader pomReader, ConnectionManager connectionManager, XmlNavigator xmlNavigator, VersionParser versionParser) {
        this.pomReader = pomReader;
        this.connectionManager = connectionManager;
        this.versionParser = versionParser;
        this.xmlNavigator = xmlNavigator;
    }

    private static class Holder {
        private static VersionHandler INSTANCE = null;
    }

    public static synchronized void init(PomReader pomReader, ConnectionManager connectionManager, XmlNavigator xmlNavigator, VersionParser versionParser) {
        if (VersionHandler.Holder.INSTANCE == null) {
            VersionHandler.Holder.INSTANCE = new VersionHandler(pomReader, connectionManager, xmlNavigator, versionParser);
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

    public Version handleInterval(String v, MetadataLink metadataLink) {

        Pattern pattern = Pattern.compile("^\\s*[\\[(]\\s*[^,\\[\\]()]*\\s*(?:,\\s*[^,\\[\\]()]*\\s*)?[])]\\s*$");
        Matcher matcher = pattern.matcher(v);
        if (!matcher.matches()) {
            return this.versionParser.split(v);
        }

        String[] versionSplit = v.split(",");
        Version first = this.versionParser.split(versionSplit[0]);
        Version second = this.versionParser.split(versionSplit[1]);

        System.out.println(first);
        System.out.println(second);

        List<Version> versions = this.extractVersions(metadataLink);
        List<Version> intervalVersions = this.findIntervalVersion(first, second, versions);

        intervalVersions.forEach(iv -> System.out.println("IV : " + iv));
        return intervalVersions.getLast();
    }

    private List<Version> extractVersions(MetadataLink metadataLink) {
        var nodes = this.xmlNavigator.getAll(metadataLink.getUri().toString(), "metadata.versioning.versions.version");
        List<Version> versions = new ArrayList<>();
        for (var n : nodes) {
            String rv = n.getNode().getTextContent();
            Version v = this.versionParser.split(rv);
            versions.add(v);
        }
        return versions;
    }

    private List<Version> findIntervalVersion(Version v1, Version v2, List<Version> versions) {
        List<Version> v1versions = this.findVersions(v1, versions);
        if(v2.getRankingPoints() == 0){
            System.out.println(v2.getRankingPoints());
            System.out.println(v1.getRankingPoints());
            return v1versions;
        }
        List<Version> v2versions = this.findVersions(v2, versions);

        v2versions.retainAll(v1versions);
        return v2versions;
    }

    private List<Version> findVersions(Version v, List<Version> versions) {

        int points = v.getRankingPoints();
        System.out.println(points);
        VersionIntervalDirection direction = v.getDirection();

        return switch (direction){
            case BIGGER_OR_EQUAL -> versions.stream()
                    .filter(vs -> !(points >= vs.getRankingPoints()))
                    .collect(Collectors.toCollection(ArrayList::new));
            case LESS_OR_EQUAL -> versions.stream()
                    .filter(vs -> points <= vs.getRankingPoints())
                    .collect(Collectors.toCollection(ArrayList::new));
            case LESS -> versions.stream()
                    .filter(vs -> points < vs.getRankingPoints())
                    .collect(Collectors.toCollection(ArrayList::new));
            case EQUAL -> versions.stream()
                    .filter(vs -> points == vs.getRankingPoints())
                    .collect(Collectors.toCollection(ArrayList::new));
            case BIGGER -> versions.stream()
                    .filter(vs -> points > vs.getRankingPoints())
                    .collect(Collectors.toCollection(ArrayList::new));
        };
    }


    public static void main(String[] args) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            PomReader.init(documentBuilder);
            PomReader pomReader = PomReader.getInstance();

            ConnectionManager connectionManager = ConnectionManager.getInstance();

            XmlNavigator.init(pomReader);
            XmlNavigator xmlNavigator = XmlNavigator.getInstance();

            VersionParser.init();
            VersionParser versionParser = VersionParser.getInstance();

            VersionHandler.init(pomReader, connectionManager, xmlNavigator, versionParser);
            VersionHandler versionHandler = VersionHandler.getInstance();

            URI uri = new URI("https://repo1.maven.org/maven2/junit/junit/maven-metadata.xml");
            MetadataLink metadataLink = new MetadataLink(null, uri, LinkExtension.XML);
            versionHandler.handleInterval("[4.13-rc-2,)", metadataLink);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
