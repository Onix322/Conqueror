package loader;

import src.com.server.configuration.Configuration;
import loader.objects.link.Link;
import loader.utilities.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Set;

public class Loader {
    public static void load(Configuration configuration, String[] args) throws ParserConfigurationException {

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        PomReader.init(documentBuilder);
        PomReader pomReader = PomReader.getInstance();

        UrlAccessor.init();
        UrlAccessor urlAccessor = UrlAccessor.getInstance();

        XmlNavigator.init(pomReader);
        XmlNavigator xmlNavigator = XmlNavigator.getInstance();

        VersionParser.init();
        VersionParser versionParser = VersionParser.getInstance();

        VersionHandler.init(pomReader, urlAccessor, xmlNavigator, versionParser);
        VersionHandler versionHandler = VersionHandler.getInstance();

        LinkGenerator.init(
                versionHandler
        );
        LinkGenerator linkGenerator = LinkGenerator.getInstance();
        Factory factory = Factory.getInstance();

        ArtifactValidator.init(configuration);
        ArtifactValidator artifactValidator = ArtifactValidator.getInstance();

        JarResolver.init(
                pomReader,
                linkGenerator,
                factory,
                artifactValidator,
                configuration
        );
        JarResolver jarResolver = JarResolver.getInstance();

        Downloader.init(
                configuration,
                urlAccessor
        );
        Downloader downloader = Downloader.getInstance();

        ProcessBuilder processBuilder = new ProcessBuilder();
        ClassPathLoader.init(
                configuration,
                processBuilder
        );
        ClassPathLoader classpathLoader = ClassPathLoader.getInstance();

        Set<Link> jarLinks = jarResolver.resolve();
        downloader.download(jarLinks);
        classpathLoader.start(args);
    }
}
