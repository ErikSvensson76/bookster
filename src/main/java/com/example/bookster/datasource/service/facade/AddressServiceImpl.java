package com.example.bookster.datasource.service.facade;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.repository.AddressRepository;
import com.example.bookster.datasource.service.mapping.MappingService;
import com.example.bookster.datasource.service.persistence.AddressPersistenceService;
import com.example.bookster.graphql.models.dto.Address;
import com.example.bookster.graphql.models.input.AddressInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository repository;
    private final AddressPersistenceService persistenceService;
    private final MappingService mappingService;


    @Override
    @Transactional
    public Mono<Address> save(Mono<AddressInput> addressMono) {
        return addressMono.map(mappingService::convert)
                .flatMap(address -> {

                    return Mono.empty();
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Address> findById(Mono<String> idMono) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Address> findAll() {
        return null;
    }

    @Override
    public Mono<Integer> delete(String s) {
        return null;
    }

    public Mono<DBAddress> update(Mono<DBAddress> addressMono){
        return Mono.empty();
    }

    public Mono<DBAddress> create(Mono<DBAddress> addressMono){
        return Mono.empty();
    }
}
