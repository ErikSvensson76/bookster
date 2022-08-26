package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAppRole;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext
class AppRolePersistenceServiceImplTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    AppRolePersistenceServiceImpl testObject;

    DBAppRole dbAppRole = new DBAppRole(null, "APP_USER");

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                        .block();
    }

    @Test
    void save_persist() {
        var persistedResult = testObject.save(dbAppRole).block();

        assertThat(persistedResult).isNotNull();
        assertThat(persistedResult.getId()).isNotNull();
        assertThat(persistedResult.getUserRole()).isEqualTo("APP_USER");

    }

    @Test
    void save_update() {
       var persisted = testObject.save(dbAppRole).block();
       assertThat(persisted).isNotNull();
       assertThat(persisted.getId()).isNotNull();

        String userRole = "APP_ADMIN";
        var updatedPayload = new DBAppRole(persisted.getId(), userRole);

        var result = testObject.save(updatedPayload).block();
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(persisted.getId());
        assertThat(result.getUserRole()).isEqualTo(userRole);
    }

    @Test
    void save_update_throws_exception(){
        var badAppRole = new DBAppRole(UUID.randomUUID(), "APP_ADMIN");

        assertThrows(
                RuntimeException.class,
                () -> testObject.save(badAppRole).block()
        );

    }

    @Test
    void delete() {
        var persisted = testObject.save(dbAppRole).block();
        Integer expected = 1;

        assert persisted != null;
        Integer actual = testObject.delete(persisted.getId()).block();

        assertThat(actual).isEqualTo(expected);
    }
}