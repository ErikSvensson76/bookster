package com.example.bookster.datasource.repository;

import com.example.bookster.datasource.models.DBAppRole;
import com.example.bookster.datasource.models.DBAppUser;
import com.example.bookster.datasource.models.DBRoleAppUser;
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
class AppRoleRepositoryTest {

    public static final String ROLE_APP_USER = "ROLE_APP_USER";
    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    AppRoleRepository testObject;

    DBAppRole dbAppRole = new DBAppRole(null, ROLE_APP_USER);

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void findByAppUserId() {
        var appUser = template.insert(DBAppUser.class).using(
                DBAppUser.builder().username("uffe").password("1234").build()
        ).block();
        assertThat(appUser).isNotNull();
        var appRole = testObject.save(dbAppRole).block();
        assertThat(appRole).isNotNull();

        var roleAppUser = template.insert(DBRoleAppUser.class)
                .using(new DBRoleAppUser(appUser.getId(), appRole.getId())).block();
        assertThat(roleAppUser).isNotNull();

        StepVerifier.create(testObject.findByAppUserId(appUser.getId()))
                .expectNextMatches(result -> result.getId().equals(appRole.getId()))
                .verifyComplete();
    }

    @Test
    void findByUserRole() {
        var appRole = testObject.save(dbAppRole).block();

        testObject.findByUserRole(dbAppRole.getUserRole())
                .subscribe(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getUserRole()).isEqualTo(ROLE_APP_USER);
                });
    }
}