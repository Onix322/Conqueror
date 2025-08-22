package build_tool.cli.command;

import build_tool.utilities.Loader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

public class InitCommand implements Command<Boolean> {

    private final Loader loader;

    public InitCommand(Loader loader) {
        this.loader = loader;
    }

    public static class Holder {
        public static InitCommand INSTANCE = null;
    }

    public synchronized static void init(Loader loader) {
        if(Holder.INSTANCE == null) {
            Holder.INSTANCE = new InitCommand(loader);
        }
    }

    public static InitCommand getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public CommandResult<Boolean> exec(Object...args) {
        try {
            this.loader.load();
            return CommandResult.<Boolean>builder()
                    .setResult(true)
                    .setCommandType("init")
                    .build();
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
