package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBContactInfo;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
class ContactInfoPersistenceServiceTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;
    @Autowired
    AddressPersistenceService addressService;
    @Autowired
    ContactInfoPersistenceService testObject;
    @Autowired
    ConnectionFactory connectionFactory;

    DBContactInfo contactInfo = DBContactInfo.builder()
            .email("test@gmail.com")
            .phone("0701234567")
            .build();
    private DBAddress dbAddress = new DBAddress(null, "TestVille", "Test 1", "123 45");


    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void save_persist() {
        var result = testObject.save(contactInfo).block();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getPhone()).isEqualTo(contactInfo.getPhone());
        assertThat(result.getEmail()).isEqualTo(contactInfo.getEmail());
        assertThat(result.getAddressId()).isNull();
    }

    @Test
    void save_update() {
        var toUpdate = testObject.save(contactInfo).block();
        assertThat(toUpdate).isNotNull();

        var address = addressService
                .save(dbAddress).block();
        assertThat(address).isNotNull();

        String phone = "0702345678";
        String email = "test2@gmail.com";
        UUID addressId = address.getId();

        var updatedObject = DBContactInfo.builder()
                .id(toUpdate.getId())
                .phone(phone)
                .email(email)
                .addressId(addressId)
                .build();

        var result = testObject.save(updatedObject).block();
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(toUpdate.getId());
        assertThat(result.getPhone()).isEqualTo(phone);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getAddressId()).isEqualTo(addressId);
    }

    @Test
    void delete() {
        var toDelete = testObject.save(contactInfo).block();
        assertThat(toDelete).isNotNull();
        Integer expected = 1;
        Integer actual = testObject.delete(toDelete.getId()).block();
        assertThat(actual).isEqualTo(expected);
    }
}