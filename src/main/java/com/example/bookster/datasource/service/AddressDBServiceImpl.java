package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressDBServiceImpl implements AddressDBService {

    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public Mono<DBAddress> save(final Mono<DBAddress> dbAddressMono) {
        return Mono.from(dbAddressMono)
                .flatMap(address -> addressRepository.findByCityAndStreetAndZipCode(address.getCity(), address.getStreet(), address.getZipCode()))
                .switchIfEmpty(Mono.from(dbAddressMono.flatMap(addressRepository::save)));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DBAddress> findById(Mono<UUID> idMono) {
        return addressRepository.findById(idMono);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBAddress> findByPremisesId(Mono<UUID> premisesId) {
        return addressRepository.findByPremisesId(premisesId);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBAddress> findByContactInfoId(Mono<UUID> contactInfoId) {
        return addressRepository.findByContactInfoId(contactInfoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBAddress> findAll() {
        return addressRepository.findAll();
    }

    @Override
    @Transactional
    public Mono<Void> delete(Mono<UUID> idMono) {
        return addressRepository.deleteById(idMono);
    }
}
