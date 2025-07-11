package loader;

import loader.utilities.*;
import loader.utilities.linkGenerator.LinkGenerator;
import loader.utilities.linkGenerator.link.VersionedLink;
import loader.utilities.pomReader.PomReader;
import loader.utilities.pomReader.handlers.XMLHandlerFactory;
import loader.utilities.version.versionHandler.VersionHandler;
import loader.utilities.version.versionHandler.VersionParser;
import org.xml.sax.SAXException;
import configuration.Configuration;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class Loader {
    public static void load(Configuration configuration, ExecutorService executorService, String[] args) throws ParserConfigurationException, SAXException {

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();

        UrlAccessor.init();
        UrlAccessor urlAccessor = UrlAccessor.getInstance();

        LinkGenerator.init();
        LinkGenerator linkGenerator = LinkGenerator.getInstance();

        VersionHandler.init(urlAccessor, linkGenerator);
        VersionHandler versionHandler = VersionHandler.getInstance();

        VersionParser.init(versionHandler);
        VersionParser versionParser = VersionParser.getInstance();

        XMLHandlerFactory.init(versionParser);
        XMLHandlerFactory xmlHandlerFactory = XMLHandlerFactory.getInstance();

        PomReader.init(saxParser, xmlHandlerFactory, versionParser);
        PomReader pomReaderNew = PomReader.getInstance();

        ArtifactValidator.init(configuration);
        ArtifactValidator artifactValidator = ArtifactValidator.getInstance();

        JarResolver.init(
                pomReaderNew,
                linkGenerator,
                artifactValidator,
                configuration
        );
        JarResolver jarResolver = JarResolver.getInstance();

        Downloader.init(
                configuration,
                urlAccessor
        );
        Downloader downloader = Downloader.getInstance();

        ClassPathLoader.init(
                configuration,
                executorService
        );
        ClassPathLoader classpathLoader = ClassPathLoader.getInstance();

        Set<VersionedLink> jarVersionedLinks = jarResolver.resolve();
        downloader.download(jarVersionedLinks);
        classpathLoader.start();
    }
}
