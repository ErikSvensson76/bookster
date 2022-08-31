package com.example.bookster.datasource.service.mapping;

import com.example.bookster.datasource.models.*;
import com.example.bookster.graphql.models.dto.*;
import com.example.bookster.graphql.models.input.*;

public interface MappingService {
    Address convert(DBAddress dbAddress);

    DBAddress convert(AddressInput addressInput);

    AppRole convert(DBAppRole dbAppRole);

    DBAppRole convert(AppRoleInput appRoleInput);

    AppUser convert(DBAppUser dbAppUser);

    DBAppUser convert(AppUserInput appUserInput);

    Booking convert(DBBooking dbBooking);

    DBBooking convert(BookingInput bookingInput);

    ContactInfo convert(DBContactInfo dbContactInfo);

    DBContactInfo convert(ContactInfoInput contactInfoInput);

    Patient convert(DBPatient dbPatient);

    DBPatient convert(PatientInput patientInput);

    Premises convert(DBPremises dbPremises);

    DBPremises convert(PremisesInput premisesInput);

    DBRoleAppUser convert(RoleAppUserInput roleAppUserInput);
}
