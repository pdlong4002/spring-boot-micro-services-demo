# 🎓 Spring Boot Microservices - Demo

## 📋 Tổng quan

Dự án minh họa kiến trúc microservices hoàn chỉnh với 5 services độc lập:

| Service | Chức năng | Port |
|---------|-----------|------|
| **Config Server** | Quản lý cấu hình tập trung cho tất cả services | 8888 |
| **Discovery Server** | Service Registry - Eureka cho service discovery | 8761 |
| **API Gateway** | Single entry point, routing & load balancing | 8222 |
| **Student Service** | Business logic quản lý học sinh | 8090 |
| **School Service** | Business logic quản lý trường học | 8070 |

## 🏗️ Kiến trúc Microservices

```
┌─────────┐
│ Client  │
└────┬────┘
     │
     ▼
┌─────────────────────┐
│   API Gateway       │ ◄─── Single Entry Point
│   (Port 8222)       │      - Load Balancing
└──────┬──────────────┘      - Routing
       │
       ├──────────────────┬────────────────────┐
       ▼                  ▼                    ▼
┌──────────────┐   ┌──────────────┐    ┌──────────────┐
│   Student    │   │    School    │    │   Discovery  │
│   Service    │◄──┤   Service    │    │   (Eureka)   │
│  (8090)      │   │   (8070)     │    │   (8761)     │
└──────┬───────┘   └──────┬───────┘    └──────┬───────┘
       │                  │                   │
       │                  │                   │
       └──────────────────┴───────────────────┘
                          │
                          ▼
                  ┌───────────────┐
                  │ Config Server │
                  │    (8888)     │
                  └───────────────┘
```

## 🚀 Công nghệ & Giải thích

### Core Framework
- **Spring Boot 4.0.3** - Framework chính để xây dựng các microservices
- **Spring Cloud 2025.1.0** - Bộ công cụ cho distributed systems/microservices
- **Java 21** - Phiên bản Java LTS mới nhất

### Service Discovery Pattern
- **Netflix Eureka** - Service registry cho phép services tự động tìm và gọi nhau
  - Services tự đăng ký khi khởi động
  - Load balancing tự động giữa các instances
  - Health checking

### API Gateway Pattern
- **Spring Cloud Gateway** - Gateway hiện đại với WebFlux (reactive)
  - Single entry point cho tất cả requests
  - Load balancing với `lb://service-name`
  - Route requests tới đúng service
  - Có thể thêm authentication, rate limiting, logging

### Centralized Configuration
- **Spring Cloud Config** - Quản lý cấu hình tập trung
  - Tất cả config ở một nơi thay vì mỗi service một file
  - Thay đổi config không cần rebuild
  - Hỗ trợ profiles (dev, prod, test)

### Inter-Service Communication
- **OpenFeign** - Declarative REST Client
  - Gọi API giữa các services một cách đơn giản
  - Tự động integrate với Eureka và Load Balancer
  - Code sạch hơn so với RestTemplate

### Data Layer
- **Spring Data JPA** - ORM framework
- **Hibernate** - JPA implementation
- **MySQL** - Relational Database

## 🎯 Microservices Patterns Demo

Dự án này minh họa các patterns quan trọng trong kiến trúc microservices:

### 1. Service Registry Pattern (Eureka)
- Services tự động đăng ký khi khởi động
- Discovery các services khác thông qua Eureka
- Health check tự động

### 2. API Gateway Pattern
- Single entry point cho tất cả external requests
- Load balancing giữa multiple instances
- Có thể thêm: authentication, rate limiting, caching

### 3. Externalized Configuration Pattern
- Cấu hình tập trung tại Config Server
- Không cần rebuild khi thay đổi config
- Support multi-environment (dev, staging, prod)

### 4. Client-Side Load Balancing
- Gateway tự động phân tải requests
- Sử dụng `lb://service-name` thay vì hardcode URL
- Ribbon/Spring Cloud LoadBalancer

