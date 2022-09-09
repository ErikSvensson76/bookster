package com.example.bookster.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericGraphQLException extends RuntimeException implements GraphQLError {

    private Map<String, Object> parameters = new HashMap<>();

    public GenericGraphQLException(String message) {
        super(message);
    }

    public GenericGraphQLException(String message, Map<String, Object> parameters) {
        super(message);
        if(parameters != null){
            this.parameters = parameters;
        }
    }
    @Override
    public String getMessage(){
        return super.getMessage();
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return null;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return this.parameters;
    }
}
