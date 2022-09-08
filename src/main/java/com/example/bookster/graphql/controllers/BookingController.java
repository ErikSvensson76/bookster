package com.example.bookster.graphql.controllers;

import com.example.bookster.graphql.facade.BookingService;
import com.example.bookster.graphql.models.dto.Booking;
import com.example.bookster.graphql.models.dto.Patient;
import com.example.bookster.graphql.models.dto.Premises;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @BatchMapping(typeName = "Patient", field = "bookings")
    public Mono<Map<Patient, Collection<Booking>>> bookingsPatients(final List<Patient> patients){
        return Flux.fromIterable(patients)
                .flatMap(patient -> bookingService.findAllByPatientId(Mono.just(patient.getId())))
                .collectMultimap(booking -> patients.stream()
                        .filter(patient -> patient.getId().equals(booking.getPatientId()))
                        .findFirst()
                        .orElseThrow());
    }

    @BatchMapping(typeName = "Premises", field = "bookings")
    public Mono<Map<Premises, Collection<Booking>>> bookingsPremises(final List<Premises> premisesList){
        return Flux.fromIterable(premisesList)
                .flatMap(premises -> bookingService.findAllByPremisesId(Mono.just(premises.getId())))
                .collectMultimap(booking -> premisesList.stream()
                        .filter(premises -> premises.getId().equals(booking.getPremisesId()))
                        .findFirst()
                        .orElseThrow()
                );
    }

}
