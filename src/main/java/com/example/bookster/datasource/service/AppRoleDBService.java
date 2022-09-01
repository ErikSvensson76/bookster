package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAppRole;
import com.example.bookster.datasource.service.generic.GenericServiceFacadeV2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AppRoleDBService extends GenericServiceFacadeV2<UUID, DBAppRole> {
    Mono<DBAppRole> save(Mono<DBAppRole> dbAppRoleMono);
    Mono<DBAppRole> findByUserRole(Mono<String> userRoleMono);
    Flux<DBAppRole> findByAppUserId(Mono<UUID> appUserId);
}
