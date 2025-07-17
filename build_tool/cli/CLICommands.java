package build_tool.cli;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class CLICommands {

    private Map<String, Supplier<?>> commands;

    private CLICommands(CLICommandsImpl implementations) {
        this.commands = Map.ofEntries(
                Map.entry("init", implementations::init),
                Map.entry("build", implementations::build),
                Map.entry("start", implementations::start),
                Map.entry("run", implementations::run),
                Map.entry("stop", implementations::stop),
                Map.entry("status", implementations::status),
                Map.entry("help", implementations::help),
                Map.entry("quit", implementations::quit)
        );
    }

    private static class Holder {
        private static CLICommands INSTANCE = null;
    }

    public static synchronized void init(CLICommandsImpl implementations) {
        if (CLICommands.Holder.INSTANCE == null) {
            CLICommands.Holder.INSTANCE = new CLICommands(implementations);
        }
    }

    public static CLICommands getInstance() {
        if (CLICommands.Holder.INSTANCE == null) {
            throw new IllegalStateException("CLICommands is not initialized. Use CLICommands.init().");
        }
        return CLICommands.Holder.INSTANCE;
    }

    private Map<String, Supplier<?>> getCommands() {
        return Map.copyOf(commands);
    }

    private <T> void add(String name, Supplier<T> supplier) {
        commands.putIfAbsent(name, supplier);
    }

    private void remove(String name) {
        commands.remove(name);
    }

    private <T> T exec(String name) {
        return (T) commands.get(name).get();
    }

    private <T> void set(String name, Supplier<T> newSupplier) {
        if (commands.containsKey(name)) {
            throw new NoSuchElementException("No command with name: " + name);
        }
        commands.put(name, newSupplier);
    }
}
