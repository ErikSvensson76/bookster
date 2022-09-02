package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAppRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AppRoleDBService {
    Mono<DBAppRole> save(Mono<DBAppRole> dbAppRoleMono);

    Mono<DBAppRole> findById(Mono<UUID> id);

    Flux<DBAppRole> findAll();

    Mono<Integer> delete(Mono<UUID> idMono);

    Mono<DBAppRole> findByUserRole(Mono<String> userRoleMono);
    Flux<DBAppRole> findByAppUserId(Mono<UUID> appUserId);
}
