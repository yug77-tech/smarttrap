package com.smarttrap.dsa;

import com.smarttrap.model.AttackLog;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DSA Component 3: HashMap-based IP Frequency Tracker.
 * O(1) lookup and update for real-time attacker profiling.
 */
@Component
public class IpFrequencyMap {

    // Map<IP, Map<AttackType, count>>
    private final Map<String, Map<AttackLog.AttackType, Integer>> ipAttackMap = new HashMap<>();
    private final Map<String, Integer> ipTotalCount = new HashMap<>();
    private final Map<String, Long> ipLastSeen = new HashMap<>();

    /** O(1) — Record attack from IP */
    public synchronized void record(String ip, AttackLog.AttackType type) {
        ipTotalCount.merge(ip, 1, Integer::sum);
        ipAttackMap.computeIfAbsent(ip, k -> new EnumMap<>(AttackLog.AttackType.class))
                   .merge(type, 1, Integer::sum);
        ipLastSeen.put(ip, System.currentTimeMillis());
    }

    /** O(1) — Get total attack count for IP */
    public synchronized int getTotalCount(String ip) {
        return ipTotalCount.getOrDefault(ip, 0);
    }

    /** O(k log k) — Get top k attacking IPs */
    public synchronized List<Map.Entry<String, Integer>> getTopAttackers(int k) {
        return ipTotalCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(k)
                .collect(Collectors.toList());
    }

    /** O(n) — Get most common attack type for an IP */
    public synchronized AttackLog.AttackType getMostCommonAttack(String ip) {
        Map<AttackLog.AttackType, Integer> typeMap = ipAttackMap.get(ip);
        if (typeMap == null) return null;
        return typeMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /** O(1) — Check if IP is a repeat offender (> 5 attacks) */
    public synchronized boolean isRepeatOffender(String ip) {
        return getTotalCount(ip) > 5;
    }

    /** O(1) */
    public synchronized int getUniqueIpCount() { return ipTotalCount.size(); }

    /** Get all IP stats as list */
    public synchronized List<Map.Entry<String, Integer>> getAllIpStats() {
        return new ArrayList<>(ipTotalCount.entrySet());
    }

    public synchronized void clear() {
        ipAttackMap.clear();
        ipTotalCount.clear();
        ipLastSeen.clear();
    }
}
