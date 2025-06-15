package org.server.httpServer.utils;

import org.server.annotations.component.Component;
import org.server.exceptions.NoSuchEntity;
import org.server.httpServer.utils.response.HttpStatus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.NoSuchFileException;
import java.rmi.NoSuchObjectException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class ExceptionMapper {

    private ExceptionMapper(){}

    private final Map<Class<? extends Throwable>, HttpStatus> EXCEPTION_CODE_MAP = Map.ofEntries(
            // Client error (4xx)
            Map.entry(NullPointerException.class, HttpStatus.BAD_REQUEST),
            Map.entry(IllegalArgumentException.class, HttpStatus.BAD_REQUEST),
            Map.entry(IndexOutOfBoundsException.class, HttpStatus.BAD_REQUEST),
            Map.entry(ArrayIndexOutOfBoundsException.class, HttpStatus.BAD_REQUEST),
            Map.entry(StringIndexOutOfBoundsException.class, HttpStatus.BAD_REQUEST),
            Map.entry(ClassCastException.class, HttpStatus.BAD_REQUEST),
            Map.entry(NoSuchEntity.class, HttpStatus.BAD_REQUEST),
            Map.entry(ArithmeticException.class, HttpStatus.BAD_REQUEST),
            Map.entry(NumberFormatException.class, HttpStatus.UNPROCESSABLE_ENTITY),
            Map.entry(UnsupportedOperationException.class, HttpStatus.METHOD_NOT_ALLOWED),
            Map.entry(ParseException.class, HttpStatus.UNPROCESSABLE_ENTITY),
            Map.entry(NoSuchMethodException.class, HttpStatus.BAD_REQUEST),
            Map.entry(NoSuchFieldException.class, HttpStatus.BAD_REQUEST),
            Map.entry(NoSuchObjectException.class, HttpStatus.BAD_REQUEST),

            // Server error (5xx)
            Map.entry(IOException.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(FileNotFoundException.class, HttpStatus.NOT_FOUND),
            Map.entry(SQLException.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(ClassNotFoundException.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(InstantiationException.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(IllegalAccessException.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(InvocationTargetException.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(NoSuchFileException.class, HttpStatus.NOT_FOUND),
            Map.entry(NoSuchElementException.class, HttpStatus.NOT_FOUND),
            Map.entry(InterruptedException.class, HttpStatus.SERVICE_UNAVAILABLE),
            Map.entry(OutOfMemoryError.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(StackOverflowError.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(NoClassDefFoundError.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(AssertionError.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(LinkageError.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(VirtualMachineError.class, HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry(Exception.class, HttpStatus.INTERNAL_SERVER_ERROR)
    );

    public HttpStatus mapException(Throwable t) {
        t.getCause().printStackTrace();
        return this.EXCEPTION_CODE_MAP.getOrDefault(t.getCause().getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
