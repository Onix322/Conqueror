package build_tool.cli.command;

import java.util.Optional;

public class CommandResult<R> {

    private String commandType;
    private R result;

    private CommandResult(Builder<R> builder) {
        this.commandType = builder.commandType;
        this.result = builder.result;
    }

    public Optional<R> getResult() {
        return Optional.ofNullable(result);
    }

    public static Optional<Object> emptyResult(){
        return Optional.empty();
    }

    public void setResult(R result) {
        this.result = result;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public static <R> Builder<R> builder(){
        return new Builder<R>();
    }

    public static class Builder<R>{
        private String commandType;
        private R result;

        private Builder(){}

        public Optional<R> getResult() {
            return Optional.ofNullable(result);
        }

        public Builder<R> setResult(R result) {
            this.result = result;
            return this;
        }

        public String getCommandType() {
            return commandType;
        }

        public Builder<R> setCommandType(String commandType) {
            this.commandType = commandType;
            return this;
        }

        public  CommandResult<R> build(){
            return new CommandResult<>(this);
        }
    }
}
