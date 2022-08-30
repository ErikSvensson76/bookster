package com.example.bookster.datasource.service.persistence;

import com.example.bookster.datasource.models.DBAppRole;
import com.example.bookster.datasource.service.generic.GenericR2DBCService;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AppRolePersistenceService extends GenericR2DBCService<DBAppRole> {
    public AppRolePersistenceService(R2dbcEntityTemplate template){
        super(template, DBAppRole.class);
    }
}
