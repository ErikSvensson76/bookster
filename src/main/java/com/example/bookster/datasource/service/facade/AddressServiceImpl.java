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

import java.util.UUID;

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
                .flatMap(address -> process(Mono.just(address)))
                .map(mappingService::convert);
    }



    @Override
    @Transactional(readOnly = true)
    public Mono<Address> findById(Mono<String> idMono) {
        return repository.findById(idMono.map(UUID::fromString))
                .map(mappingService::convert);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Address> findAll() {
        return repository.findAll()
                .map(mappingService::convert);

    }

    @Override
    @Transactional
    public Mono<Integer> delete(Mono<String> stringMono) {
        return stringMono.map(UUID::fromString)
                .flatMap(persistenceService::delete);
    }

    @Transactional
    public Mono<DBAddress> process(Mono<DBAddress> addressMono){
        return addressMono
                .flatMap(address -> Mono.from(repository.findByCityAndStreetAndZipCode(address.getCity(), address.getStreet(), address.getZipCode()))
                        .flatMap(result -> {
                            Mono<DBAddress> dbAddressMono;
                            DBAddress dbAddress;
                            if(result == null){
                                 dbAddress = new DBAddress(null, address.getCity(), address.getStreet(), address.getZipCode());
                                 dbAddressMono = Mono.from(persistenceService.save(dbAddress));
                            }else {
                                dbAddressMono = Mono.just(result);
                            }
                            return dbAddressMono;
                        })
                );
    }
}
