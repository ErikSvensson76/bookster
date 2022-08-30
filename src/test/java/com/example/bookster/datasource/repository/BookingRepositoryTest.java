package com.example.bookster.datasource.repository;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBBooking;
import com.example.bookster.datasource.models.DBPatient;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
class BookingRepositoryTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    BookingRepository testObject;

    DBBooking booking;

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
        booking = DBBooking.builder()
                .dateTime(LocalDateTime.now().plus(10, ChronoUnit.DAYS))
                .price(BigDecimal.valueOf(199))
                .vacant(true)
                .vaccineType("Test")
                .administratorId(null)
                .build();
    }

    @Test
    void findByPatientId() {
        var patient = template.insert(DBPatient.class).using(
                DBPatient.builder().firstName("Test").lastName("Testsson").build()
        ).block();

        assertThat(patient).isNotNull();
        booking.setPatientId(patient.getId());
        booking = template.insert(DBBooking.class).using(booking).block();

        StepVerifier.create(testObject.findByPatientId(patient.getId()))
                .expectNextMatches(entity -> entity.getPatientId().equals(patient.getId()))
                .verifyComplete();
    }

    @Test
    void findByPremisesId() {
        var premises = template.insert(DBPremises.class).using(
                DBPremises.builder().premisesName("Test premises").build()
        ).block();
        assertThat(premises).isNotNull();
        booking.setPremisesId(premises.getId());
        booking = template.insert(DBBooking.class).using(booking).block();
        assertThat(booking).isNotNull();

        StepVerifier.create(testObject.findByPremisesId(premises.getId()))
                .expectNextMatches(entity -> entity.getPremisesId().equals(premises.getId()))
                .verifyComplete();
    }

    @Test
    void findBookingsByCity() {
        var address = template.insert(DBAddress.class).using(DBAddress.builder().city("Testville").build()).block();
        assertThat(address).isNotNull();
        var premises = template.insert(DBPremises.class).using(DBPremises.builder().premisesAddressId(address.getId()).build()).block();
        assertThat(premises).isNotNull();
        booking.setPremisesId(premises.getId());
        booking = template.insert(DBBooking.class).using(booking).block();

        StepVerifier.create(testObject.findBookingsByCity("Testville"))
                .expectNextMatches(entity -> entity.getPremisesId().equals(premises.getId()))
                .verifyComplete();
    }
}