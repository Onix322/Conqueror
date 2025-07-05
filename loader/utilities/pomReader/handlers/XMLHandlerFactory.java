package loader.utilities.pomReader.handlers;

import loader.utilities.version.versionHandler.VersionParser;

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

    public XMLHandler create() {
        return new XMLHandler(versionParser);
    }
}
