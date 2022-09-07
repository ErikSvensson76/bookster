package com.example.bookster.graphql.facade;

import com.example.bookster.datasource.models.DBAddress;
import com.example.bookster.datasource.models.DBAppUser;
import com.example.bookster.datasource.models.DBContactInfo;
import com.example.bookster.datasource.models.DBPatient;
import com.example.bookster.datasource.service.PatientDBService;
import com.example.bookster.datasource.service.mapping.MappingService;
import com.example.bookster.graphql.models.dto.Patient;
import com.example.bookster.graphql.models.input.PatientInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientDBService patientDBService;
    private final MappingService mappingService;

    @Override
    public Mono<Patient> persist(Mono<PatientInput> patientMono) {
        return patientMono.flatMap(patientInput -> {
            DBPatient dbPatient = mappingService.convert(patientInput);
            DBContactInfo dbContactInfo = mappingService.convert(patientInput.contactInfo());
            DBAddress dbAddress = mappingService.convert(patientInput.contactInfo().address());
            DBAppUser dbAppUser = mappingService.convert(patientInput.appUser());

            return patientDBService.persist(
                    Mono.just(dbPatient),
                    Mono.just(dbContactInfo),
                    Mono.just(dbAddress),
                    Mono.just(dbAppUser)
            );
        }).map(mappingService::convert);
    }

    @Override
    public Flux<Patient> findAll() {
        return patientDBService.findAll()
                .map(mappingService::convert);
    }

    @Override
    public Mono<Patient> findByBookingId(Mono<String> bookingId) {
        return bookingId.map(mappingService::convert)
                .flatMap(uuid -> patientDBService.findByBookingId(Mono.just(uuid)))
                .map(mappingService::convert);
    }

    @Override
    public Flux<Patient> findByCity(Mono<String> city) {
        return patientDBService.findByCity(city)
                .map(mappingService::convert);
    }

    @Override
    public Mono<Patient> findByUsername(Mono<String> username) {
        return patientDBService.findByUsername(username)
                .map(mappingService::convert);
    }

    @Override
    public Mono<Patient> findById(Mono<String> id) {
        return id.map(mappingService::convert)
                .flatMap(uuid -> patientDBService.findById(Mono.just(uuid)))
                .map(mappingService::convert);
    }

    @Override
    public Mono<Patient> update(Mono<PatientInput> patientMono) {
        return patientMono.map(mappingService::convert)
                .flatMap(dbPatient -> patientDBService.update(Mono.just(dbPatient)))
                .map(mappingService::convert);
    }

    @Override
    public Mono<Void> delete(Mono<String> id) {
        return patientDBService.delete(id.map(mappingService::convert));
    }
}
