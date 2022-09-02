package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBContactInfo;
import com.example.bookster.datasource.repository.AddressRepository;
import com.example.bookster.datasource.repository.ContactInfoRepository;
import com.example.bookster.datasource.repository.PremisesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactInfoDBServiceImpl implements ContactInfoDBService {

    private final ContactInfoRepository repository;
    private final PremisesRepository premisesRepository;
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

                    //Pre declaring empty cleanup function
                    Mono<Void> cleanUp = Mono.empty();

                    //Checking if source is about to receive a new address replacing old address
                    if(source.getAddressId() != null && updated.getAddressId() != null && !source.getAddressId().equals(updated.getAddressId())){
                        Mono<UUID> sourceUUIDMono = Mono.just(source.getAddressId());
                        //Assigning a cleanup function to remove old address if usage is less or equal to 1
                        cleanUp = Mono.zip(repository.countAllByAddressId(Mono.just(source.getAddressId())), premisesRepository.countAllByAddressId(Mono.just(source.getAddressId())))
                                .map(t2 -> t2.getT1() + t2.getT2())
                                .zipWith(sourceUUIDMono)
                                .flatMap(t2 -> {
                                    if(t2.getT1() <= 1){
                                        return addressDBService.delete(Mono.just(t2.getT2()));
                                    }
                                    return Mono.empty();
                                });
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
        var contactInfoMono =  Mono.from(repository.findById(uuidMono));
        var addressMono = Mono.from(addressDBService.findByContactInfoId(uuidMono).collectList());

        return Mono.zip(contactInfoMono, addressMono)
                .flatMap(tuple2 -> {
                    DBContactInfo dbContactInfo = tuple2.getT1();
                    List<DBAddress> addressList = tuple2.getT2();
                    Mono<Void> cleanupMono = Mono.empty();
                    if(addressList.size() == 1){
                        cleanupMono = Mono.just(addressList.get(0).getId())
                                .flatMap(uuid -> addressDBService.delete(Mono.just(uuid)));
                    }
                    return Mono.zip(repository.deleteById(dbContactInfo.getId()), cleanupMono);
                })
                .then();
    }
}
