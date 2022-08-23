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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "contact_info")
public class DBContactInfo {
    @Id
    @Column("pk_contact_info")
    private UUID id;
    @Column("email")
    private String email;
    @Column("phone")
    private String phone;
    @Column("fk_user_address")
    private UUID addressId;

    @Transient
    public static DBContactInfo getNewUpdatedInstance(DBContactInfo dbContactInfo){
        DBContactInfo result = null;
        if(Objects.nonNull(dbContactInfo)){
            result = DBContactInfo.builder()
                    .id(dbContactInfo.getId())
                    .email(dbContactInfo.getEmail())
                    .phone(dbContactInfo.getPhone())
                    .addressId(dbContactInfo.getAddressId())
                    .build();
        }
        return result;
    }


}
