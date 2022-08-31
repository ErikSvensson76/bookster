package com.example.bookster.graphql.models.input;

import java.io.Serializable;

public record AppRoleInput(
        String id,
        String userRole,
        String password
) implements Serializable {
}
