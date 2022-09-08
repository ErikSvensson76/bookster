package com.example.bookster.graphql.controllers;

import com.example.bookster.graphql.facade.PremisesService;
import com.example.bookster.graphql.models.dto.Booking;
import com.example.bookster.graphql.models.dto.Premises;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PremisesController {

    private final PremisesService premisesService;

    @BatchMapping(typeName = "Booking", field = "premises")
    public Mono<Map<Booking, Premises>> premises(List<Booking> bookings){
        return Flux.fromIterable(bookings)
                .flatMap(booking -> premisesService.findByBookingId(Mono.just(booking.getId())))
                .collectMap(premises -> bookings.stream()
                        .filter(booking -> booking.getPremisesId().equals(premises.getId()))
                        .findFirst()
                        .orElseThrow()
                );
    }

}
