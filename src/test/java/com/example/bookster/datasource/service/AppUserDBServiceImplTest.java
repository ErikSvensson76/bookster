package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAppRole;
import com.example.bookster.datasource.models.DBAppUser;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class AppUserDBServiceImplTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    AppUserDBServiceImpl testObject;

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void persist() {
        var dbAppRole = template.insert(new DBAppRole(null, "ROLE_APP_USER"));
        var result = Mono.just(DBAppUser.builder().username("test").password("test").build())
                .zipWith(dbAppRole)
                .flatMap(tuple -> testObject.persist(Mono.just(tuple.getT1()), Mono.just(tuple.getT2().getUserRole())));

        StepVerifier.create(result)
                .expectNextMatches(dbAppUser -> dbAppUser != null && dbAppUser.getId() != null)
                .verifyComplete();
    }

    @Test
    void findById() {
    }

    @Test
    void findAll() {
    }

    @Test
    void findAllByAppRoleId() {
    }

    @Test
    void findByUsername() {
    }

    @Test
    void findByPatientId() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}