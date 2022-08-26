package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAppRole;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.example.bookster.datasource.models.DBAppRole.APP_ROLE_PK;

@Repository
@RequiredArgsConstructor
public class AppRolePersistenceServiceImpl implements AppRolePersistenceService {

    private final DatabaseClient client;

    @Override
    @Transactional
    public Mono<DBAppRole> save(final DBAppRole type) {
        if(type.getId() == null){
            return client.sql("INSERT INTO app_role (user_role) VALUES (:role)")
                    .bind("role", type.getUserRole())
                    .filter(setIdExtractionStrategy(APP_ROLE_PK))
                    .map(((r, m) -> r.get(0, UUID.class)))
                    .one()
                    .flatMap(uuid -> Mono.just(new DBAppRole(uuid, type.getUserRole())))
                    .log();
        }
        return client.sql("UPDATE app_role SET user_role = :userRole WHERE pk_app_role = :uuid")
                .bind("uuid", type.getId())
                .bind("userRole", type.getUserRole())
                .fetch()
                .rowsUpdated()
                .map(integer -> {
                    if(integer == null || integer == 0) {
                        throw new RuntimeException(
                                String.format("Failed to update: %1s with id: %2s", DBAppRole.class.getSimpleName(), type.getId().toString()));
                    }
                    return type;
                });
    }

    @Override
    public Mono<Integer> delete(UUID uuid) {
        return client.sql("DELETE FROM app_role WHERE pk_app_role = :uuid")
                .bind("uuid", uuid)
                .fetch().rowsUpdated();
    }
}
