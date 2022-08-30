package com.example.bookster.datasource.repository;

import com.example.bookster.datasource.models.DBAppUser;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AppUserRepository extends R2dbcRepository<DBAppUser, UUID> {
    @Query("SELECT au.* FROM app_user AS au " +
            "JOIN role_app_user ru ON au.pk_app_user = ru.fk_app_user " +
            "JOIN app_role r ON ru.fk_app_role = r.pk_app_role " +
            "WHERE r.pk_app_role = :appRoleId")
    Flux<DBAppUser> findByAppRoleId(@Param("appRoleId") UUID appRoleId);

    @Query("SELECT au.* FROM app_user au " +
            "JOIN patient p ON au.pk_app_user = p.fk_app_user " +
            "WHERE p.pk_patient = :patientId")
    Mono<DBAppUser> findByPatientId(@Param("patientId") UUID patientId);
}
