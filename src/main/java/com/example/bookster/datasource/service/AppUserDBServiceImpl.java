package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAppRole;
import com.example.bookster.datasource.models.DBAppUser;
import com.example.bookster.datasource.models.DBRoleAppUser;
import com.example.bookster.datasource.repository.AppUserRepository;
import com.example.bookster.datasource.service.persistence.RoleAppUserPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserDBServiceImpl implements AppUserDBService {

    private final AppUserRepository appUserRepository;
    private final AppRoleDBService appRoleDBService;
    private final RoleAppUserPersistenceService roleAppUserPersistenceService;

    @Override
    @Transactional
    public Mono<DBAppUser> persist(Mono<DBAppUser> dbAppUserMono, Mono<String> userRole) {
        Mono<UUID> dbAppRoleIdMono = appRoleDBService.findByUserRole(userRole)
                .map(DBAppRole::getId);

        return dbAppUserMono
                .flatMap(appUserRepository::save)
                .zipWith(dbAppRoleIdMono)
                .flatMap(tuple -> {
                    UUID uuid = tuple.getT2();
                    DBAppUser dbAppUser = tuple.getT1();
                    DBRoleAppUser dbRoleAppUser = new DBRoleAppUser(dbAppUser.getId(), uuid);

                    return Mono.zip(Mono.just(dbAppUser), Mono.from(roleAppUserPersistenceService.createAppRoleAssignment(dbRoleAppUser)))
                            .map(Tuple2::getT1);
                });
    }

    @Override
    public Mono<DBAppUser> findById(Mono<UUID> uuidMono) {
        return uuidMono.flatMap(appUserRepository::findById);
    }

    @Override
    public Flux<DBAppUser> findAll() {
        return appUserRepository.findAll();
    }

    @Override
    public Flux<DBAppUser> findAllByAppRoleId(Mono<UUID> appRoleId) {
        return appRoleId.flatMapMany(appUserRepository::findByAppRoleId);
    }

    @Override
    public Mono<DBAppUser> findByUsername(Mono<String> username) {
        return username.flatMap(appUserRepository::findByUsername);
    }

    @Override
    public Mono<DBAppUser> findByPatientId(Mono<UUID> patientIdMono) {
        return patientIdMono.flatMap(appUserRepository::findByPatientId);
    }

    @Override
    public Mono<DBAppUser> update(Mono<DBAppUser> dbAppUserMono) {
        return dbAppUserMono
                .flatMap(dbAppUser -> Mono.zip(Mono.just(dbAppUser),Mono.from(appUserRepository.findById(Mono.just(dbAppUser.getId())))))
                .flatMap(tuple -> {
                    var updated = tuple.getT1();
                    var source = tuple.getT2();
                    source.setPassword(updated.getPassword());
                    source.setUsername(updated.getUsername());
                    return appUserRepository.save(source);
                });
    }

    @Override
    public Mono<Void> delete(Mono<UUID> uuidMono) {
        return null;
    }
}
