package com.example.bookster.datasource.service.persistence;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.service.generic.GenericR2DBCService;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AddressPersistenceService extends GenericR2DBCService<DBAddress> {
    public AddressPersistenceService(R2dbcEntityTemplate template) {
        super(template, DBAddress.class);
    }
}
