package org.aviran.woodpecker;

/**
 * Created by Aviran Abady on 5/26/17.
 */

public class WoodpeckerException extends RuntimeException{
    private Exception exception;
    private String data;

    public WoodpeckerException(String data, Exception exception) {
        super();
        this.exception = exception;
        this.data = data;
    }

    public WoodpeckerException(String data) {
        this.data = data;
    }

    public Exception getException() {
        return exception;
    }

    public String getData() {
        return data;
    }
}
