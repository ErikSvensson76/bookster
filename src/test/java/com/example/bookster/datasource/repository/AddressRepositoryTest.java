package com.example.bookster.datasource.repository;

import com.example.bookster.datasource.models.DBAddress;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.core.io.Resource;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
class AddressRepositoryTest{

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    AddressRepository testObject;

    @Autowired
    AppUserRepository appUserRepository;

    DBAddress dbAddress = DBAddress.builder().street("Test street 1").zipCode("123 45").city("Testville").build();

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void findByCityAndStreetAndZipCode() {

        var persisted = testObject.save(dbAddress).block();
        assertThat(persisted).isNotNull();

        StepVerifier.create(testObject.findByCityAndStreetAndZipCode("testville", "test street 1", "123 45"))
                .expectNextMatches(address -> address.getId().equals(persisted.getId()))
                .verifyComplete();
    }

    @Test
    void countByCityStreetAndZipCode() {
        var persisted = testObject.save(dbAddress).block();
        assertThat(persisted).isNotNull();

        StepVerifier.create(testObject.countByCityStreetAndZipCode("testville", "test street 1", "123 45"))
                .expectNextMatches(number -> number == 1)
                .verifyComplete();
    }
}