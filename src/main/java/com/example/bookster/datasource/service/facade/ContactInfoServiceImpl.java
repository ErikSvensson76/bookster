package com.example.bookster.datasource.service.facade;

import com.example.bookster.datasource.models.DBContactInfo;
import com.example.bookster.datasource.repository.ContactInfoRepository;
import com.example.bookster.datasource.service.mapping.MappingService;
import com.example.bookster.datasource.service.persistence.ContactInfoPersistenceService;
import com.example.bookster.graphql.models.dto.ContactInfo;
import com.example.bookster.graphql.models.input.ContactInfoInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactInfoServiceImpl implements ContactInfoService {

    private final ContactInfoRepository repository;
    private final ContactInfoPersistenceService persistenceService;
    private final MappingService mappingService;

    @Override
    @Transactional(readOnly = true)
    public Mono<ContactInfo> findByPatientId(Mono<String> patientId) {
        return patientId.map(UUID::fromString)
                .flatMap(repository::findByContactInfoByPatientId)
                .map(mappingService::convert);
    }

    @Override
    @Transactional
    public Mono<ContactInfo> save(Mono<ContactInfoInput> contactInfoInputMono) {
        return contactInfoInputMono.map(mappingService::convert)
                .flatMap(dbContactInfo -> {
                    Mono<DBContactInfo> dbContactInfoMono;
                    if(dbContactInfo.getId() == null){
                        dbContactInfoMono = persistenceService.save(dbContactInfo);
                    }else {
                        dbContactInfoMono =  repository.findById(dbContactInfo.getId())
                                .map(toUpdate -> {
                                    toUpdate.setAddressId(dbContactInfo.getAddressId());
                                    toUpdate.setEmail(dbContactInfo.getEmail());
                                    toUpdate.setPhone(dbContactInfo.getPhone());
                                    return toUpdate;
                                })
                                .flatMap(persistenceService::save);
                    }
                    return dbContactInfoMono;
                })
                .map(mappingService::convert);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ContactInfo> findById(Mono<String> id) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ContactInfo> findAll() {
        return null;
    }

    @Override
    @Transactional
    public Mono<Integer> delete(Mono<String> id) {
        return null;
    }
}
