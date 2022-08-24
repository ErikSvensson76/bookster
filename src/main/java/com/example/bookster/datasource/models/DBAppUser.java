package com.example.bookster.datasource.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "app_user")
public class DBAppUser {

    public static final String APP_USER_PK = "pk_app_user";

    @Id
    @Column("pk_app_user")
    private UUID id;
    @Column("username")
    private String username;
    @Column("password")
    private String password;

    @Transient
    public static  DBAppUser getNewUpdatedInstance(DBAppUser dbAppUser){
        DBAppUser result = null;
        if (Objects.nonNull(dbAppUser)){
            result = DBAppUser.builder()
                    .id(dbAppUser.getId())
                    .username(dbAppUser.getUsername())
                    .password(dbAppUser.getPassword())
                    .build();
        }
        return result;
    }
}
