package com.smarttrap.repository;

import com.smarttrap.model.IoTDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IoTDeviceRepository extends JpaRepository<IoTDevice, Long> {

    List<IoTDevice> findByActiveTrue();

    Optional<IoTDevice> findByVirtualIp(String virtualIp);

    List<IoTDevice> findByDeviceType(IoTDevice.DeviceType deviceType);

    List<IoTDevice> findByVulnerabilityLevelGreaterThanEqual(int level);
}
