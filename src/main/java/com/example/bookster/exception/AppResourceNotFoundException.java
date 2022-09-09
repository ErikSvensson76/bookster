package com.example.bookster.exception;

import java.util.Map;

public class AppResourceNotFoundException extends GenericGraphQLException{
    public AppResourceNotFoundException(String message) {
        super(message);
    }

    public AppResourceNotFoundException(String message, Map<String, Object> parameters) {
        super(message, parameters);
    }
}
