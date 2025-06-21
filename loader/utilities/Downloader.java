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
    private final ConnectionManager connectionManager;

    private Downloader(Configuration configuration, ConnectionManager connectionManager) {
        this.dependenciesLocation = configuration.readProperty("dependencies.location");
        this.connectionManager = connectionManager;
    }

    private static class Holder {
        private static Downloader INSTANCE = null;
    }

    public static synchronized void init(Configuration configuration, ConnectionManager connectionManager) {
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
        File dir = new File(dependenciesLocation);
        for(Link link : links){
            try (InputStream stream = connectionManager.open(link.getUri().toURL())){
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
