package com.example.bookster.graphql.facade;

import com.example.bookster.datasource.service.AddressDBService;
import com.example.bookster.datasource.service.mapping.MappingService;
import com.example.bookster.graphql.models.dto.Address;
import com.example.bookster.graphql.models.input.AddressInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressDBService addressDBService;
    private final MappingService mappingService;

    @Override
    public Mono<Address> save(Mono<AddressInput> addressInputMono) {
        return addressDBService.save(addressInputMono.map(mappingService::convert))
                .map(mappingService::convert);
    }

    @Override
    public Mono<Address> findById(Mono<String> id) {
        return addressDBService.findById(id.map(mappingService::convert))
                .map(mappingService::convert);
    }

    @Override
    public Mono<Address> findByPremisesId(Mono<String> premisesId) {
        return addressDBService.findByPremisesId(premisesId.map(mappingService::convert))
                .collectList()
                .mapNotNull(dbAddresses -> dbAddresses.stream().findFirst().orElse(null))
                .map(mappingService::convert);
    }

    @Override
    public Mono<Address> findByContactInfoId(Mono<String> contactInfoId) {
        return addressDBService.findByContactInfoId(contactInfoId.map(mappingService::convert))
                .collectList()
                .mapNotNull(dbAddresses -> dbAddresses.stream().findFirst().orElse(null))
                .map(mappingService::convert);
    }

    @Override
    public Flux<Address> findAll() {
        return addressDBService.findAll()
                .map(mappingService::convert);
    }

    @Override
    public Mono<Void> delete(Mono<String> id) {
        return addressDBService.delete(id.map(mappingService::convert));
    }
}
