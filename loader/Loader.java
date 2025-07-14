package loader;

import configuration.Configuration;
import loader.utilities.*;
import loader.utilities.linkGenerator.LinkGenerator;
import loader.utilities.linkGenerator.link.VersionedLink;
import loader.utilities.pomReader.PomReader;
import loader.utilities.pomReader.handlers.XMLHandlerFactory;
import loader.utilities.version.versionHandler.VersionHandler;
import loader.utilities.version.versionHandler.VersionParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Loader is responsible for initializing and loading various components required for the application.
 * It sets up the necessary configurations, parsers, and handlers to process Maven POM files and manage dependencies.
 */
public class Loader {
    /**
     * Initializes the Loader with the provided configuration and executor service.
     * This method sets up the necessary components such as URL accessors, link generators,
     * version handlers, POM readers, artifact validators, and classpath loaders.
     *
     * @param configuration the configuration to be used for loading
     * @throws ParserConfigurationException if there is a configuration error in the parser
     * @throws SAXException if there is an error in parsing XML
     */
    public static void load(Configuration configuration) throws ParserConfigurationException, SAXException {

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
        Set<VersionedLink> jarVersionedLinks = jarResolver.resolve();

        Downloader.init(
                configuration,
                urlAccessor
        );
        Downloader downloader = Downloader.getInstance();
        downloader.download(jarVersionedLinks);

        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        ExecutorService executorService = Executors.newThreadPerTaskExecutor(threadFactory);

        ClassPathLoader.init(
                configuration,
                executorService
        );
        ClassPathLoader classpathLoader = ClassPathLoader.getInstance();
        classpathLoader.start();
    }
}
