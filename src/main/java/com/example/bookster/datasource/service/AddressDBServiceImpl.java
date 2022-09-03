package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.repository.AddressRepository;
import com.example.bookster.datasource.repository.ContactInfoRepository;
import com.example.bookster.datasource.repository.PremisesRepository;
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
    private final PremisesRepository premisesRepository;
    private final ContactInfoRepository contactInfoRepository;

    @Override
    @Transactional
    public Mono<DBAddress> save(final Mono<DBAddress> dbAddressMono) {
        return Mono.from(dbAddressMono)
                .flatMapMany(address -> addressRepository.getIdFromCityStreetAndZipCode(address.getCity(), address.getStreet(), address.getZipCode())).collectList()
                .flatMap(uuids -> uuids.isEmpty() ? Mono.empty() : addressRepository.findById(uuids.get(0)))
                .switchIfEmpty(dbAddressMono.flatMap(addressRepository::save));
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
        var premisesCount = Mono.from(idMono)
                .flatMap(premisesRepository::countAllByAddressId)
                .switchIfEmpty(Mono.just(0));


        var contactInfoCount = Mono.from(idMono)
                .flatMap(contactInfoRepository::countAllByAddressId)
                .switchIfEmpty(Mono.just(0));


        return Mono.zip(premisesCount, contactInfoCount)
                .map(tuple2 -> tuple2.getT1() + tuple2.getT2())
                .zipWith(idMono)
                .flatMap(tuple2 -> tuple2.getT1() <= 1 ? Mono.from(addressRepository.deleteById(tuple2.getT2())) : Mono.empty());
    }
}
