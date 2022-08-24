package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAddress;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
class AddressPersistenceServiceImplTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    AddressPersistenceServiceImpl testObject;
    private final DBAddress address = DBAddress.builder()
            .city("Växjö")
                .zipCode("352 63")
                .street("Friskhetsvägen 2")
                .build();

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void save_persist() {
        var result = testObject.save(address).block();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getCity()).isEqualTo("Växjö");
        assertThat(result.getZipCode()).isEqualTo("352 63");
        assertThat(result.getStreet()).isEqualTo("Friskhetsvägen 2");
    }

    @Test
    void save_update() {
        var persisted = testObject.save(address).block();

        assert persisted != null;
        var payload = DBAddress.builder()
                .id(persisted.getId())
                .zipCode("356 32")
                .city(persisted.getCity())
                .street(persisted.getStreet())
                .build();

        var result = testObject.save(payload).block();

        assert result != null;
        assertThat(result.getId()).isEqualTo(persisted.getId());
        assertThat(result.getZipCode()).isEqualTo("356 32");
    }

    @Test
    void delete() {
        var persisted = testObject.save(address).block();
        Integer expected = 1;

        assert persisted != null;
        Integer actual = testObject.delete(persisted.getId()).block();
        assertThat(actual).isEqualTo(expected);
    }
}