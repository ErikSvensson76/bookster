package com.example.bookster.graphql.models.input;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingInput(
        String id,
        String administratorId,
        LocalDateTime dateTime,
        BigDecimal price,
        Boolean vacant,
        String vaccineType,
        String patientId,
        String premisesId
) implements Serializable {
}
