# SmartTrap AI — IoT Threat Intelligence Platform

> **An AI-Powered Virtual IoT Honeypot & Threat Intelligence System**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-blue)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/License-MIT-green)](#license)
[![Status](https://img.shields.io/badge/Status-Active-brightgreen)](#)

---

## 🎯 Overview

**SmartTrap AI** is a sophisticated AI-powered IoT threat intelligence platform that simulates a virtual honeypot environment. It generates realistic attack scenarios against simulated IoT devices, classifies threats using machine learning, and provides comprehensive threat analysis, reporting, and visualization capabilities.

The platform is designed for:
- **Security Research** — Study attack patterns and threat evolution
- **IoT Security Training** — Understand common IoT vulnerabilities
- **Threat Intelligence** — Generate actionable threat reports
- **ML Experimentation** — Train and test ML-based attack classifiers

### Key Capabilities

✅ **Real-Time Attack Simulation** — Automated attack generation every 3 seconds  
✅ **ML-Powered Classification** — Decision Tree classifier for attack type detection  
✅ **Live Threat Dashboard** — Interactive web-based threat visualization  
✅ **Advanced Analytics** — Attack trends, geographic distribution, temporal patterns  
✅ **Automated Reporting** — Generate professional HTML threat intelligence reports  
✅ **RESTful API** — Programmatic access to all threat data and analytics  
✅ **Data Persistence** — In-memory H2 database with automatic initialization  

---

## 📋 Table of Contents

- [System Architecture](#system-architecture)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [Usage Guide](#usage-guide)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [Project Structure](#project-structure)
- [Component Details](#component-details)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    SmartTrap AI Platform                     │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │               Web Layer (Thymeleaf Templates)        │  │
│  │  ├─ Dashboard (Real-time threat visualization)      │  │
│  │  ├─ Attack Logs (Filterable attack history)         │  │
│  │  ├─ Device Management (IoT device inventory)        │  │
│  │  └─ Analytics (Threat trends & patterns)            │  │
│  └──────────────────────────────────────────────────────┘  │
│                            ▲                                │
│                            │                                │
│  ┌──────────────────────────────────────────────────────┐  │
│  │          REST API Controllers (Spring MVC)           │  │
│  │  ├─ /api/summary (Dashboard metrics)                │  │
│  │  ├─ /api/live-feed (Real-time attacks)              │  │
│  │  ├─ /api/attack-types (Attack distribution)         │  │
│  │  └─ /report/download (Generate reports)             │  │
│  └──────────────────────────────────────────────────────┘  │
│                            ▲                                │
│                            │                                │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         Business Logic Layer (Services)              │  │
│  │  ├─ AttackLogService (Core analytics)               │  │
│  │  ├─ ReportGeneratorService (Report generation)      │  │
│  │  └─ AttackSimulator (Scheduled attack generation)   │  │
│  └──────────────────────────────────────────────────────┘  │
│                            ▲                                │
│                            │                                │
│  ┌──────────────────────────────────────────────────────┐  │
│  │       ML & DSA Components (Intelligence)             │  │
│  │  ├─ DecisionTreeClassifier (Attack classification)  │  │
│  │  ├─ AttackLinkedList (LIFO attack history)          │  │
│  │  ├─ ThreatPriorityQueue (Threat ranking)            │  │
│  │  ├─ IpFrequencyMap (Attacker fingerprinting)        │  │
│  │  └─ IoTDeviceSimulator (Virtual device management)  │  │
│  └──────────────────────────────────────────────────────┘  │
│                            ▲                                │
│                            │                                │
│  ┌──────────────────────────────────────────────────────┐  │
│  │    Data Access Layer (Spring Data JPA)              │  │
│  │  ├─ AttackLogRepository                             │  │
│  │  └─ IoTDeviceRepository                             │  │
│  └──────────────────────────────────────────────────────┘  │
│                            ▲                                │
│                            │                                │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         H2 In-Memory Database                        │  │
│  │  ├─ attack_logs (Attack records)                    │  │
│  │  └─ iot_devices (Virtual device inventory)         │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Data Flow

```
IoT Device Simulator
        ↓
Attack Generator (Scheduled every 3s)
        ↓
Feature Extraction (Port, Payload, Protocol, Request Rate)
        ↓
ML Decision Tree Classifier
        ↓
Severity Classification
        ↓
Persist to H2 Database
        ↓
Update DSA Structures (LinkedList, PriorityQueue, HashMap)
        ↓
Available via REST API & Web Dashboard
```

---

## ✨ Features

### 1. **Attack Simulation Engine**
- **Realistic Attack Generation**: Creates 1-3 attack events every 3 seconds
- **Diverse Attack Types**: 
  - Brute Force
  - Port Scanning
  - SQL Injection
  - Command Injection
  - DDoS
  - Malware Injection
  - Man-in-the-Middle
  - Replay Attacks
- **Authentic Attacker Profiles**: Uses real external IP addresses and countries
- **Configurable Simulation Rate**: Adjust attack frequency via `application.properties`

### 2. **Machine Learning Classification**
- **Decision Tree Classifier**: Pre-trained ML model for attack type detection
- **Feature Engineering**:
  - Port number analysis
  - Payload size detection
  - Request rate analysis
  - Credential detection
  - SQL keyword recognition
  - Shell command detection
  - Protocol classification
- **Confidence Scoring**: ML model provides confidence percentages for each classification
- **78% Block Rate**: Realistic defense mechanism simulation

### 3. **Interactive Web Dashboard**
- **Real-Time Threat Visualization**: Live attack feed with color-coded severity
- **Dashboard Metrics**:
  - Total attacks
  - Blocked vs. unblocked
  - Average confidence
  - Critical threat count
- **Top Threats**: Priority ranking of most dangerous attacks
- **Geographic Distribution**: Attack source countries mapped
- **Attack Trends**: 7-day attack history chart
- **Device Management**: View all simulated IoT devices with vulnerability levels

### 4. **Advanced Analytics**
- **Attack Type Distribution**: Pie chart breakdown of attack categories
- **Severity Distribution**: Severity level analysis (Low, Medium, High, Critical)
- **Top Attacking IPs**: Rank external attackers by frequency
- **Top Targeted Devices**: Identify most vulnerable devices
- **Temporal Patterns**: Hour-by-hour attack frequency analysis
- **Repeated Patterns**: Detect coordinated attack campaigns

### 5. **Automated Reporting**
- **Professional HTML Reports**: Self-contained, printable threat intelligence reports
- **Executive Summary**: High-level statistics and key findings
- **Detailed Analytics**: Charts, graphs, and trend analysis
- **Critical Alerts**: Recent high-severity attacks highlighted
- **Top Threat Analysis**: Comprehensive threat breakdown
- **PDF Export Ready**: Open in browser → Print → Save as PDF

### 6. **RESTful API**
- **Programmatic Access**: JSON-based API for integration with other tools
- **Real-Time Data**: Live attack feeds and threat metrics
- **Filtering & Search**: Query attacks by severity, type, IP, or date range
- **Export Capability**: Download raw data for analysis

### 7. **Data Structure Optimization**
- **Attack LinkedList**: LIFO (Last-In-First-Out) access for recent attacks
- **Threat Priority Queue**: Min-heap for efficient threat ranking
- **IP Frequency Map**: HashMap for rapid attacker fingerprinting
- **Optimized Performance**: O(1) lookups and O(log n) threat updates

---

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Framework** | Spring Boot | 3.2.0 |
| **Language** | Java | 21 |
| **Web Template** | Thymeleaf | 3.1+ |
| **Database** | H2 (In-Memory) | Latest |
| **ORM** | Spring Data JPA | 3.2+ |
| **Build Tool** | Maven | 3.9+ |
| **Validation** | Spring Validation | 3.2+ |
| **JSON** | Jackson | Latest |
| **Testing** | JUnit 5 | Latest |

### Dependencies
- **Spring Boot Starter Web**: Web framework
- **Spring Boot Starter Thymeleaf**: Server-side templating
- **Spring Boot Starter Data JPA**: Database abstraction
- **H2 Database**: In-memory relational database
- **Jackson**: JSON serialization
- **Spring Validation**: Input validation

---

## 📋 Prerequisites

### System Requirements
- **OS**: macOS, Linux, or Windows (with WSL)
- **RAM**: Minimum 2GB (recommended 4GB)
- **Disk Space**: 500MB for installation and runtime

### Software Requirements
- **Java 21+**: OpenJDK or Oracle JDK ([download](https://openjdk.java.net/))
- **Maven 3.9+**: Build automation tool ([download](https://maven.apache.org/))
- **Git**: Version control (optional, for cloning)

### Verify Installation
```bash
java -version
mvn -version
```

---

## 📦 Installation & Setup

### Option 1: Clone & Build from Source

```bash
# Clone the repository
git clone https://github.com/yourusername/smarttrap-ai.git
cd smarttrap-ai

# Build the project
mvn clean package -DskipTests

# The packaged JAR will be at: target/smarttrap-ai-1.0.0.jar
```

### Option 2: Download Pre-Built JAR

Download the latest release from [GitHub Releases](https://github.com/yourusername/smarttrap-ai/releases)

```bash
jar extract smarttrap-ai-1.0.0.jar
```

### Option 3: Docker (Coming Soon)

```bash
docker build -t smarttrap-ai:latest .
docker run -p 8080:8080 smarttrap-ai:latest
```

---

## 🚀 Running the Application

### Start the Application

```bash
# Navigate to project directory
cd /path/to/smarttrap-ai

# Run using Java
java -jar target/smarttrap-ai-1.0.0.jar

# Or run using Maven
mvn spring-boot:run

# Or run directly from IDE
# Open SmartTrapApplication.java and run with Ctrl+Shift+F10 (IntelliJ) or F11 (Eclipse)
```

### Verify Application Started Successfully

You should see:
```
╔══════════════════════════════════════════════════════╗
║         SmartTrap AI — IoT Threat Intelligence       ║
║         Platform Started on http://localhost:8080    ║
╚══════════════════════════════════════════════════════╝
```

### Application URL
- **Web Dashboard**: [http://localhost:8080](http://localhost:8080)
- **H2 Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - URL: `jdbc:h2:mem:smarttrap_db`
  - Username: `sa`
  - Password: (leave empty)

---

## 📖 Usage Guide

### 1. View Dashboard
Navigate to [http://localhost:8080](http://localhost:8080) to see:
- Real-time attack statistics
- Live attack feed (updates every 3 seconds)
- Attack distribution charts
- Geographic threat map
- Device vulnerability dashboard

### 2. Filter Attack Logs
- **By Severity**: Click severity level (Low, Medium, High, Critical)
- **By Attack Type**: Select attack type from dropdown
- **By Source IP**: Enter attacker IP address
- **Date Range**: Use date pickers for time-based filtering

### 3. Manage Devices
View all simulated IoT devices with:
- Device type and firmware version
- Virtual IP address and open ports
- Active status and vulnerability level
- Total attack count

### 4. Generate Reports
1. Navigate to `/report/download`
2. Download HTML report
3. Open in browser
4. Print → Save as PDF for professional documentation

### 5. Use REST API
Make HTTP requests to access data programmatically:

```bash
# Get summary statistics
curl http://localhost:8080/api/summary

# Get live attack feed
curl http://localhost:8080/api/live-feed?limit=50

# Get attack type distribution
curl http://localhost:8080/api/attack-types

# Get severity distribution
curl http://localhost:8080/api/severity-dist
```

---

## 🔌 API Endpoints

### Dashboard & Web UI
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | GET | Main dashboard with threat visualization |
| `/logs` | GET | Attack logs with filtering |
| `/logs?severity=CRITICAL` | GET | Logs by severity |
| `/logs?type=DDOS` | GET | Logs by attack type |
| `/logs?ip=192.168.1.1` | GET | Logs by source IP |
| `/devices` | GET | IoT device management page |
| `/analytics` | GET | Advanced threat analytics |

### REST API - Data Access
| Endpoint | Method | Description | Response |
|----------|--------|-------------|----------|
| `/api/summary` | GET | Dashboard metrics | JSON (total, blocked, critical count, avg confidence) |
| `/api/live-feed` | GET | Recent attacks | JSON array of AttackLog objects |
| `/api/live-feed?limit=50` | GET | Recent attacks (custom limit) | JSON array |
| `/api/attack-types` | GET | Attack type distribution | JSON object with counts |
| `/api/severity-dist` | GET | Severity distribution | JSON object with counts |

### Report Generation
| Endpoint | Method | Description | Response |
|----------|--------|-------------|----------|
| `/report/download` | GET | Generate full HTML report | HTML file (downloadable) |

### Example Responses

**GET /api/summary**
```json
{
  "totalAttacks": 1250,
  "blockedCount": 975,
  "criticalCount": 45,
  "averageConfidence": 0.87,
  "topAttacker": "185.220.101.47",
  "mostCommonType": "BRUTE_FORCE"
}
```

**GET /api/live-feed?limit=5**
```json
[
  {
    "id": 1250,
    "sourceIp": "185.220.101.47",
    "targetDevice": "HoneyCAM-001",
    "attackType": "BRUTE_FORCE",
    "severity": "CRITICAL",
    "port": 22,
    "timestamp": "2026-03-18T15:30:45",
    "blocked": true,
    "confidenceScore": 0.95,
    "country": "Russia",
    "protocol": "TCP"
  },
  ...
]
```

---

## ⚙️ Configuration

### application.properties

```properties
# Server Configuration
server.port=8080
spring.application.name=SmartTrap AI

# Database Configuration (H2 In-Memory)
spring.datasource.url=jdbc:h2:mem:smarttrap_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console (View DB at http://localhost:8080/h2-console)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=create-drop  # Auto-create tables
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.defer-datasource-initialization=true

# Thymeleaf Configuration
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

# Logging
logging.level.com.smarttrap=INFO
logging.level.org.springframework.web=WARN

# Simulation Configuration (Optional)
smarttrap.simulation.interval-ms=3000  # Attack generation interval (ms)
smarttrap.simulation.enabled=true      # Enable/disable attack simulation
```

### Customizing Attack Frequency

To change how often attacks are generated, modify in `application.properties`:

```properties
# Generate attacks every 5 seconds (instead of 3)
smarttrap.simulation.interval-ms=5000
```

Or modify in `AttackSimulator.java`:
```java
@Scheduled(fixedDelayString = "${smarttrap.simulation.interval-ms:3000}")
public void simulateAttacks() { ... }
```

---

## 🗄️ Database Schema

### attack_logs Table

| Column | Type | Description |
|--------|------|-------------|
| `id` | BIGINT (PK) | Unique attack identifier |
| `source_ip` | VARCHAR(45) | Attacker IP address |
| `target_device` | VARCHAR(100) | Target device name |
| `attack_type` | ENUM | Type of attack (BRUTE_FORCE, DDOS, etc.) |
| `severity` | ENUM | Threat level (LOW, MEDIUM, HIGH, CRITICAL) |
| `port` | INT | Target port number |
| `payload` | VARCHAR(500) | Attack payload/signature |
| `timestamp` | TIMESTAMP | When attack occurred |
| `is_blocked` | BOOLEAN | Whether attack was blocked |
| `confidence_score` | DOUBLE | ML classifier confidence (0.0-1.0) |
| `ml_classified` | BOOLEAN | Whether classified by ML model |
| `country` | VARCHAR(50) | Geographic origin of attack |
| `protocol` | VARCHAR(10) | Network protocol (TCP, UDP, ICMP) |

### iot_devices Table

| Column | Type | Description |
|--------|------|-------------|
| `id` | BIGINT (PK) | Device identifier |
| `device_name` | VARCHAR(100) | Human-readable device name |
| `device_type` | ENUM | Type (SMART_CAMERA, THERMOSTAT, etc.) |
| `virtual_ip` | VARCHAR(45) | Simulated IP address (UNIQUE) |
| `open_port` | INT | Exposed service port |
| `firmware_version` | VARCHAR(20) | Firmware version string |
| `is_active` | BOOLEAN | Whether device is active |
| `vulnerability_level` | INT | Vulnerability score (1-10) |
| `created_at` | TIMESTAMP | Device creation time |
| `total_attacks` | INT | Cumulative attack count |
| `os` | VARCHAR(50) | Operating system/firmware |

---

## 📂 Project Structure

```
smarttrap-ai/
│
├── pom.xml                                    # Maven configuration
├── README.md                                  # This file
│
├── src/main/java/com/smarttrap/
│   │
│   ├── SmartTrapApplication.java             # Spring Boot entry point
│   │
│   ├── controller/                           # Web & REST controllers
│   │   ├── ApiController.java               # REST API endpoints
│   │   ├── DashboardController.java         # Web dashboard pages
│   │   ├── ReportController.java            # Report generation
│   │   └── ...
│   │
│   ├── service/                              # Business logic layer
│   │   ├── AttackLogService.java            # Core analytics & queries
│   │   └── ReportGeneratorService.java      # HTML report generation
│   │
│   ├── model/                                # Data models (JPA entities)
│   │   ├── AttackLog.java                   # Attack record entity
│   │   └── IoTDevice.java                   # Device entity
│   │
│   ├── repository/                           # Data access layer
│   │   ├── AttackLogRepository.java         # Attack log queries
│   │   └── IoTDeviceRepository.java         # Device queries
│   │
│   ├── ml/                                   # Machine Learning
│   │   └── DecisionTreeClassifier.java      # Attack classification
│   │
│   ├── dsa/                                  # Data Structures & Algorithms
│   │   ├── AttackLinkedList.java            # LIFO attack history
│   │   ├── ThreatPriorityQueue.java         # Min-heap threat ranking
│   │   └── IpFrequencyMap.java              # Attacker fingerprinting
│   │
│   ├── iot/                                  # IoT Simulation
│   │   ├── AttackSimulator.java             # Scheduled attack generator
│   │   └── IoTDeviceSimulator.java          # Virtual device manager
│   │
│   └── config/                               # Configuration classes
│       └── (Spring bean configurations)
│
├── src/main/resources/
│   │
│   ├── application.properties                # Application configuration
│   │
│   ├── templates/                            # Thymeleaf HTML templates
│   │   ├── dashboard.html                   # Main dashboard
│   │   ├── logs.html                        # Attack logs page
│   │   ├── devices.html                     # Device management
│   │   └── analytics.html                   # Analytics page
│   │
│   └── static/                               # Static assets
│       ├── css/
│       │   └── main.css                     # Dashboard styles
│       └── js/
│           └── main.js                      # Client-side logic
│
├── src/test/java/                           # Unit & integration tests
│
└── target/                                   # Build output
    └── smarttrap-ai-1.0.0.jar              # Executable JAR
```

---

## 🔍 Component Details

### AttackSimulator
- **Location**: `iot/AttackSimulator.java`
- **Purpose**: Scheduled component that generates realistic attack events every 3 seconds
- **Functionality**:
  - Selects random target device
  - Generates attacker IP, country, protocol
  - Creates attack payload
  - Classifies with ML model
  - Persists to database
  - Updates DSA structures
- **Scheduling**: `@Scheduled(fixedDelayString = "${smarttrap.simulation.interval-ms:3000}")`

### DecisionTreeClassifier
- **Location**: `ml/DecisionTreeClassifier.java`
- **Purpose**: ML-based attack type classification
- **Features Used**:
  - Port number
  - Payload size
  - Request rate
  - Credential detection
  - SQL keyword detection
  - Shell command detection
  - Packet count
  - Protocol type
- **Confidence**: Returns probability (0.0-1.0) for classification
- **Pre-trained**: Hard-coded decision rules based on attack signatures

### AttackLinkedList
- **Location**: `dsa/AttackLinkedList.java`
- **Purpose**: LIFO (Last-In-First-Out) access to recent attacks
- **Time Complexity**: O(1) insertion/deletion at head
- **Use Case**: Efficiently retrieve most recent attacks for live feed

### ThreatPriorityQueue
- **Location**: `dsa/ThreatPriorityQueue.java`
- **Purpose**: Min-heap priority queue for threat ranking
- **Ordering**: By severity (CRITICAL > HIGH > MEDIUM > LOW)
- **Time Complexity**: O(log n) insertion/deletion
- **Use Case**: Identify top priority threats for alerts

### IpFrequencyMap
- **Location**: `dsa/IpFrequencyMap.java`
- **Purpose**: HashMap for rapid attacker IP fingerprinting
- **Tracking**: Attack frequency per IP and attack type
- **Time Complexity**: O(1) lookups
- **Use Case**: Detect repeat attackers and coordinated campaigns

---

## 🐛 Troubleshooting

### Issue: Port 8080 Already in Use

**Solution 1**: Change port in `application.properties`
```properties
server.port=9090  # Use different port
```

**Solution 2**: Kill existing process on port 8080
```bash
# macOS/Linux
lsof -i :8080
kill -9 <PID>

# Windows (PowerShell)
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Issue: "BUILD FAILURE" during Maven build

**Cause**: Java version mismatch  
**Solution**: Ensure Java 21+ is installed
```bash
java -version  # Should show Java 21+
```

### Issue: Database not initializing

**Cause**: H2 console not accessible  
**Solution**: Verify in `application.properties`
```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

Then access: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

### Issue: No attacks being generated

**Cause**: Attack simulator not running  
**Solution**: Check if @Scheduled is enabled
```bash
# Check logs for: "IoT Simulation Engine"
grep "THREAT\|attack" <logfile>
```

### Issue: Machine Learning classifier not working

**Cause**: ClassificationResult is null  
**Solution**: Verify DecisionTreeClassifier is initialized in Spring context
```java
@Component
public class DecisionTreeClassifier { ... }
```

---

## 🤝 Contributing

We welcome contributions! Here's how:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** changes (`git commit -m 'Add amazing feature'`)
4. **Push** to branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Development Guidelines
- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Write unit tests for new features
- Update README.md for significant changes
- Use descriptive commit messages

### Future Enhancements
- [ ] Real attack data integration (from SHODAN, CyberGrep)
- [ ] Advanced ML models (Neural Networks, Random Forests)
- [ ] Multi-tenant support
- [ ] Docker containerization
- [ ] Kubernetes deployment manifests
- [ ] Real-time WebSocket updates
- [ ] Mobile application
- [ ] Alert & notification system
- [ ] SIEM integration (Splunk, ELK)
- [ ] Geographic threat mapping with map.js

---

## 📊 Performance & Scalability

### Current Limitations
- **In-Memory Database**: Data lost on application restart
- **Single-Threaded Simulator**: ~3 attacks per second
- **No Clustering**: Single instance only

### Scalability Roadmap
1. **Phase 1**: MySQL/PostgreSQL persistence
2. **Phase 2**: Elasticsearch for attack indexing
3. **Phase 3**: Kafka for distributed event processing
4. **Phase 4**: Microservices architecture
5. **Phase 5**: Kubernetes deployment

### Resource Usage (Single Instance)
- **RAM**: 256MB-512MB
- **CPU**: Minimal (<5% idle)
- **Disk**: 100MB for logs/reports

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

### MIT License Summary
- ✅ Commercial use
- ✅ Modification
- ✅ Distribution
- ✅ Private use
- ❌ Liability
- ❌ Warranty

---

## 📞 Support & Contact

- **Issues**: [GitHub Issues](https://github.com/yourusername/smarttrap-ai/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/smarttrap-ai/discussions)
- **Email**: support@smarttrap-ai.com
- **Documentation**: [https://docs.smarttrap-ai.com](https://docs.smarttrap-ai.com)

---

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- H2 database for in-memory database support
- The cybersecurity community for threat intelligence data
- Contributors and testers

---

## 📈 Project Statistics

| Metric | Value |
|--------|-------|
| **Lines of Code** | ~2,500+ |
| **Classes** | 16+ |
| **API Endpoints** | 10+ |
| **Database Tables** | 2 |
| **Attack Types Supported** | 8 |
| **Features** | 20+ |
| **Test Coverage** | In Progress |

---

## 🎓 Educational Value

This project is excellent for learning:
- ✅ Spring Boot web application development
- ✅ RESTful API design
- ✅ Database design with JPA/Hibernate
- ✅ Machine learning classification
- ✅ Data structures & algorithms (LinkedList, PriorityQueue, HashMap)
- ✅ IoT security concepts
- ✅ Cybersecurity threat analysis
- ✅ Web application architecture
- ✅ Test-driven development
- ✅ DevOps & containerization

---

**Made with ❤️ for the cybersecurity community**

Last Updated: March 18, 2026  
Version: 1.0.0  
Maintained by: SmartTrap Dev Team

---

### Quick Links
- 🌐 [Website](https://smarttrap-ai.com) (Coming Soon)
- 📚 [Documentation](https://docs.smarttrap-ai.com) (Coming Soon)
- 🐛 [Report Bug](https://github.com/yourusername/smarttrap-ai/issues)
- 🎯 [Request Feature](https://github.com/yourusername/smarttrap-ai/discussions)
- ⭐ [Star on GitHub](https://github.com/yourusername/smarttrap-ai)
