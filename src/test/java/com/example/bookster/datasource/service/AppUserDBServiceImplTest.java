package com.example.bookster.datasource.service;

import com.example.bookster.FakeObjectGenerator;
import com.example.bookster.datasource.models.DBAppRole;
import com.example.bookster.datasource.models.DBAppUser;
import com.example.bookster.datasource.models.DBPatient;
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

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

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

    Mono<DBAppUser> initializeUser(){
        return template.insert(FakeObjectGenerator.getInstance().randomDBAppUser());
    }

    Mono<DBAppRole> initializeAppRole(){
        return template.insert(new DBAppRole(null, "ROLE_APP_USER"));
    }

    @Test
    void persist() {
        var dbAppRoleMono = template.insert(new DBAppRole(null, "ROLE_APP_ADMIN"));
        var result = Mono.just(DBAppUser.builder().username("test").password("test").build())
                .zipWith(dbAppRoleMono)
                .flatMap(tuple -> testObject.persist(Mono.just(tuple.getT1()), Mono.just(tuple.getT2().getUserRole())));

        StepVerifier.create(result)
                .expectNextMatches(dbAppUser -> dbAppUser != null && dbAppUser.getId() != null)
                .verifyComplete();
    }

    @Test
    void findById() {
        Mono<DBAppUser> result = Mono.from(initializeUser())
                .map(DBAppUser::getId)
                .flatMap(uuid -> testObject.findById(Mono.just(uuid)));

        StepVerifier.create(result)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void findAll() {
        Flux<DBAppUser> dbAppUsersFlux = Flux.fromStream(Stream.generate(() -> initializeUser().block()).limit(5))
                .thenMany(Flux.from(testObject.findAll()));

        final int expected = 5;
        StepVerifier.create(dbAppUsersFlux)
                .expectNextCount(expected)
                .verifyComplete();
    }

    @Test
    void findAllByAppRoleId() {
        var dbAppRoleMono = initializeAppRole();
        var dbAppUserMono = initializeUser();

        var result = dbAppUserMono.zipWith(dbAppRoleMono)
                .flatMap(tuple -> {
                    DBRoleAppUser dbRoleAppUser = new DBRoleAppUser(tuple.getT1().getId(), tuple.getT2().getId());
                    return Mono.zip(Mono.just(tuple.getT2()), template.insert(dbRoleAppUser))
                            .map(t -> t.getT1().getId());
                })
                .flatMapMany(uuid -> testObject.findAllByAppRoleId(Mono.just(uuid)));

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findByUsername() {
        Mono<DBAppUser> result = Mono.from(initializeUser())
                .map(DBAppUser::getUsername)
                .flatMap(username -> testObject.findByUsername(Mono.just(username)));

        StepVerifier.create(result)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void findByPatientId() {
        Mono<DBPatient> dbPatientMono = Mono.from(template.insert(FakeObjectGenerator.getInstance().randomDBPatient()));
        Mono<DBAppUser> result = Mono.from(initializeUser())
                .zipWith(dbPatientMono)
                .flatMap(tuple -> {
                    var patient = tuple.getT2();
                    patient.setAppUserId(tuple.getT1().getId());
                    return Mono.from(template.update(patient));
                })
                .map(DBPatient::getId)
                .flatMap(uuid -> testObject.findByPatientId(Mono.just(uuid).cast(UUID.class)));

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void update() {
        DBAppUser dbAppUser = new DBAppUser(null, "test", "test123");
        var result = Mono.just(dbAppUser)
                .flatMap(entity -> template.insert(entity))
                .map(entity -> new DBAppUser(entity.getId(), "foo", "bar"))
                .flatMap(entity -> testObject.update(Mono.just(entity)));

        StepVerifier.create(result)
                .expectNextMatches(entity ->
                        entity != null &&
                        entity.getId().equals(dbAppUser.getId()) &&
                        entity.getUsername().equals("foo") &&
                        entity.getPassword().equals("bar")
                )
                .verifyComplete();
    }

    @Test
    void delete() {
        var dbAppRoleMono = initializeAppRole();
        var dbAppUserMono = initializeUser();

        var result = dbAppUserMono.zipWith(dbAppRoleMono)
                .flatMap(tuple -> {
                    DBRoleAppUser dbRoleAppUser = new DBRoleAppUser(tuple.getT1().getId(), tuple.getT2().getId());
                    return Mono.zip(Mono.just(tuple.getT1()), template.insert(dbRoleAppUser))
                            .map(t -> t.getT1().getId());
                })
                .flatMap(uuid -> testObject.delete(Mono.just(uuid)));

        StepVerifier.create(result)
                .expectSubscription()
                .verifyComplete();

    }
}