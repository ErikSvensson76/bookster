package com.example.bookster.datasource.repository;

import com.example.bookster.datasource.models.DBContactInfo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ContactInfoRepository extends R2dbcRepository<DBContactInfo, UUID> {
    @Query("SELECT ci.* FROM contact_info ci " +
            "JOIN patient p ON ci.pk_contact_info = p.fk_contact_info " +
            "WHERE p.pk_patient = :patientId")
    Mono<DBContactInfo> findByContactInfoByPatientId(@Param("patientId") UUID patientId);
    
}
