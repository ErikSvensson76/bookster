package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBPremises;
import com.example.bookster.datasource.service.generic.GenericR2DBCService;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PremisesPersistenceService extends GenericR2DBCService<DBPremises> {
    public PremisesPersistenceService(R2dbcEntityTemplate template) {
        super(template, DBPremises.class);
    }
}
