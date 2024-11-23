package com.proyecto.mangareader.app.exceptions;

public class SelfRoleModificationException extends RuntimeException {
    public SelfRoleModificationException(String message) {
        super(message);
    }
}
