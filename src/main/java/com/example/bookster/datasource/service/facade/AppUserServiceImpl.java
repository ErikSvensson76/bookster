package com.example.bookster.datasource.service.facade;

import com.example.bookster.datasource.models.DBRoleAppUser;
import com.example.bookster.datasource.repository.AppUserRepository;
import com.example.bookster.datasource.service.mapping.MappingService;
import com.example.bookster.datasource.service.persistence.AppUserPersistenceService;
import com.example.bookster.datasource.service.persistence.RoleAppUserPersistenceService;
import com.example.bookster.graphql.models.dto.AppUser;
import com.example.bookster.graphql.models.input.AppUserInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository repository;
    private final MappingService mappingService;
    private final AppUserPersistenceService persistenceService;
    private final AppRoleService appRoleService;
    private final RoleAppUserPersistenceService roleAppUserPersistenceService;

    @Override
    public Mono<AppUser> save(Mono<AppUserInput> appUserInputMono) {
        return appUserInputMono.map(mappingService::convert)
                .flatMap(dbAppUser -> {
                    if(dbAppUser.getId() == null){
                        return persistenceService.save(dbAppUser);
                    }else{
                        return Mono.from(repository.findById(dbAppUser.getId()))
                                .flatMap(result -> {
                                    result.setPassword(dbAppUser.getPassword());
                                    result.setUsername(dbAppUser.getUsername());
                                    return persistenceService.save(result);
                                });
                    }

                }).map(mappingService::convert);
    }

    @Override
    public Mono<AppUser> findById(Mono<String> stringMono) {
        return stringMono.map(UUID::fromString)
                .flatMap(repository::findById)
                .map(mappingService::convert);
    }

    @Override
    public Flux<AppUser> findAll() {
        return repository.findAll()
                .map(mappingService::convert);
    }

    @Override
    public Mono<Integer> delete(Mono<String> stringMono) {
        Mono<UUID> appUserIdMono= stringMono.map(UUID::fromString);
        Mono<List<UUID>> appRolesListMono = appRoleService.findByAppUserId(stringMono)
                .map(appRole -> UUID.fromString(appRole.getId()))
                .collectList();

        return Mono.zip(appUserIdMono, appRolesListMono)
                .flatMap(tuple -> {
                    UUID appUserId = tuple.getT1();
                    var roleAppUserRowsDeletedCount= Flux.fromIterable(tuple.getT2())
                            .map(appRoleId -> new DBRoleAppUser(appUserId, appRoleId))
                            .flatMap(roleAppUserPersistenceService::deleteAppRoleAssignment)
                            .reduce(0, Integer::sum);
                    var appUserDeleteCount = persistenceService.delete(appUserId);

                    return Mono.zip(roleAppUserRowsDeletedCount, appUserDeleteCount)
                            .map(result -> result.getT1() + result.getT2());
                });
    }
}
