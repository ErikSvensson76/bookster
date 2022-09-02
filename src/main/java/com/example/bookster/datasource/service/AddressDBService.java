package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAddress;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AddressDBService {

    Mono<DBAddress> saveOrGet(Mono<DBAddress> dbAddressMono);

    Mono<DBAddress> findById(Mono<UUID> idMono);

    Flux<DBAddress> findByPremisesId(Mono<UUID> premisesId);

    Flux<DBAddress> findByContactInfoId(Mono<UUID> contactInfoId);

    Flux<DBAddress> findAll();

    Mono<Void> delete(Mono<UUID> idMono);



}
