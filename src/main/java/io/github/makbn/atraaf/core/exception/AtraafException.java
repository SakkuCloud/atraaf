package io.github.makbn.atraaf.core.exception;

import org.springframework.http.ResponseEntity;

public abstract class AtraafException extends RuntimeException {
    public int code = 500;

    public AtraafException(String message, int code) {
        super(message);
        this.code = code;
    }

    public AtraafException(int code) {
        this.code = code;
    }

    public AtraafException() {
        this.code = -1;
    }



    public abstract ResponseEntity getResponse();
}
