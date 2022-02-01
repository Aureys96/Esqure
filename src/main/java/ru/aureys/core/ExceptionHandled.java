package ru.aureys.core;

public class ExceptionHandled {

    private final String exceptionDescription;
    private final Exception cause;

    public String getExceptionDescription() {
        return exceptionDescription;
    }

    public Exception getCause() {
        return cause;
    }

    public ExceptionHandled(String exceptionDescription, Exception cause) {
        this.exceptionDescription = exceptionDescription;
        this.cause = cause;
    }
}
