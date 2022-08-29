package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBContactInfo;
import com.example.bookster.datasource.service.generic.GenericR2DBCService;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ContactInfoPersistenceService extends GenericR2DBCService<DBContactInfo> {
    public ContactInfoPersistenceService(R2dbcEntityTemplate template){
        super(template, DBContactInfo.class);
    }
}
