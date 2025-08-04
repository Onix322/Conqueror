package build_tool;

import build_tool.cli.command.Command;
import build_tool.cli.command.CommandRegistry;
import build_tool.cli.command.CommandResult;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class InterfaceCLI {

    private final CommandRegistry commandRegistry;

    private InterfaceCLI(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    private static class Holder {
        private static InterfaceCLI INSTANCE = null;
    }

    public static synchronized void init(CommandRegistry commandRegistry) {
        if (InterfaceCLI.Holder.INSTANCE == null) {
            InterfaceCLI.Holder.INSTANCE = new InterfaceCLI(commandRegistry);
        }
    }

    public static InterfaceCLI getInstance() {
        if (InterfaceCLI.Holder.INSTANCE == null) {
            throw new IllegalStateException("InterfaceCLI is not initialized. Use InterfaceCLI.init().");
        }
        return InterfaceCLI.Holder.INSTANCE;
    }

    public void start() {

        boolean running = true;
        Scanner scanner = new Scanner(System.in);
        AtomicReference<Process> result = new AtomicReference<>();
        System.out.println("[conqueror] -> Type 'help' to see the available commands.");

        while (running) {
            System.out.print("[conqueror] -> ");
            String line = scanner.nextLine().trim().toLowerCase();
            Command<?> command = this.commandRegistry.requestCommand(line);

            switch (line) {
                case "start", "run" -> {
                    if (result.get() != null) {
                        System.out.println("[conqueror] -> Couldn't start the app, because another process is running.");
                        System.out.println("[conqueror] -> Please stop the process and run / start again.");
                        continue;
                    }
                    CommandResult<?> commandResult = command.exec();
                    commandResult.getResult()
                            .ifPresent(e -> {
                                Process process = (Process) e;
                                result.set(process);
                            });
                }
                case "status" -> command.exec(result.get());
                case "stop" -> {
                    command.exec(result.get());
                    result.set(null);
                }
                case "quit" -> {
                    command.exec(result.get());
                    running = false;
                    result.set(null);
                }
                default -> command.exec();
            }
        }
    }
}
