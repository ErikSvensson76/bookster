package com.example.bookster.graphql.facade;

import com.example.bookster.datasource.service.AppUserDBService;
import com.example.bookster.datasource.service.mapping.MappingService;
import com.example.bookster.graphql.models.dto.AppUser;
import com.example.bookster.graphql.models.input.AppUserInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserDBService appUserDBService;
    private final MappingService mappingService;

    @Override
    public Mono<AppUser> persist(Mono<AppUserInput> appUserInputMono, Mono<String> appRole) {
        return appUserDBService.persist(appUserInputMono.map(mappingService::convert), appRole)
                .map(mappingService::convert);
    }

    @Override
    public Mono<AppUser> findById(Mono<String> id) {
        return appUserDBService.findById(id.map(mappingService::convert))
                .map(mappingService::convert);
    }

    @Override
    public Flux<AppUser> findAll() {
        return appUserDBService.findAll().map(mappingService::convert);
    }

    @Override
    public Flux<AppUser> findByAppRoleId(Mono<String> appRoleId) {
        return appUserDBService.findAllByAppRoleId(appRoleId.map(mappingService::convert))
                .map(mappingService::convert);
    }

    @Override
    public Mono<AppUser> findByUsername(Mono<String> username) {
        return appUserDBService.findByUsername(username).map(mappingService::convert);
    }

    @Override
    public Mono<AppUser> findByPatientId(Mono<String> patientId) {
        return appUserDBService.findByPatientId(patientId.map(mappingService::convert))
                .map(mappingService::convert);
    }

    @Override
    public Mono<AppUser> update(Mono<String> id, Mono<AppUserInput> appUserInputMono) {
        return appUserInputMono.map(mappingService::convert)
                .zipWith(id.map(mappingService::convert))
                .map(tuple2 -> {
                    tuple2.getT1().setId(tuple2.getT2());
                    return tuple2.getT1();
                })
                .flatMap(dbAppUser -> appUserDBService.update(Mono.just(dbAppUser)))
                .map(mappingService::convert);
    }

    @Override
    public Mono<Void> delete(Mono<String> id) {
        return appUserDBService.delete(id.map(mappingService::convert));
    }
}
