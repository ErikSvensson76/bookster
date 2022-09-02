package com.example.bookster;

import com.example.bookster.datasource.models.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FakeObjectGenerator {

    private static final FakeObjectGenerator INSTANCE = new FakeObjectGenerator(new Faker(new Locale("sv","SE")));

    public static FakeObjectGenerator getInstance(){
        return INSTANCE;
    }

    private final Faker faker;

    public DBAppUser randomDBAppUser(){
        return new DBAppUser(null, faker.name().username(), faker.internet().password());
    }

    public DBPatient randomDBPatient(){
        LocalDate birthDate = faker.date().birthday().toLocalDateTime().toLocalDate();

        String pnr = birthDate.format(DateTimeFormatter.BASIC_ISO_DATE) + Stream.generate(() -> ThreadLocalRandom.current().nextInt(1, 10))
                .limit(4)
                .map(String::valueOf)
                .collect(Collectors.joining());

        return new DBPatient(null, birthDate, faker.name().firstName(), faker.name().lastName(), pnr, null, null);
    }


    public DBAddress randomDBAddress() {
        return DBAddress.builder()
                .street(faker.address().streetAddress())
                .zipCode(faker.address().zipCode())
                .city(faker.address().city())
                .build();
    }

    public DBPremises randomDBPremises(){
        return DBPremises.builder()
                .premisesName("Vårdcentral " + faker.company().name())
                .build();
    }

    public DBContactInfo randomDBContactInfo() {
        return DBContactInfo.builder()
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().cellPhone())
                .build();
    }
}
