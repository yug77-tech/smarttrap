package com.smarttrap.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "iot_devices")
public class IoTDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_name", nullable = false, length = 100)
    private String deviceName;

    @Column(name = "device_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Column(name = "virtual_ip", nullable = false, unique = true, length = 45)
    private String virtualIp;

    @Column(name = "open_port")
    private Integer openPort;

    @Column(name = "firmware_version", length = 20)
    private String firmwareVersion;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "vulnerability_level")
    private int vulnerabilityLevel; // 1-10

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "total_attacks")
    private int totalAttacks;

    @Column(name = "os", length = 50)
    private String os;

    public Long getId() { return id; }
    public String getDeviceName() { return deviceName; }
    public DeviceType getDeviceType() { return deviceType; }
    public String getVirtualIp() { return virtualIp; }
    public Integer getOpenPort() { return openPort; }
    public String getFirmwareVersion() { return firmwareVersion; }
    public boolean isActive() { return active; }
    public int getVulnerabilityLevel() { return vulnerabilityLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getTotalAttacks() { return totalAttacks; }
    public String getOs() { return os; }

    public void setId(Long id) { this.id = id; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public void setDeviceType(DeviceType deviceType) { this.deviceType = deviceType; }
    public void setVirtualIp(String virtualIp) { this.virtualIp = virtualIp; }
    public void setOpenPort(Integer openPort) { this.openPort = openPort; }
    public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }
    public void setActive(boolean active) { this.active = active; }
    public void setVulnerabilityLevel(int vulnerabilityLevel) { this.vulnerabilityLevel = vulnerabilityLevel; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setTotalAttacks(int totalAttacks) { this.totalAttacks = totalAttacks; }
    public void setOs(String os) { this.os = os; }

    public static IoTDeviceBuilder builder() {
        return new IoTDeviceBuilder();
    }

    public static class IoTDeviceBuilder {
        private Long id;
        private String deviceName;
        private DeviceType deviceType;
        private String virtualIp;
        private Integer openPort;
        private String firmwareVersion;
        private boolean active;
        private int vulnerabilityLevel;
        private LocalDateTime createdAt;
        private int totalAttacks;
        private String os;

        public IoTDeviceBuilder id(Long id) { this.id = id; return this; }
        public IoTDeviceBuilder deviceName(String deviceName) { this.deviceName = deviceName; return this; }
        public IoTDeviceBuilder deviceType(DeviceType deviceType) { this.deviceType = deviceType; return this; }
        public IoTDeviceBuilder virtualIp(String virtualIp) { this.virtualIp = virtualIp; return this; }
        public IoTDeviceBuilder openPort(Integer openPort) { this.openPort = openPort; return this; }
        public IoTDeviceBuilder firmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; return this; }
        public IoTDeviceBuilder active(boolean active) { this.active = active; return this; }
        public IoTDeviceBuilder vulnerabilityLevel(int vulnerabilityLevel) { this.vulnerabilityLevel = vulnerabilityLevel; return this; }
        public IoTDeviceBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public IoTDeviceBuilder totalAttacks(int totalAttacks) { this.totalAttacks = totalAttacks; return this; }
        public IoTDeviceBuilder os(String os) { this.os = os; return this; }

        public IoTDevice build() {
            IoTDevice device = new IoTDevice();
            device.id = this.id;
            device.deviceName = this.deviceName;
            device.deviceType = this.deviceType;
            device.virtualIp = this.virtualIp;
            device.openPort = this.openPort;
            device.firmwareVersion = this.firmwareVersion;
            device.active = this.active;
            device.vulnerabilityLevel = this.vulnerabilityLevel;
            device.createdAt = this.createdAt;
            device.totalAttacks = this.totalAttacks;
            device.os = this.os;
            return device;
        }
    }

    public enum DeviceType {
        SMART_CAMERA,
        SMART_THERMOSTAT,
        INDUSTRIAL_SENSOR,
        SMART_LOCK,
        MEDICAL_DEVICE,
        SMART_METER,
        ROUTER,
        PLC_CONTROLLER
    }
}
