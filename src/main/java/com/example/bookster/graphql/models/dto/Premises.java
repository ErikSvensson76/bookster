package com.example.bookster.graphql.models.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Premises implements Serializable {
    private String id;
    private String premisesName;
    @EqualsAndHashCode.Exclude
    private UUID premisesAddressId;
    @EqualsAndHashCode.Exclude
    private Address address;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Booking> bookings;
}
