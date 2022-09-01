package com.example.bookster.datasource.repository;

import com.example.bookster.datasource.models.DBAppRole;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AppRoleRepository extends R2dbcRepository<DBAppRole, UUID> {
    @Query("SELECT role.* " +
            "FROM app_role role " +
            "JOIN role_app_user ru ON role.pk_app_role = ru.fk_app_role " +
            "JOIN app_user u ON ru.fk_app_user = u.pk_app_user " +
            "WHERE u.pk_app_user = :userId")
    Flux<DBAppRole> findByAppUserId(@Param("userId") UUID userId);

    Mono<DBAppRole> findByUserRole(String userRole);

    @Modifying
    Mono<Integer> deleteDBAppRoleById(UUID id);
}
