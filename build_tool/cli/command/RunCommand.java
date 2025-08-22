package build_tool.cli.command;

public class RunCommand implements Command<Process> {
    private final InitCommand initCommand;
    private final StartCommand startCommand;
    private final BuildCommand buildCommand;

    public RunCommand(InitCommand initCommand, StartCommand startCommand, BuildCommand buildCommand) {
        this.initCommand = initCommand;
        this.startCommand = startCommand;
        this.buildCommand = buildCommand;
    }

    public static class Holder {
        public static RunCommand INSTANCE = null;
    }

    public synchronized static void init(InitCommand initCommand, StartCommand startCommand, BuildCommand buildCommand) {
        if (RunCommand.Holder.INSTANCE == null) {
            RunCommand.Holder.INSTANCE = new RunCommand(initCommand, startCommand, buildCommand);
        }
    }

    public static RunCommand getInstance() {
        return RunCommand.Holder.INSTANCE;
    }

    @Override
    public CommandResult<Process> exec(Object... args) {
        initCommand.exec(args);
        buildCommand.exec(args);
        return startCommand.exec(args);
    }

}
