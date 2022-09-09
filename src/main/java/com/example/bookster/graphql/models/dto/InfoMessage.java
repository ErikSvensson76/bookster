package com.example.bookster.graphql.models.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class InfoMessage implements Serializable {
    private final String message;
}
