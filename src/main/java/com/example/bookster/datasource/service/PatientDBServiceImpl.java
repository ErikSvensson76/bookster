package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBAppUser;
import com.example.bookster.datasource.models.DBContactInfo;
import com.example.bookster.datasource.models.DBPatient;
import com.example.bookster.datasource.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientDBServiceImpl implements PatientDBService {

    private final PatientRepository repository;
    private final ContactInfoDBService contactInfoDBService;
    private final AppUserDBService appUserDBService;

    @Override
    @Transactional
    public Mono<DBPatient> persist(Mono<DBPatient> dbPatientMono,
                                   Mono<DBContactInfo> dbContactInfoMono,
                                   Mono<DBAddress> dbAddressMono,
                                   Mono<DBAppUser> dbAppUserMono) {

        Mono<DBContactInfo> persistedDbc = Mono.zip(dbContactInfoMono, dbAddressMono)
                .flatMap(contactAndAddress -> contactInfoDBService.persist(
                        Mono.just(contactAndAddress.getT1()),
                        Mono.just(contactAndAddress.getT2())
                ));

        Mono<DBAppUser> persistedAppUser = dbAppUserMono
                .flatMap(dbAppUser -> appUserDBService.persist(Mono.just(dbAppUser), Mono.just("ROLE_APP_USER")));

        return Mono.zip(dbPatientMono, persistedDbc, persistedAppUser)
                .map(tuple3 -> {
                    DBAppUser dbAppUser = tuple3.getT3();
                    DBContactInfo dbContactInfo = tuple3.getT2();
                    DBPatient dbPatient = tuple3.getT1();
                    dbPatient.setAppUserId(dbAppUser.getId());
                    dbPatient.setContactInfoId(dbContactInfo.getId());
                    return dbPatient;
                })
                .flatMap(repository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBPatient> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DBPatient> findByBookingId(Mono<UUID> bookingIdMono) {
        return bookingIdMono.flatMap(repository::findPatientByBookingId);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBPatient> findByCity(Mono<String> city) {
        return city.flatMapMany(repository::findPatientsByCity);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DBPatient> findByUsername(Mono<String> username) {
        return username.flatMap(repository::findByUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DBPatient> findById(Mono<UUID> uuidMono) {
        return repository.findById(uuidMono);
    }

    @Override
    @Transactional
    public Mono<DBPatient> update(Mono<DBPatient> dbPatientMono) {
        return Mono.zip(dbPatientMono, Mono.from(dbPatientMono).flatMap(dbPatient -> repository.findById(dbPatient.getId())))
                .flatMap(tuple2 -> {
                    DBPatient updated = tuple2.getT1();
                    DBPatient source = tuple2.getT2();
                    source.setPnr(updated.getPnr());
                    source.setBirthDate(updated.getBirthDate());
                    source.setFirstName(updated.getFirstName());
                    source.setLastName(updated.getLastName());
                    return repository.save(source);
                });
    }

    @Override
    @Transactional
    public Mono<Void> delete(Mono<UUID> uuidMono) {
        return null;
    }
}
