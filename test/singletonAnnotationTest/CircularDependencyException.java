package test.singletonAnnotationTest;

public class CircularDependencyException extends RuntimeException {
  public CircularDependencyException(String message) {
    super(message);
  }
}
