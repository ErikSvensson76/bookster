package com.example.bookster.graphql.facade;

import com.example.bookster.graphql.models.dto.Patient;
import com.example.bookster.graphql.models.input.PatientInput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PatientService {

    Mono<Patient> persist(Mono<PatientInput> patientMono);

    Flux<Patient> findAll();

    Mono<Patient> findByBookingId(Mono<String> bookingId);

    Flux<Patient> findByCity(Mono<String> city);

    Mono<Patient> findByUsername(Mono<String> username);

    Mono<Patient> findById(Mono<String> id);

    Mono<Patient> update(Mono<PatientInput> patientMono);

    Mono<Void> delete(Mono<String> id);

}
