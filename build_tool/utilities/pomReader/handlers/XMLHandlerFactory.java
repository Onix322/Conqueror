package build_tool.utilities.pomReader.handlers;

import build_tool.utilities.version.versionHandler.VersionParser;

/**
 * Factory class for creating instances of {@link XMLHandler}.
 * Ensures a single instance of the factory is initialized and provides a thread-safe way to create {@link XMLHandler} objects.
 */
public class XMLHandlerFactory {

    private final VersionParser versionParser;

    private XMLHandlerFactory(VersionParser versionParser){
        this.versionParser = versionParser;
    }

    private static class Holder {
        private static XMLHandlerFactory INSTANCE = null;
    }

    public static synchronized void init(VersionParser versionParser) {
        if (XMLHandlerFactory.Holder.INSTANCE == null) {
            XMLHandlerFactory.Holder.INSTANCE = new XMLHandlerFactory(versionParser);
        }
    }

    public static XMLHandlerFactory getInstance() {
        if (XMLHandlerFactory.Holder.INSTANCE == null) {
            throw new IllegalStateException("XMLHandlerFactory is not initialized. Use XMLHandlerFactory.init().");
        }
        return XMLHandlerFactory.Holder.INSTANCE;
    }

    /**
     * Creates a new instance of {@link XMLHandler}.
     * This method uses the {@link VersionParser} instance provided during factory initialization
     * @return a new {@link XMLHandler} instance
     */
    public XMLHandler create() {
        return new XMLHandler(versionParser);
    }
}
