package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAppRole;
import com.example.bookster.datasource.models.DBAppUser;
import com.example.bookster.datasource.models.DBRoleAppUser;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AppRoleDBServiceImplTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    AppRoleDBServiceImpl testObject;

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void save_persist() {
        DBAppRole dbAppRole = new DBAppRole(null, "ROLE_APP_USER");
        var result = testObject.save(Mono.just(dbAppRole)).block();
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserRole()).isEqualTo(dbAppRole.getUserRole());
    }

    @Test
    void save_update() {
        DBAppRole appRole = new DBAppRole(null, "ROLE_APP_ADMIN");
        appRole = template.insert(appRole).block();
        assertThat(appRole).isNotNull();

        DBAppRole dbAppRoleUpdate = new DBAppRole(appRole.getId(), "ROLE_APP_USER");
        var result = testObject.save(Mono.just(dbAppRoleUpdate)).block();
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(appRole.getId());
        assertThat(result.getUserRole()).isEqualTo("ROLE_APP_USER");
    }

    @Test
    void findById() {
        var result =Mono.from(template.insert(DBAppRole.class).using(new DBAppRole(null, "test")))
                .map(DBAppRole::getId)
                .flatMap(id -> testObject.findById(Mono.just(id)));

        StepVerifier.create(result)
                .expectNextMatches(dbAppRole -> dbAppRole != null && dbAppRole.getId() != null)
                .verifyComplete();
    }

    @Test
    void findAll() {
        Flux<DBAppRole> result = Mono.from(template.insert(new DBAppRole(null, "test")))
                .flatMapMany(dbAppRole -> testObject.findAll());

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void delete() {
        Mono<Integer> resultMono = Mono.from(
                template.insert(new DBAppRole(null, "test"))
                .map(DBAppRole::getId)
                .flatMap(uuid -> testObject.delete(Mono.just(uuid)))
        );

        StepVerifier.create(resultMono)
                .expectNextMatches(integer -> integer == 1)
                .verifyComplete();
    }

    @Test
    void findByAppUserId() {
        Mono<DBAppUser> dbAppUserMono = template.insert(DBAppUser.builder().username("test").password("test").build());
        Mono<DBAppRole> dbAppRoleMono = template.insert(new DBAppRole(null, "ROLE_APP_USER"));

        var result = Mono.zip(dbAppRoleMono, dbAppUserMono)
                .flatMap(tuple -> template.insert(new DBRoleAppUser(tuple.getT2().getId(), tuple.getT1().getId())))
                .flatMapMany(dbRoleAppUser -> testObject.findByAppUserId(Mono.just(dbRoleAppUser.getAppUserId())));

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findByUserRole() {
        Mono<DBAppRole> result = template.insert(new DBAppRole(null, "ROLE_APP_ADMIN"))
                .flatMap(dbAppRole -> testObject.findByUserRole(Mono.just(dbAppRole.getUserRole())));

        StepVerifier.create(result)
                .expectNextMatches(dbAppRole -> dbAppRole.getUserRole().equals("ROLE_APP_ADMIN"))
                .verifyComplete();
    }
}