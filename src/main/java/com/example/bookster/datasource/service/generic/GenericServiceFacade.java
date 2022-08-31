package com.example.bookster.datasource.service.generic;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GenericServiceFacade <INPUT, OUTPUT, ID>{
    Mono<OUTPUT> save(Mono<INPUT> t);
    Mono<OUTPUT> findById(Mono<ID> id);
    Flux<OUTPUT> findAll();
    Mono<Integer> delete(Mono<ID> id);
}
