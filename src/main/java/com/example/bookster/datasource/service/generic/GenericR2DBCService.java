package com.example.bookster.datasource.service.generic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.query.Query;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.data.relational.core.query.Criteria.where;

@RequiredArgsConstructor
public abstract class GenericR2DBCService <T>{

    protected final R2dbcEntityTemplate template;
    private final Class<T> clazz;


    @Transactional
    public Mono<Integer> delete(final UUID uuid) {
        return Mono.just(uuid)
                .flatMap(id -> {
                    String column;
                    try {
                        column = Stream.of(clazz.getDeclaredFields())
                                .filter(f -> f.isAnnotationPresent(Id.class))
                                .map(f -> f.isAnnotationPresent(Column.class) ? f.getAnnotation(Column.class).value() : f.getName())
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("Couldn't find id column"));
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException(e));
                    }
                    return template.delete(clazz)
                            .matching(Query.query(where(column).is(id)))
                            .all();
                });
    }

    @Transactional
    public Mono<T> save(final T type){
        return Mono.justOrEmpty(type)
                .flatMap(obj -> {
                    if(obj == null) return Mono.empty();
                    UUID uuid;
                    try {
                        var field = Arrays.stream(type.getClass().getDeclaredFields())
                                .filter(f -> f.isAnnotationPresent(Id.class))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("Couldn't find id column"));
                        field.setAccessible(true);
                        uuid = (UUID) field.get(obj);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException(e));
                    }

                    T entity = clazz.cast(obj);
                    if(uuid == null){
                        return template.insert(clazz)
                                .using(entity);
                    }
                    return template.update(entity);
                });
    }
}
