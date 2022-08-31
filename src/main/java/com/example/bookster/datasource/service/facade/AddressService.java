package com.example.bookster.datasource.service.facade;

import com.example.bookster.datasource.service.generic.GenericServiceFacade;
import com.example.bookster.graphql.models.dto.Address;
import com.example.bookster.graphql.models.input.AddressInput;

public interface AddressService extends GenericServiceFacade<AddressInput, Address, String> {
}
