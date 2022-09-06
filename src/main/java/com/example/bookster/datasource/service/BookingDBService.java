package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBBooking;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BookingDBService {

    Mono<DBBooking> persist(Mono<DBBooking> dbBookingMono, Mono<UUID> premisesIdMono);

    Flux<DBBooking> findAll();

    Flux<DBBooking> findAllByVacant(Mono<Boolean> vacantMono);

    Flux<DBBooking> findAllByPatientId(Mono<UUID> patientIdMono);

    Flux<DBBooking> findAllByPremisesId(Mono<UUID> premisesIdMono);

    Flux<DBBooking> findAllByCity(Mono<String> cityMono);

    Flux<DBBooking> findAllByCity(Mono<String> cityMono, Mono<Boolean> available);

    Mono<DBBooking> findById(Mono<UUID> uuidMono);

    Mono<DBBooking> book(Mono<UUID> bookingIdMono, Mono<UUID> patientIdMono);

    Mono<DBBooking> cancelBooking(Mono<UUID> bookingIdMono);

    Mono<DBBooking> update(Mono<DBBooking> dbBookingMono);

    Mono<Void> delete(Mono<UUID> uuidMono);

}
