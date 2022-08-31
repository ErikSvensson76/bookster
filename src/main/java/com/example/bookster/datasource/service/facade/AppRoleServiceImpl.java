package com.example.bookster.datasource.service.facade;

import com.example.bookster.datasource.repository.AppRoleRepository;
import com.example.bookster.datasource.service.mapping.MappingService;
import com.example.bookster.datasource.service.persistence.AppRolePersistenceService;
import com.example.bookster.graphql.models.dto.AppRole;
import com.example.bookster.graphql.models.input.AppRoleInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppRoleServiceImpl implements AppRoleService {

    private final AppRoleRepository repository;
    private final MappingService mappingService;

    private final AppRolePersistenceService persistenceService;

    @Override
    @Transactional
    public Mono<AppRole> save(Mono<AppRoleInput> appRoleInputMono) {
        return appRoleInputMono.map(mappingService::convert)
                .flatMap(dbAppRole -> {
                    if(dbAppRole.getId() == null){
                        return Mono.just(dbAppRole)
                                .flatMap(persistenceService::save)
                                .map(mappingService::convert);
                    }
                    return Mono.from(repository.findById(dbAppRole.getId()))
                            .flatMap(original -> {
                                original.setUserRole(dbAppRole.getUserRole());
                                return persistenceService.save(original);
                            })
                            .map(mappingService::convert);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AppRole> findById(Mono<String> id) {
        return id.map(UUID::fromString)
                .flatMap(repository::findById)
                .map(mappingService::convert);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AppRole> findAll() {
        return repository.findAll()
                .map(mappingService::convert);
    }

    @Override
    @Transactional
    public Mono<Integer> delete(Mono<String> stringMono) {
        return  stringMono.map(UUID::fromString)
                .flatMap(persistenceService::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AppRole> findByAppUserId(Mono<String> appUserId) {
        return appUserId.map(UUID::fromString)
                .flatMapMany(repository::findByAppUserId)
                .map(mappingService::convert);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AppRole> findByUserRole(Mono<String> appRole) {
        return appRole
                .flatMap(repository::findByUserRole)
                .map(mappingService::convert);
    }
}
