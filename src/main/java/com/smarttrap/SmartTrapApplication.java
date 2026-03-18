package com.smarttrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartTrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartTrapApplication.class, args);
        System.out.println("""
                
                ╔══════════════════════════════════════════════════════╗
                ║         SmartTrap AI — IoT Threat Intelligence       ║
                ║         Platform Started on http://localhost:8080    ║
                ╚══════════════════════════════════════════════════════╝
                """);
    }
}
