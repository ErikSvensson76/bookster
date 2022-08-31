package com.example.bookster.datasource.service.facade;

import com.example.bookster.datasource.service.generic.GenericServiceFacade;
import com.example.bookster.graphql.models.dto.ContactInfo;
import com.example.bookster.graphql.models.input.ContactInfoInput;
import reactor.core.publisher.Mono;

public interface ContactInfoService extends GenericServiceFacade<ContactInfoInput, ContactInfo, String> {
    Mono<ContactInfo> findByPatientId(Mono<String> patientId);
}
