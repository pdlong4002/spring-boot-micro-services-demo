# 🚀 Công nghệ trong Microservices Project

Tài liệu tổng hợp **tất cả** các công nghệ, framework, patterns và tools được sử dụng trong dự án.

---

## 1️⃣ Core Technologies

### Spring Framework Stack
| Công nghệ | Phiên bản | Mục đích | Vị trí sử dụng |
|-----------|-----------|----------|----------------|
| **Spring Boot** | 4.0.3 | Framework chính để build microservices | Tất cả services |
| **Spring Cloud** | 2025.1.0 | Bộ công cụ cho distributed systems | Tất cả services |
| **Spring Data JPA** | 4.0.x | ORM - Object Relational Mapping | Student, School |
| **Spring Web MVC** | 7.0.5 | REST API framework | Student, School |
| **Spring Cloud Config** | 5.0.0 | Centralized configuration management | Config Server + Clients |
| **Spring Cloud Gateway** | 5.0.0 | API Gateway routing & filtering | Gateway |
| **Spring Cloud Netflix Eureka** | 5.0.0 | Service discovery & registration | Discovery + All clients |
| **Spring Cloud OpenFeign** | 5.0.0 | Declarative REST client | School Service |
| **Spring Boot Actuator** | 4.0.3 | Health check & monitoring endpoints | Tất cả services |

### Java Platform
| Công nghệ | Chi tiết |
|-----------|----------|
| **Java** | Version 21 (LTS) |
| **Maven** | Build tool & dependency management |
| **Lombok** | Giảm boilerplate code (getters/setters) |

### Database & Persistence
| Công nghệ | Mục đích |
|-----------|----------|
| **MySQL** | Relational database |
| **Hibernate** | JPA implementation |
| **HikariCP** | Connection pool (default trong Spring Boot) |
| **JDBC Driver** | MySQL Connector/J |

---

## 2️⃣ Microservices Patterns

### Service Discovery Pattern
**Công nghệ:** Netflix Eureka Server & Client

**Cách hoạt động:**
```
1. Services đăng ký với Eureka khi khởi động
2. Services định kỳ gửi heartbeat (mặc định 30s)
3. Services query Eureka để tìm instances của services khác
4. Load balancing tự động giữa các instances
```

**Code example:**
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
    register-with-eureka: true
    fetch-registry: true
```

### API Gateway Pattern
**Công nghệ:** Spring Cloud Gateway (Reactive)

**Chức năng:**
- ✅ Single entry point cho tất cả requests
- ✅ Routing requests tới đúng service
- ✅ Load balancing với `lb://service-name`
- ✅ Có thể thêm: Authentication, Rate Limiting, Circuit Breaker

