package com.example.bookster.datasource.service.persistence;

import com.example.bookster.datasource.models.DBAppRole;
import com.example.bookster.datasource.models.DBAppUser;
import com.example.bookster.datasource.models.DBRoleAppUser;
import com.example.bookster.datasource.service.persistence.persistence.AppRolePersistenceService;
import com.example.bookster.datasource.service.persistence.persistence.AppUserPersistenceService;
import com.example.bookster.datasource.service.persistence.persistence.RoleAppUserPersistenceService;
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
class RoleAppUserPersistenceServiceTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    RoleAppUserPersistenceService testObject;

    @Autowired
    AppRolePersistenceService appRolePersistenceService;

    @Autowired
    AppUserPersistenceService appUserPersistenceService;

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void createAppRoleAssignment() {
        var appUser = DBAppUser.builder().username("test").password("password").build();
        var appRole = new DBAppRole(null, "ROLE_APP_USER");

        appRole = appRolePersistenceService.save(appRole).block();
        appUser = appUserPersistenceService.save(appUser).block();
        assertThat(appRole).isNotNull();
        assertThat(appUser).isNotNull();

        var roleAppUser = DBRoleAppUser.builder().appRoleId(appRole.getId()).appUserId(appUser.getId()).build();

        Integer expected = 1;

        Integer actual = testObject.createAppRoleAssignment(roleAppUser).block();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteAppRoleAssignment() {
        var appUser = DBAppUser.builder().username("test").password("password").build();
        var appRole = new DBAppRole(null, "ROLE_APP_USER");

        appRole = appRolePersistenceService.save(appRole).block();
        appUser = appUserPersistenceService.save(appUser).block();
        assertThat(appRole).isNotNull();
        assertThat(appUser).isNotNull();

        var roleAppUser = DBRoleAppUser.builder().appRoleId(appRole.getId()).appUserId(appUser.getId()).build();
        testObject.createAppRoleAssignment(roleAppUser).block();

        Integer expected = 1;
        Integer actual = testObject.deleteAppRoleAssignment(roleAppUser).block();

        assertThat(actual).isEqualTo(expected);

    }
}