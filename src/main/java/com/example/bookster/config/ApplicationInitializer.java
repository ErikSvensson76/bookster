package com.example.bookster.config;

import com.example.bookster.datasource.models.DBAppRole;
import com.example.bookster.datasource.repository.AppRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitializer {

    @Value("${spring.profiles.active}")
    String profile;

    private final AppRoleRepository appRoleRepository;

    @PostConstruct
    @Transactional
    void postConstruct(){
        if(!profile.equals("test")){
            Mono.from(appRoleRepository.count())
                    .zipWith(Flux.fromStream(Stream.of("ROLE_APP_USER", "ROLE_APP_ADMIN"))
                            .map(s -> new DBAppRole(null, s)).collectList())
                    .flatMap(tuple2 -> {
                        Long count = tuple2.getT1();
                        if(count == 0){
                            return appRoleRepository.saveAll(tuple2.getT2()).then();
                        }
                        return Mono.empty();
                    }).subscribe();
        }
    }

}
