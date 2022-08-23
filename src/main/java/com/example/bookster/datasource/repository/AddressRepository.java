package com.example.bookster.datasource.repository;

import com.example.bookster.datasource.models.DBAddress;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface AddressRepository extends R2dbcRepository<DBAddress, UUID> {
    @Query("SELECT a.* FROM address AS a " +
            "WHERE upper(a.city) = upper(:city) AND " +
            "upper(a.street) = upper(:street) AND " +
            "a.zip_code = :zipCode")
    Flux<DBAddress> findByCityAndStreetAndZipCode(
            @Param("city") String city,
            @Param("street") String street,
            @Param("zipCode") String zipCode);
}
