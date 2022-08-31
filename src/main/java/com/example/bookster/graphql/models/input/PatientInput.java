package com.example.bookster.graphql.models.input;

import java.io.Serializable;
import java.time.LocalDate;

public record PatientInput(
        String id,
        LocalDate birthDate,
        String firstName,
        String lastName,
        String pnr,
        AppUserInput appUser,
        ContactInfoInput contactInfo
) implements Serializable {
}
