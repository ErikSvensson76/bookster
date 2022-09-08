package com.example.bookster.graphql.facade;

import com.example.bookster.graphql.models.dto.Premises;
import com.example.bookster.graphql.models.input.PremisesInput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PremisesService {

    Mono<Premises> persist(Mono<PremisesInput> premisesInputMono);

    Flux<Premises> findAll();

    Mono<Premises> findById(Mono<String> idMono);

    Mono<Premises> findByBookingId(Mono<String> bookingId);

    Mono<Premises> update(Mono<String> premisesId, Mono<PremisesInput> premisesInputMono);

    Mono<Void> delete(Mono<String> uuid);

}
