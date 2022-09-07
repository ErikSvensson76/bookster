package com.example.bookster.graphql.controllers;

import com.example.bookster.graphql.facade.AddressService;
import com.example.bookster.graphql.models.dto.Address;
import com.example.bookster.graphql.models.dto.ContactInfo;
import com.example.bookster.graphql.models.dto.Premises;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @BatchMapping(typeName = "ContactInfo", field = "address")
    public Mono<Map<ContactInfo, Address>> getContactInfoAddress(List<ContactInfo> contactInfos){
        return Flux.fromIterable(contactInfos)
                .flatMap(contactInfo -> addressService.findByPremisesId(Mono.just(contactInfo.getAddressId())))
                .collectMap(address -> contactInfos.stream()
                        .filter(contactInfo -> contactInfo.getAddressId().equals(address.getId()))
                        .findFirst()
                        .orElseThrow()
                );
    }

    @BatchMapping(typeName = "Premises", field = "address")
    public Mono<Map<Premises, Address>> getPremisesAddress(List<Premises> premisesList){
        return Flux.fromIterable(premisesList)
                .flatMap(premises -> addressService.findByPremisesId(Mono.just(premises.getPremisesAddressId())))
                .collectMap(address -> premisesList.stream()
                        .filter(premises -> premises.getPremisesAddressId().equals(address.getId()))
                        .findFirst()
                        .orElseThrow()
                ).onErrorReturn(new HashMap<>());
    }

}
