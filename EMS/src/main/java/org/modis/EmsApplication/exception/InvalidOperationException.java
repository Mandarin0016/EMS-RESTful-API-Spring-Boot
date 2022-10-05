package org.modis.EmsApplication.exception;

public class InvalidOperationException extends Throwable {
    public InvalidOperationException() {
    }

    public InvalidOperationException(String message) {
        super(message);
    }

    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOperationException(Throwable cause) {
        super(cause);
    }
}
