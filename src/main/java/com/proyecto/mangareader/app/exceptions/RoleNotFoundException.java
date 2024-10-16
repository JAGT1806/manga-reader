package com.proyecto.mangareader.app.exceptions;

public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException(String message){
        super(message);
    }
}
