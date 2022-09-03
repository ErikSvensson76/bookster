package com.example.bookster.datasource.service;

import com.example.bookster.FakeObjectGenerator;
import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBContactInfo;
import com.example.bookster.datasource.models.DBPremises;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

@SpringBootTest
@DirtiesContext
class AddressDBServiceImplTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    final FakeObjectGenerator generator = FakeObjectGenerator.getInstance();

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    AddressDBServiceImpl testObject;

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    @Test
    void save() {
        DBAddress dbAddress = generator.randomDBAddress();
        var result = Mono.just(dbAddress)
                .flatMap(address -> testObject.save(Mono.just(address)));

        StepVerifier.create(result)
                .expectNextMatches(obj -> obj != null && obj.getId() != null)
                .verifyComplete();
    }

    @Test
    void findById() {
        DBAddress dbAddress = generator.randomDBAddress();
        var result = Mono.from(template.insert(dbAddress))
                .map(DBAddress::getId)
                .flatMap(uuid -> testObject.findById(Mono.just(uuid)));

        StepVerifier.create(result)
                .expectNextMatches(obj -> obj != null && obj.getId() != null)
                .verifyComplete();
    }

    @Test
    void findByPremisesId() {
        var premisesMono = Mono.from(template.insert(generator.randomDBPremises()))
                .zipWith(Mono.from(template.insert(generator.randomDBAddress())))
                .map(tuple2 -> {
                    var premises = tuple2.getT1();
                    premises.setPremisesAddressId(tuple2.getT2().getId());
                    return premises;
                })
                .flatMap(template::update).cast(DBPremises.class);

        StepVerifier.create(premisesMono)
                .expectNextMatches(dbPremises -> dbPremises != null && dbPremises.getPremisesAddressId() != null)
                .verifyComplete();
    }

    @Test
    void findByContactInfoId() {
        var contactInfoMono = Mono.from(template.insert(generator.randomDBContactInfo()))
                .zipWith(Mono.from(template.insert(generator.randomDBAddress())))
                .map(tuple2 -> {
                    var contactInfo = tuple2.getT1();
                    contactInfo.setAddressId(tuple2.getT2().getId());
                    return contactInfo;
                })
                .flatMap(template::update).cast(DBContactInfo.class);

        StepVerifier.create(contactInfoMono)
                .expectNextMatches(dbContactInfo -> dbContactInfo != null && dbContactInfo.getAddressId() != null)
                .verifyComplete();
    }

    @Test
    void findAll() {
        Flux<DBAddress> result = Flux.fromStream(Stream.generate(generator::randomDBAddress).limit(5))
                .flatMap(template::insert)
                .thenMany(testObject.findAll());

        StepVerifier.create(result)
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void delete_should_delete() {
        var result = Mono.just(generator.randomDBAddress())
                .flatMap(template::insert)
                .flatMap(address -> testObject.delete(Mono.just(address.getId())));

        StepVerifier.create(result)
                .expectSubscription()
                .verifyComplete();






    }
}