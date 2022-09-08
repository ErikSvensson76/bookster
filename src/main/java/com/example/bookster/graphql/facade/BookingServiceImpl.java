package com.example.bookster.graphql.facade;

import com.example.bookster.datasource.models.DBBooking;
import com.example.bookster.datasource.service.BookingDBService;
import com.example.bookster.datasource.service.mapping.MappingService;
import com.example.bookster.graphql.models.dto.Booking;
import com.example.bookster.graphql.models.input.BookingInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingDBService bookingDBService;
    private final MappingService mappingService;

    @Override
    public Mono<Booking> persist(Mono<BookingInput> bookingInputMono) {
       return bookingInputMono.flatMap(bookingInput -> {
           DBBooking dbBooking = mappingService.convert(bookingInput);
           UUID premisesId = dbBooking.getPremisesId();
           return bookingDBService.persist(
                   Mono.just(dbBooking),
                   Mono.just(premisesId)
           );
       }).map(mappingService::convert);
    }

    @Override
    public Flux<Booking> findAll() {
        return bookingDBService.findAll().map(mappingService::convert);
    }

    @Override
    public Flux<Booking> findAllVacant() {
        return bookingDBService.findAllByVacant(Mono.just(true))
                .map(mappingService::convert);
    }

    @Override
    public Flux<Booking> findAllByPatientId(Mono<String> patientId) {
        return bookingDBService.findAllByPatientId(patientId.map(mappingService::convert))
                .map(mappingService::convert);
    }

    @Override
    public Flux<Booking> findAllByPremisesId(Mono<String> premisesId) {
        return bookingDBService.findAllByPremisesId(premisesId.map(mappingService::convert))
                .map(mappingService::convert);
    }

    @Override
    public Flux<Booking> findAllByCity(Mono<String> city) {
        return bookingDBService.findAllByCity(city)
                .map(mappingService::convert);
    }

    @Override
    public Flux<Booking> findAllByCity(Mono<String> city, Mono<Boolean> available) {
        return bookingDBService.findAllByCity(city, available)
                .map(mappingService::convert);
    }

    @Override
    public Mono<Booking> findById(Mono<String> idMono) {
        return bookingDBService.findById(idMono.map(mappingService::convert))
                .map(mappingService::convert);
    }

    @Override
    public Mono<Booking> book(Mono<String> bookingIdMono, Mono<String> patientIdMono) {
        return bookingDBService.book(
                bookingIdMono.map(mappingService::convert),
                patientIdMono.map(mappingService::convert)
        ).map(mappingService::convert);
    }

    @Override
    public Mono<Booking> cancelBooking(Mono<String> bookingId) {
        return bookingDBService.cancelBooking(bookingId.map(mappingService::convert))
                .map(mappingService::convert);
    }

    @Override
    public Mono<Booking> update(Mono<String> idMono, Mono<BookingInput> bookingInputMono) {
        return bookingInputMono.map(mappingService::convert)
                .zipWith(idMono.map(mappingService::convert))
                .map(tuple2 -> {
                    tuple2.getT1().setId(tuple2.getT2());
                    return tuple2.getT1();
                }).flatMap(dbBooking -> bookingDBService.update(Mono.just(dbBooking)))
                .map(mappingService::convert);
    }

    @Override
    public Mono<Void> delete(Mono<String> idMono) {
        return bookingDBService.delete(idMono.map(mappingService::convert));
    }
}
