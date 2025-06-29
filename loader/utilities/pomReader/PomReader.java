package loader.utilities.pomReader;

import loader.utilities.pomReader.handlers.ProjectHandler;
import loader.utilities.pomReader.supportedTagsClasses.artifact.project.Project;
import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PomReader {

    private final SAXParser saxParser;
    private final ProjectHandler projectHandler;

    private PomReader(SAXParser saxParser, ProjectHandler projectHandler) {
        this.saxParser = saxParser;
        this.projectHandler = projectHandler;
    }

    private static class Holder {
        private static PomReader INSTANCE = null;
    }

    public static synchronized void init(SAXParser saxParser, ProjectHandler projectHandler) {
        if (Holder.INSTANCE == null) {
            Holder.INSTANCE = new PomReader(saxParser, projectHandler);
        }
    }

    public static PomReader getInstance() {
        if (Holder.INSTANCE == null) {
            throw new IllegalStateException("PomReader is not initialized. Use PomReader.init().");
        }
        return Holder.INSTANCE;
    }

    public Project readString(String uri) {
        return this.read(uri);
    }

    public Project readStream(InputStream stream) {
        return this.read(stream);
    }

    public Project readFile(File file) {
        return this.read(file);
    }

    private Project read(Object o) {
        try {
            switch (o) {
                case String uri -> this.saxParser.parse(uri, projectHandler);
                case InputStream stream -> this.saxParser.parse(stream, projectHandler);
                case File file -> this.saxParser.parse(file, projectHandler);
                case null, default -> throw new IllegalArgumentException("Class type not supported");
            }
            return projectHandler.getProjectBuilder();
        } catch (IllegalArgumentException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
