package com.drakkarpress.platform.controller.admin;

import com.drakkarpress.platform.dto.StrategicReportRequest;
import com.drakkarpress.platform.service.StrategicReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
public class StrategicReportController {

    @Autowired
    private StrategicReportService reportService;

    @PostMapping(value = "/generate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> generate(@RequestBody StrategicReportRequest request) {
        String markdown = reportService.generateReport(request);
        Map<String,Object> body = new HashMap<>();
        body.put("success", true);
        body.put("format", "markdown");
        body.put("content", markdown);
        return ResponseEntity.ok(body);
    }
}