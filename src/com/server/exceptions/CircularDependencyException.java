package src.com.server.exceptions;

public class CircularDependencyException extends RuntimeException {
  public CircularDependencyException(String message) {
    super(message);
  }
}
