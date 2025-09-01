package com.cd_u.catatdulu_users.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0")
public class StatusController {

    @GetMapping("/condition")
    public String statusCheck() {
        return "Radar Searching...Radar Searching...FenFen Detected!";
    }
}
