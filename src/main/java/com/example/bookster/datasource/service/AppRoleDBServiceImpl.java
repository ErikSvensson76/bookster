package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAppRole;
import com.example.bookster.datasource.repository.AppRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppRoleDBServiceImpl implements AppRoleDBService {

    private final AppRoleRepository appRoleRepository;

    @Override
    @Transactional
    public Mono<DBAppRole> save(Mono<DBAppRole> dbAppRoleMono){
        return dbAppRoleMono
                .flatMap(dbAppRole -> {
                    if (dbAppRole == null) return Mono.empty();
                    if(dbAppRole.getId() == null){
                        return Mono.just(dbAppRole).flatMap(appRoleRepository::save);
                    }
                    return Mono.from(appRoleRepository.findById(dbAppRole.getId()))
                            .flatMap(source -> {
                                source.setUserRole(dbAppRole.getUserRole());
                                return appRoleRepository.save(source);
                            });
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DBAppRole> findById(Mono<UUID> id) {
        return id.flatMap(appRoleRepository::findById);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBAppRole> findAll() {
        return appRoleRepository.findAll();
    }

    @Override
    @Transactional
    public Mono<Integer> delete(Mono<UUID> idMono) {
        return idMono.flatMap(appRoleRepository::deleteDBAppRoleById);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DBAppRole> findByUserRole(Mono<String> appRole) {
        return appRole.flatMap(appRoleRepository::findByUserRole);
    }

    @Transactional(readOnly = true)
    public Flux<DBAppRole> findByAppUserId(Mono<UUID> appUserId) {
        return appUserId.flatMapMany(appRoleRepository::findByAppUserId);

    }

}
