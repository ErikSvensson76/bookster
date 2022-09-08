package com.example.bookster.graphql.controllers;

import com.example.bookster.graphql.facade.AppRoleService;
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
public class AppRoleController {

    private final AppRoleService appRoleService;

    @BatchMapping(typeName = "AppUser", field = "roles")
    public Mono<Map<AppUser, List<AppRole>>> roles(final List<AppUser> appUsers){
        return Mono.from(Flux.fromIterable(appUsers)
                .flatMapSequential(appUser -> appRoleService.findByAppUserId(Mono.just(appUser.getId()))
                        .collectList()
                        .zipWith(Mono.just(appUser))
                )
                .map(tuple2 -> {
                    tuple2.getT2().setRoles(tuple2.getT1());
                    return tuple2.getT2();
                }).collectList()
                .map(roles -> {
                    final Map<AppUser, List<AppRole>> appRoleListMap = new HashMap<>();
                    roles.forEach(user -> appRoleListMap.put(user, user.getRoles()));
                    return appRoleListMap;
                })
        );
    }

}
