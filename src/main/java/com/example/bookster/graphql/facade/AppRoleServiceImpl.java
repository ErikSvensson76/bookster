package com.example.bookster.graphql.facade;

import com.example.bookster.datasource.service.AppRoleDBService;
import com.example.bookster.datasource.service.mapping.MappingService;
import com.example.bookster.graphql.models.dto.AppRole;
import com.example.bookster.graphql.models.input.AppRoleInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AppRoleServiceImpl implements AppRoleService {

    private final AppRoleDBService appRoleDBService;
    private final MappingService mappingService;

    @Override
    public Mono<AppRole> save(Mono<AppRoleInput> appRoleInputMono) {
        return appRoleDBService.save(appRoleInputMono.map(mappingService::convert))
                .map(mappingService::convert);
    }

    @Override
    public Mono<AppRole> findById(Mono<String> id) {
        return appRoleDBService.findById(id.map(mappingService::convert))
                .map(mappingService::convert);
    }

    @Override
    public Flux<AppRole> findAll() {
        return appRoleDBService.findAll().map(mappingService::convert);
    }

    @Override
    public Mono<Void> delete(Mono<String> id) {
        return appRoleDBService.delete(id.map(mappingService::convert)).then();
    }

    @Override
    public Mono<AppRole> findByUserRole(Mono<String> userRoleMono) {
        return appRoleDBService.findByUserRole(userRoleMono)
                .map(mappingService::convert);
    }

    @Override
    public Flux<AppRole> findByAppUserId(Mono<String> appUserId) {
        return appRoleDBService.findByAppUserId(appUserId.map(mappingService::convert))
                .map(mappingService::convert);
    }
}
