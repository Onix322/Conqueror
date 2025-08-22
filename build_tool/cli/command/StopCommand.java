package build_tool.cli.command;

public class StopCommand implements Command<Boolean> {

    private StopCommand() {
    }

    public static class Holder {
        public static StopCommand INSTANCE = null;
    }

    public synchronized static void init() {
        if (StopCommand.Holder.INSTANCE == null) {
            StopCommand.Holder.INSTANCE = new StopCommand();
        }
    }

    public static StopCommand getInstance() {
        return StopCommand.Holder.INSTANCE;
    }

    @Override
    public CommandResult<Boolean> exec(Object... args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Invalid number of arguments! Max && Min = 1");
        }

        CommandResult.Builder<Boolean> result = CommandResult.<Boolean>builder()
                .setResult(false)
                .setCommandType("stop");

        if (args[0] == null) {
            System.out.println("No running app found...");
            return result.build();
        }

        Process handle = (Process) args[0];

        System.out.println("App is stopping...");
        handle.destroy();
        handle.descendants()
                .forEach(ProcessHandle::destroy);

        return result.setResult(true)
                .build();
    }
}
