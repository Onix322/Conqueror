package loader.utilities;


import loader.utilities.linkGenerator.LinkGenerator;
import loader.utilities.linkGenerator.link.LinkExtension;
import loader.utilities.linkGenerator.link.VersionedLink;
import loader.utilities.pomReader.PomReader;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.Dependencies;
import loader.utilities.pomReader.supportedTagsClasses.artifact.dependency.Dependency;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.XMLParsed;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.project.Project;
import src.com.server.configuration.Configuration;

import java.util.HashSet;
import java.util.Set;

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

    public Set<VersionedLink> resolve() {

        System.out.println("[" + this.getClass().getSimpleName() + "] -> Resolving pom.xml...");
        XMLParsed xmlParsed = this.pomReader.readString(pomFileLocation);
        if (xmlParsed == null) return Set.of();
        Dependencies dependencies = xmlParsed.<Project>getAs().getDependencies();
        Set<Dependency> allDps = this.recursiveResolve(new HashSet<>(dependencies), visited);
        return this.generateJarLinks(new HashSet<>(allDps));
    }

    private Set<Dependency> recursiveResolve(Set<Dependency> loadedDps, Set<VersionedLink> visited) {

        Set<Dependency> allDps = new HashSet<>(loadedDps);
        for (Dependency dp : loadedDps) {
            VersionedLink pomVersionedLink = this.linkGenerator.generateLink(dp, LinkExtension.POM);
            boolean checking = mustCheck(pomVersionedLink, visited, allDps, dp);
            if (!checking) continue;
            visited.add(pomVersionedLink);
            XMLParsed xmlParsed = this.pomReader.readString(pomVersionedLink.getUri().toString());
            if (xmlParsed == null) continue;
            Project project = xmlParsed.getAs();
            Dependencies dependencies = project.getDependencies();
            allDps.addAll(this.recursiveResolve(new HashSet<>(dependencies), visited));
        }

        return allDps;
    }

    private boolean mustCheck(VersionedLink pomVersionedLink, Set<VersionedLink> visited, Set<Dependency> allDps, Dependency dp) {
        // taken dependency from pomLink
        // because it includes a version
        if (dp.getVersion().isUnknown(dp.getVersion())) {
            System.out.println("[" + this.getClass().getSimpleName() + "] -> Ghost dependency detected "
                    + dp.getGroupId()
                    + "::"
                    + dp.getArtifactId()
                    + "::" + dp.getVersion()
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

        if (dp.getScope() != null && dp.getScope().equals("test")) {
            return false;
        }

        if (dp.getType() != null && !(dp.getType().equals("jar"))) {
            allDps.remove(dp);
            return false;
        }

        if (visited.contains(pomVersionedLink)) {
            return false;
        }

        return true;
    }

    private Set<VersionedLink> generateJarLinks(Set<Dependency> dependencies) {

        Set<VersionedLink> versionedLinks = new HashSet<>();

        for (Dependency dependency : dependencies) {
            VersionedLink jarVersionedLink = this.linkGenerator.generateLink(dependency, LinkExtension.JAR);
            versionedLinks.add(jarVersionedLink);
        }

        return versionedLinks;
    }

}