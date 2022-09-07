package com.example.bookster.graphql.facade;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBContactInfo;
import com.example.bookster.datasource.service.ContactInfoDBService;
import com.example.bookster.datasource.service.mapping.MappingService;
import com.example.bookster.graphql.models.dto.ContactInfo;
import com.example.bookster.graphql.models.input.ContactInfoInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ContactInfoServiceImpl implements ContactInfoService {

    private final MappingService mappingService;
    private final ContactInfoDBService contactInfoDBService;

    @Override
    public Mono<ContactInfo> persist(Mono<ContactInfoInput> contactInfoInputMono) {
        return contactInfoInputMono
                .flatMap(contactInfoInput -> {
                    DBAddress dbAddress = mappingService.convert(contactInfoInput.address());
                    DBContactInfo dbContactInfo = mappingService.convert(contactInfoInput);
                    return contactInfoDBService.persist(Mono.just(dbContactInfo), Mono.just(dbAddress));
                }).map(mappingService::convert);
    }

    @Override
    public Flux<ContactInfo> findAll() {
        return contactInfoDBService.findAll()
                .map(mappingService::convert);
    }

    @Override
    public Mono<ContactInfo> findById(Mono<String> idMono) {
        return contactInfoDBService.findById(idMono.map(mappingService::convert))
                .map(mappingService::convert);
    }

    @Override
    public Mono<ContactInfo> findByPatientId(Mono<String> patientIdMono) {
        return contactInfoDBService.findByPatientId(patientIdMono.map(mappingService::convert))
                .map(mappingService::convert);
    }

    @Override
    public Mono<ContactInfo> update(Mono<String> idMono, Mono<ContactInfoInput> contactInfoInputMono) {
        return contactInfoInputMono.map(mappingService::convert)
                .zipWith(idMono)
                .map(tuple2 -> {
                    tuple2.getT1().setId(mappingService.convert(tuple2.getT2()));
                    return tuple2.getT1();
                })
                .flatMap(dbContactInfo -> contactInfoDBService.update(Mono.just(dbContactInfo)))
                .map(mappingService::convert);
    }

    @Override
    public Mono<Void> delete(Mono<String> stringMono) {
        return contactInfoDBService.delete(stringMono.map(mappingService::convert));
    }
}
