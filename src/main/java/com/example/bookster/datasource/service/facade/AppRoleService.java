package com.example.bookster.datasource.service.facade;

import com.example.bookster.datasource.service.generic.GenericServiceFacade;
import com.example.bookster.graphql.models.dto.AppRole;
import com.example.bookster.graphql.models.input.AppRoleInput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AppRoleService extends GenericServiceFacade<AppRoleInput, AppRole, String> {
    Flux<AppRole> findByAppUserId(Mono<String> appUserIdMono);
    Mono<AppRole> findByUserRole(Mono<String> userRoleMono);

}
