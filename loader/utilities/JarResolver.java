package loader.utilities;


import loader.objects.Dependency;
import loader.objects.link.Link;
import loader.objects.link.LinkExtension;
import src.com.server.configuration.Configuration;
import org.w3c.dom.Document;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

public class JarResolver {

    private final LinkGenerator linkGenerator;
    private final PomReader pomReader;
    private final Factory factory;
    private final String pomFileLocation;
    private final ArtifactValidator artifactValidator;
    private final Set<Link> visited = new HashSet<>();

    public JarResolver(PomReader pomReader, LinkGenerator linkGenerator, Factory factory, ArtifactValidator artifactValidator, Configuration configuration) {
        this.linkGenerator = linkGenerator;
        this.pomReader = pomReader;
        this.factory = factory;
        this.artifactValidator = artifactValidator;
        this.pomFileLocation = configuration.readProperty("pom.location");
    }

    private static class Holder {
        private static JarResolver INSTANCE = null;
    }

    public static synchronized void init(PomReader pomReader, LinkGenerator linkGenerator, Factory factory, ArtifactValidator artifactValidator, Configuration configuration) {
        if (JarResolver.Holder.INSTANCE == null) {
            JarResolver.Holder.INSTANCE = new JarResolver(pomReader, linkGenerator, factory, artifactValidator, configuration);
        }
    }

    public static JarResolver getInstance() {
        if (JarResolver.Holder.INSTANCE == null) {
            throw new IllegalStateException("JarResolver is not initialized. Use JarResolver.init().");
        }
        return JarResolver.Holder.INSTANCE;
    }

    public Set<Link> resolve() {

        System.out.println("[" + this.getClass().getSimpleName() + "] -> Resolving pom.xml...");

        var document = this.pomReader.readString(pomFileLocation);
        var nodeDependencies = this.pomReader.extractFullDependencies(document);
        var dependencies = this.factory.buildDependencies(nodeDependencies);

        Set<Dependency> allDps = this.recursiveResolve(new HashSet<>(dependencies), visited);
        return this.generateJarLinks(allDps);
    }

    private Set<Dependency> recursiveResolve(Set<Dependency> loadedDps, Set<Link> visited) {
        Set<Dependency> allDps = new HashSet<>(loadedDps);

        for (Dependency dp : loadedDps) {
            Link pomLink = this.linkGenerator.generateLink(dp, LinkExtension.POM);
            boolean checking = mustCheck(pomLink, visited, allDps, dp);
            if(!checking) continue;
            visited.add(pomLink);
            try {
                Document document = this.pomReader.readString(pomLink.getUri().toURL().toString());
                var nodeDependencies = this.pomReader.extractFullDependencies(document);
                var dependencies = new HashSet<>(this.factory.buildDependencies(nodeDependencies));
                allDps.addAll(this.recursiveResolve(dependencies, visited));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        return allDps;
    }

    private boolean mustCheck(Link pomLink, Set<Link> visited, Set<Dependency> allDps, Dependency dp){
        // taken dependency from pomLink
        // because it includes a version
        if(this.artifactValidator.verifyExistence(pomLink)){
            System.out.println("[" + this.getClass().getSimpleName()
                    + "] -> Existing dependency: "
                    + dp.getArtifactId() + "::"
                    + dp.getVersion()
            );
            allDps.remove(dp);
            return false;
        }
        if(dp.getScope() != null && dp.getScope().equals("test")) {
            return false;
        }
        if (visited.contains(pomLink)) {
            return false;
        }

        return true;
    }

    private Set<Link> generateJarLinks(Set<Dependency> dependencies) {

        Set<Link> links = new HashSet<>();

        for (Dependency dependency : dependencies) {
            Link jarLink = this.linkGenerator.generateLink(dependency, LinkExtension.JAR);
            links.add(jarLink);
        }

        return links;
    }

}