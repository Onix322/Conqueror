package build_tool.cli.command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CommandRegistry {

    private final Map<String, Supplier<Command<?>>> commands;
    private final NoCommand noCommand;

    private CommandRegistry(NoCommand noCommand) {
        this.commands = new HashMap<>();
        this.noCommand = noCommand;
    }

    public static class Holder {
        public static CommandRegistry INSTANCE = null;
    }

    public synchronized static void init(NoCommand noCommand) {
        if (CommandRegistry.Holder.INSTANCE == null) {
            CommandRegistry.Holder.INSTANCE = new CommandRegistry(noCommand);
        }
    }

    public static CommandRegistry getInstance() {
        return CommandRegistry.Holder.INSTANCE;
    }

    public Command<?> requestCommand(String command) {
        return commands.getOrDefault(command, () -> noCommand)
                .get();
    }

    public void registerCommand(String name, Supplier<Command<?>> command) {
        commands.putIfAbsent(name, command);
    }
}
