package build_tool.cli.command;

public class NoCommand implements Command<Boolean>{
    private NoCommand() {}

    public static class Holder {
        public static NoCommand INSTANCE = null;
    }

    public static void init() {
        if (NoCommand.Holder.INSTANCE == null) {
            NoCommand.Holder.INSTANCE = new NoCommand();
        }
    }

    public static NoCommand getInstance() {
        return NoCommand.Holder.INSTANCE;
    }

    @Override
    public CommandResult<Boolean> exec(Object... args) {
        System.out.println("Inexistent command. Type 'help' for command overview.");
        return CommandResult.<Boolean>builder()
                .setResult(null)
                .setCommandType("none")
                .build();
    }
}