**Code example:**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: students
          uri: lb://students  # Load balanced via Eureka
          predicates:
            - Path=/api/v1/students/**
```

### Externalized Configuration Pattern
**Công nghệ:** Spring Cloud Config Server

**Lợi ích:**
- ✅ Tập trung config tại một nơi
- ✅ Thay đổi config không cần rebuild
- ✅ Support profiles (dev, prod, staging)
- ✅ Git backend (có thể dùng Git để version control configs)

**Cấu trúc:**
```
config-server/configurations/
├── students.yml      # Config cho student service
├── schools.yml       # Config cho school service
├── gateway.yml       # Config cho gateway
└── discovery.yml     # Config cho eureka
```

### Client-Side Load Balancing
**Công nghệ:** Spring Cloud LoadBalancer (thay thế Ribbon)

**Hoạt động:**
- Gateway hoặc Feign Client tự chọn instance nào để gọi
- Round-robin algorithm mặc định
- Health check tự động loại bỏ unhealthy instances

### Inter-Service Communication Pattern
**Công nghệ:** OpenFeign (Declarative REST Client)

**So sánh:**
```java
// Trước kia với RestTemplate (imperative)
RestTemplate restTemplate = new RestTemplate();
Student[] students = restTemplate.getForObject(
    "http://localhost:8090/students/school/" + schoolId, 
    Student[].class
);

// Bây giờ với Feign (declarative)
@FeignClient(name = "students")
public interface StudentClient {
    @GetMapping("/students/school/{id}")
    List<Student> getBySchool(@PathVariable Integer id);
}
```

---

## 3️⃣ Network & Communication

### HTTP Clients
| Công nghệ | Sử dụng ở đâu | Mục đích |
|-----------|---------------|----------|
| **OpenFeign** | School Service | Gọi Student Service |
| **RestClient** | Eureka Client | HTTP communication với Eureka |
| **WebClient** | Gateway (reactive) | Non-blocking HTTP calls |

### Serialization/Deserialization
| Công nghệ | Mục đích |
|-----------|----------|
| **Jackson** | JSON ↔ Java Objects |
| **Jackson Datatype JSR310** | Support Java 8 Date/Time API |
| **Jackson Module Parameter Names** | Deserialize constructor parameters |

---

## 4️⃣ Reactive Programming (Gateway)

### Project Reactor
**Sử dụng trong:** Spring Cloud Gateway

| Component | Mục đích |
|-----------|----------|
| **Reactor Core** | Reactive programming foundation |
| **Reactor Netty** | Non-blocking HTTP server/client |
| **WebFlux** | Reactive web framework |

**Tại sao dùng Reactive?**
- ✅ Non-blocking I/O → hiệu suất cao hơn
- ✅ Xử lý concurrent requests tốt hơn
- ✅ Phù hợp cho API Gateway (routing nhiều requests)

---

## 5️⃣ Monitoring & Observability

### Spring Boot Actuator
**Endpoints có sẵn:**
```
/actuator/health       # Health check
/actuator/info         # Application info
/actuator/metrics      # Application metrics
/actuator/env          # Environment properties
```

### Metrics & Monitoring (Có thể mở rộng)
| Công nghệ | Mục đích |
|-----------|----------|
| **Micrometer** | Metrics collection framework |
| **Prometheus** | (Có thể thêm) Time-series database |
| **Grafana** | (Có thể thêm) Visualization |
| **Zipkin/Sleuth** | (Có thể thêm) Distributed tracing |

---

## 6️⃣ Data Validation & Mapping

### Validation
| Công nghệ | Mục đích |
|-----------|----------|
| **Jakarta Validation API** | Bean validation annotations |
| **Hibernate Validator** | Validation implementation |

**Annotations:**
```java
@NotNull, @NotEmpty, @Size, @Email, @Min, @Max, etc.
```

### Object Mapping
| Công nghệ | Mục đích |
|-----------|----------|
| **Lombok** | Auto-generate getters/setters/constructors |
| **MapStruct** | (Có thể thêm) DTO ↔ Entity mapping |

---

## 7️⃣ Security (Có thể mở rộng)

### Spring Security
**Hiện tại:** Chưa implement
**Có thể thêm:**
- ✅ JWT Authentication
- ✅ OAuth2 Resource Server
- ✅ API Gateway authentication/authorization
- ✅ Method-level security

---

## 8️⃣ Resilience Patterns (Có thể mở rộng)

### Resilience4j
**Chưa implement, nhưng nên thêm:**

| Pattern | Mục đích |
|---------|----------|
| **Circuit Breaker** | Ngăn cascade failures |
| **Retry** | Tự động retry failed requests |
| **Rate Limiter** | Giới hạn số requests |
| **Bulkhead** | Isolate resources |
| **Time Limiter** | Timeout cho requests |

**Example config:**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      studentService:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10000
```

---

## 9️⃣ Testing (Có thể mở rộng)

### Testing Frameworks
| Công nghệ | Mục đích |
|-----------|----------|
| **JUnit 5** | Unit testing framework |
| **Mockito** | Mocking framework |
| **Spring Boot Test** | Integration testing |
| **TestContainers** | (Có thể thêm) Database testing với Docker |
| **WireMock** | (Có thể thêm) Mock external services |

---

## 🔟 DevOps & Deployment (Có thể mở rộng)

### Containerization
| Công nghệ | Mục đích |
|-----------|----------|
| **Docker** | Containerize applications |
| **Docker Compose** | Multi-container orchestration |
| **Kubernetes** | Container orchestration (production) |

**Dockerfile example:**
```dockerfile
FROM eclipse-temurin:21-jre
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

**docker-compose.yml example:**
```yaml
services:
  config-server:
    build: ./config-server
    ports: ["8888:8888"]
  
  discovery:
    build: ./discovery
    ports: ["8761:8761"]
    depends_on: [config-server]
  
  student:
    build: ./student
    ports: ["8090:8090"]
    depends_on: [discovery, mysql]
```

### CI/CD (Có thể thêm)
| Công nghệ | Mục đích |
|-----------|----------|
| **GitHub Actions** | CI/CD pipeline |
| **Jenkins** | Automation server |
| **ArgoCD** | GitOps deployment |

---

## 1️⃣1️⃣ Message Broker (Có thể mở rộng)

### Asynchronous Communication
**Chưa implement, nhưng trong microservices nên có:**

| Công nghệ | Mục đích |
|-----------|----------|
| **Apache Kafka** | Event streaming platform |
| **RabbitMQ** | Message broker (AMQP) |
| **Spring Cloud Stream** | Abstraction cho messaging |

**Use case:**
- Event-driven architecture
- Async communication giữa services
- Event sourcing, CQRS pattern

---

## 1️⃣2️⃣ API Documentation

### OpenAPI/Swagger (Nên thêm)
| Công nghệ | Mục đích |
|-----------|----------|
| **SpringDoc OpenAPI** | Auto-generate API docs |
| **Swagger UI** | Interactive API documentation |

**Configuration:**
```java
@OpenAPIDefinition(
    info = @Info(
        title = "Student Service API",
        version = "1.0"
    )
)
```

---

## 1️⃣3️⃣ Logging & Tracing

### Logging
| Công nghệ | Mục đích |
|-----------|----------|
| **SLF4J** | Logging facade |
| **Logback** | Logging implementation (default) |
| **ELK Stack** | (Có thể thêm) Log aggregation |

### Distributed Tracing (Nên thêm)
| Công nghệ | Mục đích |
|-----------|----------|
| **Spring Cloud Sleuth** | Auto trace ID generation |
| **Zipkin** | Distributed tracing UI |
| **Jaeger** | Alternative tracing system |

**Lợi ích:**
- Track requests qua nhiều services
- Debug performance issues
- Visualize service dependencies

---

## 1️⃣4️⃣ Configuration Management

### Externalized Configuration Sources
| Source | Hiện tại | Có thể mở rộng |
|--------|----------|----------------|
| **YAML files** | ✅ Đang dùng | - |
| **Git repository** | ❌ | ✅ Store configs in Git |
| **Vault** | ❌ | ✅ Secrets management |
| **Consul** | ❌ | ✅ Dynamic config updates |

---

## 1️⃣5️⃣ Database Patterns

### Patterns được implement
| Pattern | Mô tả |
|---------|-------|
| **Database per Service** | Mỗi service có database riêng (students, schools) |
| **Repository Pattern** | Spring Data JPA Repositories |

### Có thể thêm
| Pattern | Mục đích |
|---------|----------|
| **CQRS** | Command Query Responsibility Segregation |
| **Event Sourcing** | Store events thay vì state |
| **Saga Pattern** | Distributed transactions |

---

## 📊 Architecture Patterns Summary

### ✅ Đã implement
1. **Service Registry** (Eureka)
2. **API Gateway** (Spring Cloud Gateway)
3. **Externalized Configuration** (Config Server)
4. **Load Balancing** (Client-side với Spring Cloud LoadBalancer)
5. **Service Discovery** (Eureka Client)
6. **Database per Service**
7. **REST API** (Spring Web MVC)

### 🔄 Có thể mở rộng
1. **Circuit Breaker** (Resilience4j)
2. **Distributed Tracing** (Sleuth + Zipkin)
3. **Centralized Logging** (ELK Stack)
4. **API Documentation** (SpringDoc OpenAPI)
5. **Message-driven** (Kafka/RabbitMQ)
6. **Authentication/Authorization** (Spring Security + JWT)
7. **Rate Limiting** (Gateway filters)
8. **Caching** (Redis/Caffeine)
9. **Saga Pattern** (Distributed transactions)

---

## 🎯 Tech Stack by Service

### Config Server
```
- Spring Cloud Config Server
- Native file system backend
- YAML configuration files
```

### Discovery Server (Eureka)
```
- Netflix Eureka Server
- Service registry & discovery
- Health checking
```

### API Gateway
```
- Spring Cloud Gateway (WebFlux)
- Reactive programming (Reactor)
- Client-side load balancing
- Route predicates & filters
```

### Student Service
```
- Spring Boot Web MVC
- Spring Data JPA
- MySQL Database
- Eureka Client
- Config Client
```

### School Service
```
- Spring Boot Web MVC
- Spring Data JPA
- MySQL Database
- OpenFeign Client (gọi Student Service)
- Eureka Client
- Config Client
```

---

## 🔗 Useful Resources

### Official Documentation
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Cloud](https://spring.io/projects/spring-cloud)
- [Netflix Eureka](https://github.com/Netflix/eureka/wiki)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [OpenFeign](https://spring.io/projects/spring-cloud-openfeign)

### Learning Paths
- Microservices Architecture
- Distributed Systems Design
- Cloud Native Applications
- DevOps & CI/CD
- Reactive Programming

---

**Tổng kết:** Dự án này sử dụng **15+ công nghệ chính** và minh họa **7 microservices patterns** quan trọng. Đây là foundation tốt để mở rộng thêm các patterns nâng cao như Circuit Breaker, Distributed Tracing, Message-driven Architecture, và CQRS.
