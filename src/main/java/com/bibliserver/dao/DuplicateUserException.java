package com.bibliserver.dao;

public class DuplicateUserException extends Exception {
    public DuplicateUserException(String message, Throwable cause) {
        super(message, cause);
    }
    public DuplicateUserException(String message) {
        super(message);
    }
} 