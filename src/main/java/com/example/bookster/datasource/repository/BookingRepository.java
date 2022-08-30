package com.example.bookster.datasource.repository;

import com.example.bookster.datasource.models.DBBooking;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface BookingRepository extends R2dbcRepository<DBBooking, UUID> {
    Flux<DBBooking> findByPatientId(UUID patientId);
    Flux<DBBooking> findByPremisesId(UUID premisesId);
    @Query("SELECT b.* FROM booking b " +
            "JOIN premises p ON p.pk_premises = b.fk_premises " +
            "JOIN address a ON a.pk_address = p.fk_premises_address " +
            "WHERE lower(a.city) = lower(:city)")
    Flux<DBBooking> findBookingsByCity(@Param("city") String city);
}
