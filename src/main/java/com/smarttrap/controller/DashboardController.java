package com.smarttrap.controller;

import com.smarttrap.iot.IoTDeviceSimulator;
import com.smarttrap.model.AttackLog;
import com.smarttrap.service.AttackLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DashboardController {

    private final AttackLogService attackLogService;
    private final IoTDeviceSimulator deviceSimulator;

    public DashboardController(AttackLogService attackLogService, IoTDeviceSimulator deviceSimulator) {
        this.attackLogService = attackLogService;
        this.deviceSimulator = deviceSimulator;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("summary",        attackLogService.getDashboardSummary());
        model.addAttribute("liveFeed",       attackLogService.getLiveFeed(20));
        model.addAttribute("topThreats",     attackLogService.getTopPriorityThreats(10));
        model.addAttribute("topIps",         attackLogService.getTopAttackingIps(5));
        model.addAttribute("topDevices",     attackLogService.getTopTargetedDevices(5));
        model.addAttribute("attackTypes",    attackLogService.getAttackTypeDistribution());
        model.addAttribute("severityDist",   attackLogService.getSeverityDistribution());
        model.addAttribute("devices",        deviceSimulator.getAllDevices());
        model.addAttribute("attacksPerDay",  attackLogService.getAttacksPerDay(7));
        return "dashboard";
    }

    @GetMapping("/logs")
    public String logs(@RequestParam(required = false) String severity,
                       @RequestParam(required = false) String type,
                       @RequestParam(required = false) String ip,
                       Model model) {
        List<AttackLog> logs;

        if (ip != null && !ip.isBlank()) {
            logs = attackLogService.searchByIp(ip);
            model.addAttribute("filterLabel", "IP: " + ip);
        } else if (severity != null && !severity.isBlank()) {
            logs = attackLogService.filterBySeverity(AttackLog.Severity.valueOf(severity.toUpperCase()));
            model.addAttribute("filterLabel", "Severity: " + severity);
        } else if (type != null && !type.isBlank()) {
            logs = attackLogService.filterByType(AttackLog.AttackType.valueOf(type.toUpperCase()));
            model.addAttribute("filterLabel", "Type: " + type.replace("_"," "));
        } else {
            logs = attackLogService.getAllRecent(50);
            model.addAttribute("filterLabel", "All Recent");
        }

        model.addAttribute("logs", logs);
        model.addAttribute("severities", AttackLog.Severity.values());
        model.addAttribute("attackTypes", AttackLog.AttackType.values());
        return "logs";
    }

    @GetMapping("/devices")
    public String devices(Model model) {
        model.addAttribute("devices", deviceSimulator.getAllDevices());
        model.addAttribute("topDevices", attackLogService.getTopTargetedDevices(8));
        return "devices";
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {
        model.addAttribute("attackTypes",      attackLogService.getAttackTypeDistribution());
        model.addAttribute("severityDist",     attackLogService.getSeverityDistribution());
        model.addAttribute("attacksPerDay",    attackLogService.getAttacksPerDay(30));
        model.addAttribute("topIps",           attackLogService.getTopAttackingIps(10));
        model.addAttribute("summary",          attackLogService.getDashboardSummary());
        model.addAttribute("weeklyByType",     attackLogService.getWeeklyAttacksByType());
        model.addAttribute("temporalPatterns", attackLogService.getTemporalPatterns());
        return "analytics";
    }
}
