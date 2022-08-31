package com.example.bookster.datasource.service.facade;

import com.example.bookster.datasource.service.generic.GenericServiceFacade;
import com.example.bookster.graphql.models.dto.AppUser;
import com.example.bookster.graphql.models.input.AppUserInput;

public interface AppUserService extends GenericServiceFacade<AppUserInput, AppUser, String> {
}
