package com.example.blog.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RemoteServiceException extends RuntimeException {

    private final HttpStatus status;

    public RemoteServiceException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
