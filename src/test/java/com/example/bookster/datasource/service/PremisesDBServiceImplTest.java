package com.example.bookster.datasource.service;

import com.example.bookster.FakeObjectGenerator;
import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBBooking;
import com.example.bookster.datasource.models.DBPremises;
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
class PremisesDBServiceImplTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    final FakeObjectGenerator generator = FakeObjectGenerator.getInstance();

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    PremisesDBServiceImpl testObject;

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void persist() {
        String premisesName = "Vårdcentralen Test";
        DBPremises newDBPremises = DBPremises.builder()
                .premisesName(premisesName)
                .build();

        DBAddress dbAddress = generator.randomDBAddress();

        Mono.zip(Mono.just(newDBPremises), Mono.just(dbAddress))
                .flatMap(tuple2 -> testObject.persist(Mono.just(tuple2.getT1()), Mono.just(tuple2.getT2())))
                .as(StepVerifier::create)
                .expectNextMatches(dbPremises ->
                        dbPremises != null
                        && dbPremises.getId() != null
                        && dbPremises.getPremisesName().equals(premisesName)
                        && dbPremises.getPremisesAddressId() != null
                ).verifyComplete();
    }

    @Test
    void findAll() {
        Flux.fromStream(Stream.generate(generator::randomDBPremises).limit(5))
                .flatMap(template::insert)
                .thenMany(testObject.findAll())
                .as(StepVerifier::create)
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void findById() {
        Mono.just(generator.randomDBPremises())
                .flatMap(template::insert)
                .map(DBPremises::getId)
                .flatMap(uuid -> testObject.findById(Mono.just(uuid)))
                .as(StepVerifier::create)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void findByBookingId() {
        Mono.just(generator.randomDBPremises())
                .flatMap(template::insert)
                .zipWith(Mono.from(template.insert(generator.randomDBBooking())))
                .flatMap(tuple2 -> {
                    tuple2.getT2().setPremisesId(tuple2.getT1().getId());
                    return template.update(tuple2.getT2());
                }).map(DBBooking::getId)
                .flatMap(uuid -> testObject.findByBookingId(Mono.just(uuid)))
                .as(StepVerifier::create)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void update() {
        DBPremises dbPremises = generator.randomDBPremises();
        DBAddress dbAddress = generator.randomDBAddress();

        Mono.zip(template.insert(dbAddress), template.insert(dbPremises))
                .map(tuple2 -> {
                    DBPremises premises = tuple2.getT2();
                    premises.setPremisesName("Vårdcentral test");
                    premises.setPremisesAddressId(tuple2.getT1().getId());
                    return premises;
                })
                .flatMap(premises -> testObject.update(Mono.just(premises)))
                .as(StepVerifier::create)
                .expectNextMatches(premises ->
                        premises != null &&
                                premises.getPremisesName().equals("Vårdcentral test") &&
                                premises.getPremisesAddressId() != null
                        )
                .verifyComplete();
    }

    @Test
    void update_change_address() {
        DBPremises dbPremises = generator.randomDBPremises();
        DBAddress initialAddress = generator.randomDBAddress();
        DBAddress newAddress = generator.randomDBAddress();

        Mono.zip(Mono.just(dbPremises), template.insert(initialAddress))
                .map(tuple2 -> {
                    tuple2.getT1().setPremisesAddressId(tuple2.getT2().getId());
                    return tuple2.getT1();
                })
                .flatMap(template::insert)
                .zipWith(Mono.from(template.insert(newAddress)))
                .map(tuple2 -> {
                    tuple2.getT1().setPremisesAddressId(tuple2.getT2().getId());
                    return tuple2.getT1();
                })
                .flatMap(premises -> testObject.update(Mono.just(premises)))
                .as(StepVerifier::create)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void delete_no_address() {
        Mono.from(template.insert(generator.randomDBPremises()))
                .map(DBPremises::getId)
                .flatMap(uuid -> testObject.delete(Mono.just(uuid)))
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void delete_with_address() {
        DBPremises dbPremises = generator.randomDBPremises();
        DBAddress dbAddress = generator.randomDBAddress();

        Mono.zip(template.insert(dbAddress), template.insert(dbPremises))
                .map(tuple2 -> {
                    DBPremises premises = tuple2.getT2();
                    premises.setPremisesAddressId(tuple2.getT1().getId());
                    return premises;
                })
                .flatMap(premises -> testObject.update(Mono.just(premises)))
                .map(DBPremises::getId)
                .flatMap(id -> testObject.delete(Mono.just(id)))
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }
}