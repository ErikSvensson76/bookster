package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAppUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AppUserDBService {

    Mono<DBAppUser> persist(Mono<DBAppUser> dbAppUserMono, Mono<String> userRole);

    Mono<DBAppUser> findById(Mono<UUID> uuidMono);

    Flux<DBAppUser> findAll();

    Flux<DBAppUser> findAllByAppRoleId(Mono<UUID> appRoleId);

    Mono<DBAppUser> findByUsername(Mono<String> username);

    Mono<DBAppUser> findByPatientId(Mono<UUID> patientIdMono);

    Mono<DBAppUser> update(Mono<DBAppUser> dbAppUserMono);

    Mono<Void> delete(Mono<UUID> userId);


}