### 5. Inter-Service Communication
- **Synchronous**: OpenFeign REST calls giữa School ↔ Student
- **Service-to-service**: School Service gọi Student Service để lấy data

## 📦 Yêu cầu & Cài đặt

### Yêu cầu hệ thống
```
- Java 21 (JDK)
- Maven 3.8+
- MySQL 8.0+
- RAM: Tối thiểu 4GB (chạy 5 services đồng thời)
```

### Chuẩn bị Database
```sql
-- Tạo databases
CREATE DATABASE students;
CREATE DATABASE schools;

-- Kiểm tra user
-- username: csdl_longsama
-- password: 12345
```

## 🚀 Khởi động hệ thống

### Cách 1: Khởi động thủ công (Recommended cho Demo)

**Thứ tự quan trọng:**
```bash
# Bước 1: Config Server (phải chạy đầu tiên)
cd config-server
./mvnw spring-boot:run
# Đợi: "Started ConfigServerApplication"

# Bước 2: Discovery Server (Eureka)
cd ../discovery
./mvnw spring-boot:run
# Đợi: "Started Eureka Server"
# Kiểm tra: http://localhost:8761

# Bước 3: Business Services (chạy song song)
cd ../student
./mvnw spring-boot:run

cd ../school
./mvnw spring-boot:run

# Bước 4: API Gateway (chạy cuối)
cd ../gateway
./mvnw spring-boot:run
# Đợi: "Started GatewayApplication"
```

### Cách 2: Windows Script
```bash
# Chạy tất cả trong 1 lệnh (cần tạo script)
start-all-services.bat
```

## 🔌 API Documentation

### Qua API Gateway (Production-like)
```http
# Lấy tất cả students
GET http://localhost:8222/api/v1/students

# Tạo student mới
POST http://localhost:8222/api/v1/students
Content-Type: application/json
{
  "firstname": "Nguyen Van",
  "lastname": "A",
  "email": "nva@example.com",
  "schoolId": 1
}

# Lấy tất cả schools
GET http://localhost:8222/api/v1/schools

# Lấy school với danh sách students (Inter-service call)
GET http://localhost:8222/api/v1/schools/with-students/1
```

### Direct Service Access (Development)
```http
# Gọi trực tiếp Student Service
GET http://localhost:8090/api/v1/students
GET http://localhost:8090/api/v1/students/school/1

# Gọi trực tiếp School Service
GET http://localhost:8070/api/v1/schools
GET http://localhost:8070/api/v1/schools/with-students/1
```

### Admin & Monitoring
```http
# Eureka Dashboard - Xem tất cả services đã đăng ký
http://localhost:8761

# Config Server - Xem cấu hình của từng service
http://localhost:8888/students/default
http://localhost:8888/schools/default
http://localhost:8888/gateway/default
```

## 📝 Cấu hình tập trung

### Cấu trúc Config Server
```
config-server/src/main/resources/
└── configurations/
    ├── students.yml      # Cấu hình cho Student Service
    ├── schools.yml       # Cấu hình cho School Service
    ├── gateway.yml       # Cấu hình cho API Gateway
    └── discovery.yml     # Cấu hình cho Eureka
```

### Ưu điểm
- ✅ Tất cả config ở một nơi, dễ quản lý
- ✅ Thay đổi config không cần rebuild application
- ✅ Support nhiều environments (dev, prod, staging)
- ✅ Services tự động load config khi khởi động

## 🎬 Demo Scenarios

### Scenario 1: Service Discovery
```bash
# 1. Khởi động Config Server và Eureka
# 2. Truy cập: http://localhost:8761
# 3. Khởi động Student và School services
# 4. Reload Eureka Dashboard → Thấy 2 services mới xuất hiện
# 5. Services tự động tìm nhau qua Eureka (không cần biết IP/Port)
```

