package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBBooking;
import com.example.bookster.datasource.service.generic.GenericR2DBCService;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BookingPersistenceService extends GenericR2DBCService<DBBooking> {
    public BookingPersistenceService(R2dbcEntityTemplate template){
        super(template, DBBooking.class);
    }
}
