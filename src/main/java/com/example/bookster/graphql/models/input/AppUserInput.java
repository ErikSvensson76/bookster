package com.example.bookster.graphql.models.input;

import java.io.Serializable;

public record AppUserInput(
        String id,
        String username,
        String password
) implements Serializable {}
