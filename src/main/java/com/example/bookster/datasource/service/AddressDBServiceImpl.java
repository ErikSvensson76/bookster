package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.repository.AddressRepository;
import com.example.bookster.datasource.repository.ContactInfoRepository;
import com.example.bookster.datasource.repository.PremisesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
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
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    @Transactional
    public Mono<DBAddress> saveOrGet(final Mono<DBAddress> dbAddressMono) {
        return Mono.from(dbAddressMono).flatMap(dbAddress -> addressRepository.findByCityAndStreetAndZipCode(dbAddress.getCity(), dbAddress.getStreet(), dbAddress.getZipCode()))
                .flatMap(r2dbcEntityTemplate::insert);

    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DBAddress> findById(Mono<UUID> idMono) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBAddress> findByPremisesId(Mono<UUID> premisesId) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBAddress> findByContactInfoId(Mono<UUID> contactInfoId) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBAddress> findAll() {
        return null;
    }

    @Override
    @Transactional
    public Mono<Void> delete(Mono<UUID> idMono) {
        return null;
    }
}
