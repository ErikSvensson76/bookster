package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBContactInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.example.bookster.datasource.models.DBContactInfo.CONTACT_INFO_PK;
import static org.springframework.data.relational.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
public class ContactInfoPersistenceServiceImpl implements ContactInfoPersistenceService {

    private final DatabaseClient client;
    private final R2dbcEntityTemplate r2dbc;

    @Override
    @Transactional
    public Mono<DBContactInfo> save(DBContactInfo type) {
        return  Mono.justOrEmpty(type)
                .flatMap(dbContactInfo -> {
                    if(dbContactInfo == null) return Mono.empty();
                    if(dbContactInfo.getId() == null){
                        return r2dbc.insert(DBContactInfo.class)
                                .using(dbContactInfo);

                    }
                    return r2dbc.update(dbContactInfo);
                });
    }

    @Override
    @Transactional
    public Mono<Integer> delete(UUID uuid) {
        return r2dbc.delete(DBContactInfo.class)
                .matching(Query.query(where(CONTACT_INFO_PK).is(uuid)))
                .all();
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
