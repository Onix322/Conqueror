package loader.utilities.version.versionHandler;

import loader.utilities.UrlAccessor;
import loader.utilities.linkGenerator.LinkGenerator;
import loader.utilities.linkGenerator.link.Link;
import loader.utilities.linkGenerator.link.LinkExtension;
import loader.utilities.linkGenerator.link.VersionedLink;
import loader.utilities.pomReader.PomReader;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.Dependencies;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.Dependency;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.DependencyManagement;
import loader.utilities.pomReader.supportedTagsClasses.artifact.parent.Parent;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.XMLParsed;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.metadata.Metadata;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.metadata.Versions;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.project.Project;
import loader.utilities.version.FixedVersion;
import loader.utilities.version.IntervalVersion;
import loader.utilities.version.Version;

import java.util.Objects;
import java.util.Optional;
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

    public Project handleVersion(Project project, PomReader pomReader) {
        if (project.getDependencies() == null) {
            project.setDependencies(new Dependencies());
        }
        for (Dependency dependency : project.getDependencies()) {

            // Resolve null version
            if (dependency.getVersion() == null) {
                Version notNullVersion = this.handleNullVersion(dependency, pomReader);
                dependency.setVersion(notNullVersion);
            }
            if (dependency.getVersion() != null && dependency.getVersion().isFixed()) continue;

            // Interval treating
            if (dependency.getVersion().isInterval()) {
                Version versionFromInterval = this.handleInterval(dependency, pomReader);
                dependency.setVersion(versionFromInterval);
                continue;
            }
            // DependencyManagement search
            Version dmVersion = this.searchDependencyManagement(dependency, project.getDependencyManagement());
            if (dmVersion != null && dmVersion.isFixed()) {
                dependency.setVersion(dmVersion);
                continue;
            }
            // Parent search
            Version parentVersion = this.searchParent(dependency, project.getParent(), pomReader);
            if (dmVersion != null && dmVersion.isFixed()) {
                dependency.setVersion(parentVersion);
            }

        }
        return project;
    }

    private Version handleNullVersion(Dependency dependency, PomReader pomReader) {
        Link metadataLink = this.linkGenerator.generateLink(dependency);
        XMLParsed xmlParsed = pomReader.readString(metadataLink.getUri().toString());
        if (xmlParsed == null) return FixedVersion.unknown();
        Metadata versionMetadata = xmlParsed.getAs();
        return versionMetadata.getVersioning().getVersions().getLast();
    }

    private Version searchDependencyManagement(Dependency dependency, DependencyManagement dependencyManagement) {
        if (dependencyManagement == null) return dependency.getVersion();

        Optional<Dependency> dmOptional = dependencyManagement.getDependencies()
                .stream()
                .filter(dm -> Objects.equals(dependency.getGroupId(), dm.getGroupId()) &&
                        Objects.equals(dependency.getArtifactId(), dm.getArtifactId())
                )
                .findFirst();

        return dmOptional.isPresent() ? dmOptional.get().getVersion() : dependency.getVersion();
    }

    private Version searchParent(Dependency dependency, Parent parent, PomReader pomReader) {
        if (parent == null) return dependency.getVersion();
        VersionedLink versionedLink = this.linkGenerator.generateVersionedLink(parent, LinkExtension.POM);
        Project parentPom = pomReader.readString(versionedLink.getUri().toString())
                .getAs();
        return this.searchDependencyManagement(dependency, parentPom.getDependencyManagement());
    }

    public Version handleInterval(Dependency dependency, PomReader pomReader) {

        if (!dependency.getVersion().isInterval()) {
            return dependency.getVersion();
        }

        Link metadataLink = this.linkGenerator.generateLink(dependency);
        Metadata versionMetadata = pomReader.readString(metadataLink.getUri().toString())
                .getAs();

        IntervalVersion intervalVersion = dependency.getVersion().getAs(IntervalVersion.class);

        Versions fixedVersions = this.findIntervalVersion(intervalVersion, versionMetadata.getVersioning().getVersions());
        return fixedVersions.getLast();
    }

    private Versions findIntervalVersion(IntervalVersion intervalVersion, Versions versions) {
        Versions v1versions = this.findVersions(intervalVersion.getFirst(), versions);
        if (intervalVersion.getSecond().getRankingPoints() == 0) {
            return v1versions;
        }
        Versions v2versions = this.findVersions(intervalVersion.getSecond(), versions);

        v1versions.retainAll(v2versions);
        return v1versions;
    }

    private Versions findVersions(FixedVersion v, Versions versions) {

        VersionIntervalDirection direction = v.getDirection();
        return switch (direction) {
            case BIGGER_OR_EQUAL -> versions.stream()
                    .filter(vs -> vs.compareTo(v) == 1 || vs.compareTo(v) == 0)
                    .collect(Collectors.toCollection(Versions::new));
            case LESS_OR_EQUAL -> versions.stream()
                    .filter(vs -> vs.compareTo(v) == 0 || vs.compareTo(v) == -1)
                    .collect(Collectors.toCollection(Versions::new));
            case LESS -> versions.stream()
                    .filter(vs -> vs.compareTo(v) == -1)
                    .collect(Collectors.toCollection(Versions::new));
            case EQUAL -> versions.stream()
                    .filter(vs -> vs.compareTo(v) == 0)
                    .collect(Collectors.toCollection(Versions::new));
            case BIGGER -> versions.stream()
                    .filter(vs -> vs.compareTo(v) == 1)
                    .collect(Collectors.toCollection(Versions::new));
        };
    }

}
