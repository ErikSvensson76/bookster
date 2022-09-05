package com.example.bookster.datasource.service;

import com.example.bookster.FakeObjectGenerator;
import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBContactInfo;
import com.example.bookster.datasource.models.DBPatient;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.stream.Stream;

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

    public DBContactInfo generateDBContactInfo(){
        return generator.randomDBContactInfo();
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
        Flux<DBContactInfo> dbContactInfoFlux = Flux.fromStream(Stream.generate(this::generateDBContactInfo).limit(5))
                .flatMap(template::insert)
                .thenMany(testObject.findAll());

        StepVerifier.create(dbContactInfoFlux)
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void findById() {
        Mono<DBContactInfo> result = Mono.from(template.insert(generateDBContactInfo()))
                .map(DBContactInfo::getId)
                .flatMap(uuid -> testObject.findById(Mono.just(uuid)));

        StepVerifier.create(result)
                .expectNextMatches(dbContactInfo -> dbContactInfo != null && dbContactInfo.getId() != null)
                .verifyComplete();
    }

    @Test
    void findByPatientId() {
        var dbContactInfoMono = Mono.just(generator.randomDBContactInfo())
                .flatMap(template::insert);

        Mono.from(template.insert(generator.randomDBPatient()))
                .zipWith(dbContactInfoMono)
                .flatMap(tuple2 -> {
                    var patient = tuple2.getT1();
                    patient.setContactInfoId(tuple2.getT2().getId());
                    return Mono.from(template.update(patient));
                })
                .map(DBPatient::getId)
                .flatMap(uuid -> testObject.findByPatientId(Mono.just(uuid)))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void update() {
        DBContactInfo dbContactInfo = new DBContactInfo(null, "test@test.com", "070-5255232", null);
        DBAddress dbAddress = generator.randomDBAddress();

        var result = Mono.zip(template.insert(dbAddress), template.insert(dbContactInfo))
                .map(tuple2 -> {
                    DBContactInfo contactInfo = tuple2.getT2();
                    contactInfo.setEmail("test1@test.com");
                    contactInfo.setPhone(null);
                    contactInfo.setAddressId(tuple2.getT1().getId());
                    return contactInfo;
                })
                .flatMap(entity -> testObject.update(Mono.just(entity)));


        StepVerifier.create(result)
            .expectNextMatches(contactInfo ->
                    contactInfo != null &&
                    contactInfo.getEmail().equals("test1@test.com") &&
                    contactInfo.getPhone() == null &&
                    contactInfo.getAddressId() != null
            ).verifyComplete();
    }

    @Test
    void update_change_address() {
        DBContactInfo dbContactInfo = generator.randomDBContactInfo();
        DBAddress initialAddress = generator.randomDBAddress();
        DBAddress newAddress = generator.randomDBAddress();

        Mono.zip(Mono.just(dbContactInfo), template.insert(initialAddress))
                .map(tuple2 -> {
                    var contactInfo = tuple2.getT1();
                    contactInfo.setAddressId(tuple2.getT2().getId());
                    return contactInfo;
                })
                .flatMap(template::insert)
                .zipWith(Mono.from(template.insert(newAddress)))
                .map(tuple2 -> {
                    var contactInfo = (DBContactInfo) tuple2.getT1();
                    contactInfo.setAddressId(tuple2.getT2().getId());
                    return contactInfo;
                })
                .flatMap(contactInfo -> testObject.update(Mono.just(contactInfo)))
                .as(StepVerifier::create)
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void delete_no_address() {
        Mono.from(template.insert(generator.randomDBContactInfo()))
                .map(DBContactInfo::getId)
                .flatMap(uuid -> testObject.delete(Mono.just(uuid)))
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void delete_with_address() {
        DBContactInfo dbContactInfo = new DBContactInfo(null, "test@test.com", "070-5255232", null);
        DBAddress dbAddress = generator.randomDBAddress();

        Mono.zip(template.insert(dbAddress), template.insert(dbContactInfo))
                .map(tuple2 -> {
                    DBContactInfo contactInfo = tuple2.getT2();
                    contactInfo.setEmail("test@test.com");
                    contactInfo.setPhone(null);
                    contactInfo.setAddressId(tuple2.getT1().getId());
                    return contactInfo;
                })
                .flatMap(entity -> testObject.update(Mono.just(entity)))
                .map(DBContactInfo::getId)
                .flatMap(id -> testObject.delete(Mono.just(id)))
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }
}