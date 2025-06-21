package src.com.server.exceptions;

public class NoEntityMatchesJson extends RuntimeException {
    public NoEntityMatchesJson() {
        super("No entity with this fields offered by current request json properties");
    }
}
