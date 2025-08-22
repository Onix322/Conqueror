package build_tool.cli.command;

public class HelpCommand implements Command<Boolean> {

    private HelpCommand() {}

    public static class Holder {
        public static HelpCommand INSTANCE = null;
    }

    public synchronized static void init() {
        if (HelpCommand.Holder.INSTANCE == null) {
            HelpCommand.Holder.INSTANCE = new HelpCommand();
        }
    }

    public static HelpCommand getInstance() {
        return HelpCommand.Holder.INSTANCE;
    }

    @Override
    public CommandResult<Boolean> exec(Object... args) {
        System.out.println("""
                Available commands:
                - init           -> Initialize dependencies
                - build          -> Build framework and user app
                - start          -> Start the user app
                - stop           -> Stop the app
                - run            -> Init, Build and Start
                - status         -> Check if app status
                - jar            -> Creates a jar file in a specific directory
                - quit           -> exit build tool
                """);

        return CommandResult.<Boolean>builder()
                .setResult(true)
                .setCommandType("help")
                .build();
    }
}
