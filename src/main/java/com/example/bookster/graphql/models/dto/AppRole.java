package com.example.bookster.graphql.models.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class AppRole implements Serializable {
    private String id;
    private String userRole;
    @EqualsAndHashCode.Exclude
    private List<AppUser> members;
}
