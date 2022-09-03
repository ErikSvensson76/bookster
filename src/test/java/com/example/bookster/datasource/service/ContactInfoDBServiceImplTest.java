package com.example.bookster.datasource.service;

import com.example.bookster.FakeObjectGenerator;
import com.example.bookster.datasource.models.DBContactInfo;
import com.example.bookster.datasource.repository.ContactInfoRepository;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@DirtiesContext
class ContactInfoDBServiceImplTest {

    @Value("classpath:/sql/testdb.sql")
    Resource resource;

    @Autowired
    ConnectionFactory connectionFactory;

    final FakeObjectGenerator generator = FakeObjectGenerator.getInstance();

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    ContactInfoRepository contactInfoRepository;

    @Autowired
    ContactInfoDBServiceImpl testObject;

    @BeforeEach
    void setUp() {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> ScriptUtils.executeSqlScript(connection, resource).then(Mono.from(connection.close())))
                .block();
    }

    public Mono<DBContactInfo> generateDBContactInfo(){
        return Mono.just(generator.randomDBContactInfo()).flatMap(template::insert);
    }

    @Test
    void persist() {
        String phone = "070-1234567";
        String email = "test@test.com";
        var result = Mono.just(DBContactInfo.builder().phone(phone).email(email).build())
                .flatMap(ci -> testObject.persist(Mono.just(ci), Mono.just(generator.randomDBAddress())));


        StepVerifier.create(result)
                .expectNextMatches(ci -> ci != null && ci.getId() != null && ci.getPhone().equals(phone) && ci.getEmail().equals(email) && ci.getAddressId() != null)
                .verifyComplete();
    }

    @Test
    void persist_with_same_address(){
        final var dbAddress = generator.randomDBAddress();

        var result = Mono.just(dbAddress)
                .flatMap(address -> testObject.persist(Mono.just(generator.randomDBContactInfo()), Mono.just(dbAddress)))
                .then(Mono.just(dbAddress))
                .flatMap(address -> testObject.persist(Mono.just(generator.randomDBContactInfo()), Mono.just(dbAddress)))
                .flatMap(dbContactInfo -> contactInfoRepository.countAllByAddressId(dbContactInfo.getAddressId()));

        StepVerifier.create(result)
                .expectNextMatches(integer -> integer.equals(2))
                .verifyComplete();
    }

    @Test
    void findAll() {
    }

    @Test
    void findById() {
    }

    @Test
    void findByPatientId() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}