package com.example.bookster.graphql.models.input;

import java.io.Serializable;

public record PremisesInput(
        String id,
        String premisesName,
        AddressInput address
) implements Serializable {
}
