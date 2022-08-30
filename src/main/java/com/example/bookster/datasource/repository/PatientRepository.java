package com.example.bookster.datasource.repository;

import com.example.bookster.datasource.models.DBPatient;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PatientRepository extends R2dbcRepository<DBPatient, UUID> {

    @Query("SELECT p.* FROM patient p " +
            "JOIN contact_info ci ON ci.pk_contact_info = p.fk_contact_info " +
            "JOIN address a ON a.pk_address = ci.fk_user_address " +
            "WHERE lower(a.city) = lower(:city)")
    Flux<DBPatient> findPatientsByCity(@Param("city") String city);

    @Query("SELECT p.* FROM patient p " +
            "JOIN app_user au ON au.pk_app_user = p.fk_app_user " +
            "WHERE lower(au.username) = lower(:username)")
    Mono<DBPatient> findByUsername(@Param("username") String username);

    @Query("SELECT p.* FROM patient p " +
            "JOIN booking b ON p.pk_patient = b.fk_patient " +
            "WHERE b.pk_booking = :bookingId")
    Mono<DBPatient> findPatientByBookingId(@Param("bookingId") UUID bookingId);

}
