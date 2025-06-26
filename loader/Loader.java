package loader;

import org.xml.sax.SAXNotRecognizedException;
import src.com.server.configuration.Configuration;
import loader.objects.link.Link;
import loader.utilities.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Set;

public class Loader {
    public static void load(Configuration configuration, String[] args) throws ParserConfigurationException {

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        PomReader.init(documentBuilder);
        PomReader pomReader = PomReader.getInstance();

        ConnectionManager connectionManager = ConnectionManager.getInstance();

        XmlNavigator.init(pomReader);
        XmlNavigator xmlNavigator = XmlNavigator.getInstance();

        VersionParser.init();
        VersionParser versionParser = VersionParser.getInstance();

        VersionHandler.init(pomReader, connectionManager, xmlNavigator, versionParser);
        VersionHandler versionHandler = VersionHandler.getInstance();

        LinkGenerator.init(
                pomReader,
                connectionManager,
                versionHandler
        );
        LinkGenerator linkGenerator = LinkGenerator.getInstance();
        Factory factory = Factory.getInstance();

        JarResolver.init(
                pomReader,
                linkGenerator,
                factory,
                configuration
        );
        JarResolver jarResolver = JarResolver.getInstance();

        Downloader.init(
                configuration,
                connectionManager
        );
        Downloader downloader = Downloader.getInstance();

        ProcessBuilder processBuilder = new ProcessBuilder();
        ClasspathLoader.init(
                configuration,
                processBuilder
        );
        ClasspathLoader classpathLoader = ClasspathLoader.getInstance();

        Set<Link> jarLinks = jarResolver.resolve();
        downloader.download(jarLinks);
        classpathLoader.start(args);
    }
}
