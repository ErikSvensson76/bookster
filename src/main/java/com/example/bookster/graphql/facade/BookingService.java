package com.example.bookster.graphql.facade;

import com.example.bookster.graphql.models.dto.Booking;
import com.example.bookster.graphql.models.input.BookingInput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingService {

    Mono<Booking> persist(Mono<BookingInput> bookingInputMono);

    Flux<Booking> findAll();

    Flux<Booking> findAllVacant();

    Flux<Booking> findAllByPatientId(Mono<String> patientId);

    Flux<Booking> findAllByPremisesId(Mono<String> premisesId);

    Flux<Booking> findAllByCity(Mono<String> city);

    Flux<Booking> findAllByCity(Mono<String> city, Mono<Boolean> available);

    Mono<Booking> findById(Mono<String> idMono);

    Mono<Booking> book(Mono<String> bookingIdMono, Mono<String> patientIdMono);

    Mono<Booking> cancelBooking(Mono<String> bookingId);

    Mono<Booking> update(Mono<String> idMono, Mono<BookingInput> bookingInputMono);

    Mono<Void> delete(Mono<String> idMono);

}
