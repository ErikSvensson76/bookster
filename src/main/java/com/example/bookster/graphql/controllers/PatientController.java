package com.example.bookster.graphql.controllers;

import com.example.bookster.graphql.facade.PatientService;
import com.example.bookster.graphql.models.dto.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @QueryMapping
    public Mono<Patient> patientById(@Argument(name = "id") String id){
        return patientService.findById(Mono.just(id));
    }



}
