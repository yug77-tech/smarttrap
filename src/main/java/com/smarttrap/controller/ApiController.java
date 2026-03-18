package com.smarttrap.controller;

import com.smarttrap.service.AttackLogService;
import com.smarttrap.model.AttackLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final AttackLogService attackLogService;

    public ApiController(AttackLogService attackLogService) {
        this.attackLogService = attackLogService;
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        return ResponseEntity.ok(attackLogService.getDashboardSummary());
    }

    @GetMapping("/live-feed")
    public ResponseEntity<List<AttackLog>> getLiveFeed(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(attackLogService.getLiveFeed(limit));
    }

    @GetMapping("/attack-types")
    public ResponseEntity<Map<String, Long>> getAttackTypes() {
        return ResponseEntity.ok(attackLogService.getAttackTypeDistribution());
    }

    @GetMapping("/severity")
    public ResponseEntity<Map<String, Long>> getSeverity() {
        return ResponseEntity.ok(attackLogService.getSeverityDistribution());
    }

    @GetMapping("/top-ips")
    public ResponseEntity<List<Map<String, Object>>> getTopIps(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(attackLogService.getTopAttackingIps(limit));
    }

    @GetMapping("/attacks-per-day")
    public ResponseEntity<List<Map<String, Object>>> getAttacksPerDay(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(attackLogService.getAttacksPerDay(days));
    }

    @GetMapping("/threats")
    public ResponseEntity<List<AttackLog>> getThreats(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(attackLogService.getTopPriorityThreats(limit));
    }
}
