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
@Table(value = "address")
public class DBAddress {

    public static final String ADDRESS_PK = "pk_address";
    @Id
    @Column(ADDRESS_PK)
    private UUID id;
    @Column("city")
    private String city;
    @Column("street")
    private String street;
    @Column("zip_code")
    private String zipCode;

    @Transient
    public static DBAddress getNewUpdatedInstance(DBAddress dbAddress){
        DBAddress result = null;
        if(Objects.nonNull(dbAddress)){
            result = DBAddress.builder()
                    .id(dbAddress.getId())
                    .city(dbAddress.getCity())
                    .street(dbAddress.getStreet())
                    .zipCode(dbAddress.getZipCode())
                    .build();
        }
        return result;
    }
}
