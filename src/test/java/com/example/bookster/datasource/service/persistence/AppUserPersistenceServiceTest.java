package com.example.bookster.datasource.service.persistence;

import com.example.bookster.datasource.models.DBAppUser;
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
class AppUserPersistenceServiceTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    AppUserPersistenceService testObject;

    private final DBAppUser dbAppUser = DBAppUser.builder()
            .username("test")
            .password("test123")
            .build();

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void save_persist() {

        var result = testObject.save(dbAppUser).block();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUsername()).isEqualTo(dbAppUser.getUsername());
        assertThat(result.getPassword()).isEqualTo(dbAppUser.getPassword());
    }

    @Test
    void save_update() {
        var appUser = testObject.save(dbAppUser).block();

        assert appUser != null;
        var updatedPayload = DBAppUser.builder()
                .id(appUser.getId())
                .password(appUser.getPassword())
                .username("Nisse")
                .build();

        var result = testObject.save(updatedPayload).block();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(appUser.getId());
        assertThat(result.getPassword()).isEqualTo(appUser.getPassword());
        assertThat(result.getUsername()).isEqualTo("Nisse");
    }

    @Test
    void delete() {
        var appUser = testObject.save(dbAppUser).block();
        assertThat(appUser).isNotNull();
        Integer expected = 1;

        Integer actual = testObject.delete(appUser.getId()).block();

        assertThat(actual).isEqualTo(expected);
    }
}