package com.lastminute.recruitment.domain.error;

public class WikiPageNotFound extends RuntimeException{
    public WikiPageNotFound(String message) {
        super(message);
    }

    public WikiPageNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
