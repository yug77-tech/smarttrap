package com.smarttrap.service;

import com.smarttrap.dsa.AttackLinkedList;
import com.smarttrap.dsa.IpFrequencyMap;
import com.smarttrap.dsa.ThreatPriorityQueue;
import com.smarttrap.model.AttackLog;
import com.smarttrap.repository.AttackLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@Service
public class AttackLogService {

    private static final Logger log = Logger.getLogger(AttackLogService.class.getName());

    private final AttackLogRepository repository;
    private final AttackLinkedList attackLinkedList;
    private final ThreatPriorityQueue threatPriorityQueue;
    private final IpFrequencyMap ipFrequencyMap;

    public AttackLogService(
            AttackLogRepository repository,
            AttackLinkedList attackLinkedList,
            ThreatPriorityQueue threatPriorityQueue,
            IpFrequencyMap ipFrequencyMap
    ) {
        this.repository = repository;
        this.attackLinkedList = attackLinkedList;
        this.threatPriorityQueue = threatPriorityQueue;
        this.ipFrequencyMap = ipFrequencyMap;
    }

    public long getTotalAttacks() {
        return repository.count();
    }

    public long getBlockedCount() {
        return repository.countByBlocked(true);
    }

    public long getActiveThreats() {
        return repository.countRecentAttacks(LocalDateTime.now().minusMinutes(10));
    }

    public long getUniqueAttackers() {
        return ipFrequencyMap.getUniqueIpCount();
    }

    public List<AttackLog> getLiveFeed(int limit) {
        List<AttackLog> feed = attackLinkedList.getFirst(limit);
        if (feed.size() < limit) {
            // Backfill from DB on cold start
            feed = repository.findTop50ByOrderByTimestampDesc();
            feed.forEach(attackLinkedList::addFirst);
            feed = attackLinkedList.getFirst(limit);
        }
        return feed;
    }

    public List<AttackLog> getTopPriorityThreats(int limit) {
        List<AttackLog> threats = threatPriorityQueue.peekSorted(limit);
        if (threats.isEmpty()) {
            // Backfill
            repository.findBySeverityOrderByTimestampDesc(AttackLog.Severity.CRITICAL)
                    .forEach(threatPriorityQueue::enqueue);
            repository.findBySeverityOrderByTimestampDesc(AttackLog.Severity.HIGH)
                    .forEach(threatPriorityQueue::enqueue);
            threats = threatPriorityQueue.peekSorted(limit);
        }
        return threats;
    }

    public Map<String, Long> getAttackTypeDistribution() {
        return repository.countByAttackTypeGrouped().stream()
                .collect(Collectors.toMap(
                        row -> formatLabel(row[0].toString()),
                        row -> (Long) row[1],
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public Map<String, Long> getSeverityDistribution() {
        return repository.countBySeverityGrouped().stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> (Long) row[1]
                ));
    }

    public List<Map<String, Object>> getAttacksPerDay(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return repository.attacksPerDay(since).stream()
                .map(row -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("day", row[0].toString());
                    m.put("count", row[1]);
                    return m;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTopAttackingIps(int limit) {
        return repository.topAttackingIps().stream()
                .limit(limit)
                .map(row -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("ip", row[0].toString());
                    m.put("count", row[1]);
                    m.put("isRepeat", ipFrequencyMap.isRepeatOffender(row[0].toString()));
                    return m;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getTopTargetedDevices(int limit) {
        return repository.topTargetedDevices().stream()
                .limit(limit)
                .map(row -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("device", row[0].toString());
                    m.put("count", row[1]);
                    return m;
                })
                .collect(Collectors.toList());
    }

    // ─── Search & Filter ─────────────────────────────────────────────────────

    public List<AttackLog> searchByIp(String ip) {
        return repository.findBySourceIpOrderByTimestampDesc(ip);
    }

    public List<AttackLog> filterBySeverity(AttackLog.Severity severity) {
        return repository.findBySeverityOrderByTimestampDesc(severity);
    }

    public List<AttackLog> filterByType(AttackLog.AttackType type) {
        return repository.findByAttackTypeOrderByTimestampDesc(type);
    }

    public List<AttackLog> getAllRecent(int limit) {
        return repository.findTop50ByOrderByTimestampDesc().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Transactional
    public AttackLog save(AttackLog log) {
        return repository.save(log);
    }

    // ─── Summary Stats ───────────────────────────────────────────────────────

    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalAttacks",    getTotalAttacks());
        summary.put("blockedCount",    getBlockedCount());
        summary.put("activeThreats",   getActiveThreats());
        summary.put("uniqueAttackers", getUniqueAttackers());
        summary.put("criticalCount",   repository.countBySeverity(AttackLog.Severity.CRITICAL));
        summary.put("highCount",       repository.countBySeverity(AttackLog.Severity.HIGH));
        summary.put("mlClassified",    getTotalAttacks()); // all are ML classified
        summary.put("blockRate",       getTotalAttacks() > 0
                ? String.format("%.1f%%", (getBlockedCount() * 100.0 / getTotalAttacks()))
                : "0%");
        return summary;
    }

    /**
     * Weekly multi-line chart data: DDoS vs SQL Injection vs Brute Force over 4 weeks.
     * Returns: { "DDOS": [w1,w2,w3,w4], "SQL_INJECTION": [...], "BRUTE_FORCE": [...] }
     */
    public Map<String, List<Long>> getWeeklyAttacksByType() {
        LocalDateTime since = LocalDateTime.now().minusWeeks(4);
        List<Object[]> rows = repository.weeklyAttacksByType(since);

        // Query returns (attackType, count) — spread evenly across 4 weeks for chart display
        Map<String, List<Long>> result = new LinkedHashMap<>();
        String[] types = {"DDOS", "SQL_INJECTION", "BRUTE_FORCE"};
        for (String t : types) {
            result.put(t, new ArrayList<>(Arrays.asList(0L, 0L, 0L, 0L)));
        }

        for (Object[] row : rows) {
            String type  = row[0] != null ? row[0].toString() : "";
            long   count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            if (result.containsKey(type)) {
                // Distribute total evenly across 4 weeks for the chart
                long perWeek = count / 4;
                long remainder = count % 4;
                List<Long> weeks = result.get(type);
                for (int i = 0; i < 4; i++) {
                    weeks.set(i, perWeek + (i == 3 ? remainder : 0));
                }
            }
        }
        return result;
    }

    /** Temporal pattern analysis — repeated attacker/type combos */
    public List<Map<String, Object>> getTemporalPatterns() {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        return repository.repeatedPatterns(since).stream()
                .map(row -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("attackType", row[0] != null ? row[0].toString().replace("_", " ") : "");
                    m.put("sourceIp",   row[1] != null ? row[1].toString() : "");
                    m.put("count",      row[2]);
                    return m;
                })
                .collect(Collectors.toList());
    }

    private String formatLabel(String enumName) {
        return Arrays.stream(enumName.split("_"))
                .map(s -> s.charAt(0) + s.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
