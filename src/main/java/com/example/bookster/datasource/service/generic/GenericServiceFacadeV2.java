package com.example.bookster.datasource.service.generic;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GenericServiceFacadeV2 <ID, T> {

    Mono<T> findById(Mono<ID> idMono);

    Flux<T> findAll();

    Mono<Integer> delete(Mono<ID> idMono);

}
