package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBPatient;
import com.example.bookster.datasource.service.generic.GenericR2DBCService;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PatientPersistenceService extends GenericR2DBCService<DBPatient> {
    public PatientPersistenceService(R2dbcEntityTemplate template){
        super(template, DBPatient.class);
    }
}
