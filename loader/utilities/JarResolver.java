package loader.utilities;


import loader.utilities.linkGenerator.LinkGenerator;
import loader.utilities.linkGenerator.link.LinkExtension;
import loader.utilities.linkGenerator.link.VersionedLink;
import loader.utilities.pomReader.PomReader;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.Dependencies;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.Dependency;
import loader.utilities.pomReader.supportedTagsClasses.artifact.exclusion.Exclusion;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.XMLParsed;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.project.Project;
import configuration.Configuration;

import java.util.*;

/**
 * JarResolver is responsible for resolving JAR dependencies from a Maven POM file.
 * It reads the POM file, resolves dependencies recursively, and generates links for JAR files.
 */
public class JarResolver {

    private final LinkGenerator linkGenerator;
    private final PomReader pomReader;
    private final String pomFileLocation;
    private final ArtifactValidator artifactValidator;
    private final Set<VersionedLink> visited = new HashSet<>();

    public JarResolver(PomReader pomReader, LinkGenerator linkGenerator, ArtifactValidator artifactValidator, Configuration configuration) {
        this.linkGenerator = linkGenerator;
        this.pomReader = pomReader;
        this.artifactValidator = artifactValidator;
        this.pomFileLocation = configuration.readProperty("pom.location");
    }

    private static class Holder {
        private static JarResolver INSTANCE = null;
    }

    public static synchronized void init(PomReader pomReader, LinkGenerator linkGenerator, ArtifactValidator artifactValidator, Configuration configuration) {
        if (JarResolver.Holder.INSTANCE == null) {
            JarResolver.Holder.INSTANCE = new JarResolver(pomReader, linkGenerator, artifactValidator, configuration);
        }
    }

    public static JarResolver getInstance() {
        if (JarResolver.Holder.INSTANCE == null) {
            throw new IllegalStateException("JarResolver is not initialized. Use JarResolver.init().");
        }
        return JarResolver.Holder.INSTANCE;
    }

    /**
     * Resolves the JAR dependencies from the POM file located at pomFileLocation.
     * It reads the POM file, extracts dependencies, and recursively resolves them.
     *
     * @return a set of VersionedLink objects representing the resolved JAR dependencies.
     */
    public Set<VersionedLink> resolve() {
        System.out.println("[" + this.getClass().getSimpleName() + "] -> Resolving pom.xml...");
        XMLParsed xmlParsed = this.pomReader.readString(pomFileLocation);
        if (xmlParsed == null) return Set.of();
        Dependencies dependencies = xmlParsed.<Project>getAs().getDependencies();
        List<Exclusion> exclusions = new LinkedList<>();
        dependencies.forEach(d -> exclusions.addAll(d.getExclusions()));
        Set<Dependency> allDps = this.recursiveResolve(new LinkedHashSet<>(dependencies), visited, exclusions);
        return this.generateJarLinks(new LinkedHashSet<>(allDps));
    }

    /**
     * Recursively resolves dependencies from the given set of loaded dependencies.
     * It checks each dependency's POM file, extracts its dependencies, and continues resolving them recursively.
     *
     * @param loadedDps the set of currently loaded dependencies
     * @param visited a set of visited VersionedLinks to avoid cycles
     * @param exclusions a list of exclusions to filter out certain dependencies
     * @return a set of all resolved dependencies
     */
    private Set<Dependency> recursiveResolve(Set<Dependency> loadedDps, Set<VersionedLink> visited, List<Exclusion> exclusions) {
        Set<Dependency> allDps = new LinkedHashSet<>(loadedDps);
        for (Dependency dp : loadedDps) {
            VersionedLink pomVersionedLink = this.linkGenerator.generateVersionedLink(dp, LinkExtension.POM);
            boolean checking = mustCheck(pomVersionedLink, visited, allDps, dp, exclusions);
            if (!checking) continue;
            visited.add(pomVersionedLink);
            XMLParsed xmlParsed = this.pomReader.readString(pomVersionedLink.getUri().toString());
            if (xmlParsed == null) continue;
            Project project = xmlParsed.getAs();
            Dependencies dependencies = project.getDependencies();
            allDps.addAll(this.recursiveResolve(new LinkedHashSet<>(dependencies), visited, exclusions));
        }
        return allDps;
    }

    /**
     * Generates a set of VersionedLink objects for JAR files based on the provided dependencies.
     * Each dependency is converted into a VersionedLink with the JAR file extension.
     *
     * @param dependencies the set of dependencies to generate links for
     * @return a set of VersionedLink objects representing the JAR files
     */
    private Set<VersionedLink> generateJarLinks(Set<Dependency> dependencies) {

        Set<VersionedLink> versionedLinks = new HashSet<>();

        for (Dependency dependency : dependencies) {
            VersionedLink jarVersionedLink = this.linkGenerator.generateVersionedLink(dependency, LinkExtension.JAR);
            versionedLinks.add(jarVersionedLink);
        }

        return versionedLinks;
    }

    /**
     * Checks if a dependency must be processed further based on various conditions.
     * It checks for exclusions, existence in the repository, scope, type, and whether it has been visited.
     *
     * @param pomVersionedLink the VersionedLink of the POM file for the dependency
     * @param visited a set of visited VersionedLinks
     * @param allDps a set of all dependencies to be processed
     * @param dp the current dependency being checked
     * @param exclusions a list of exclusions to filter out certain dependencies
     * @return true if the dependency must be checked further, false otherwise
     */
    private boolean mustCheck(VersionedLink pomVersionedLink, Set<VersionedLink> visited, Set<Dependency> allDps, Dependency dp, List<Exclusion> exclusions) {
        boolean removedExclusion = exclusions.removeIf(e -> e.getArtifactId().equals(dp.getArtifactId()) && e.getGroupId().equals(dp.getGroupId()));
        if(removedExclusion){
            visited.add(pomVersionedLink);
            allDps.remove(dp);
        }

        // taken dependency from pomLink
        // because it includes a version
        boolean foundLinks = visited.stream()
                .anyMatch(vl -> vl.getArtifact().getArtifactId().equals(pomVersionedLink.getArtifact().getArtifactId())
                && vl.getArtifact().getGroupId().equals(pomVersionedLink.getArtifact().getGroupId()));
        if(foundLinks){
            visited.add(pomVersionedLink);
        }

        if (dp.getVersion().isUnknown(dp.getVersion())) {
            System.out.println("[" + this.getClass().getSimpleName() + "] -> Ghost dependency detected "
                    + dp.getGroupId()
                    + "::"
                    + dp.getArtifactId()
                    + "::" + dp.getVersion().asString()
            );
            allDps.remove(dp);
            return false;
        }

        if (this.artifactValidator.verifyExistence(pomVersionedLink)) {
            System.out.println("[" + this.getClass().getSimpleName()
                    + "] -> Existing dependency: "
                    + dp.getArtifactId() + "::"
                    + dp.getVersion().asString()
            );
            allDps.remove(dp);
            return false;
        }

        if (dp.getScope() != null && dp.getScope().equals("test") ||
                dp.getScope().equals("system") ||
                dp.getScope().equals("provided")) {
            allDps.remove(dp);
            return false;
        }

        if (dp.getType() != null && !(dp.getType().equals("jar"))) {
            allDps.remove(dp);
            return false;
        }

        if (visited.contains(pomVersionedLink)) {
            allDps.remove(dp);
            return false;
        }

        return true;
    }

}