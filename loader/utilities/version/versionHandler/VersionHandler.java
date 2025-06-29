package loader.utilities.version.versionHandler;

import loader.utilities.UrlAccessor;
import loader.utilities.linkGenerator.LinkGenerator;
import loader.utilities.linkGenerator.link.VersionedLink;
import loader.utilities.linkGenerator.link.LinkExtension;
import loader.utilities.pomReader.PomReader;
import loader.utilities.pomReader.supportedTagsClasses.artifact.Artifact;
import loader.utilities.pomReader.supportedTagsClasses.artifact.VersionedArtifact;
import loader.utilities.pomReader.supportedTagsClasses.artifact.project.Project;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.Dependency;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.DependencyManagement;
import loader.utilities.pomReader.supportedTagsClasses.artifact.parent.Parent;
import loader.utilities.version.Version;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VersionHandler {

    private final UrlAccessor urlAccessor;
    private final LinkGenerator linkGenerator;

    public VersionHandler(UrlAccessor urlAccessor, LinkGenerator linkGenerator) {
        this.urlAccessor = urlAccessor;
        this.linkGenerator = linkGenerator;
    }

    private static class Holder {
        private static VersionHandler INSTANCE = null;
    }

    public static synchronized void init(UrlAccessor connectionManager, LinkGenerator linkGenerator) {
        if (Holder.INSTANCE == null) {
            Holder.INSTANCE = new VersionHandler(connectionManager, linkGenerator);
        }
    }

    public static VersionHandler getInstance() {
        if (Holder.INSTANCE == null) {
            throw new IllegalStateException("VersionHandler is not initialized. Use VersionHandler.init().");
        }
        return Holder.INSTANCE;
    }

    public Project.Builder handleVersion(Project.Builder project, PomReader pomReader) {
        for (Dependency dependency : project.getDependencies()) {
            if (dependency.getVersion() != null) continue;
            Dependency dmVersion = this.searchDependencyManagement(dependency, project.getDependencyManagement());
            if (dmVersion.getVersion() != null) continue;
            Dependency parentVersion = this.searchParent(dependency, project.getParent(), pomReader);
            if (parentVersion.getVersion() != null) continue;
        }
        return project;
    }

    private Dependency searchDependencyManagement(Dependency dependency, DependencyManagement dependencyManagement) {
        Optional<Dependency> dmOptional = dependencyManagement.getDependencies()
                .stream()
                .filter(dm -> Objects.equals(dependency.getGroupId(), dm.getGroupId()) &&
                        Objects.equals(dependency.getArtifactId(), dm.getArtifactId())
                )
                .findFirst();

        if (dmOptional.isPresent()) {
            dependency.setVersion(dmOptional.get().getVersion());
        } else {
            dependency.setVersion(null);
        }
        return dependency;
    }

    private Dependency searchParent(Dependency dependency, Parent parent, PomReader pomReader) {
        VersionedLink versionedLink = this.linkGenerator.generateLink(parent, LinkExtension.POM);
        Project parentPom = pomReader.readString(versionedLink.getUri().getRawPath());
        Dependency parentDependency = this.searchDependencyManagement(dependency, parentPom.getDependencyManagement());
        dependency.setVersion(parentDependency.getVersion());
        return dependency;
    }

    //! MUST IMPLEMENT
    public Version handleInterval(String rawVersion, VersionParser parser){
//        Pattern pattern = Pattern.compile("^\\s*[\\[(]\\s*[^,\\[\\]()]*\\s*(?:,\\s*[^,\\[\\]()]*\\s*)?[])]\\s*$");
//        Matcher matcher = pattern.matcher(rawVersion);
//        if (!matcher.matches()) {
//            return parser.split(rawVersion);
//        }
//
//        String[] versionSplit = rawVersion.split(",");
//        Version first = parser.split(versionSplit[0]);
//        Version second = parser.split(versionSplit[1]);
//
//        List<Version> versions = this.extractVersions(metadataLink);
//        List<Version> intervalVersions = this.findIntervalVersion(first, second, versions);

//        return intervalVersions.getLast();

        return null;
    }

//    private Version handleURL(Dependency dependency) {
//        try (InputStream inputStream = urlAccessor.open(metadataLink.getUri().toURL())) {
//            Document document = pomReader.readStream(inputStream);
//            Map<String, String> versions = pomReader.extractTagsAsMap("version", document);
//            String rawVersion = versions.get("version");
//            Version handledVersion = this.handleInterval(rawVersion, metadataLink);
//            metadataLink.getDependency().setVersion(handledVersion.asString());
//            return handledVersion.asString();
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//    }
//
//    public Version handleInterval(String v, Link metadataLink) {
//
//        Pattern pattern = Pattern.compile("^\\s*[\\[(]\\s*[^,\\[\\]()]*\\s*(?:,\\s*[^,\\[\\]()]*\\s*)?[])]\\s*$");
//        Matcher matcher = pattern.matcher(v);
//        if (!matcher.matches()) {
//            return this.versionParser.split(v);
//        }
//
//        String[] versionSplit = v.split(",");
//        Version first = this.versionParser.split(versionSplit[0]);
//        Version second = this.versionParser.split(versionSplit[1]);
//
//        List<Version> versions = this.extractVersions(metadataLink);
//        List<Version> intervalVersions = this.findIntervalVersion(first, second, versions);
//
//        return intervalVersions.getLast();
//    }
//
//    private List<Version> extractVersions(MetadataLink metadataLink) {
//        var nodes = this.xmlNavigator.getAll(metadataLink.getUri().toString(), "metadata.versioning.versions.version");
//        List<Version> versions = new ArrayList<>();
//        for (var n : nodes) {
//            String rv = n.getNode().getTextContent();
//            Version v = this.versionParser.split(rv);
//            versions.add(v);
//        }
//        return versions;
//    }

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
