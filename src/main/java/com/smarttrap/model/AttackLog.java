package com.smarttrap.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attack_logs")
public class AttackLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_ip", nullable = false, length = 45)
    private String sourceIp;

    @Column(name = "target_device", nullable = false, length = 100)
    private String targetDevice;

    @Column(name = "attack_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private AttackType attackType;

    @Column(name = "severity", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Column(name = "port")
    private Integer port;

    @Column(name = "payload", length = 500)
    private String payload;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "is_blocked")
    private boolean blocked;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "ml_classified")
    private boolean mlClassified;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "protocol", length = 10)
    private String protocol;

    public Long getId() { return id; }
    public String getSourceIp() { return sourceIp; }
    public String getTargetDevice() { return targetDevice; }
    public AttackType getAttackType() { return attackType; }
    public Severity getSeverity() { return severity; }
    public Integer getPort() { return port; }
    public String getPayload() { return payload; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isBlocked() { return blocked; }
    public Double getConfidenceScore() { return confidenceScore; }
    public boolean isMlClassified() { return mlClassified; }
    public String getCountry() { return country; }
    public String getProtocol() { return protocol; }

    public void setId(Long id) { this.id = id; }
    public void setSourceIp(String sourceIp) { this.sourceIp = sourceIp; }
    public void setTargetDevice(String targetDevice) { this.targetDevice = targetDevice; }
    public void setAttackType(AttackType attackType) { this.attackType = attackType; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public void setPort(Integer port) { this.port = port; }
    public void setPayload(String payload) { this.payload = payload; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
    public void setMlClassified(boolean mlClassified) { this.mlClassified = mlClassified; }
    public void setCountry(String country) { this.country = country; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public static AttackLogBuilder builder() {
        return new AttackLogBuilder();
    }

    public static class AttackLogBuilder {
        private Long id;
        private String sourceIp;
        private String targetDevice;
        private AttackType attackType;
        private Severity severity;
        private Integer port;
        private String payload;
        private LocalDateTime timestamp;
        private boolean blocked;
        private Double confidenceScore;
        private boolean mlClassified;
        private String country;
        private String protocol;

        public AttackLogBuilder id(Long id) { this.id = id; return this; }
        public AttackLogBuilder sourceIp(String sourceIp) { this.sourceIp = sourceIp; return this; }
        public AttackLogBuilder targetDevice(String targetDevice) { this.targetDevice = targetDevice; return this; }
        public AttackLogBuilder attackType(AttackType attackType) { this.attackType = attackType; return this; }
        public AttackLogBuilder severity(Severity severity) { this.severity = severity; return this; }
        public AttackLogBuilder port(Integer port) { this.port = port; return this; }
        public AttackLogBuilder payload(String payload) { this.payload = payload; return this; }
        public AttackLogBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public AttackLogBuilder blocked(boolean blocked) { this.blocked = blocked; return this; }
        public AttackLogBuilder confidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; return this; }
        public AttackLogBuilder mlClassified(boolean mlClassified) { this.mlClassified = mlClassified; return this; }
        public AttackLogBuilder country(String country) { this.country = country; return this; }
        public AttackLogBuilder protocol(String protocol) { this.protocol = protocol; return this; }

        public AttackLog build() {
            AttackLog log = new AttackLog();
            log.id = this.id;
            log.sourceIp = this.sourceIp;
            log.targetDevice = this.targetDevice;
            log.attackType = this.attackType;
            log.severity = this.severity;
            log.port = this.port;
            log.payload = this.payload;
            log.timestamp = this.timestamp;
            log.blocked = this.blocked;
            log.confidenceScore = this.confidenceScore;
            log.mlClassified = this.mlClassified;
            log.country = this.country;
            log.protocol = this.protocol;
            return log;
        }
    }

    public enum AttackType {
        BRUTE_FORCE,
        PORT_SCANNING,
        SQL_INJECTION,
        COMMAND_INJECTION,
        DDOS,
        MALWARE_INJECTION,
        MAN_IN_THE_MIDDLE,
        REPLAY_ATTACK
    }

    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
