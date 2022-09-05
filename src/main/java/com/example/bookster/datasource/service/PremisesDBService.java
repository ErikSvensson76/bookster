package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBPremises;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PremisesDBService {

    Mono<DBPremises> persist(Mono<DBPremises> dbPremisesMono, Mono<DBAddress> dbAddressMono);

    Flux<DBPremises> findAll();

    Mono<DBPremises> findById(Mono<UUID> uuidMono);

    Mono<DBPremises> findByBookingId(Mono<UUID> bookingId);

    Mono<DBPremises> update(Mono<DBPremises> dbPremisesMono);

    Mono<Void> delete(Mono<UUID> uuidMono);

}
