package com.example.bookster.datasource.service.facade;

import com.example.bookster.datasource.models.DBAppRole;
import com.example.bookster.graphql.models.input.AppRoleInput;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AppRoleServiceImplTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    AppRoleServiceImpl testObject;

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void save_persist() {
        AppRoleInput appRoleInput = new AppRoleInput(null, "ROLE_APP_USER");
        var result = testObject.save(Mono.just(appRoleInput)).block();
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserRole()).isEqualTo(appRoleInput.userRole());
    }

    @Test
    void save_update() {
        DBAppRole appRole = new DBAppRole(null, "ROLE_APP_ADMIN");
        appRole = template.insert(appRole).block();
        assertThat(appRole).isNotNull();

        AppRoleInput appRoleInput = new AppRoleInput(appRole.getId().toString(), "ROLE_APP_USER");
        var result = testObject.save(Mono.just(appRoleInput)).block();
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(appRole.getId().toString());
        assertThat(result.getUserRole()).isEqualTo("ROLE_APP_USER");
    }

    @Test
    void findById() {
    }

    @Test
    void findAll() {
    }

    @Test
    void delete() {
    }

    @Test
    void findByAppUserId() {
    }

    @Test
    void findByUserRole() {
    }
}