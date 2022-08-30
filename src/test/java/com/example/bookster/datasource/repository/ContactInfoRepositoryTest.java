package com.example.bookster.datasource.repository;

import com.example.bookster.datasource.models.DBContactInfo;
import com.example.bookster.datasource.models.DBPatient;
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
class ContactInfoRepositoryTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    ContactInfoRepository testObject;

    DBContactInfo contactInfo = DBContactInfo.builder()
            .email("test@test.com")
            .phone("0701234567")
            .build();

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
        contactInfo = template.insert(DBContactInfo.class).using(contactInfo).block();
    }

    @Test
    void findByPatientId() {
        assertThat(contactInfo).isNotNull();
        DBPatient patient = DBPatient.builder().contactInfoId(contactInfo.getId()).build();
        patient = template.insert(DBPatient.class).using(patient).block();
        assertThat(patient).isNotNull();

        StepVerifier.create(testObject.findByPatientId(patient.getId()))
                .expectNextMatches(ci -> ci.getId().equals(contactInfo.getId()))
                .verifyComplete();
    }
}