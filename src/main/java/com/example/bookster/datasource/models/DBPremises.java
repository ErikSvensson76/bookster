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
@Table(name = "premises")
public class DBPremises {

    @Id
    @Column("pk_premises")
    private UUID id;
    @Column("premises_name")
    private String premisesName;
    @Column("fk_premises_address")
    private UUID premisesAddressId;

    @Transient
    public static DBPremises getNewUpdatedInstance(DBPremises dbPremises){
        DBPremises result = null;
        if(Objects.nonNull(dbPremises)){
            result = DBPremises.builder()
                    .id(dbPremises.getId())
                    .premisesName(dbPremises.getPremisesName())
                    .premisesAddressId(dbPremises.getPremisesAddressId())
                    .build();
        }

        return result;
    }

}
