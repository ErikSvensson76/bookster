package com.example.bookster.graphql.controllers;

import com.example.bookster.graphql.facade.AppUserService;
import com.example.bookster.graphql.models.dto.AppRole;
import com.example.bookster.graphql.models.dto.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @BatchMapping(typeName = "AppRole", field = "members")
    public Mono<Map<AppRole, List<AppUser>>> members(final List<AppRole> appRoles){
        return Mono.from(Flux.fromIterable(appRoles)
                .flatMapSequential(appRole -> appUserService.findByAppRoleId(Mono.just(appRole.getId()))
                        .collectList()
                        .zipWith(Mono.just(appRole))
                )
                        .map(tuple2 -> {
                            tuple2.getT2().setMembers(tuple2.getT1());
                            return tuple2.getT2();
                        }).collectList()
                .map(roles -> {
                    final Map<AppRole, List<AppUser>> appRoleListMap = new HashMap<>();
                    roles.forEach(role -> appRoleListMap.put(role, role.getMembers()));
                    return appRoleListMap;
                })
        );
    }















}
