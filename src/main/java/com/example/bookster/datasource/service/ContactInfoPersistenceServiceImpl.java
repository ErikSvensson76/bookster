package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBContactInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

import static com.example.bookster.datasource.models.DBContactInfo.CONTACT_INFO_PK;

@Repository
@RequiredArgsConstructor
public class ContactInfoPersistenceServiceImpl implements ContactInfoPersistenceService {

    private final DatabaseClient client;

    @Override
    @Transactional
    public Mono<DBContactInfo> save(DBContactInfo type) {
        if(type == null) return Mono.empty();
        if(Objects.nonNull(type.getId())){
            return client.sql("INSERT INTO contact_info (email, phone, fk_user_address) VALUES(:email, :phone, :addressId)")
                    .bind("email", type.getEmail())
                    .bind("phone", type.getPhone())
                    .bind("addressId", type.getAddressId())
                    .filter(setIdExtractionStrategy(CONTACT_INFO_PK))
                    .map((r,m) -> r.get(0, UUID.class))
                    .one()
                    .flatMap(uuid -> Mono.justOrEmpty(
                                DBContactInfo.builder()
                                        .id(uuid)
                                        .email(type.getEmail())
                                        .phone(type.getPhone())
                                        .addressId(type.getAddressId())
                                        .build()
                            )
                    );
        }
        return client.sql("UPDATE contact_info SET email = :email, phone = :phone, fk_user_address = :addressId WHERE pk_contact_info = :id")
                .bind("email", type.getEmail())
                .bind("phone", type.getPhone())
                .bind("addressId", type.getAddressId())
                .bind("id", type.getId())
                .fetch()
                .rowsUpdated()
                .map(integer -> {
                    if(integer == null || integer == 0){
                        throw new RuntimeException(
                                String.format("Failed to update: %1s with id: %2s", DBContactInfo.class.getSimpleName(), type.getId().toString())
                        );
                    }
                    return type;
                });
    }

    @Override
    @Transactional
    public Mono<Integer> delete(UUID uuid) {
        return client.sql("DELETE FROM contact_info WHERE pk_contact_info = :id")
                .bind("id", uuid)
                .fetch()
                .rowsUpdated();
    }

    @Override
    @Transactional
    public Mono<Integer> setAddressRelation(UUID contactInfoId, UUID addressId) {
        return client.sql("UPDATE contact_info SET fk_user_address = :addressId WHERE pk_contact_info = :contactInfoId")
                .bind("addressId", addressId)
                .bind("contactInfoId", contactInfoId)
                .fetch()
                .rowsUpdated()
                .defaultIfEmpty(0);
    }

    @Override
    @Transactional
    public Mono<Integer> removeAddressRelation(UUID contactInfoId, UUID addressId) {
        return client.sql("UPDATE contact_info SET fk_user_address = null WHERE pk_contact_info = :contactInfoId AND fk_user_address = :addressId")
                .bind("contactInfoId", contactInfoId)
                .bind("addressId", addressId)
                .fetch()
                .rowsUpdated()
                .defaultIfEmpty(0);
    }
}
