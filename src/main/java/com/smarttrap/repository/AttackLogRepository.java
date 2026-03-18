package com.smarttrap.repository;

import com.smarttrap.model.AttackLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttackLogRepository extends JpaRepository<AttackLog, Long> {

    List<AttackLog> findTop50ByOrderByTimestampDesc();

    List<AttackLog> findBySeverityOrderByTimestampDesc(AttackLog.Severity severity);

    List<AttackLog> findByAttackTypeOrderByTimestampDesc(AttackLog.AttackType attackType);

    List<AttackLog> findBySourceIpOrderByTimestampDesc(String sourceIp);

    long countBySeverity(AttackLog.Severity severity);

    long countByAttackType(AttackLog.AttackType attackType);

    long countByBlocked(boolean blocked);

    @Query("SELECT a.attackType, COUNT(a) FROM AttackLog a GROUP BY a.attackType ORDER BY COUNT(a) DESC")
    List<Object[]> countByAttackTypeGrouped();

    @Query("SELECT a.severity, COUNT(a) FROM AttackLog a GROUP BY a.severity")
    List<Object[]> countBySeverityGrouped();

    @Query("SELECT a.sourceIp, COUNT(a) as cnt FROM AttackLog a GROUP BY a.sourceIp ORDER BY cnt DESC")
    List<Object[]> topAttackingIps();

    @Query("SELECT a FROM AttackLog a WHERE a.timestamp >= :since ORDER BY a.timestamp DESC")
    List<AttackLog> findRecentAttacks(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(a) FROM AttackLog a WHERE a.timestamp >= :since")
    long countRecentAttacks(@Param("since") LocalDateTime since);

    @Query("SELECT a.targetDevice, COUNT(a) as cnt FROM AttackLog a GROUP BY a.targetDevice ORDER BY cnt DESC")
    List<Object[]> topTargetedDevices();

    @Query("SELECT function('formatdatetime', a.timestamp, 'yyyy-MM-dd'), COUNT(a) FROM AttackLog a WHERE a.timestamp >= :since GROUP BY function('formatdatetime', a.timestamp, 'yyyy-MM-dd') ORDER BY function('formatdatetime', a.timestamp, 'yyyy-MM-dd')")
    List<Object[]> attacksPerDay(@Param("since") LocalDateTime since);

    @Query("SELECT function('hour', a.timestamp), COUNT(a) FROM AttackLog a GROUP BY function('hour', a.timestamp) ORDER BY function('hour', a.timestamp)")
    List<Object[]> attacksByHour();

    // Multi-type weekly chart — returns (attack_type, count) for the period
    @Query("SELECT a.attackType, COUNT(a) FROM AttackLog a WHERE a.timestamp >= :since AND a.attackType IN ('DDOS', 'SQL_INJECTION', 'BRUTE_FORCE') GROUP BY a.attackType")
    List<Object[]> weeklyAttacksByType(@Param("since") LocalDateTime since);

    // For temporal pattern analysis
    @Query("SELECT a.attackType, a.sourceIp, COUNT(a) as cnt FROM AttackLog a WHERE a.timestamp >= :since GROUP BY a.attackType, a.sourceIp HAVING COUNT(a) > 3 ORDER BY COUNT(a) DESC")
    List<Object[]> repeatedPatterns(@Param("since") LocalDateTime since);
}
