package com.example.bookster.graphql.controllers;

import com.example.bookster.graphql.facade.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;



}
