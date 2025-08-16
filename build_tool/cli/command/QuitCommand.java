package build_tool.cli.command;

public class QuitCommand implements Command<Boolean>{

    private final StopCommand stopCommand;

    public QuitCommand(StopCommand stopCommand) {
        this.stopCommand = stopCommand;
    }

    public static class Holder {
        public static QuitCommand INSTANCE = null;
    }

    public static void init(StopCommand stopCommand) {
        if(QuitCommand.Holder.INSTANCE == null) {
            QuitCommand.Holder.INSTANCE = new QuitCommand(stopCommand);
        }
    }

    public static QuitCommand getInstance() {
        return QuitCommand.Holder.INSTANCE;
    }


    @Override
    public CommandResult<Boolean> exec(Object... args) {
        if(args.length != 1) {
            throw new IllegalArgumentException("Invalid number of arguments! Max && Min = 1");
        }

        stopCommand.exec(args[0]);
        System.out.println("Have a nice day!");

        return CommandResult.<Boolean>builder()
                .setResult(true)
                .setCommandType("quit")
                .build();
    }
}
