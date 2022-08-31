package com.example.bookster.graphql.models.input;

import java.io.Serializable;

public record RoleAppUserInput(
        String appUserId,
        String appRoleId
) implements Serializable {}
