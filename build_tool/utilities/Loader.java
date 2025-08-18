package build_tool.utilities;

import configuration.Configuration;
import build_tool.utilities.linkGenerator.LinkGenerator;
import build_tool.utilities.linkGenerator.link.VersionedLink;
import build_tool.utilities.depsReader.DepsReader;
import build_tool.utilities.depsReader.handlers.XMLHandlerFactory;
import build_tool.utilities.version.versionHandler.VersionHandler;
import build_tool.utilities.version.versionHandler.VersionParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.Set;

/**
 * Loader is responsible for initializing and loading various components required for the application.
 * It sets up the necessary configurations, parsers, and handlers to process Maven POM files and manage dependencies.
 */
public class Loader {

    private final Configuration configuration;

    private Loader(Configuration configuration) {
        this.configuration = configuration;
    }

    private static class Holder {
        private static Loader INSTANCE = null;
    }

    public static synchronized void init(Configuration configuration) {
        if (Loader.Holder.INSTANCE == null) {
            Loader.Holder.INSTANCE = new Loader(configuration);
        }
    }

    public static Loader getInstance() {
        if (Loader.Holder.INSTANCE == null) {
            throw new IllegalStateException("Loader is not initialized. Use Loader.init().");
        }
        return Loader.Holder.INSTANCE;
    }
    /**
     * Initializes the Loader with the provided configuration and executor service.
     * This method sets up the necessary components such as URL accessors, link generators,
     * version handlers, POM readers, artifact validators, and classpath loaders.
     *
     * @throws ParserConfigurationException if there is a configuration error in the parser
     * @throws SAXException if there is an error in parsing XML
     */
    public void load() throws ParserConfigurationException, SAXException {

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

        DepsReader.init(saxParser, xmlHandlerFactory, versionParser);
        DepsReader depsReaderNew = DepsReader.getInstance();

        ArtifactValidator.init(configuration);
        ArtifactValidator artifactValidator = ArtifactValidator.getInstance();

        JarResolver.init(
                depsReaderNew,
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
    }
}
