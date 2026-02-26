# 📦 Microservices - Chức năng từng Service

## 1. Config Server (Port 8888)

### Tác dụng:
- **Quản lý cấu hình tập trung** cho tất cả các services
- Lưu trữ config files (yml) tại `configurations/`
- Services khác kết nối tới Config Server để lấy cấu hình khi khởi động

### Cách hoạt động:
```
1. Config Server đọc file yml từ folder configurations/
2. Tên file phải trùng với spring.application.name của service
   Ví dụ: schools.yml → spring.application.name: schools
3. Service gọi: http://localhost:8888/{application-name}/default
4. Config Server trả về cấu hình cho service đó
```

### Ví dụ:
```yaml
# schools.yml trong Config Server
spring:
  application:
    name: schools  # ← Tên này match với file schools.yml
  datasource:
    url: jdbc:mysql://localhost:3306/schools

application:
  config:
    students-url: http://localhost:8090  # ← Custom property
```

### Lợi ích:
- ✅ Thay đổi config không cần rebuild
- ✅ Quản lý tập trung
- ✅ Support nhiều environments (dev, prod)

---

## 2. Discovery Server / Eureka (Port 8761)

### Tác dụng:
- **Service Registry** - Đăng ký và quản lý tất cả các services
- Cho phép services tự động tìm nhau mà không cần biết IP/Port
- Health checking - kiểm tra services có còn sống không

### Cách hoạt động:
```
1. Service khởi động → Đăng ký với Eureka: "Tôi là students, đang chạy ở 8090"
2. Eureka lưu thông tin service (name, IP, port, status)
3. Service khác muốn gọi → Hỏi Eureka: "students ở đâu?"
4. Eureka trả về danh sách instances của students
5. Load balancer tự động chọn instance nào để gọi
```

### Dashboard:
```
http://localhost:8761
→ Xem tất cả services đã đăng ký
→ Status: UP, DOWN, STARTING
→ Số lượng instances
```

### Lợi ích:
- ✅ Dynamic service discovery
- ✅ Auto load balancing
- ✅ Không cần hardcode URL

---

## 3. API Gateway (Port 8222)

### Tác dụng:
- **Single Entry Point** - Điểm vào duy nhất cho tất cả requests từ client
- Routing - định tuyến request tới đúng service
- Load balancing - phân tải tự động

### Cách hoạt động:
```
Client gọi: http://localhost:8222/api/v1/students
                      ↓
            API Gateway routing
                      ↓
      Hỏi Eureka: "students service ở đâu?"
                      ↓
      Forward tới: http://localhost:8090/api/v1/students
                      ↓
              Trả response về client
```

### Routing Config:
```yaml
routes:
  - id: students
    uri: lb://students  # lb = load balanced qua Eureka
    predicates:
      - Path=/api/v1/students/**
```

### Lợi ích:
- ✅ Client chỉ cần biết 1 địa chỉ Gateway
- ✅ Có thể thêm authentication, rate limiting
- ✅ Load balancing tự động

---

## 4. Student Service (Port 8090)

### Tác dụng:
- **Business logic** quản lý học sinh
- CRUD operations cho Student entity
- Kết nối MySQL database `students`

### API Endpoints:
```
GET    /api/v1/students              → Lấy tất cả students
POST   /api/v1/students              → Tạo student mới
GET    /api/v1/students/school/{id}  → Lấy students theo school ID
```

### Database:
```sql
Database: students
Table: student
  - id (PK)
  - firstname
  - lastname
  - email
  - school_id (FK)
```

### Dependencies:
- Spring Data JPA
- MySQL Driver
- Eureka Client
- Config Client

---

## 5. School Service (Port 8070)

### Tác dụng:
- **Business logic** quản lý trường học
- CRUD operations cho School entity
- **Gọi Student Service** qua OpenFeign để lấy danh sách students

### API Endpoints:
```
GET    /api/v1/schools                      → Lấy tất cả schools
POST   /api/v1/schools                      → Tạo school mới
GET    /api/v1/schools/with-students/{id}   → Lấy school + students
```

### Inter-Service Communication:
```java
@FeignClient(name = "student-service", url = "${application.config.students-url}")
public interface StudentClient {
    @GetMapping("/api/v1/students/school/{school-id}")
    List<Student> findAllStudentsBySchool(@PathVariable("school-id") Integer schoolId);
}
```

**Giải thích `application.config.students-url`:**
- Đây là **custom property** được định nghĩa trong `schools.yml` (Config Server)
- School Service đọc giá trị này từ Config Server
- Dùng để cấu hình URL của Student Service cho Feign Client
- Có thể thay đổi URL mà không cần rebuild code

### Database:
```sql
Database: schools
Table: school
  - id (PK)
  - name
  - email
```

### Flow khi gọi `/schools/with-students/1`:
```
1. Client → Gateway → School Service
2. School Service query database → lấy School(id=1)
3. School Service gọi Student Service (qua Feign):
   GET http://localhost:8090/api/v1/students/school/1
4. Student Service trả về List<Student>
5. School Service merge data: School + Students
6. Trả response về Client
```

---

## 📊 Flow tổng quan

```
┌────────┐
│ Client │
└───┬────┘
    │ http://localhost:8222/api/v1/schools/with-students/1
    ↓
┌───────────┐
│  Gateway  │ → Hỏi Eureka: "schools ở đâu?"
└─────┬─────┘
      │ Forward tới School Service
      ↓
┌──────────────┐
│    School    │ → Query DB: SELECT * FROM school WHERE id=1
│   Service    │ → Gọi Student Service (Feign)
└──────┬───────┘
       │
       ↓ OpenFeign: GET /students/school/1
┌──────────────┐
│   Student    │ → Query DB: SELECT * FROM student WHERE school_id=1
│   Service    │ → Trả về List<Student>
└──────────────┘
```

---

## 🔑 Key Concepts

### Services giao tiếp với nhau như thế nào?
**2 cách:**
- **Synchronous**: REST API qua OpenFeign (School → Student)
- **Asynchronous**: Message queue (chưa implement, có thể dùng Kafka/RabbitMQ)

---

## 📝 Tổng kết

| Service | Port | Tác dụng chính | Dependencies |
|---------|------|----------------|--------------|
| **Config Server** | 8888 | Quản lý config tập trung | Spring Cloud Config Server |
| **Discovery** | 8761 | Service registry & discovery | Netflix Eureka Server |
| **Gateway** | 8222 | API Gateway, routing, load balancing | Spring Cloud Gateway |
| **Student** | 8090 | CRUD Students, database | JPA, MySQL, Eureka Client |
| **School** | 8070 | CRUD Schools, gọi Student Service | JPA, MySQL, Feign, Eureka Client |

**Thứ tự khởi động:** Config Server → Discovery → Student/School → Gateway
