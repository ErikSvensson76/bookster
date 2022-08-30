package com.example.bookster.datasource.service.persistence;

import com.example.bookster.datasource.models.DBAppUser;
import com.example.bookster.datasource.models.DBContactInfo;
import com.example.bookster.datasource.models.DBPatient;
import com.example.bookster.datasource.service.persistence.persistence.AppUserPersistenceService;
import com.example.bookster.datasource.service.persistence.persistence.ContactInfoPersistenceService;
import com.example.bookster.datasource.service.persistence.persistence.PatientPersistenceService;
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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
class PatientPersistenceServiceTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    ContactInfoPersistenceService contactInfoPersistenceService;

    @Autowired
    AppUserPersistenceService appUserPersistenceService;

    @Autowired
    PatientPersistenceService testObject;

    DBPatient patient = DBPatient.builder()
            .pnr("123")
            .birthDate(LocalDate.now().minusYears(30))
            .lastName("Testsson")
            .firstName("Test")
            .build();

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void testSave() {
        var result = testObject.save(patient).block();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result.getPnr()).isEqualTo(patient.getPnr());
        Assertions.assertThat(result.getBirthDate()).isEqualTo(patient.getBirthDate());
        Assertions.assertThat(result.getFirstName()).isEqualTo(patient.getFirstName());
        Assertions.assertThat(result.getLastName()).isEqualTo(patient.getLastName());
        Assertions.assertThat(result.getContactInfoId()).isNull();
        Assertions.assertThat(result.getAppUserId()).isNull();
    }

    @Test
    void testUpdate() {
        var appUser = DBAppUser.builder().username("test").password("test").build();
        var contactInfo = DBContactInfo.builder().email("test@test.com").phone("123").build();

        var persistedPatient = testObject.save(patient).block();
        appUser = appUserPersistenceService.save(appUser).block();
        contactInfo = contactInfoPersistenceService.save(contactInfo).block();

        Assertions.assertThat(persistedPatient).isNotNull();
        assertThat(appUser).isNotNull();
        assertThat(contactInfo).isNotNull();

        var updatedPayload = DBPatient.builder()
                .id(persistedPatient.getId())
                .birthDate(persistedPatient.getBirthDate())
                .firstName(persistedPatient.getFirstName())
                .lastName(persistedPatient.getLastName())
                .pnr(persistedPatient.getPnr())
                .contactInfoId(contactInfo.getId())
                .appUserId(appUser.getId())
                .build();

        var result = testObject.save(updatedPayload).block();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getAppUserId()).isEqualTo(appUser.getId());
        Assertions.assertThat(result.getContactInfoId()).isEqualTo(contactInfo.getId());
    }

    @Test
    void delete() {
        var result = testObject.save(patient).block();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isNotNull();

        var rows = testObject.delete(result.getId()).block();
        Assertions.assertThat(rows).isEqualTo(1);
    }
}