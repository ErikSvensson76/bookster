package com.example.bookster.datasource.service.persistence;

import com.example.bookster.datasource.models.DBAppUser;
import com.example.bookster.datasource.service.generic.GenericR2DBCService;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AppUserPersistenceService extends GenericR2DBCService<DBAppUser> {
    public AppUserPersistenceService(R2dbcEntityTemplate template){
        super(template, DBAppUser.class);
    }
}
