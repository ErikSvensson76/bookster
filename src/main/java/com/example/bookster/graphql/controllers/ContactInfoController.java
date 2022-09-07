package com.example.bookster.graphql.controllers;

import com.example.bookster.graphql.facade.ContactInfoService;
import com.example.bookster.graphql.models.dto.ContactInfo;
import com.example.bookster.graphql.models.dto.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ContactInfoController {

    private final ContactInfoService contactInfoService;

    @SchemaMapping
    Mono<ContactInfo> contactInfo(Mono<Patient> patient){
        return patient.map(Patient::getId)
                .flatMap(id -> contactInfoService.findByPatientId(Mono.just(id)));
    }

    @BatchMapping(field = "contactInfo", typeName = "ContactInfo")
    Mono<Map<Patient, ContactInfo>> contactInfo(List<Patient> patients){
        return Flux.fromIterable(patients)
                .flatMap(patient -> contactInfoService.findByPatientId(Mono.just(patient.getContactInfoId().toString())))
                .collectMap(contactInfo -> patients.stream()
                        .filter(patient -> patient.getContactInfoId().equals(contactInfo.getId()))
                        .findFirst()
                        .orElseThrow()
                );
    }
}
