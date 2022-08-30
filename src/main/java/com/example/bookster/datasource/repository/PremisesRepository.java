package com.example.bookster.datasource.repository;

import com.example.bookster.datasource.models.DBPremises;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PremisesRepository extends R2dbcRepository<DBPremises, UUID> {

    @Query("SELECT p.* FROM premises p " +
            "JOIN booking b ON p.pk_premises = b.fk_premises " +
            "WHERE b.pk_booking = :bookingId")
    Mono<DBPremises> findByBookingId(@Param("bookingId") UUID bookingId);

}
