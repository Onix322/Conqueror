package loader.utilities.pomReader;

import loader.utilities.pomReader.handlers.XMLHandler;
import loader.utilities.pomReader.handlers.XMLHandlerFactory;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.XMLParsed;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.project.Project;
import loader.utilities.version.versionHandler.VersionParser;

import javax.xml.parsers.SAXParser;
import java.io.File;
import java.io.InputStream;

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

    public XMLParsed readString(String uri) {
        return this.read(uri);
    }

    public XMLParsed readStream(InputStream stream) {
        return this.read(stream);
    }

    public XMLParsed readFile(File file) {
        return this.read(file);
    }

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
