package com.drakkarpress.platform.controller.admin;

import com.drakkarpress.platform.metrics.SocialProvisioningMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/metrics/social")
public class SocialMetricsController {

    @Autowired
    private SocialProvisioningMetrics metrics;

    @GetMapping
    public ResponseEntity<Map<String,Object>> getMetrics() {
        Map<String,Object> body = new HashMap<>();
        body.put("newUsersSocial", metrics.getNewUsers());
        body.put("existingUsersSocialLogins", metrics.getExistingUsers());
        return ResponseEntity.ok(body);
    }
}