package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBContactInfo;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ContactInfoPersistenceService extends GenericPersistenceService<DBContactInfo, UUID>{
    Mono<Integer> setAddressRelation(UUID contactInfoId, UUID addressId);
    Mono<Integer> removeAddressRelation(UUID contactInfoId, UUID addressId);
}
