package loader.utilities.pomReader;

import loader.utilities.pomReader.handlers.XMLHandler;
import loader.utilities.pomReader.supportedTagsClasses.artifact.xml.XMLParsed;
import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PomReader {

    private final SAXParser saxParser;
    private final XMLHandler XMLHandler;

    private PomReader(SAXParser saxParser, XMLHandler XMLHandler) {
        this.saxParser = saxParser;
        this.XMLHandler = XMLHandler;
    }

    private static class Holder {
        private static PomReader INSTANCE = null;
    }

    public static synchronized void init(SAXParser saxParser, XMLHandler XMLHandler) {
        if (Holder.INSTANCE == null) {
            Holder.INSTANCE = new PomReader(saxParser, XMLHandler);
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
        try {
            switch (o) {
                case String uri -> this.saxParser.parse(uri, XMLHandler);
                case InputStream stream -> this.saxParser.parse(stream, XMLHandler);
                case File file -> this.saxParser.parse(file, XMLHandler);
                case null, default -> throw new IllegalArgumentException("Class type not supported");
            }
            return XMLHandler.getXmlParsed();
        } catch (IllegalArgumentException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
