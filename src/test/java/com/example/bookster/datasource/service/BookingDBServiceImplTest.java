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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@SpringBootTest
@DirtiesContext
class BookingDBServiceImplTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    final FakeObjectGenerator generator = FakeObjectGenerator.getInstance();

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    PremisesDBService premisesDBService;

    @Autowired
    BookingDBServiceImpl testObject;

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void persist() {
        DBBooking dbBooking = generator.randomDBBooking();
        DBPremises dbPremises = generator.randomDBPremises();

        Mono.from(template.insert(dbPremises))
                .map(DBPremises::getId)
                .zipWith(Mono.just(dbBooking))
                .flatMap(tuple2 -> testObject.persist(Mono.just(tuple2.getT2()), Mono.just(tuple2.getT1())))
                .as(StepVerifier::create)
                .expectNextMatches(
                        booking -> dbBooking.getId() != null &&
                                booking.getVacant() &&
                                booking.getPatientId() == null &&
                                booking.getPremisesId() != null

                ).verifyComplete();
    }

    @Test
    void findAll() {
        Flux.fromStream(Stream.generate(generator::randomDBBooking).limit(5))
                .flatMap(template::insert)
                .thenMany(testObject.findAll())
                .as(StepVerifier::create)
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void findAllByVacant() {
        Flux.fromStream(Stream.generate(generator::randomDBBooking).limit(5))
                .flatMap(template::insert)
                .thenMany(testObject.findAllByVacant(Mono.just(true)))
                .as(StepVerifier::create)
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void findAllByPatientId() {
        Flux.fromStream(Stream.generate(generator::randomDBBooking).limit(3)).collectList()
                .zipWith(Mono.from(template.insert(generator.randomDBPatient())))
                .flatMapMany(tuple2 -> {
                    List<DBBooking> dbBookingList = tuple2.getT1().stream()
                            .peek(dbBooking -> dbBooking.setPatientId(tuple2.getT2().getId())).toList();
                    return Flux.fromIterable(dbBookingList);
                })
                .flatMap(template::insert).collectList()
                .flatMap(dbBookings -> Mono.just(dbBookings.get(0).getPatientId()))
                .flatMapMany(uuid -> testObject.findAllByPatientId(Mono.just(uuid)))
                .as(StepVerifier::create)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findAllByPremisesId() {
        Flux.fromStream(Stream.generate(generator::randomDBBooking).limit(3)).collectList()
                .zipWith(Mono.from(template.insert(generator.randomDBPremises())))
                .flatMapMany(tuple2 -> Flux.fromStream(tuple2.getT1().stream()).map(dbBooking -> {
                    dbBooking.setPremisesId(tuple2.getT2().getId());
                    return dbBooking;
                }))
                .flatMap(template::insert).collectList()
                .map(dbBookings -> dbBookings.get(0).getPremisesId())
                .flatMapMany(uuid -> testObject.findAllByPremisesId(Mono.just(uuid)))
                .as(StepVerifier::create)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findAllByCity() {
        String city = "Testville";
        Mono.from(premisesDBService.persist(Mono.just(DBPremises.builder().build()), Mono.just(DBAddress.builder().city(city).build())))
                .flatMap(dbPremises -> {
                    DBBooking dbBooking = DBBooking.builder().premisesId(dbPremises.getId()).build();
                    return template.insert(dbBooking);
                }).thenMany(testObject.findAllByCity(Mono.just(city)))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findAllByCity_available_false() {
        String city = "Testville";
        Mono.from(premisesDBService.persist(Mono.just(DBPremises.builder().build()), Mono.just(DBAddress.builder().city(city).build())))
                .flatMap(dbPremises -> {
                    DBBooking dbBooking = DBBooking.builder().premisesId(dbPremises.getId()).vacant(false).build();
                    return template.insert(dbBooking);
                }).thenMany(testObject.findAllByCity(Mono.just(city), Mono.just(false)))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findById() {
        DBBooking dbBooking = generator.randomDBBooking();
        Mono.from(template.insert(dbBooking))
                .map(DBBooking::getId)
                .flatMap(uuid -> testObject.findById(Mono.just(uuid)))
                .as(StepVerifier::create)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void book() {
        Mono.zip(Mono.from(template.insert(generator.randomDBBooking())), Mono.from(template.insert(generator.randomDBPatient())))
                .flatMap(tuple2 -> testObject.book(Mono.just(tuple2.getT1().getId()), Mono.just(tuple2.getT2().getId())))
                .as(StepVerifier::create)
                .expectNextMatches(dbBooking -> dbBooking != null && dbBooking.getPatientId() != null)
                .verifyComplete();
    }

    @Test
    void cancelBooking() {
        Mono.zip(Mono.from(template.insert(generator.randomDBBooking())), Mono.from(template.insert(generator.randomDBPatient())))
                .flatMap(tuple2 -> {
                    tuple2.getT1().setPatientId(tuple2.getT2().getId());
                    return template.update(tuple2.getT1());
                })
                .map(DBBooking::getId)
                .flatMap(uuid -> testObject.cancelBooking(Mono.just(uuid)))
                .as(StepVerifier::create)
                .expectNextMatches(dbBooking -> dbBooking.getPatientId() == null && dbBooking.getVacant())
                .verifyComplete();
    }

    @Test
    void update() {
        DBBooking dbBooking = DBBooking.builder()
                .price(BigDecimal.valueOf(100))
                .dateTime(LocalDateTime.parse("2022-10-06T13:00"))
                .vacant(true)
                .vaccineType("Placeholder").build();

        Mono.from(template.insert(dbBooking))
                .flatMap(booking -> {
                    booking.setVaccineType("Omicron");
                    return testObject.update(Mono.just(booking));
                })
                .as(StepVerifier::create)
                .expectNextMatches(booking -> booking.getVaccineType().equals("Omicron"))
                .verifyComplete();
    }

    @Test
    void delete() {
        Mono.just(generator.randomDBBooking())
                .flatMap(template::insert)
                .map(DBBooking::getId)
                .flatMap(uuid -> testObject.delete(Mono.just(uuid)))
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }
}