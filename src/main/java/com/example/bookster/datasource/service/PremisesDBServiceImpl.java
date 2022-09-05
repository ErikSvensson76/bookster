package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBPremises;
import com.example.bookster.datasource.repository.PremisesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PremisesDBServiceImpl implements PremisesDBService {

    private final PremisesRepository repository;
    private final AddressDBService addressDBService;

    @Override
    @Transactional
    public Mono<DBPremises> persist(Mono<DBPremises> dbPremisesMono, Mono<DBAddress> dbAddressMono) {
        Mono<DBAddress> persistedAddress = dbAddressMono.flatMap(dbAddress -> addressDBService.save(Mono.just(dbAddress)));
        return dbPremisesMono
                .zipWith(persistedAddress)
                .flatMap(tuple2 -> {
                    DBPremises dbPremises = tuple2.getT1();
                    dbPremises.setPremisesAddressId(tuple2.getT2().getId());
                    return repository.save(dbPremises);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DBPremises> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DBPremises> findById(Mono<UUID> uuidMono) {
        return repository.findById(uuidMono);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DBPremises> findByBookingId(Mono<UUID> bookingId) {
        return bookingId.flatMap(repository::findByBookingId);
    }

    @Override
    @Transactional
    public Mono<DBPremises> update(Mono<DBPremises> dbPremisesMono) {
        return dbPremisesMono
                .flatMap(dbPremises -> Mono.zip(Mono.just(dbPremises), Mono.from(repository.findById(Mono.just(dbPremises.getId())))))
                .flatMap(tuple2 -> {
                    var updated = tuple2.getT1();
                    var source = tuple2.getT2();

                    Mono<Void> cleanupMono = Mono.empty();

                    if(source.getPremisesAddressId() != null
                            && updated.getPremisesAddressId() != null
                            && !source.getPremisesAddressId().equals(updated.getPremisesAddressId())) {
                        Mono<UUID> addressIdMono = Mono.just(source.getPremisesAddressId());
                        cleanupMono = Mono.from(addressDBService.delete(addressIdMono));
                    }
                    source.setPremisesAddressId(updated.getPremisesAddressId());
                    source.setPremisesName(updated.getPremisesName());
                    return Mono.zip(Mono.just(source), cleanupMono)
                            .flatMap(t2 -> repository.save(t2.getT1()))
                            .switchIfEmpty(repository.save(source));
                });
    }

    @Override
    @Transactional
    public Mono<Void> delete(Mono<UUID> uuidMono) {
       return Mono.from(repository.findById(uuidMono))
               .flatMap(dbPremises -> {
                   Mono<Void> addressDelete = Mono.empty();
                   UUID addressId = dbPremises.getPremisesAddressId();
                   if(addressId != null){
                       addressDelete = addressDBService.delete(Mono.just(addressId));
                   }
                   return
                           addressDelete
                           .then(repository.deleteById(dbPremises.getId()));
               });
    }
}
