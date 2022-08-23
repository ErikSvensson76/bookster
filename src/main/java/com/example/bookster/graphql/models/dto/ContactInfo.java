package com.example.bookster.graphql.models.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@Builder
public class ContactInfo implements Serializable {
    private String id;
    private String email;
    private String phone;
    @EqualsAndHashCode.Exclude
    private Address address;
}
