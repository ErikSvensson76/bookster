package com.example.bookster.datasource.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "app_role")
public class DBAppRole {

    public static final String APP_ROLE_PK = "pk_app_role";

    @Id
    @Column("pk_app_role")
    private UUID id;
    @Column("user_role")
    private String userRole;
}
