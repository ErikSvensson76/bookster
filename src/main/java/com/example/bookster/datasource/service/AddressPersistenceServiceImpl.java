package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.example.bookster.datasource.models.DBAddress.ADDRESS_PK;

@Repository
@RequiredArgsConstructor
public class AddressPersistenceServiceImpl implements AddressPersistenceService {

    private final DatabaseClient client;

    @Override
    @Transactional
    public Mono<DBAddress> save(DBAddress type) {
        if(type.getId() == null){
            return client.sql("INSERT INTO address (city, street, zip_code) VALUES (:city, :street, :zipCode)")
                    .bind("city", type.getCity())
                    .bind("street", type.getStreet())
                    .bind("zipCode", type.getZipCode())
                    .filter(setIdExtractionStrategy(ADDRESS_PK))
                    .map(((r, m) -> r.get(0, UUID.class)))
                    .one()
                    .flatMap(uuid -> Mono.just(DBAddress.builder()
                                    .id(uuid)
                                    .city(type.getCity())
                                    .street(type.getStreet())
                                    .zipCode(type.getZipCode())
                                    .build())
                    );
        }
        return client.sql("UPDATE address " +
                "SET city = :city, street = :street, zip_code = :zipCode " +
                "WHERE pk_address = :id")
                .bind("city", type.getCity())
                .bind("street", type.getStreet())
                .bind("zipCode", type.getZipCode())
                .bind("id", type.getId())
                .fetch()
                .rowsUpdated()
                .map(integer -> {
                    if(integer == null || integer == 0) {
                        throw new RuntimeException(
                                String.format("Failed to update: %1s with id: %2s", DBAddress.class.getSimpleName(), type.getId().toString()));
                    }
                    return type;
                });
    }

    @Override
    @Transactional
    public Mono<Integer> delete(UUID uuid) {
        return client.sql("DELETE FROM address WHERE pk_address = :id")
                .bind("id", uuid)
                .fetch().rowsUpdated();
    }
}