### Scenario 2: Load Balancing qua Gateway
```bash
# 1. Gọi API qua Gateway:
GET http://localhost:8222/api/v1/students

# 2. Gateway tự động:
#    - Tìm Student Service qua Eureka
#    - Load balance nếu có nhiều instances
#    - Forward request
#    - Trả response về client
```

### Scenario 3: Inter-Service Communication
```bash
# 1. Gọi School với Students:
GET http://localhost:8222/api/v1/schools/with-students/1

# 2. Flow:
#    Client → Gateway → School Service
#             School Service → (Feign) → Student Service
#             School Service ← (JSON) ← Student Service
#    Client ← Gateway ← School Service (merged data)
```

### Scenario 4: Centralized Config
```bash
# 1. Sửa file: config-server/configurations/students.yml
# 2. Thay đổi port: 8090 → 8091
# 3. Restart Student Service
# 4. Service tự động lấy config mới từ Config Server
# 5. Chạy trên port 8091 (không cần rebuild)
```

## 🛠️ Development & Testing

### Build toàn bộ project
```bash
# Build tất cả modules
mvn clean install

# Build riêng từng service
cd student
mvn clean package
```

### Run tests
```bash
mvn test
```

### Debug tips
```bash
# Kiểm tra service đã đăng ký Eureka chưa
curl http://localhost:8761/eureka/apps

# Xem config của service
curl http://localhost:8888/students/default
curl http://localhost:8888/gateway/default

# Health check
curl http://localhost:8090/actuator/health
curl http://localhost:8070/actuator/health
```

## 📚 Code Examples

### OpenFeign Client (School → Student)
```java
@FeignClient(name = "student-service", url = "${application.config.students-url}")
public interface StudentClient {
    @GetMapping("/api/v1/students/school/{school-id}")
    List<Student> findAllStudentsBySchool(@PathVariable("school-id") Integer schoolId);
}
```

### Gateway Routes Configuration
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: students
          uri: lb://students        # Load balanced
          predicates:
            - Path=/api/v1/students/**
```

### Service Discovery
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
  instance:
    prefer-ip-address: true
```

## 🔍 Troubleshooting

### Service không đăng ký được với Eureka
```bash
# Kiểm tra Eureka đã chạy chưa
curl http://localhost:8761

# Kiểm tra config
# File: application.yml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

### Gateway không tìm thấy service
```bash
# 1. Kiểm tra service đã đăng ký Eureka chưa
# 2. Xem Eureka Dashboard: http://localhost:8761
# 3. Kiểm tra gateway routes config
# 4. Restart gateway
```

### Config Server không load được config
```bash
# Kiểm tra path
# File: config-server/application.yml
spring:
  cloud:
    config:
      server:
        native:
          search-locations: classpath:/configurations
```

## 🎓 Learning Points

Dự án này giúp học về:
- ✅ **Microservices Architecture** - Tách ứng dụng thành các services nhỏ độc lập
- ✅ **Service Discovery** - Services tự động tìm nhau không cần hardcode
- ✅ **API Gateway Pattern** - Single entry point, routing, load balancing
- ✅ **Externalized Configuration** - Quản lý config tập trung
- ✅ **Inter-Service Communication** - Services gọi nhau qua REST/Feign
- ✅ **Load Balancing** - Phân tải request tự động
- ✅ **Distributed Systems** - Quản lý nhiều services cùng lúc

## 📊 Architecture Benefits

| Traditional Monolith | Microservices (Dự án này) |
|---------------------|---------------------------|
| 1 ứng dụng lớn | 5 services độc lập |
| Deploy tất cả cùng lúc | Deploy từng service riêng |
| Scale toàn bộ app | Scale từng service theo nhu cầu |
| 1 database | Mỗi service 1 database |
| Khó maintain | Dễ maintain, test |

## 👨‍💻 Author

**pdlong4002**

---

## 📄 License

This project is for educational and demonstration purposes.

**Keywords:** Spring Boot, Microservices, Spring Cloud, Eureka, API Gateway, Config Server, OpenFeign, Service Discovery, Load Balancing, Distributed Systems
