package com.example.bookster.graphql.facade;

import com.example.bookster.graphql.models.dto.AppUser;
import com.example.bookster.graphql.models.input.AppUserInput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AppUserService {

    Mono<AppUser> persist(Mono<AppUserInput> appUserInputMono, Mono<String> appRole);

    Mono<AppUser> findById(Mono<String> id);

    Flux<AppUser> findAll();

    Flux<AppUser> findByAppRoleId(Mono<String> appRoleId);

    Mono<AppUser> findByUsername(Mono<String> username);

    Mono<AppUser> findByPatientId(Mono<String> patientId);

    Mono<AppUser> update(Mono<String> id, Mono<AppUserInput> appUserInputMono);

    Mono<Void> delete(Mono<String> id);


}
