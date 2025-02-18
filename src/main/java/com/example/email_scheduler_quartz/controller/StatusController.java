package com.example.email_scheduler_quartz.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {
    @GetMapping("/status")
    private ResponseEntity<String> healthCheck() {
        return ResponseEntity.status(HttpStatus.OK).body("READY");
    }
}
