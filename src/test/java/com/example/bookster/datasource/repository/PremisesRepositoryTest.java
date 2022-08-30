package com.example.bookster.datasource.repository;

import com.example.bookster.datasource.models.DBBooking;
import com.example.bookster.datasource.models.DBPremises;
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
class PremisesRepositoryTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    PremisesRepository testObject;

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void findByBookingId() {
        DBPremises premises = DBPremises.builder().premisesName("Test name").build();
        var persistedPremises = template.insert(premises).block();
        assertThat(persistedPremises).isNotNull();

        DBBooking booking = template.insert(
                DBBooking.builder().premisesId(persistedPremises.getId()).build()
        ).block();
        assertThat(booking).isNotNull();

        StepVerifier.create(testObject.findByBookingId(booking.getId()))
                .expectNextMatches(entity -> entity.getId().equals(booking.getPremisesId()))
                .verifyComplete();
    }
}