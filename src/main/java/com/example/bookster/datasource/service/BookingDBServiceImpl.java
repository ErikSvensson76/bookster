package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBBooking;
import com.example.bookster.datasource.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingDBServiceImpl implements BookingDBService {

    private final BookingRepository repository;
    private final PremisesDBService premisesDBService;
    private final PatientDBService patientDBService;

    @Override
    @Transactional
    public Mono<DBBooking> persist(Mono<DBBooking> dbBookingMono, Mono<UUID> premisesIdMono) {
        return Mono.from(premisesDBService.findById(premisesIdMono))
                .zipWith(dbBookingMono)
                .flatMap(tuple2 -> {
                    tuple2.getT2().setPremisesId(tuple2.getT1().getId());
                    return repository.save(tuple2.getT2());
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBBooking> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBBooking> findAllByVacant(Mono<Boolean> vacantMono) {
        return vacantMono.flatMapMany(vacant -> repository.findAllByVacant(Objects.requireNonNullElse(vacant, true)));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBBooking> findAllByPatientId(Mono<UUID> patientIdMono) {
        return patientIdMono.flatMapMany(repository::findByPatientId);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBBooking> findAllByPremisesId(Mono<UUID> premisesIdMono) {
        return premisesIdMono.flatMapMany(repository::findByPremisesId);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBBooking> findAllByCity(Mono<String> cityMono) {
        return cityMono.flatMapMany(repository::findBookingsByCity);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBBooking> findAllByCity(Mono<String> cityMono, Mono<Boolean> available) {
       return cityMono.zipWith(available)
               .flatMapMany(tuple2 -> repository.findBookingsByCity(tuple2.getT1(), tuple2.getT2()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DBBooking> findById(Mono<UUID> uuidMono) {
        return repository.findById(uuidMono);
    }

    @Override
    @Transactional
    public Mono<DBBooking> book(Mono<UUID> bookingIdMono, Mono<UUID> patientIdMono) {
        return Mono.from(repository.findById(bookingIdMono))
                .zipWith(patientDBService.findById(patientIdMono))
                .flatMap(tuple2 -> {
                    tuple2.getT1().setPatientId(tuple2.getT2().getId());
                    tuple2.getT1().setVacant(false);
                    return repository.save(tuple2.getT1());
                });
    }

    @Override
    @Transactional
    public Mono<DBBooking> cancelBooking(Mono<UUID> bookingIdMono) {
        return bookingIdMono.flatMap(repository::findById)
                .map(dbBooking -> {
                    dbBooking.setVacant(true);
                    dbBooking.setPatientId(null);
                    return dbBooking;
                }).flatMap(repository::save);
    }

    @Override
    @Transactional
    public Mono<DBBooking> update(Mono<DBBooking> dbBookingMono) {
        return Mono.zip(dbBookingMono, Mono.from(dbBookingMono.flatMap(dbBooking -> repository.findById(dbBooking.getId()))))
                .map(tuple2 -> {
                    DBBooking updated = tuple2.getT1();
                    DBBooking source = tuple2.getT2();
                    source.setPrice(updated.getPrice());
                    source.setVacant(updated.getVacant());
                    source.setDateTime(updated.getDateTime());
                    source.setAdministratorId(updated.getAdministratorId());
                    source.setVaccineType(updated.getVaccineType());
                    return source;
                }).flatMap(repository::save);
    }

    @Override
    @Transactional
    public Mono<Void> delete(Mono<UUID> uuidMono) {
        return repository.deleteById(uuidMono);
    }
}
