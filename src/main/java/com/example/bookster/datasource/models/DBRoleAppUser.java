package com.example.bookster.datasource.models;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("role_app_user")
public class DBRoleAppUser{
    @Column("fk_app_user")
    private UUID appUserId;
    @Column("fk_app_role")
    private UUID appRoleId;
}
