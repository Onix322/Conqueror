package org.server.exepltions;

public class CircularDependencyException extends RuntimeException {
  public CircularDependencyException(String message) {
    super(message);
  }
}
