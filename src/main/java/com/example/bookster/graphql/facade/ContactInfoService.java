package com.example.bookster.graphql.facade;

import com.example.bookster.graphql.models.dto.ContactInfo;
import com.example.bookster.graphql.models.input.ContactInfoInput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContactInfoService {

    Mono<ContactInfo> persist(Mono<ContactInfoInput> contactInfoInputMono);

    Flux<ContactInfo> findAll();

    Mono<ContactInfo> findById(Mono<String> idMono);

    Mono<ContactInfo> findByPatientId(Mono<String> patientIdMono);

    Mono<ContactInfo> update(Mono<String> idMono, Mono<ContactInfoInput> contactInfoInputMono);

    Mono<Void> delete(Mono<String> stringMono);

}
