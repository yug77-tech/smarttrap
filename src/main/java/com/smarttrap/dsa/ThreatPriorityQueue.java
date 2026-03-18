package com.smarttrap.dsa;

import com.smarttrap.model.AttackLog;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * DSA Component 2: Max-Heap Priority Queue — threats sorted by severity.
 * CRITICAL > HIGH > MEDIUM > LOW for rapid incident response.
 */
@Component
public class ThreatPriorityQueue {

    private final PriorityQueue<AttackLog> queue;

    public ThreatPriorityQueue() {
        this.queue = new PriorityQueue<>((a, b) ->
                getSeverityScore(b.getSeverity()) - getSeverityScore(a.getSeverity())
        );
    }

    private int getSeverityScore(AttackLog.Severity severity) {
        return switch (severity) {
            case LOW      -> 1;
            case MEDIUM   -> 2;
            case HIGH     -> 3;
            case CRITICAL -> 4;
        };
    }

    public synchronized void enqueue(AttackLog log) {
        queue.offer(log);
    }

    public synchronized AttackLog dequeueHighest() {
        return queue.poll();
    }

    public synchronized List<AttackLog> peekSorted(int limit) {
        List<AttackLog> all = new ArrayList<>(queue);
        all.sort((a, b) -> getSeverityScore(b.getSeverity()) - getSeverityScore(a.getSeverity()));
        return all.subList(0, Math.min(limit, all.size()));
    }

    public synchronized int size() { return queue.size(); }

    public synchronized boolean isEmpty() { return queue.isEmpty(); }

    public synchronized AttackLog peekHighest() { return queue.peek(); }

    public synchronized long countBySeverity(AttackLog.Severity severity) {
        return queue.stream()
                .filter(a -> a.getSeverity() == severity)
                .count();
    }
}
