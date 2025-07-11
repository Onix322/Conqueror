package loader.utilities;

import loader.utilities.linkGenerator.link.VersionedLink;
import configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;

public class Downloader {

    private final String dependenciesLocation;
    private final UrlAccessor urlAccessor;

    private Downloader(Configuration configuration, UrlAccessor urlAccessor) {
        this.dependenciesLocation = configuration.readProperty("dependencies.location");
        this.urlAccessor = urlAccessor;
    }

    private static class Holder {
        private static Downloader INSTANCE = null;
    }

    public static synchronized void init(Configuration configuration, UrlAccessor connectionManager) {
        if (Downloader.Holder.INSTANCE == null) {
            Downloader.Holder.INSTANCE = new Downloader(configuration, connectionManager);
        }
    }

    public static Downloader getInstance() {
        if (Downloader.Holder.INSTANCE == null) {
            throw new IllegalStateException("Downloader is not initialized. Use Downloader.init().");
        }
        return Downloader.Holder.INSTANCE;
    }

    public void download(Set<VersionedLink> versionedLinks) {
        if(versionedLinks.isEmpty()){
            System.out.println("[" + this.getClass().getSimpleName() + "] -> No jars for downloading...");
            return;
        }

        System.out.println("[" + this.getClass().getSimpleName() + "] -> Downloading jars...");
        File dir = new File(dependenciesLocation);
        for(VersionedLink versionedLink : versionedLinks){
            try (InputStream stream = urlAccessor.open(versionedLink.getUri().toURL())){
                if(stream == null) return;
                System.out.println("[" + this.getClass().getSimpleName() + "] -> Downloading "
                        + versionedLink.getArtifact().getGroupId()
                        + "::" + versionedLink.getArtifact().getArtifactId()
                        + "::" + versionedLink.getArtifact().getVersion().asString()
                );

                Path fileName = Path.of(dir.getCanonicalPath() + '/'
                        + versionedLink.getArtifact().getArtifactId() + '-'
                        + versionedLink.getArtifact().getVersion().asString() + '.'
                        + versionedLink.getLinkExtension().getValue()
                );

                Files.write(fileName, stream.readAllBytes(), StandardOpenOption.CREATE);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("[" + this.getClass().getSimpleName() + "] -> Download finished!");
    }
}
