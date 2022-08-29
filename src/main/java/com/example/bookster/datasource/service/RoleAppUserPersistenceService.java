package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBRoleAppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
public class RoleAppUserPersistenceService {
    private final R2dbcEntityTemplate template;

    @Transactional
    public Mono<Integer> createAppRoleAssignment(DBRoleAppUser roleAppUser){
       return template.insert(roleAppUser)
               .map(entity -> entity == null ? 0 : 1);
    }

    @Transactional
    public Mono<Integer> deleteAppRoleAssignment(DBRoleAppUser roleAppUser){
        return template.delete(DBRoleAppUser.class)
                .matching(Query.query(
                        where("fk_app_user")
                        .is(roleAppUser.getAppUserId())
                        .and(where("fk_app_role")
                        .is(roleAppUser.getAppRoleId())))
                )
                .all();
    }
}
