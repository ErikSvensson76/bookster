package com.example.bookster.graphql.models.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Address implements Serializable {
    private String id;
    private String city;
    private String street;
    private String zipCode;
}
