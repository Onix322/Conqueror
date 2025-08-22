package build_tool.cli.command;

public class StatusCommand implements Command<Boolean> {
    private StatusCommand() {
    }

    public static class Holder {
        public static StatusCommand INSTANCE = null;
    }

    public synchronized static void init() {
        if (StatusCommand.Holder.INSTANCE == null) {
            StatusCommand.Holder.INSTANCE = new StatusCommand();
        }
    }

    public static StatusCommand getInstance() {
        return StatusCommand.Holder.INSTANCE;
    }

    @Override
    public CommandResult<Boolean> exec(Object... args) {
        if(args.length != 1) {
            throw new IllegalArgumentException("Invalid number of arguments! Max && Min = 1");
        }

        CommandResult.Builder<Boolean> result = CommandResult.<Boolean>builder()
                .setResult(true)
                .setCommandType("status");

        if(args[0] == null) {
            System.out.println("Status: Shutdown");
            return result.build();
        }

        Process process = (Process) args[0];

        System.out.println("Status: Running");
        System.out.println("Parent:" + process + " -> " + process.isAlive());
        process.descendants()
                .forEach(c -> System.out.println("Child:" + c + " -> " + c.isAlive()));

        return result.setResult(true)
                .build();
    }
}
