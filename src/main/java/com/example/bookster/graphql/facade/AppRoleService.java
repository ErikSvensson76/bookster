package com.example.bookster.graphql.facade;

import com.example.bookster.graphql.models.dto.AppRole;
import com.example.bookster.graphql.models.input.AppRoleInput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AppRoleService {

    Mono<AppRole> save(Mono<AppRoleInput> appRoleInputMono);

    Mono<AppRole> findById(Mono<String> id);

    Flux<AppRole> findAll();

    Mono<Void> delete(Mono<String> id);

    Mono<AppRole> findByUserRole(Mono<String> userRoleMono);

    Flux<AppRole> findByAppUserId(Mono<String> appUserId);

}
