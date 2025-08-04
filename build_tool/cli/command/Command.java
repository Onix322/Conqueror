package build_tool.cli.command;

public interface Command<R> {

    CommandResult<R> exec(Object... args);

}
