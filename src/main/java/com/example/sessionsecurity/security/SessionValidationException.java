package com.example.sessionsecurity.security;

public class SessionValidationException extends RuntimeException {

    public SessionValidationException(String message) {
        super(message);
    }
}
