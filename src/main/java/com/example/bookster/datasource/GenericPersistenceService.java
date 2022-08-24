package com.example.bookster.datasource;

import org.springframework.r2dbc.core.StatementFilterFunction;
import reactor.core.publisher.Mono;

public interface GenericPersistenceService<T, ID> {
    Mono<T> save(T type);
    Mono<Integer> delete(ID uuid);

    default StatementFilterFunction setIdExtractionStrategy(final String dbIDColumnName){
        return ((statement, executeFunction) -> statement.returnGeneratedValues(dbIDColumnName).execute());
    }
}
