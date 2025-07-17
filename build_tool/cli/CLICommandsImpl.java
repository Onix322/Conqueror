package build_tool.cli;

import build_tool.utilities.Loader;
import configuration.Configuration;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

public class CLICommandsImpl {

    private final Configuration configuration;
    private final Loader loader;

    private CLICommandsImpl(Configuration configuration, Loader loader) {
        this.configuration = configuration;
        this.loader = loader;
    }

    private static class Holder {
        private static CLICommandsImpl INSTANCE = null;
    }

    public static synchronized void init(Configuration configuration, Loader loader) {
        if (CLICommandsImpl.Holder.INSTANCE == null) {
            CLICommandsImpl.Holder.INSTANCE = new CLICommandsImpl(configuration, loader);
        }
    }

    public static CLICommandsImpl getInstance() {
        if (CLICommandsImpl.Holder.INSTANCE == null) {
            throw new IllegalStateException("CLICommandsImpl is not initialized. Use CLICommandsImpl.init().");
        }
        return CLICommandsImpl.Holder.INSTANCE;
    }

    public boolean init(){
        try {
            this.loader.load();
            return true;
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean build(){
        return false;
    }

    public boolean start(){
        return false;
    }

    public boolean run(){
        return false;
    }

    public boolean stop(){
        return false;
    }

    public boolean status(){
        return false;
    }

    public boolean help(){
        return false;
    }

    public boolean quit(){
        return false;
    }
}
