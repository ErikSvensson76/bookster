package com.example.bookster.datasource.repository;

import com.example.bookster.datasource.models.*;
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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
class PatientRepositoryTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    PatientRepository testObject;

    DBPatient dbPatient = DBPatient.builder()
            .pnr("200208301012")
            .firstName("Test")
            .lastName("Testsson")
            .birthDate(LocalDate.now().minusYears(20))
            .build();

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void findPatientsByCity() {
        DBAddress address = DBAddress.builder().city("Testville").build();
        address = template.insert(DBAddress.class).using(address).block();
        assertThat(address).isNotNull();

        DBContactInfo contactInfo = DBContactInfo.builder().addressId(address.getId()).build();
        contactInfo = template.insert(DBContactInfo.class).using(contactInfo).block();
        assertThat(contactInfo).isNotNull();

        dbPatient.setContactInfoId(contactInfo.getId());
        dbPatient = template.insert(DBPatient.class).using(dbPatient).block();
        assertThat(dbPatient).isNotNull();

        StepVerifier.create(testObject.findPatientsByCity(address.getCity()))
                .expectNextMatches(patient -> patient.getContactInfoId() != null)
                .verifyComplete();
    }

    @Test
    void findByUsername() {
        DBAppUser appUser = DBAppUser.builder().username("test").password("test").build();
        appUser = template.insert(DBAppUser.class).using(appUser).block();
        assertThat(appUser).isNotNull();

        dbPatient.setAppUserId(appUser.getId());
        dbPatient = template.insert(dbPatient).block();
        assertThat(dbPatient).isNotNull();

        StepVerifier.create(testObject.findByUsername(appUser.getUsername()))
                .expectNextMatches(patient -> patient.getAppUserId() != null)
                .verifyComplete();
    }

    @Test
    void findPatientByBookingId() {
        dbPatient = template.insert(dbPatient).block();
        assertThat(dbPatient).isNotNull();

        DBBooking booking = DBBooking.builder().patientId(dbPatient.getId()).build();
        booking = template.insert(booking).block();
        assertThat(booking).isNotNull();

        StepVerifier.create(testObject.findPatientByBookingId(booking.getId()))
                .expectNextMatches(patient -> patient.getId().equals(dbPatient.getId()))
                .verifyComplete();
    }
}