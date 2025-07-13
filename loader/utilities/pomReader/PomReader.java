package loader.utilities.pomReader;

import loader.utilities.pomReader.handlers.XMLHandler;
import loader.utilities.pomReader.handlers.XMLHandlerFactory;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.XMLParsed;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.project.Project;
import loader.utilities.version.versionHandler.VersionParser;

import javax.xml.parsers.SAXParser;
import java.io.File;
import java.io.InputStream;

/**
 * PomReader is a singleton class responsible for reading and parsing Maven POM files.
 * It uses a SAXParser to read XML data and an XMLHandler to process the parsed data.
 * The class also integrates with VersionParser to handle versioning of artifacts.
 */
public class PomReader {

    private final SAXParser saxParser;
    private final XMLHandlerFactory xmlHandlerFactory;
    private final VersionParser versionParser;

    private PomReader(SAXParser saxParser, XMLHandlerFactory xmlHandlerFactory, VersionParser versionParser) {
        this.saxParser = saxParser;
        this.xmlHandlerFactory = xmlHandlerFactory;
        this.versionParser = versionParser;
    }

    private static class Holder {
        private static PomReader INSTANCE = null;
    }

    public static synchronized void init(SAXParser saxParser, XMLHandlerFactory xmlHandlerFactory, VersionParser versionParser) {
        if (Holder.INSTANCE == null) {
            Holder.INSTANCE = new PomReader(saxParser, xmlHandlerFactory, versionParser);
        }
    }

    public static PomReader getInstance() {
        if (Holder.INSTANCE == null) {
            throw new IllegalStateException("PomReader is not initialized. Use PomReader.init().");
        }
        return Holder.INSTANCE;
    }

    /**
     * Reads a POM file from a given URI, InputStream, or File.
     * The method uses the SAXParser to parse the XML and the XMLHandler to handle the parsed data.
     *
     * @param uri the URI of the POM file as a String
     * @return an XMLParsed object containing the parsed data
     */
    public XMLParsed readString(String uri) {
        return this.read(uri);
    }

    /**
     * Reads a POM file from an InputStream.
     * The method uses the SAXParser to parse the XML and the XMLHandler to handle the parsed data.
     *
     * @param stream the InputStream of the POM file
     * @return an XMLParsed object containing the parsed data
     */
    public XMLParsed readStream(InputStream stream) {
        return this.read(stream);
    }

    /**
     * Reads a POM file from a File object.
     * The method uses the SAXParser to parse the XML and the XMLHandler to handle the parsed data.
     *
     * @param file the File object representing the POM file
     * @return an XMLParsed object containing the parsed data
     */
    public XMLParsed readFile(File file) {
        return this.read(file);
    }

    /**
     * Reads a POM file from an Object that can be a String, InputStream, or File.
     * The method uses the SAXParser to parse the XML and the XMLHandler to handle the parsed data.
     *
     * @param o an Object that can be a String URI, InputStream, or File
     * @return an XMLParsed object containing the parsed data
     */
    private XMLParsed read(Object o) {
        XMLHandler xmlHandler = xmlHandlerFactory.create();
        XMLParsed rawParsed;
        try {
            switch (o) {
                case String uri -> this.saxParser.parse(uri, xmlHandler);
                case InputStream stream -> this.saxParser.parse(stream, xmlHandler);
                case File file -> this.saxParser.parse(file, xmlHandler);
                case null, default -> throw new IllegalArgumentException("Class type not supported");
            }
            rawParsed = xmlHandler.getXmlParsed();
            if (rawParsed instanceof Project project) {
                return versionParser.handleVersions(project.getAs(), this);
            }
            return rawParsed;
        } catch (Exception e) {
//            e.printStackTrace();
            System.err.println("[" + this.getClass().getSimpleName() + "] -> Dependency not found in repository maybe is a ghost...\n");
            return null;
        }
    }
}
