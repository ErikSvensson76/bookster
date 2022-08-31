package com.example.bookster.graphql.models.input;

import java.io.Serializable;

public record ContactInfoInput(
        String id,
        String email,
        String phone,
        AddressInput address
) implements Serializable {
}
