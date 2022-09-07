package com.example.bookster.graphql.facade;

import com.example.bookster.graphql.models.dto.Address;
import com.example.bookster.graphql.models.input.AddressInput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AddressService {

    Mono<Address> save(Mono<AddressInput> addressInputMono);

    Mono<Address> findById(Mono<String> id);

    Mono<Address> findByPremisesId(Mono<String> premisesId);

    Mono<Address> findByContactInfoId(Mono<String> contactInfoId);

    Flux<Address> findAll();

    Mono<Void> delete(Mono<String> id);
}
