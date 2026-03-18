package com.smarttrap.iot;

import com.smarttrap.dsa.AttackLinkedList;
import com.smarttrap.dsa.IpFrequencyMap;
import com.smarttrap.dsa.ThreatPriorityQueue;
import com.smarttrap.ml.DecisionTreeClassifier;
import com.smarttrap.model.AttackLog;
import com.smarttrap.model.IoTDevice;
import com.smarttrap.repository.AttackLogRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Logger;

@Component
public class AttackSimulator {

    private static final Logger log = Logger.getLogger(AttackSimulator.class.getName());

    private final IoTDeviceSimulator deviceSimulator;
    private final DecisionTreeClassifier classifier;
    private final AttackLogRepository attackLogRepository;
    private final AttackLinkedList attackLinkedList;
    private final ThreatPriorityQueue threatPriorityQueue;
    private final IpFrequencyMap ipFrequencyMap;

    private final Random random = new Random();

    public AttackSimulator(
            IoTDeviceSimulator deviceSimulator,
            DecisionTreeClassifier classifier,
            AttackLogRepository attackLogRepository,
            AttackLinkedList attackLinkedList,
            ThreatPriorityQueue threatPriorityQueue,
            IpFrequencyMap ipFrequencyMap
    ) {
        this.deviceSimulator = deviceSimulator;
        this.classifier = classifier;
        this.attackLogRepository = attackLogRepository;
        this.attackLinkedList = attackLinkedList;
        this.threatPriorityQueue = threatPriorityQueue;
        this.ipFrequencyMap = ipFrequencyMap;
    }

    private static final String[] ATTACKER_IPS = {
            "185.220.101.47", "45.33.32.156",  "198.7.62.204",  "91.240.118.172",
            "194.165.16.78",  "103.214.147.3", "77.247.181.163","162.247.74.200",
            "185.130.44.108", "178.73.215.171","5.188.206.14",  "141.98.11.130",
            "193.32.162.44",  "23.129.64.137", "171.25.193.25", "64.113.32.29",
            "109.70.100.81",  "176.10.104.240","199.87.154.255","216.218.135.130"
    };

    private static final String[] COUNTRIES = {
            "Russia", "China", "Netherlands", "Germany", "USA",
            "Romania", "Ukraine", "Brazil", "India", "Iran"
    };

    private static final String[] PROTOCOLS = {"TCP", "UDP", "ICMP"};

    @Scheduled(fixedDelayString = "${smarttrap.simulation.interval-ms:3000}")
    public void simulateAttacks() {
        IoTDevice target = deviceSimulator.getRandomTarget();
        if (target == null) return;

        int numAttacks = 1 + random.nextInt(3); // 1-3 attacks per cycle

        for (int i = 0; i < numAttacks; i++) {
            try {
                generateAndProcessAttack(target);
            } catch (Exception e) {
                log.warning("Error simulating attack: " + e.getMessage());
            }
        }
    }

    private void generateAndProcessAttack(IoTDevice target) {
        String attackerIp = ATTACKER_IPS[random.nextInt(ATTACKER_IPS.length)];
        String country    = COUNTRIES[random.nextInt(COUNTRIES.length)];
        String protocol   = PROTOCOLS[random.nextInt(PROTOCOLS.length)];
        int port          = target.getOpenPort();
        int requestRate   = 5 + random.nextInt(95);
        int packetCount   = 1 + random.nextInt(999);

        String payloadHint = pickAttackHint(port, protocol, requestRate, packetCount);
        String payload     = IoTDeviceSimulator.generatePayload(payloadHint, port);

        DecisionTreeClassifier.ClassificationResult result =
                classifier.classifyAttack(port, payload, requestRate, packetCount, protocol);

        AttackLog.Severity severity = classifier.classifySeverity(result.attackType(), result.confidence());

        AttackLog attackLog = AttackLog.builder()
                .sourceIp(attackerIp)
                .targetDevice(target.getDeviceName())
                .attackType(result.attackType())
                .severity(severity)
                .port(port)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .blocked(random.nextDouble() < 0.78)
                .confidenceScore(result.confidence())
                .mlClassified(true)
                .country(country)
                .protocol(protocol)
                .build();

        AttackLog saved = attackLogRepository.save(attackLog);

        attackLinkedList.addFirst(saved);
        threatPriorityQueue.enqueue(saved);
        ipFrequencyMap.record(attackerIp, result.attackType());

        deviceSimulator.recordAttackOnDevice(target);

        if (severity == AttackLog.Severity.CRITICAL || severity == AttackLog.Severity.HIGH) {
            log.warning(
                    "[THREAT] " + result.attackType() + " from " + attackerIp + " on " + saved.getTargetDevice()
                    + " (port " + port + ") | Severity: " + severity + " | Confidence: "
                    + String.format("%.1f", result.confidence() * 100) + "%"
            );
        }
    }

    private String pickAttackHint(int port, String protocol, int reqRate, int packetCount) {
        if ("ICMP".equals(protocol) && packetCount > 500) return "DDOS";
        if (reqRate > 80 && packetCount > 700) return "DDOS";
        if (port == 22 || port == 23 || port == 3389) return "BRUTE_FORCE";
        if (port == 80 || port == 8080 || port == 443) {
            int r = random.nextInt(3);
            return r == 0 ? "SQL_INJECTION" : r == 1 ? "COMMAND_INJECTION" : "BRUTE_FORCE";
        }
        if (reqRate > 60) return "PORT_SCANNING";
        String[] types = {"BRUTE_FORCE","SQL_INJECTION","COMMAND_INJECTION",
                          "MALWARE_INJECTION","PORT_SCANNING","MAN_IN_THE_MIDDLE","REPLAY_ATTACK"};
        return types[random.nextInt(types.length)];
    }
}
