package com.example.bookster.datasource.service.mapping;

import com.example.bookster.datasource.models.*;
import com.example.bookster.graphql.models.dto.*;
import com.example.bookster.graphql.models.input.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.UUID;

@Component
public class Mapper implements MappingService {

    private String convert(UUID uuid){
        return uuid == null ? null : uuid.toString();
    }

    private UUID convert(String id){
        return id == null ? null : UUID.fromString(id);
    }

    @Override
    public Address convert(DBAddress dbAddress){
        Address address = null;
        if(Objects.nonNull(dbAddress)){
            address = Address.builder()
                    .id(convert(dbAddress.getId()))
                    .street(dbAddress.getStreet())
                    .zipCode(dbAddress.getZipCode())
                    .city(dbAddress.getCity())
                    .build();
        }
        return address;
    }

    @Override
    public DBAddress convert(AddressInput addressInput){
        DBAddress dbAddress = null;
        if(Objects.nonNull(addressInput)){
            dbAddress = DBAddress.builder()
                    .id(convert(addressInput.id()))
                    .city(addressInput.city())
                    .street(addressInput.street())
                    .zipCode(addressInput.zipCode())
                    .build();
        }
        return dbAddress;
    }

    @Override
    public AppRole convert(DBAppRole dbAppRole){
        AppRole appRole = null;
        if(Objects.nonNull(dbAppRole)){
            appRole = AppRole.builder()
                    .id(convert(dbAppRole.getId()))
                    .userRole(dbAppRole.getUserRole())
                    .build();
        }
        return appRole;
    }

    @Override
    public DBAppRole convert(AppRoleInput appRoleInput){
        DBAppRole dbAppRole = null;
        if(Objects.nonNull(appRoleInput)){
            dbAppRole = new DBAppRole(
                    convert(appRoleInput.id()),
                    appRoleInput.userRole()
            );
        }
        return dbAppRole;
    }

    @Override
    public AppUser convert(DBAppUser dbAppUser){
        AppUser appUser = null;
        if(Objects.nonNull(dbAppUser)){
            appUser = AppUser.builder()
                    .id(convert(dbAppUser.getId()))
                    .username(dbAppUser.getUsername())
                    .build();
        }
        return appUser;
    }

    @Override
    public Booking convert(DBBooking dbBooking){
        Booking booking = null;
        if(Objects.nonNull(dbBooking)){
            booking = Booking.builder()
                    .id(convert(dbBooking.getId()))
                    .price(dbBooking.getPrice())
                    .administratorId(dbBooking.getAdministratorId())
                    .dateTime(dbBooking.getDateTime())
                    .vacant(dbBooking.getVacant())
                    .vaccineType(dbBooking.getVaccineType())
                    .patientId(dbBooking.getPatientId())
                    .premisesId(dbBooking.getPremisesId())
                    .build();
        }
        return booking;
    }

    @Override
    public DBBooking convert(BookingInput bookingInput){
        DBBooking dbBooking = null;
        if(Objects.nonNull(bookingInput)){
            dbBooking = DBBooking.builder()
                    .id(convert(bookingInput.id()))
                    .administratorId(bookingInput.administratorId())
                    .dateTime(bookingInput.dateTime())
                    .price(bookingInput.price())
                    .vacant(bookingInput.vacant())
                    .vaccineType(bookingInput.vaccineType())
                    .patientId(convert(bookingInput.patientId()))
                    .premisesId(convert(bookingInput.premisesId()))
                    .build();
        }
        return dbBooking;
    }

    @Override
    public ContactInfo convert(DBContactInfo dbContactInfo){
        ContactInfo contactInfo = null;
        if(Objects.nonNull(dbContactInfo)){
            contactInfo = ContactInfo.builder()
                    .id(convert(dbContactInfo.getId()))
                    .email(dbContactInfo.getEmail())
                    .phone(dbContactInfo.getPhone())
                    .addressId(dbContactInfo.getAddressId())
                    .build();
        }
        return contactInfo;
    }

    @Override
    public DBContactInfo convert(ContactInfoInput contactInfoInput){
        DBContactInfo dbContactInfo = null;
        if(Objects.nonNull(contactInfoInput)){
            dbContactInfo = DBContactInfo.builder()
                    .id(convert(contactInfoInput.id()))
                    .email(contactInfoInput.email())
                    .phone(contactInfoInput.phone())
                    .addressId(convert(contactInfoInput.address().id()))
                    .build();
        }
        return dbContactInfo;
    }

    @Override
    public Patient convert(DBPatient dbPatient){
        Patient patient = null;
        if(Objects.nonNull(dbPatient)){
            patient = Patient.builder()
                    .id(convert(dbPatient.getId()))
                    .birthDate(dbPatient.getBirthDate())
                    .firstName(dbPatient.getFirstName())
                    .lastName(dbPatient.getLastName())
                    .pnr(dbPatient.getPnr())
                    .age(Period.between(dbPatient.getBirthDate(), LocalDate.now()).getYears())
                    .appUserId(dbPatient.getAppUserId())
                    .contactInfoId(dbPatient.getContactInfoId())
                    .build();
        }
        return patient;
    }

    @Override
    public DBPatient convert(PatientInput patientInput){
        DBPatient dbPatient = null;
        if(Objects.nonNull(patientInput)){
            dbPatient = DBPatient.builder()
                    .id(convert(patientInput.id()))
                    .birthDate(patientInput.birthDate())
                    .firstName(patientInput.firstName())
                    .lastName(patientInput.lastName())
                    .pnr(patientInput.pnr())
                    .appUserId(convert(patientInput.appUser().id()))
                    .contactInfoId(convert(patientInput.contactInfo().id()))
                    .build();
        }
        return dbPatient;
    }

    @Override
    public Premises convert(DBPremises dbPremises){
        Premises premises = null;
        if(Objects.nonNull(dbPremises)){
            premises = Premises.builder()
                    .id(convert(dbPremises.getId()))
                    .premisesName(dbPremises.getPremisesName())
                    .premisesAddressId(dbPremises.getPremisesAddressId())
                    .build();
        }
        return premises;
    }

    @Override
    public DBPremises convert(PremisesInput premisesInput){
        DBPremises dbPremises = null;
        if(Objects.nonNull(premisesInput)){
            dbPremises = DBPremises.builder()
                    .id(convert(premisesInput.id()))
                    .premisesName(premisesInput.premisesName())
                    .premisesAddressId(convert(premisesInput.address().id()))
                    .build();
        }
        return dbPremises;
    }

    @Override
    public DBRoleAppUser convert(RoleAppUserInput roleAppUserInput){
        DBRoleAppUser dbRoleAppUser = null;
        if(Objects.nonNull(roleAppUserInput)){
            dbRoleAppUser = DBRoleAppUser.builder()
                    .appRoleId(convert(roleAppUserInput.appRoleId()))
                    .appUserId(convert(roleAppUserInput.appUserId()))
                    .build();
        }
        return dbRoleAppUser;
    }

}
