package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBContactInfo;
import com.example.bookster.datasource.repository.AddressRepository;
import com.example.bookster.datasource.repository.ContactInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactInfoDBServiceImpl implements ContactInfoDBService {

    private final ContactInfoRepository repository;
    private final AddressRepository addressRepository;
    private final AddressDBService addressDBService;

    @Override
    @Transactional
    public Mono<DBContactInfo> persist(Mono<DBContactInfo> contactInfoMono, Mono<DBAddress> dbAddressMono) {
        var foundAddressMono = dbAddressMono
                .flatMap(dbAddress -> {
                    var found = addressRepository.findByCityAndStreetAndZipCode(
                            dbAddress.getCity(), dbAddress.getStreet(), dbAddress.getZipCode()
                    );

                    if(found != null){
                        return found;
                    }

                    return addressDBService.save(Mono.just(dbAddress));
                }).map(DBAddress::getId);


        return contactInfoMono.zipWith(foundAddressMono)
                .flatMap(tuple2 -> {
                    tuple2.getT1().setAddressId(tuple2.getT2());
                    return repository.save(tuple2.getT1());
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBContactInfo> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DBContactInfo> findById(Mono<UUID> uuidMono) {
        return repository.findById(uuidMono);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DBContactInfo> findByPatientId(Mono<UUID> patientIdMono) {
        return repository.findByPatientId(patientIdMono);
    }

    @Override
    @Transactional
    public Mono<DBContactInfo> update(Mono<DBContactInfo> contactInfoMono) {
        return contactInfoMono
                .flatMap(dbContactInfo -> Mono.zip(Mono.just(dbContactInfo), Mono.from(repository.findById(Mono.just(dbContactInfo.getId())))))
                .flatMap(tuple2 -> {
                    var updated = tuple2.getT1();
                    var source = tuple2.getT2();

                    Mono<Void> cleanUp = Mono.empty();

                    if(source.getAddressId() != null && updated.getAddressId() != null && !source.getAddressId().equals(updated.getAddressId())){
                        //Todo: FIX CLEANUP
                    }

                    source.setAddressId(updated.getAddressId());
                    source.setEmail(updated.getEmail());
                    source.setPhone(updated.getPhone());

                    return Mono.zip(repository.save(source), cleanUp);
                })
                .map(Tuple2::getT1);
    }

    @Override
    @Transactional
    public Mono<Void> delete(Mono<UUID> uuidMono) {
        return null;
    }
}
