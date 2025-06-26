package loader.utilities;

import loader.objects.link.Link;
import src.com.server.configuration.Configuration;

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

    public void download(Set<Link> links) {
        System.out.println("[" + this.getClass().getSimpleName() + "] -> Downloading jars...");
        File dir = new File(dependenciesLocation);
        for(Link link : links){
            try (InputStream stream = urlAccessor.open(link.getUri().toURL())){
                System.out.println("[" + this.getClass().getSimpleName() + "] -> Downloading "
                        + link.getDependency().getGroupId()
                        + "::" + link.getDependency().getArtifactId()
                        + "::" + link.getDependency().getVersion()
                );

                Path fileName = Path.of(dir.getCanonicalPath() + '/'
                        + link.getDependency().getArtifactId() + '-'
                        + link.getDependency().getVersion() + '.'
                        + link.getExtension().getValue()
                );

                Files.write(fileName, stream.readAllBytes(), StandardOpenOption.CREATE);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
