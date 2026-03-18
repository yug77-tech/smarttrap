package com.smarttrap.ml;

import com.smarttrap.model.AttackLog;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DecisionTreeClassifier {

    private TreeNode root;
    private final Map<AttackLog.AttackType, Integer> classCounts = new EnumMap<>(AttackLog.AttackType.class);
    private int totalPredictions = 0;

    public DecisionTreeClassifier() {
        buildHardcodedTree();
    }

    private static class TreeNode {
        int featureIndex = -1;
        double threshold;
        TreeNode left;   // feature <= threshold
        TreeNode right;  // feature > threshold
        AttackLog.AttackType prediction; // leaf
        double confidence;

        // Leaf node
        TreeNode(AttackLog.AttackType prediction, double confidence) {
            this.prediction = prediction;
            this.confidence = confidence;
        }

        // Split node
        TreeNode(int featureIndex, double threshold) {
            this.featureIndex = featureIndex;
            this.threshold = threshold;
        }

        boolean isLeaf() { return prediction != null; }
    }

    private void buildHardcodedTree() {
        root = new TreeNode(2, 0.6);

        TreeNode left = new TreeNode(4, 0.5);
        root.left = left;

        TreeNode ll = new TreeNode(5, 0.5);
        left.left = ll;

        TreeNode lll = new TreeNode(3, 0.5);

        lll.left  = new TreeNode(AttackLog.AttackType.MAN_IN_THE_MIDDLE, 0.82);
        lll.right = new TreeNode(AttackLog.AttackType.BRUTE_FORCE, 0.91);

        TreeNode llr = new TreeNode(0, 0.3);
        ll.right = llr;
        llr.left  = new TreeNode(AttackLog.AttackType.COMMAND_INJECTION, 0.88);
        llr.right = new TreeNode(AttackLog.AttackType.REPLAY_ATTACK, 0.78);

        TreeNode lr = new TreeNode(1, 0.4);
        lr.left  = new TreeNode(AttackLog.AttackType.SQL_INJECTION, 0.94);
        lr.right = new TreeNode(AttackLog.AttackType.MALWARE_INJECTION, 0.86);

        TreeNode right = new TreeNode(6, 0.7);
        root.right = right;

        TreeNode rl = new TreeNode(7, 0.5);
        right.left = rl;

        TreeNode rll = new TreeNode(0, 0.5);
        rl.left = rll;
        rll.left  = new TreeNode(AttackLog.AttackType.PORT_SCANNING, 0.89);
        rll.right = new TreeNode(AttackLog.AttackType.BRUTE_FORCE, 0.84);

        rl.right = new TreeNode(AttackLog.AttackType.PORT_SCANNING, 0.92);

        TreeNode rr = new TreeNode(1, 0.6);
        right.right = rr;
        rr.left  = new TreeNode(AttackLog.AttackType.DDOS, 0.95);
        rr.right = new TreeNode(AttackLog.AttackType.MALWARE_INJECTION, 0.87);
    }

    public double[] extractFeatures(int port, String payload, int requestRate,
                                     int packetCount, String protocol) {
        double[] features = new double[8];

        features[0] = Math.min(port, 65535) / 65535.0;

        int payloadLen = (payload != null) ? payload.length() : 0;
        features[1] = Math.min(payloadLen, 1000) / 1000.0;

        features[2] = Math.min(requestRate, 100) / 100.0;

        String pl = payload != null ? payload.toLowerCase() : "";
        features[3] = (pl.contains("password") || pl.contains("passwd") ||
                       pl.contains("login") || pl.contains("admin") ||
                       pl.contains("credential")) ? 1.0 : 0.0;

        features[4] = (pl.contains("select") || pl.contains("union") ||
                       pl.contains("drop") || pl.contains("insert") ||
                       pl.contains("' or") || pl.contains("--")) ? 1.0 : 0.0;

        features[5] = (pl.contains("/bin/sh") || pl.contains("bash") ||
                       pl.contains("cmd.exe") || pl.contains("exec(") ||
                       pl.contains("system(") || pl.contains("wget ") ||
                       pl.contains("curl ")) ? 1.0 : 0.0;

        // 6: packet count normalized
        features[6] = Math.min(packetCount, 1000) / 1000.0;

        // 7: protocol (TCP=0, UDP=0.5, ICMP=1)
        features[7] = switch (protocol != null ? protocol.toUpperCase() : "TCP") {
            case "UDP"  -> 0.5;
            case "ICMP" -> 1.0;
            default     -> 0.0;
        };

        return features;
    }

    // ─── Predict ────────────────────────────────────────────────────────────

    public ClassificationResult predict(double[] features) {
        TreeNode node = root;
        while (!node.isLeaf()) {
            if (features[node.featureIndex] <= node.threshold) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        totalPredictions++;
        classCounts.merge(node.prediction, 1, Integer::sum);
        return new ClassificationResult(node.prediction, node.confidence);
    }

    /** Convenience method — predict directly from raw inputs */
    public ClassificationResult classifyAttack(int port, String payload,
                                                int requestRate, int packetCount,
                                                String protocol) {
        double[] features = extractFeatures(port, payload, requestRate, packetCount, protocol);
        return predict(features);
    }

    /** Determine severity from attack type + confidence */
    public AttackLog.Severity classifySeverity(AttackLog.AttackType type, double confidence) {
        return switch (type) {
            case DDOS, MALWARE_INJECTION -> confidence > 0.85 ? AttackLog.Severity.CRITICAL : AttackLog.Severity.HIGH;
            case COMMAND_INJECTION, SQL_INJECTION -> AttackLog.Severity.HIGH;
            case BRUTE_FORCE, MAN_IN_THE_MIDDLE -> AttackLog.Severity.MEDIUM;
            case PORT_SCANNING, REPLAY_ATTACK -> confidence > 0.85 ? AttackLog.Severity.MEDIUM : AttackLog.Severity.LOW;
        };
    }

    public int getTotalPredictions() { return totalPredictions; }
    public Map<AttackLog.AttackType, Integer> getClassCounts() { return Collections.unmodifiableMap(classCounts); }

    // ─── Result DTO ─────────────────────────────────────────────────────────

    public record ClassificationResult(
            AttackLog.AttackType attackType,
            double confidence
    ) {}
}
