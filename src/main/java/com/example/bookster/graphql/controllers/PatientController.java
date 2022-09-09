package com.example.bookster.graphql.controllers;

import com.example.bookster.graphql.facade.PatientService;
import com.example.bookster.graphql.models.dto.Booking;
import com.example.bookster.graphql.models.dto.InfoMessage;
import com.example.bookster.graphql.models.dto.Patient;
import com.example.bookster.graphql.models.input.PatientInput;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
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

    @QueryMapping
    public Mono<Patient> patientByBookingId(@Argument(name = "bookingId") String bookingId){
        return patientService.findByBookingId(Mono.just(bookingId));
    }

    @QueryMapping
    public Mono<Patient> patientByUsername(@Argument(name = "username") String username){
        return patientService.findByUsername(Mono.just(username));
    }

    @QueryMapping
    public Flux<Patient> patientsAll(){
        return patientService.findAll();
    }

    @QueryMapping
    public Flux<Patient> patientsByCity(@Argument(name = "city") String city){
        return patientService.findByCity(Mono.just(city));
    }

    @MutationMapping
    public Mono<Patient> createPatient(@Argument(name = "patientInput")PatientInput patientInput){
        return patientService.persist(Mono.just(patientInput));
    }

    @MutationMapping
    public Mono<Patient> updatePatient(
            @Argument(name = "patientInput") PatientInput patientInput){
        return patientService.update(Mono.just(patientInput));
    }

    @MutationMapping
    public Mono<InfoMessage> deletePatient(@Argument(name = "id") String id){
        return patientService.delete(Mono.just(id))
                .then(Mono.just(id)
                        .map(stringId -> new InfoMessage("Delete operation completed for Patient with id " + stringId)));
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
