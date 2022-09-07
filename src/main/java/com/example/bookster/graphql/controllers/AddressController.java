package com.example.bookster.graphql.controllers;

import com.example.bookster.graphql.facade.AddressService;
import com.example.bookster.graphql.models.dto.Address;
import com.example.bookster.graphql.models.dto.ContactInfo;
import com.example.bookster.graphql.models.dto.Premises;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @SchemaMapping(typeName = "ContactInfo", field = "address")
    public Mono<Address> getContactInfoAddress(Mono<ContactInfo> contactInfo){
        return addressService.findByContactInfoId(contactInfo.map(ContactInfo::getId));
    }

    @SchemaMapping(typeName = "Premises", field = "address")
    public Mono<Address> getPremisesAddress(Mono<Premises> premises){
        return addressService.findByPremisesId(premises.map(Premises::getId));
    }

}
