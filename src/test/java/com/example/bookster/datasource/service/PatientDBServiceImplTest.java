package com.example.bookster.datasource.service;

import com.example.bookster.FakeObjectGenerator;
import com.example.bookster.datasource.models.*;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.stream.Stream;

@SpringBootTest
@DirtiesContext
class PatientDBServiceImplTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    final FakeObjectGenerator generator = FakeObjectGenerator.getInstance();

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    PatientDBServiceImpl testObject;

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
        Flux.fromStream(Stream.of("ROLE_APP_USER", "ROLE_APP_ADMIN"))
                .flatMap(role -> template.insert(new DBAppRole(null, role))).blockLast();
    }

    @Test
    void persist() {
        DBPatient patient = generator.randomDBPatient();
        DBContactInfo contactInfo = generator.randomDBContactInfo();
        DBAddress address = generator.randomDBAddress();
        DBAppUser appUser = generator.randomDBAppUser();

        Mono.from(testObject.persist(
                Mono.just(patient),
                Mono.just(contactInfo),
                Mono.just(address),
                Mono.just(appUser)
        )).as(StepVerifier::create)
                .expectNextMatches(dbPatient ->
                        dbPatient != null &&
                                dbPatient.getId() != null &&
                                dbPatient.getPnr() != null &&
                                dbPatient.getAppUserId() != null &&
                                dbPatient.getFirstName() != null &&
                                dbPatient.getLastName() != null &&
                                dbPatient.getContactInfoId() != null &&
                                dbPatient.getBirthDate() != null
                ).verifyComplete();


    }

    @Test
    void findAll() {
        int expected = 5;
        Flux.fromStream(Stream.generate(generator::randomDBPatient).limit(expected))
                .flatMap(template::insert)
                .thenMany(testObject.findAll())
                .as(StepVerifier::create)
                .expectNextCount(expected)
                .verifyComplete();
    }

    @Test
    void findByBookingId() {
        Mono.zip(template.insert(generator.randomDBPatient()), template.insert(generator.randomDBBooking()))
                .map(tuple2 -> {
                    tuple2.getT2().setPatientId(tuple2.getT1().getId());
                    return tuple2.getT2();
                })
                .flatMap(template::update)
                .map(DBBooking::getId)
                .flatMap(uuid -> testObject.findByBookingId(Mono.just(uuid)))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findByCity() {
        String city = "Test";
        DBPatient patient = generator.randomDBPatient();
        DBContactInfo contactInfo = generator.randomDBContactInfo();
        DBAddress address = DBAddress.builder().city(city).build();

        Mono.from(template.insert(address))
                .zipWith(Mono.just(contactInfo))
                .flatMap(tuple2 -> {
                    tuple2.getT2().setAddressId(tuple2.getT1().getId());
                    return template.insert(tuple2.getT2());
                }).zipWith(Mono.just(patient))
                .flatMap(tuple2 -> {
                    tuple2.getT2().setContactInfoId(tuple2.getT1().getId());
                    return template.insert(tuple2.getT2());
                })
                .thenMany(testObject.findByCity(Mono.just("test")))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findByUsername() {
        DBAppUser dbAppUser = DBAppUser.builder().username("test").password("password").build();
        Mono.just(generator.randomDBPatient())
                .zipWith(Mono.from(template.insert(dbAppUser)))
                .flatMap(tuple2 -> {
                    tuple2.getT1().setAppUserId(tuple2.getT2().getId());
                    return template.insert(tuple2.getT1());
                })
                .then(testObject.findByUsername(Mono.just(dbAppUser.getUsername())))
                .as(StepVerifier::create)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void findById() {
        DBPatient patient = generator.randomDBPatient();
        Mono.from(template.insert(patient))
                .map(DBPatient::getId)
                .flatMap(uuid -> testObject.findById(Mono.just(uuid)))
                .as(StepVerifier::create)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}