package com.example.bookster.datasource.repository;

import com.example.bookster.datasource.models.DBAppRole;
import com.example.bookster.datasource.models.DBAppUser;
import com.example.bookster.datasource.models.DBPatient;
import com.example.bookster.datasource.models.DBRoleAppUser;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
class AppUserRepositoryTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    AppUserRepository testObject;

    DBAppUser dbAppUser = DBAppUser.builder()
            .username("test")
            .password("test")
            .build();

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
        dbAppUser = template.insert(DBAppUser.class).using(dbAppUser).block();
    }

    @Test
    void findByAppRoleId() {
        assertThat(dbAppUser).isNotNull();
        var result = Mono.from(template.insert(new DBAppRole(null, "ROLE_APP_USER")))
                .zipWith(Mono.just(dbAppUser))
                .flatMap(tuple -> Mono.from(template.insert(
                        DBRoleAppUser.builder()
                                .appUserId(tuple.getT2().getId())
                                .appRoleId(tuple.getT1().getId())
                                .build()
                )))
                .map(DBRoleAppUser::getAppRoleId)
                .flatMapMany(uuid -> testObject.findByAppRoleId(uuid));

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findByPatientId() {
        assertThat(dbAppUser).isNotNull();
        DBPatient patient = DBPatient.builder().appUserId(dbAppUser.getId()).build();
        patient = template.insert(DBPatient.class).using(patient).block();
        assertThat(patient).isNotNull();

        StepVerifier.create(testObject.findByPatientId(patient.getId()))
                .expectNextMatches(user -> user.getId().equals(dbAppUser.getId()))
                .verifyComplete();
    }
}