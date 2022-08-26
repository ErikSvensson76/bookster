package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAppUser;
import com.example.bookster.datasource.models.DBContactInfo;
import com.example.bookster.datasource.models.DBPatient;
import io.r2dbc.spi.ConnectionFactory;
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
class PatientPersistenceServiceImplTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    ContactInfoPersistenceService contactInfoPersistenceService;

    @Autowired
    AppUserPersistenceService appUserPersistenceService;

    @Autowired
    PatientPersistenceServiceImpl testObject;

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

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getPnr()).isEqualTo(patient.getPnr());
        assertThat(result.getBirthDate()).isEqualTo(patient.getBirthDate());
        assertThat(result.getFirstName()).isEqualTo(patient.getFirstName());
        assertThat(result.getLastName()).isEqualTo(patient.getLastName());
        assertThat(result.getContactInfoId()).isNull();
        assertThat(result.getAppUserId()).isNull();
    }

    @Test
    void testUpdate() {
        var appUser = DBAppUser.builder().username("test").password("test").build();
        var contactInfo = DBContactInfo.builder().email("test@test.com").phone("123").build();

        var persistedPatient = testObject.save(patient).block();
        appUser = appUserPersistenceService.save(appUser).block();
        contactInfo = contactInfoPersistenceService.save(contactInfo).block();

        assertThat(persistedPatient).isNotNull();
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

        assertThat(result).isNotNull();
        assertThat(result.getAppUserId()).isEqualTo(appUser.getId());
        assertThat(result.getContactInfoId()).isEqualTo(contactInfo.getId());
    }

    @Test
    void delete() {
        var result = testObject.save(patient).block();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();

        var rows = testObject.delete(result.getId()).block();
        assertThat(rows).isEqualTo(1);
    }
}