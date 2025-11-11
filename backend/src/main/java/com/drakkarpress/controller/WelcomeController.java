package com.drakkarpress.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WelcomeController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("application", "DrakkarPress Platform");
        response.put("version", "1.0.0");
        response.put("status", "Running");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "⚔️ Welcome to DrakkarPress API");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("health", "/api/health");
        endpoints.put("database", "/api/health/db");
        endpoints.put("auth", "/api/auth/login");
        endpoints.put("books", "/api/books");
        endpoints.put("actuator", "/actuator/health");
        endpoints.put("generators", "file:///c:/Users/SuperUsuario/DrakkarPress.com/generators.html");
        
        response.put("endpoints", endpoints);
        
        return ResponseEntity.ok(response);
    }
}
