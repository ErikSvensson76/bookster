package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBContactInfo;
import com.example.bookster.datasource.repository.ContactInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactInfoDBServiceImpl implements ContactInfoDBService {

    private final ContactInfoRepository repository;
    private final AddressDBService addressDBService;

    @Override
    @Transactional
    public Mono<DBContactInfo> persist(Mono<DBContactInfo> contactInfoMono, Mono<DBAddress> dbAddressMono) {
        var foundAddressMono = dbAddressMono
                .flatMap(address -> addressDBService.save(Mono.just(address)));

        return contactInfoMono
                .zipWith(foundAddressMono)
                .flatMap(tuple2 -> {
                    var contactInfo = tuple2.getT1();
                    contactInfo.setAddressId(tuple2.getT2().getId());
                    return repository.save(contactInfo);
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
        return patientIdMono.flatMap(repository::findByContactInfoByPatientId);
    }

    @Override
    @Transactional
    public Mono<DBContactInfo> update(Mono<DBContactInfo> contactInfoMono) {
        return contactInfoMono
                .flatMap(dbContactInfo -> Mono.zip(Mono.just(dbContactInfo), Mono.from(repository.findById(Mono.just(dbContactInfo.getId())))))
                .flatMap(tuple2 -> {
                    var updated = tuple2.getT1();
                    var source = tuple2.getT2();

                    //Pre declaring empty cleanup function
                    Mono<Void> cleanUp = Mono.empty();

                    //Checking if source is about to receive a new address replacing old address
                    if(source.getAddressId() != null && updated.getAddressId() != null && !source.getAddressId().equals(updated.getAddressId())){
                        Mono<UUID> sourceUUIDMono = Mono.just(source.getAddressId());
                        //Assigning a cleanup function to remove old address if usage is less or equal to 1
                        cleanUp = Mono.from(addressDBService.delete(sourceUUIDMono));
                    }
                    source.setAddressId(updated.getAddressId());
                    source.setEmail(updated.getEmail());
                    source.setPhone(updated.getPhone());

                    return Mono.zip(Mono.just(source), cleanUp)
                            .flatMap(t2 -> repository.save(t2.getT1()))
                            //If cleanup didn't run just save source and skip cleanup
                            .switchIfEmpty(repository.save(source));
                });

    }

    @Override
    @Transactional
    public Mono<Void> delete(Mono<UUID> uuidMono) {
        return Mono.from(repository.findById(uuidMono))
                .flatMap(contactInfo -> {
                    Mono<Void> addressDelete = Mono.empty();
                    UUID addressId = contactInfo.getAddressId();
                    if(addressId != null){
                        addressDelete = addressDBService.delete(Mono.just(addressId));
                    }
                    return addressDelete
                            .then(repository.deleteById(contactInfo.getId()));
                });
    }
}
