package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBAppUser;
import com.example.bookster.datasource.models.DBContactInfo;
import com.example.bookster.datasource.models.DBPatient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PatientDBService {

    Mono<DBPatient> persist(
            Mono<DBPatient> dbPatientMono,
            Mono<DBContactInfo> dbContactInfoMono,
            Mono<DBAddress> dbAddressMono,
            Mono<DBAppUser> dbAppUserMono
    );

    Flux<DBPatient> findAll();

    Mono<DBPatient> findByBookingId(Mono<UUID> bookingIdMono);

    Flux<DBPatient> findByCity(Mono<String> city);

    Mono<DBPatient> findByUsername(Mono<String> username);

    Mono<DBPatient> findById(Mono<UUID> uuidMono);

    Mono<DBPatient> update(Mono<DBPatient> dbPatientMono);

    Mono<Void> delete(Mono<UUID> uuidMono);

}
