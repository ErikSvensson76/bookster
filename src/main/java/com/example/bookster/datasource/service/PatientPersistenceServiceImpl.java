package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBPatient;
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
public class PatientPersistenceServiceImpl implements PatientPersistenceService {

    private final R2dbcEntityTemplate template;

    @Override
    @Transactional
    public Mono<DBPatient> save(DBPatient type) {
        return Mono.just(type)
                .flatMap(dbPatient -> {
                    if(dbPatient == null) return Mono.error(new IllegalArgumentException("Patient was null"));
                    if(dbPatient.getId() == null){
                        return template.insert(DBPatient.class)
                                .using(dbPatient);

                    }
                    return template.update(dbPatient);
                });
    }

    @Override
    @Transactional
    public Mono<Integer> delete(UUID uuid) {
        return template.delete(DBPatient.class)
                .matching(Query.query(where("pk_patient").is(uuid)))
                .all();
    }
}
