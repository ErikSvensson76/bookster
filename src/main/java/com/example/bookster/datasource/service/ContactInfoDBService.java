package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBContactInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ContactInfoDBService {

    Mono<DBContactInfo> persist(Mono<DBContactInfo> contactInfoMono, Mono<DBAddress> dbAddressMono);

    Flux<DBContactInfo> findAll();

    Mono<DBContactInfo> findById(Mono<UUID> uuidMono);

    Mono<DBContactInfo> findByPatientId(Mono<UUID> patientIdMono);

    Mono<DBContactInfo> update(Mono<DBContactInfo> contactInfoMono);

    Mono<Void> delete(Mono<UUID> uuidMono);

}
