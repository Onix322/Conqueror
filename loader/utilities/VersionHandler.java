package loader.utilities;

import loader.objects.Dependency;
import loader.objects.link.MetadataLink;
import loader.objects.link.Version;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VersionHandler {

    private final PomReader pomReader;
    private final UrlAccessor urlAccessor;
    private final VersionParser versionParser;
    private final XmlNavigator xmlNavigator;

    public VersionHandler(PomReader pomReader, UrlAccessor urlAccessor, XmlNavigator xmlNavigator, VersionParser versionParser) {
        this.pomReader = pomReader;
        this.urlAccessor = urlAccessor;
        this.versionParser = versionParser;
        this.xmlNavigator = xmlNavigator;
    }

    private static class Holder {
        private static VersionHandler INSTANCE = null;
    }

    public static synchronized void init(PomReader pomReader, UrlAccessor connectionManager, XmlNavigator xmlNavigator, VersionParser versionParser) {
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
        try (InputStream inputStream = urlAccessor.open(metadataLink.getUri().toURL())) {
            Document document = pomReader.readStream(inputStream);
            Map<String, String> versions = pomReader.extractTagsAsMap("version", document);
            String rawVersion = versions.get("version");
            Version handledVersion = this.handleInterval(rawVersion, metadataLink);
            metadataLink.getDependency().setVersion(handledVersion.asString());
            return handledVersion.asString();
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

        List<Version> versions = this.extractVersions(metadataLink);
        List<Version> intervalVersions = this.findIntervalVersion(first, second, versions);

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
            return v1versions;
        }
        List<Version> v2versions = this.findVersions(v2, versions);

        v2versions.retainAll(v1versions);
        return v2versions;
    }

    private List<Version> findVersions(Version v, List<Version> versions) {

        int points = v.getRankingPoints();
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
}
