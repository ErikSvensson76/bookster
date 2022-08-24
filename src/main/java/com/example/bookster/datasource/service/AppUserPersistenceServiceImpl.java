package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.example.bookster.datasource.models.DBAppUser.APP_USER_PK;

@Repository
@RequiredArgsConstructor
public class AppUserPersistenceServiceImpl implements AppUserPersistenceService {

    private final DatabaseClient client;

    @Override
    @Transactional
    public Mono<DBAppUser> save(DBAppUser type) {
        if(type == null) return Mono.empty();
        if(type.getId() == null){
            return client.sql("INSERT INTO app_user (username, password) VALUES (:username, :password)")
                    .bind("username", type.getUsername())
                    .bind("password", type.getPassword())
                    .filter(setIdExtractionStrategy(APP_USER_PK))
                    .map(((r, m) -> r.get(0, UUID.class)))
                    .one()
                    .flatMap(uuid -> Mono.justOrEmpty(
                            DBAppUser.builder()
                                    .id(uuid)
                                    .username(type.getUsername())
                                    .password(type.getPassword())
                                    .build()
                            )
                    );
        }
        return client.sql("UPDATE app_user " +
                        "SET username = :username,password = :password " +
                        "WHERE pk_app_user = :id")
                .bind("username", type.getUsername())
                .bind("password", type.getPassword())
                .bind("id", type.getId())
                .fetch()
                .rowsUpdated()
                .map(integer -> {
                    if(integer == null || integer == 0) {
                        throw new RuntimeException(
                                String.format("Failed to update: %1s with id: %2s", DBAppUser.class.getSimpleName(), type.getId().toString()));
                    }
                    return type;
                });
    }

    @Override
    @Transactional
    public Mono<Integer> delete(UUID uuid) {
        return client.sql("DELETE FROM app_user WHERE pk_app_user = :id")
                .bind("id", uuid)
                .fetch()
                .rowsUpdated();
    }
}
