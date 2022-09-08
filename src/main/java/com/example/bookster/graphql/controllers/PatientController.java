package com.example.bookster.graphql.controllers;

import com.example.bookster.graphql.facade.PatientService;
import com.example.bookster.graphql.models.dto.Booking;
import com.example.bookster.graphql.models.dto.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @QueryMapping
    public Mono<Patient> patientById(@Argument(name = "id") String id){
        return patientService.findById(Mono.just(id));
    }

    @BatchMapping(typeName = "Booking", field = "patient")
    public Mono<Map<Booking, Patient>> patient(List<Booking> bookings){
        return Flux.fromIterable(bookings)
                .flatMap(booking -> patientService.findByBookingId(Mono.just(booking.getPatientId())))
                .collectMap(patient -> bookings.stream()
                        .filter(booking -> booking.getPatientId().equals(patient.getId()))
                        .findFirst()
                        .orElseThrow()
                );
    }

}
