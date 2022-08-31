package com.example.bookster.graphql.models.input;

import java.io.Serializable;

public record AddressInput(
        String id,
        String city,
        String street,
        String zipCode
) implements Serializable {}
