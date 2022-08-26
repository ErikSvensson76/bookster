package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBPremises;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.data.relational.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
public class PremisesPersistenceServiceImpl implements PremisesPersistenceService {

    private final R2dbcEntityTemplate template;

    @Override
    @Transactional
    public Mono<DBPremises> save(DBPremises type) {
        return Mono.just(type)
                .flatMap(entity -> {
                    if(entity == null) return Mono.error(new IllegalArgumentException("DBPremises was null"));
                    if(entity.getId() == null){
                        return template.insert(DBPremises.class)
                                .using(entity);
                    }
                    return template.update(entity);
                });
    }

    @Override
    @Transactional
    public Mono<Integer> delete(UUID uuid) {
        return template.delete(DBPremises.class)
                .matching(Query.query(where("pk_premises").is(uuid)))
                .all();
    }
}
