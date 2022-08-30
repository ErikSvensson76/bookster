package com.example.bookster.datasource.service.persistence;

import com.example.bookster.datasource.models.DBBooking;
import com.example.bookster.datasource.models.DBPatient;
import com.example.bookster.datasource.models.DBPremises;
import com.example.bookster.datasource.service.persistence.persistence.BookingPersistenceService;
import com.example.bookster.datasource.service.persistence.persistence.PatientPersistenceService;
import com.example.bookster.datasource.service.persistence.persistence.PremisesPersistenceService;
import io.r2dbc.spi.ConnectionFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
class BookingPersistenceServiceTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    BookingPersistenceService testObject;

    @Autowired
    PatientPersistenceService patientPersistenceService;

    @Autowired
    PremisesPersistenceService premisesPersistenceService;

    DBBooking dbBooking = DBBooking.builder()
            .dateTime(LocalDateTime.now().plus(10, ChronoUnit.DAYS))
            .price(BigDecimal.valueOf(390.90))
            .vacant(true)
            .vaccineType("Test vaccine")
            .build();


    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void save_persist() {
        var premises = DBPremises.builder().premisesName("Vårdcentralen Test").build();
        var persistedPremises = premisesPersistenceService.save(premises).block();
        Assertions.assertThat(persistedPremises).isNotNull();

        dbBooking.setPremisesId(persistedPremises.getId());

        var result = testObject.save(dbBooking).block();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getPrice()).isEqualTo(dbBooking.getPrice());
        assertThat(result.getVacant()).isTrue();
        assertThat(result.getPatientId()).isNull();
        assertThat(result.getDateTime()).isEqualTo(dbBooking.getDateTime());
        assertThat(result.getPremisesId()).isEqualTo(persistedPremises.getId());
        assertThat(result.getVaccineType()).isEqualTo(dbBooking.getVaccineType());
        assertThat(result.getAdministratorId()).isNull();
    }

    @Test
    void save_update() {
        var premises = DBPremises.builder().premisesName("Vårdcentralen Test").build();
        var persistedPremises = premisesPersistenceService.save(premises).block();
        Assertions.assertThat(persistedPremises).isNotNull();
        dbBooking.setPremisesId(persistedPremises.getId());

        var booking = testObject.save(dbBooking).block();
        assertThat(booking).isNotNull();

        var patient = DBPatient.builder()
                .firstName("Test")
                .lastName("Testsson")
                .birthDate(LocalDate.now().minusYears(20))
                .pnr("2002-08-29 2782")
                .build();

        var persistedPatient = patientPersistenceService.save(patient).block();
        Assertions.assertThat(persistedPatient).isNotNull();

        var updatedBooking = DBBooking.builder()
                .id(booking.getId())
                .price(booking.getPrice())
                .vaccineType(booking.getVaccineType())
                .administratorId(booking.getAdministratorId())
                .premisesId(booking.getPremisesId())
                .vacant(false)
                .dateTime(booking.getDateTime())
                .patientId(persistedPatient.getId())
                .build();

        var result = testObject.save(updatedBooking).block();
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getPrice()).isEqualTo(booking.getPrice());
        assertThat(result.getVaccineType()).isEqualTo(booking.getVaccineType());
        assertThat(result.getVacant()).isFalse();
        assertThat(result.getAdministratorId()).isNull();
        assertThat(result.getPremisesId()).isEqualTo(persistedPremises.getId());
        assertThat(result.getPatientId()).isEqualTo(persistedPatient.getId());
    }

    @Test
    void delete() {
        var persistedBooking = testObject.save(dbBooking).block();
        assertThat(persistedBooking).isNotNull();
        Integer expected = 1;
        Integer actual = testObject.delete(persistedBooking.getId()).block();
        assertThat(actual).isEqualTo(expected);
    }
}