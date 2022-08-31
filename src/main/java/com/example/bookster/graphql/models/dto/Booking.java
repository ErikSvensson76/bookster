package com.example.bookster.graphql.models.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Booking implements Serializable {
    private String id;
    private String administratorId;
    private LocalDateTime dateTime;
    private BigDecimal price;
    private Boolean vacant;
    private String vaccineType;
    @EqualsAndHashCode.Exclude
    private UUID patientId;
    @EqualsAndHashCode.Exclude
    private UUID premisesId;
    @EqualsAndHashCode.Exclude
    private Patient patient;
    @EqualsAndHashCode.Exclude
    private Premises premises;
}
