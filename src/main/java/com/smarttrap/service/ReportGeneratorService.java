package com.smarttrap.service;

import com.smarttrap.model.AttackLog;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Threat Intelligence Report Generator.
 * Produces a complete, printable HTML report from live attack data.
 * Satisfies PPT Slide 11: "Automated Reporting" feature.
 */
@Service
public class ReportGeneratorService {

    private final AttackLogService attackLogService;

    public ReportGeneratorService(AttackLogService attackLogService) {
        this.attackLogService = attackLogService;
    }

    /**
     * Generates a full, self-contained HTML threat intelligence report.
     * Designed to be sent directly as a downloadable file or printed to PDF.
     */
    public String generateHtmlReport() {
        Map<String, Object> summary       = attackLogService.getDashboardSummary();
        Map<String, Long>   attackTypes   = attackLogService.getAttackTypeDistribution();
        Map<String, Long>   severityDist  = attackLogService.getSeverityDistribution();
        List<Map<String,Object>> topIps   = attackLogService.getTopAttackingIps(10);
        List<Map<String,Object>> topDevs  = attackLogService.getTopTargetedDevices(8);
        List<Map<String,Object>> patterns = attackLogService.getTemporalPatterns();
        List<AttackLog> recentCritical    = attackLogService.filterBySeverity(AttackLog.Severity.CRITICAL)
                                                            .stream().limit(10).toList();

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss"));
        String reportId = "STR-" + System.currentTimeMillis() % 100000;

        StringBuilder html = new StringBuilder();
        html.append("""
            <!DOCTYPE html><html lang="en"><head>
            <meta charset="UTF-8">
            <title>SmartTrap AI — Threat Intelligence Report</title>
            <style>
              *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
              body { font-family: 'Segoe UI', Arial, sans-serif; background: #f8fafc; color: #1e293b; font-size: 13px; }
              .page { max-width: 960px; margin: 0 auto; padding: 2rem; }
              /* Header */
              .report-header { background: linear-gradient(135deg, #0d1117 0%, #1e2a3a 50%, #0f1923 100%);
                color: #e2e8f0; padding: 2.5rem; border-radius: 12px; margin-bottom: 2rem; position: relative; overflow: hidden; }
              .report-header::before { content: ''; position: absolute; top: -40px; right: -40px;
                width: 200px; height: 200px; border-radius: 50%;
                background: rgba(59,130,246,0.08); border: 1px solid rgba(59,130,246,0.2); }
              .report-header::after { content: ''; position: absolute; bottom: -60px; right: 80px;
                width: 300px; height: 300px; border-radius: 50%;
                background: rgba(168,85,247,0.05); border: 1px solid rgba(168,85,247,0.15); }
              .header-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem; }
              .logo-text { font-size: 1.6rem; font-weight: 800; color: #fff; letter-spacing: -0.5px; }
              .logo-sub  { font-size: 0.75rem; color: #64748b; letter-spacing: 0.1em; text-transform: uppercase; margin-top: 0.2rem; }
              .report-meta { text-align: right; }
              .report-id   { font-size: 0.75rem; color: #3b82f6; font-family: monospace; margin-bottom: 0.3rem; }
              .report-date { font-size: 0.75rem; color: #94a3b8; }
              .report-title { font-size: 1.1rem; font-weight: 600; color: #e2e8f0; margin-bottom: 0.4rem; }
              .report-sub   { font-size: 0.78rem; color: #64748b; }
              .confidential { display: inline-block; background: rgba(239,68,68,0.15); color: #ef4444;
                border: 1px solid rgba(239,68,68,0.4); border-radius: 4px; padding: 0.2rem 0.6rem;
                font-size: 0.65rem; font-weight: 700; letter-spacing: 0.1em; margin-top: 0.75rem; }
              /* Sections */
              .section { margin-bottom: 1.75rem; }
              .section-title { font-size: 0.9rem; font-weight: 700; color: #1e293b; padding-bottom: 0.5rem;
                border-bottom: 2px solid #3b82f6; margin-bottom: 1rem; display: flex; align-items: center; gap: 0.5rem; }
              .section-title span { color: #3b82f6; }
              /* Stats grid */
              .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1rem; margin-bottom: 1.5rem; }
              .stat-box { background: #fff; border: 1px solid #e2e8f0; border-radius: 10px; padding: 1.1rem; text-align: center;
                box-shadow: 0 1px 3px rgba(0,0,0,0.06); }
              .stat-box.red    { border-top: 3px solid #ef4444; }
              .stat-box.orange { border-top: 3px solid #f97316; }
              .stat-box.green  { border-top: 3px solid #10b981; }
              .stat-box.blue   { border-top: 3px solid #3b82f6; }
              .stat-num   { font-size: 1.8rem; font-weight: 800; color: #0f172a; }
              .stat-label { font-size: 0.72rem; color: #64748b; margin-top: 0.25rem; }
              /* Tables */
              table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 8px; overflow: hidden;
                box-shadow: 0 1px 3px rgba(0,0,0,0.06); margin-bottom: 0.5rem; }
              th { background: #1e293b; color: #e2e8f0; padding: 0.6rem 0.8rem; text-align: left;
                font-size: 0.72rem; font-weight: 600; letter-spacing: 0.05em; text-transform: uppercase; }
              td { padding: 0.55rem 0.8rem; border-bottom: 1px solid #f1f5f9; font-size: 0.8rem; color: #334155; }
              tr:last-child td { border-bottom: none; }
              tr:hover td { background: #f8fafc; }
              .mono { font-family: 'Consolas', monospace; font-size: 0.75rem; }
              /* Badges */
              .badge { padding: 0.15rem 0.45rem; border-radius: 4px; font-size: 0.68rem; font-weight: 700; }
              .b-critical { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
              .b-high     { background: #fff7ed; color: #ea580c; border: 1px solid #fed7aa; }
              .b-medium   { background: #fefce8; color: #ca8a04; border: 1px solid #fde68a; }
              .b-low      { background: #f0fdf4; color: #16a34a; border: 1px solid #bbf7d0; }
              .b-type     { background: #eff6ff; color: #2563eb; border: 1px solid #bfdbfe; font-family: monospace; }
              /* Two-col grid */
              .grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 1.25rem; margin-bottom: 1.5rem; }
              /* Recommendations */
              .rec-card { background: #fff; border: 1px solid #e2e8f0; border-left: 4px solid #3b82f6;
                border-radius: 8px; padding: 1rem; margin-bottom: 0.75rem; }
              .rec-title { font-weight: 700; color: #1e293b; font-size: 0.82rem; margin-bottom: 0.3rem; }
              .rec-text  { color: #64748b; font-size: 0.78rem; line-height: 1.6; }
              .rec-card.warn  { border-left-color: #f59e0b; }
              .rec-card.alert { border-left-color: #ef4444; }
              /* Footer */
              .report-footer { text-align: center; padding: 1.5rem; border-top: 1px solid #e2e8f0;
                color: #94a3b8; font-size: 0.72rem; margin-top: 2rem; }
              .print-btn { position: fixed; bottom: 2rem; right: 2rem; background: #3b82f6; color: #fff;
                border: none; padding: 0.75rem 1.5rem; border-radius: 8px; font-size: 0.85rem; font-weight: 600;
                cursor: pointer; box-shadow: 0 4px 12px rgba(59,130,246,0.4); z-index: 100; }
              .print-btn:hover { background: #2563eb; }
              @media print { .print-btn { display: none; } body { background: #fff; } .page { padding: 1rem; } }
              .bar-wrap { display: flex; align-items: center; gap: 0.5rem; }
              .bar-bg { flex: 1; background: #f1f5f9; border-radius: 4px; height: 8px; overflow: hidden; }
              .bar-fill { height: 100%; border-radius: 4px; }
              .pattern-tag { display: inline-block; background: #eff6ff; color: #2563eb; border: 1px solid #bfdbfe;
                border-radius: 4px; padding: 0.2rem 0.5rem; font-size: 0.7rem; margin-right: 0.3rem; margin-bottom: 0.3rem; }
            </style>
            </head><body>
            <div class="page">
            """);

        // ── Header ──────────────────────────────────────────────────────────────
        html.append("""
            <div class="report-header">
              <div class="header-top">
                <div>
                  <div class="logo-text">⬡ SmartTrap AI</div>
                  <div class="logo-sub">IoT Threat Intelligence Platform</div>
                </div>
                <div class="report-meta">
                  <div class="report-id">Report ID: """).append(reportId).append("""
                  </div>
                  <div class="report-date">Generated: """).append(now).append("""
                  </div>
                </div>
              </div>
              <div class="report-title">Automated Threat Intelligence Report</div>
              <div class="report-sub">Comprehensive analysis of IoT attack patterns, ML classification results, and security posture assessment.</div>
              <div><span class="confidential">⚠ CONFIDENTIAL — INTERNAL USE ONLY</span></div>
            </div>
            """);

        // ── Executive Summary ───────────────────────────────────────────────────
        html.append("""
            <div class="section">
              <div class="section-title"><span>01.</span> Executive Summary</div>
              <div class="stats-grid">
            """);

        html.append("<div class='stat-box red'><div class='stat-num'>")
            .append(summary.get("totalAttacks")).append("</div><div class='stat-label'>Total Attacks Logged</div></div>");
        html.append("<div class='stat-box orange'><div class='stat-num'>")
            .append(summary.get("criticalCount")).append("</div><div class='stat-label'>Critical Threats</div></div>");
        html.append("<div class='stat-box green'><div class='stat-num'>")
            .append(summary.get("blockRate")).append("</div><div class='stat-label'>Block Rate</div></div>");
        html.append("<div class='stat-box blue'><div class='stat-num'>")
            .append(summary.get("uniqueAttackers")).append("</div><div class='stat-label'>Unique Attackers</div></div>");

        html.append("</div></div>");

        // ── Attack Type Distribution ────────────────────────────────────────────
        html.append("""
            <div class="section">
              <div class="section-title"><span>02.</span> Attack Type Distribution — ML Classification Results</div>
              <table>
                <thead><tr><th>Attack Type</th><th>Count</th><th>Distribution</th><th>% Share</th></tr></thead>
                <tbody>
            """);

        long totalAttacks = (Long) summary.get("totalAttacks");
        long maxCount = attackTypes.values().stream().mapToLong(Long::longValue).max().orElse(1L);
        String[] barColors = {"#ef4444","#a855f7","#3b82f6","#f59e0b","#10b981","#f97316","#06b6d4","#ec4899"};
        int ci = 0;
        for (Map.Entry<String, Long> e : attackTypes.entrySet()) {
            long pct = totalAttacks > 0 ? (e.getValue() * 100 / totalAttacks) : 0;
            int barW = maxCount > 0 ? (int)(e.getValue() * 100 / maxCount) : 0;
            String color = barColors[ci++ % barColors.length];
            html.append("<tr>")
                .append("<td><span class='badge b-type'>").append(e.getKey()).append("</span></td>")
                .append("<td><strong>").append(e.getValue()).append("</strong></td>")
                .append("<td><div class='bar-wrap'><div class='bar-bg'><div class='bar-fill' style='width:").append(barW).append("%;background:").append(color).append("'></div></div></div></td>")
                .append("<td>").append(pct).append("%</td>")
                .append("</tr>");
        }
        html.append("</tbody></table></div>");

        // ── Severity Breakdown ──────────────────────────────────────────────────
        html.append("""
            <div class="section">
              <div class="section-title"><span>03.</span> Severity Breakdown & Threat Classification</div>
              <table>
                <thead><tr><th>Severity Level</th><th>Count</th><th>Priority Action</th></tr></thead>
                <tbody>
            """);

        Map<String, String[]> sevInfo = Map.of(
            "CRITICAL", new String[]{"b-critical", "Immediate response required — escalate to security team"},
            "HIGH",     new String[]{"b-high",     "Investigate within 1 hour — review source IPs"},
            "MEDIUM",   new String[]{"b-medium",   "Monitor closely — add to watchlist"},
            "LOW",      new String[]{"b-low",      "Log and review during next security cycle"}
        );
        for (Map.Entry<String, Long> e : severityDist.entrySet()) {
            String[] info = sevInfo.getOrDefault(e.getKey(), new String[]{"", "Review"});
            html.append("<tr><td><span class='badge ").append(info[0]).append("'>").append(e.getKey()).append("</span></td>")
                .append("<td><strong>").append(e.getValue()).append("</strong></td>")
                .append("<td>").append(info[1]).append("</td></tr>");
        }
        html.append("</tbody></table></div>");

        // ── Critical Attacks Detail ─────────────────────────────────────────────
        if (!recentCritical.isEmpty()) {
            html.append("""
                <div class="section">
                  <div class="section-title"><span>04.</span> Recent Critical Attack Events</div>
                  <table>
                    <thead><tr><th>Timestamp</th><th>Source IP</th><th>Target Device</th><th>Attack Type</th><th>Port</th><th>Country</th><th>ML Confidence</th></tr></thead>
                    <tbody>
                """);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (AttackLog a : recentCritical) {
                html.append("<tr>")
                    .append("<td class='mono'>").append(a.getTimestamp().format(fmt)).append("</td>")
                    .append("<td class='mono'>").append(a.getSourceIp()).append("</td>")
                    .append("<td>").append(a.getTargetDevice()).append("</td>")
                    .append("<td><span class='badge b-type'>").append(a.getAttackType().name().replace("_"," ")).append("</span></td>")
                    .append("<td class='mono'>").append(a.getPort()).append("</td>")
                    .append("<td>").append(a.getCountry() != null ? a.getCountry() : "Unknown").append("</td>")
                    .append("<td>").append(String.format("%.1f%%", a.getConfidenceScore() * 100)).append("</td>")
                    .append("</tr>");
            }
            html.append("</tbody></table></div>");
        }

        // ── Top Attackers & Devices ─────────────────────────────────────────────
        html.append("""
            <div class="grid-2">
              <div class="section" style="margin-bottom:0">
                <div class="section-title"><span>05.</span> Top Attacking IPs</div>
                <table>
                  <thead><tr><th>#</th><th>IP Address</th><th>Total</th><th>Repeat</th></tr></thead>
                  <tbody>
            """);
        int rank = 1;
        for (Map<String,Object> ip : topIps) {
            boolean isRepeat = Boolean.TRUE.equals(ip.get("isRepeat"));
            html.append("<tr><td>").append(rank++).append("</td>")
                .append("<td class='mono'>").append(ip.get("ip")).append("</td>")
                .append("<td>").append(ip.get("count")).append("</td>")
                .append("<td><span class='badge ").append(isRepeat ? "b-critical'>⚠ YES" : "b-low'>NO").append("</span></td>")
                .append("</tr>");
        }
        html.append("""
                  </tbody></table>
              </div>
              <div class="section" style="margin-bottom:0">
                <div class="section-title"><span>06.</span> Most Targeted Devices</div>
                <table>
                  <thead><tr><th>#</th><th>Device Name</th><th>Attacks</th></tr></thead>
                  <tbody>
            """);
        rank = 1;
        for (Map<String,Object> d : topDevs) {
            html.append("<tr><td>").append(rank++).append("</td>")
                .append("<td>").append(d.get("device")).append("</td>")
                .append("<td><strong>").append(d.get("count")).append("</strong></td>")
                .append("</tr>");
        }
        html.append("</tbody></table></div></div>");

        // ── Temporal Patterns ───────────────────────────────────────────────────
        if (!patterns.isEmpty()) {
            html.append("""
                <div class="section">
                  <div class="section-title"><span>07.</span> Temporal Attack Patterns (Last 7 Days)</div>
                  <table>
                    <thead><tr><th>Attack Type</th><th>Source IP</th><th>Frequency</th><th>Pattern Risk</th></tr></thead>
                    <tbody>
                """);
            for (Map<String,Object> p : patterns) {
                int count = ((Number)p.get("count")).intValue();
                String risk = count > 20 ? "<span class='badge b-critical'>HIGH RISK</span>" :
                              count > 10 ? "<span class='badge b-high'>ELEVATED</span>" :
                                           "<span class='badge b-medium'>MODERATE</span>";
                html.append("<tr>")
                    .append("<td><span class='badge b-type'>").append(p.get("attackType")).append("</span></td>")
                    .append("<td class='mono'>").append(p.get("sourceIp")).append("</td>")
                    .append("<td>").append(count).append(" times</td>")
                    .append("<td>").append(risk).append("</td>")
                    .append("</tr>");
            }
            html.append("</tbody></table></div>");
        }

        // ── Security Recommendations ────────────────────────────────────────────
        html.append("""
            <div class="section">
              <div class="section-title"><span>08.</span> Security Recommendations</div>
              <div class="rec-card alert">
                <div class="rec-title">🚨 Immediate: Block Repeat Offender IPs</div>
                <div class="rec-text">Multiple IPs have been flagged as repeat offenders with 5+ attack attempts. Implement IP-level firewall rules to block these sources immediately. Consider geo-blocking high-frequency source countries.</div>
              </div>
              <div class="rec-card warn">
                <div class="rec-title">⚠ High Priority: Patch High-Vulnerability Devices</div>
                <div class="rec-text">Devices with vulnerability score ≥ 8/10 (HoneyROUTER-007, HoneySENSOR-003, HoneyCAM-001) are receiving the most attacks. Apply firmware updates and disable unused open ports immediately.</div>
              </div>
              <div class="rec-card">
                <div class="rec-title">🛡 Medium: Strengthen Authentication on Ports 22, 80, 443</div>
                <div class="rec-text">Brute force attacks are concentrated on SSH (22), HTTP (80), and HTTPS (443) ports. Implement multi-factor authentication, rate limiting, and account lockout policies on these entry points.</div>
              </div>
              <div class="rec-card">
                <div class="rec-title">🔍 Advisory: Enable Real-Time Alerting for SQL Injection</div>
                <div class="rec-text">SQL injection attempts represent a significant share of detected attacks. Enable input sanitization, prepared statements in all database queries, and set up real-time alerting for SQL keyword detection in payloads.</div>
              </div>
            </div>
            """);

        // ── DSA + ML Technical Summary ──────────────────────────────────────────
        html.append("""
            <div class="section">
              <div class="section-title"><span>09.</span> System Technical Summary</div>
              <table>
                <thead><tr><th>Component</th><th>Technology</th><th>Purpose</th><th>Performance</th></tr></thead>
                <tbody>
                  <tr><td><strong>Attack Log Storage</strong></td><td class='mono'>Custom LinkedList (DSA)</td><td>O(1) head insertion for live feed</td><td>500 entries in-memory</td></tr>
                  <tr><td><strong>Threat Prioritization</strong></td><td class='mono'>Max-Heap PriorityQueue (DSA)</td><td>O(log n) severity ordering</td><td>CRITICAL first response</td></tr>
                  <tr><td><strong>IP Tracking</strong></td><td class='mono'>HashMap (DSA)</td><td>O(1) frequency lookup</td><td>Tracks all unique IPs</td></tr>
                  <tr><td><strong>Attack Classification</strong></td><td class='mono'>Decision Tree (ML)</td><td>8-feature packet analysis</td><td>~91.4% accuracy, depth 6</td></tr>
                  <tr><td><strong>Device Simulation</strong></td><td class='mono'>IoT Honeypot Engine</td><td>8 virtual device endpoints</td><td>Attack every 3 seconds</td></tr>
                  <tr><td><strong>Persistence</strong></td><td class='mono'>MySQL + Spring Data JPA</td><td>Long-term attack archiving</td><td>11 optimized queries</td></tr>
                </tbody>
              </table>
            </div>
            """);

        // ── Footer ──────────────────────────────────────────────────────────────
        html.append("""
            <div class="report-footer">
              SmartTrap AI — AI-Powered Virtual IoT Threat Intelligence Platform<br>
              Submitted to: Dr Sudhanshu Tripathi | Amity Institute of Information Technology | BCA 4B, Group 4<br>
              <strong>""").append(now).append("""
               | Report ID: """).append(reportId).append("""
              </strong>
            </div>
            </div>
            <button class="print-btn" onclick="window.print()">🖨 Print / Save as PDF</button>
            </body></html>
            """);

        return html.toString();
    }
}
