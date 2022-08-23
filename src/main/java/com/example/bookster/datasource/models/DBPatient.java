package com.example.bookster.datasource.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "patient")
public class DBPatient {

    @Id
    @Column("pk_patient")
    private UUID id;
    @Column("birth_date")
    private LocalDate birthDate;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    @Column("pnr")
    private String pnr;
    @Column("fk_contact_info")
    private UUID contactInfoId;
    @Column("fk_app_user")
    private UUID appUserId;

    @Transient
    public static DBPatient getNewUpdatedInstance(DBPatient dbPatient){
        DBPatient result = null;
        if(Objects.nonNull(dbPatient)){
            result = DBPatient.builder()
                    .id(dbPatient.getId())
                    .birthDate(dbPatient.getBirthDate())
                    .firstName(dbPatient.getFirstName())
                    .lastName(dbPatient.getLastName())
                    .pnr(dbPatient.getPnr())
                    .contactInfoId(dbPatient.getContactInfoId())
                    .appUserId(dbPatient.getAppUserId())
                    .build();
        }
        return result;
    }

}
