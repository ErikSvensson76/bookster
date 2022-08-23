package com.example.bookster.datasource.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "booking")
public class DBBooking {
    @Id
    @Column("pk_booking")
    private UUID id;
    @Column("administratorId")
    private String administratorId;
    @Column("date_time")
    private LocalDateTime dateTime;
    @Column("price")
    private BigDecimal price;
    @Column("vacant")
    private Boolean vacant = true;
    @Column("vaccineType")
    private String vaccineType;
    @Column("fk_patient")
    private UUID patientId;
    @Column("fk_premises")
    private UUID premisesId;

    @Transient
    public static DBBooking getNewUpdatedInstance(DBBooking dbBooking){
        DBBooking result = null;
        if(Objects.nonNull(dbBooking)){
            result = DBBooking.builder()
                    .id(dbBooking.getId())
                    .administratorId(dbBooking.getAdministratorId())
                    .dateTime(dbBooking.getDateTime())
                    .price(dbBooking.getPrice())
                    .vacant(dbBooking.getVacant())
                    .vaccineType(dbBooking.getVaccineType())
                    .patientId(dbBooking.getPatientId())
                    .premisesId(dbBooking.getPremisesId())
                    .build();
        }
        return result;
    }

}
