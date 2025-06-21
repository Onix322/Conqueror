package src.com.server.exceptions;

public class NoEmptyConstructorFound extends RuntimeException {
    public NoEmptyConstructorFound() {
        super("Create an empty constructor for the entity!");
    }
}
