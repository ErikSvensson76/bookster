package com.example.bookster.graphql.facade;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBPremises;
import com.example.bookster.datasource.service.PremisesDBService;
import com.example.bookster.datasource.service.mapping.MappingService;
import com.example.bookster.graphql.models.dto.Premises;
import com.example.bookster.graphql.models.input.PremisesInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PremisesServiceImpl implements PremisesService {

    private final PremisesDBService premisesDBService;
    private final MappingService mappingService;

    @Override
    public Mono<Premises> persist(Mono<PremisesInput> premisesInputMono) {
        return premisesInputMono.flatMap(premisesInput -> {
            DBPremises dbPremises = mappingService.convert(premisesInput);
            DBAddress dbAddress = mappingService.convert(premisesInput.address());
            return Mono.zip(Mono.just(dbPremises), Mono.just(dbAddress));
        }).flatMap(tuple2 -> premisesDBService.persist(Mono.just(tuple2.getT1()), Mono.just(tuple2.getT2())))
        .map(mappingService::convert);
    }

    @Override
    public Flux<Premises> findAll() {
        return premisesDBService.findAll().map(mappingService::convert);
    }

    @Override
    public Mono<Premises> findById(Mono<String> idMono) {
        return premisesDBService.findById(idMono.map(mappingService::convert))
                .map(mappingService::convert);
    }

    @Override
    public Mono<Premises> findByBookingId(Mono<String> bookingId) {
        return premisesDBService.findByBookingId(bookingId.map(mappingService::convert))
                .map(mappingService::convert);
    }

    @Override
    public Mono<Premises> update(Mono<String> premisesId, Mono<PremisesInput> premisesInputMono) {
        return premisesInputMono.map(mappingService::convert)
                .zipWith(premisesId)
                .map(tuple2 -> {
                    tuple2.getT1().setId(mappingService.convert(tuple2.getT2()));
                    return tuple2.getT1();
                })
                .flatMap(dbPremises -> premisesDBService.update(Mono.just(dbPremises)))
                .map(mappingService::convert);
    }

    @Override
    public Mono<Void> delete(Mono<String> id) {
        return premisesDBService.delete(id.map(mappingService::convert));
    }
}
