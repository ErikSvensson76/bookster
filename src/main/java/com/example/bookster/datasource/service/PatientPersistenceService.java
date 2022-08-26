package com.example.bookster.datasource.service;

import com.example.bookster.datasource.models.DBPatient;

import java.util.UUID;

public interface PatientPersistenceService extends GenericPersistenceService<DBPatient, UUID> {
}
