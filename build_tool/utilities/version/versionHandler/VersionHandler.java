package build_tool.utilities.version.versionHandler;

import build_tool.utilities.UrlAccessor;
import build_tool.utilities.linkGenerator.LinkGenerator;
import build_tool.utilities.linkGenerator.link.Link;
import build_tool.utilities.linkGenerator.link.LinkExtension;
import build_tool.utilities.linkGenerator.link.VersionedLink;
import build_tool.utilities.depsReader.DepsReader;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.dependency.Dependencies;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.dependency.Dependency;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.dependency.DependencyManagement;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.parent.Parent;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.xml.XMLParsed;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.xml.metadata.Metadata;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.xml.metadata.Versions;
import build_tool.utilities.depsReader.supportedTagsClasses.artifact.xml.project.Project;
import build_tool.utilities.version.FixedVersion;
import build_tool.utilities.version.IntervalVersion;
import build_tool.utilities.version.Version;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * VersionHandler is responsible for managing and resolving versions of dependencies in a Maven project.
 * It handles null versions, interval versions, and searches for versions in dependency management and parent POMs.
 */
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

    public Project handleVersion(Project project, DepsReader depsReader) {
        if (project.getDependencies() == null) {
            project.setDependencies(new Dependencies());
        }
        for (Dependency dependency : project.getDependencies()) {

            // Resolve null version
            if (dependency.getVersion() == null) {
                Version notNullVersion = this.handleNullVersion(dependency, depsReader);
                dependency.setVersion(notNullVersion);
            }
            if (dependency.getVersion() != null && dependency.getVersion().isFixed()) continue;

            // Interval treating
            if (dependency.getVersion().isInterval()) {
                Version versionFromInterval = this.handleInterval(dependency, depsReader);
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
            Version parentVersion = this.searchParent(dependency, project.getParent(), depsReader);
            if (dmVersion != null && dmVersion.isFixed()) {
                dependency.setVersion(parentVersion);
            }

        }
        return project;
    }

    private Version handleNullVersion(Dependency dependency, DepsReader depsReader) {
        Link metadataLink = this.linkGenerator.generateLink(dependency);
        XMLParsed xmlParsed = depsReader.readString(metadataLink.getUri().toString());
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

    private Version searchParent(Dependency dependency, Parent parent, DepsReader depsReader) {
        if (parent == null) return dependency.getVersion();
        VersionedLink versionedLink = this.linkGenerator.generateVersionedLink(parent, LinkExtension.POM);
        Project parentPom = depsReader.readString(versionedLink.getUri().toString())
                .getAs();
        return this.searchDependencyManagement(dependency, parentPom.getDependencyManagement());
    }

    public Version handleInterval(Dependency dependency, DepsReader depsReader) {

        if (!dependency.getVersion().isInterval()) {
            return dependency.getVersion();
        }

        Link metadataLink = this.linkGenerator.generateLink(dependency);
        Metadata versionMetadata = depsReader.readString(metadataLink.getUri().toString())
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
