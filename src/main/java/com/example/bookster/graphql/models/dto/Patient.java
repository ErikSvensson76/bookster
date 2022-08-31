package com.example.bookster.graphql.models.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Patient implements Serializable {
    private String id;
    private LocalDate birthDate;
    private String firstName;
    private String lastName;
    private String pnr;
    private Integer age;
    @EqualsAndHashCode.Exclude
    private UUID contactInfoId;
    @EqualsAndHashCode.Exclude
    private UUID appUserId;
    @EqualsAndHashCode.Exclude
    private ContactInfo contactInfo;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private AppUser appUser;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Booking> bookings;
}
