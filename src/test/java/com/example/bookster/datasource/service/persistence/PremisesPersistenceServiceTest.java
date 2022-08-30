package com.example.bookster.datasource.service.persistence;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBPremises;
import com.example.bookster.datasource.service.persistence.persistence.AddressPersistenceService;
import com.example.bookster.datasource.service.persistence.persistence.PremisesPersistenceService;
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
class PremisesPersistenceServiceTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    PremisesPersistenceService testObject;

    @Autowired
    AddressPersistenceService addressPersistenceService;

    DBPremises dbPremises = DBPremises.builder()
            .premisesName("Vårdcentralen Test")
            .build();

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void save_persist() {
        var result = testObject.save(dbPremises).block();
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getPremisesName()).isEqualTo(dbPremises.getPremisesName());
        assertThat(result.getPremisesAddressId()).isNull();
    }

    @Test
    void save_update() {
        var persistedPremises = testObject.save(dbPremises).block();
        assertThat(persistedPremises).isNotNull();

        var address = addressPersistenceService.save(DBAddress.builder().city("Växjö").street("Häslogatan 1").zipCode("134 45").build())
                .block();
        assertThat(address).isNotNull();


        String premisesName = "Vårdcentralen Hälsan";
        UUID addressId = address.getId();
        var updatedPremises = DBPremises.builder()
                .id(persistedPremises.getId())
                .premisesName(premisesName)
                .premisesAddressId(addressId)
                .build();

        var result = testObject.save(updatedPremises).block();
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(updatedPremises.getId());
        assertThat(result.getPremisesName()).isEqualTo(premisesName);
        assertThat(result.getPremisesAddressId()).isEqualTo(addressId);
    }

    @Test
    void delete() {
        var premises = testObject.save(this.dbPremises).block();
        assertThat(premises).isNotNull();
        Integer expected = 1;
        Integer actual = testObject.delete(premises.getId()).block();
        assertThat(actual).isEqualTo(expected);
    }
}