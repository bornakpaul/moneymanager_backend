package com.apptechlab.moneymanager.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/status","/health"})
@Tag(name = "Home Controller",description = "API's to test if the application is running")
public class HomeController {

    @GetMapping
    public String healthCheck() {
        return "Application is running..";
    }
}
