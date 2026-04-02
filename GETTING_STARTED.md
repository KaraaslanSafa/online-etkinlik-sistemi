# Events Management System - Başlangıç Rehberi

## 📌 Proje Yapısı Özeti

Backend'in tamamı aşağıdaki katmanlar ile yapılandırılmıştır:

### 1️⃣ **Entity Layer** (Veri Modelleri)
Veritabanında tablolara karşılık gelen sınıflar:
- `Category` - Etkinlik kategorileri
- `Participant` - Katılımcılar
- `Event` - Etkinlikler
- `EventParticipant` - Etkinlik-Katılımcı ilişkisi (Many-to-Many)
- `EventStatus` - Etkinlik durumu (Enum)
- `ParticipationStatus` - Katılım durumu (Enum)

### 2️⃣ **Repository Layer** (Veri Erişimi)
Spring Data JPA repositories:
- `CategoryRepository` - Kategori CRUD + özel sorgular
- `ParticipantRepository` - Katılımcı CRUD + email araması
- `EventRepository` - Etkinlik CRUD + filtreleme/arama
- `EventParticipantRepository` - Kayıt yönetimi

### 3️⃣ **Service Layer** (İş Mantığı)
Business logic ve validation:
- `CategoryService(Impl)` - Kategori fonksiyonları
- `ParticipantService(Impl)` - Katılımcı işlemleri
- `EventService(Impl)` - Etkinlik yönetimi
- `EventParticipantService(Impl)` - Katılım kayıt/çıkış

### 4️⃣ **Controller Layer** (REST API)
HTTP endpoint'leri:
- `CategoryController` - `/api/categories`
- `ParticipantController` - `/api/participants`
- `EventController` - `/api/events`
- `EventParticipantController` - `/api/event-participants`

### 5️⃣ **DTO Layer** (Veri Transfer)
API istek/yanıt modelleri:
- `CategoryDTO`
- `ParticipantDTO`
- `EventDTO`
- `EventParticipantDTO`

### 6️⃣ **Exception Handling** (Hata Yönetimi)
Custom exceptions ve global handler:
- `ResourceNotFoundException` - Kaynak bulunamadı
- `EventFullException` - Etkinlik dolu
- `DuplicateRegistrationException` - Çift kayıt
- `GlobalExceptionHandler` - Merkezi hata işleme

---

## 🔨 Kurulum Adımları

### Step 1: SQL Server Veritabanını Oluşturun

```sql
-- SQL Server Management Studio'da çalıştırın
CREATE DATABASE EventsManagementSystem;

-- Veritabanı oluşturulduktan sonra, Spring Boot 
-- otomatik olarak tabloları oluşturacaktır
```

### Step 2: application.properties Kontrol Edin

```properties
# Veritabanı bilgilerinize göre güncelleyin
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=EventsManagementSystem;encrypt=true;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=Salih.12345
```

### Step 3: Uygulamayı Çalıştırın

```bash
# Terminalde proje klasörüne girin
cd EventsManagementSystem/demo

# Bağımlılıkları yükleyin
mvn clean install

# Uygulamayı başlatın
mvn spring-boot:run
```

**Başarılı başlangıç mesajı:**
```
2024-04-02 10:30:00 INFO  o.s.b.w.e.tomcat.TomcatWebServer    : Tomcat started on port(s): 8080 (http)
2024-04-02 10:30:00 INFO  c.e.d.DemoApplication               : Started DemoApplication in 2.5 seconds
```

---

## 🧪 API Test Etme

### Postman ile Test Etme

1. **Postman'i açın** → **New Collection** oluşturun
2. **Aşağıdaki requests'i ekleyin:**

#### 📌 Request 1: Kategori Oluştur
```
Method: POST
URL: http://localhost:8080/api/categories
Headers: 
  Content-Type: application/json

Body:
{
  "name": "Spor Etkinlikleri",
  "description": "Tüm spor aktiviteleri"
}
```

#### 📌 Request 2: Katılımcı Oluştur
```
Method: POST
URL: http://localhost:8080/api/participants
Headers:
  Content-Type: application/json

Body:
{
  "firstName": "Mehmet",
  "lastName": "Yılmaz",
  "email": "mehmet@example.com",
  "phoneNumber": "05551234567"
}
```

#### 📌 Request 3: Etkinlik Oluştur

**Önce oluşturduğunuz kategori ID'sini not edin (örn: 1)**

```
Method: POST
URL: http://localhost:8080/api/events
Headers:
  Content-Type: application/json

Body:
{
  "title": "Ankara Half Marathon 2024",
  "description": "21km çalıştırma etkinliği",
  "startDate": "2024-06-15T08:00:00",
  "endDate": "2024-06-15T10:00:00",
  "location": "Ankara, Anıtkabir Meydanı",
  "capacity": 500,
  "categoryId": 1
}
```

#### 📌 Request 4: Etkinliğe Katılımcı Kaydet

**Oluşturduğunuz etkinlik (örn: 1) ve katılımcı (örn: 1) ID'lerini kullanın**

```
Method: POST
URL: http://localhost:8080/api/event-participants/register?eventId=1&participantId=1
```

**Yanıt (201):**
```json
{
  "id": 1,
  "eventId": 1,
  "participantId": 1,
  "participantName": "Mehmet Yılmaz",
  "participantEmail": "mehmet@example.com",
  "status": "REGISTERED",
  "registeredAt": "2024-04-02T10:30:00"
}
```

#### 📌 Request 5: Etkinliğin Katılımcılarını Getir

```
Method: GET
URL: http://localhost:8080/api/event-participants/event/1
```

---

## 🔄 İş Akışı Örnekleri

### Senaryo 1: Yeni Etkinlik Oluştur ve Katılımcı Kaydet

```
1. POST /api/categories
   └─ Kategori ID alın (örn: 1)

2. POST /api/participants
   └─ Katılımcı ID alın (örn: 1)

3. POST /api/events (categoryId=1)
   └─ Etkinlik ID alın (örn: 1)

4. POST /api/event-participants/register
   ├─ eventId=1
   └─ participantId=1
```

### Senaryo 2: Tüm Planlanan Etkinlikleri Getir

```
GET /api/events/status/PLANNED
```

### Senaryo 3: Etkinlik Ara

```
GET /api/events/search?keyword=maraton&status=PLANNED
```

### Senaryo 4: Katılımcının Tüm Etkinliklerini Getir

```
GET /api/event-participants/participant/1
```

---

## ⚙️ Önemli Konfigürasyonlar

### application.properties Detaylı Açıklaması

```properties
# Uygulama Adı
spring.application.name=EventManagement

# Server Portu
server.port=8080

# ===== Database Configuration =====
# SQL Server bağlantı URL'i (encrypt=true, trustServerCertificate=true)
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=EventsManagementSystem;encrypt=true;trustServerCertificate=true;

# Veritabanı kullanıcı adı
spring.datasource.username=sa

# Veritabanı şifresi
spring.datasource.password=Salih.12345

# SQL Server Driver
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver

# ===== JPA / Hibernate Configuration =====
# SQL Server dialect
spring.jpa.database-platform=org.hibernate.dialect.SQLServer2012Dialect

# DDL otomasyonu: update (mevcut tabloları güncelle, yeni oluştur)
# Değerler: create-drop, create, update, validate
spring.jpa.hibernate.ddl-auto=update

# SQL cümlelerini gösterme
spring.jpa.show-sql=false

# SQL'i okunabilir format'ta göster
spring.jpa.properties.hibernate.format_sql=true

# ===== Logging =====
# Root log seviyesi
logging.level.root=INFO

# Uygulamamızın DEBUG log'u
logging.level.com.example.demo=DEBUG

# Web request log'u
logging.level.org.springframework.web=DEBUG

# Hibernate SQL log'u
logging.level.org.hibernate.SQL=DEBUG

# ===== Error Handling =====
# Hata mesajını dahil et
server.error.include-message=always

# Binding error'larını dahil et
server.error.include-binding-errors=always

# Stack trace belirli durumda göster
server.error.include-stacktrace=on_param
```

---

## 🐛 Yaygın Sorunlar ve Çözümleri

### Problem 1: "Connection refused" - Veritabanı bağlanamıyor
```
❌ Error: Communications link failure
   java.sql.SQLException: Cannot connect to SQL Server
```

**Çözüm:**
```bash
# SQL Server'ın çalışıp çalışmadığını kontrol edin
# Services.msc → SQL Server (SQLEXPRESS) → Running?

# Veritabanı adı ve bağlantı bilgilerini kontrol edin
# application.properties'de doğru kullanıcı adı/şifre mi?
```

### Problem 2: "Unique constraint violation" - Duplicate email
```
❌ Error: Violation of UNIQUE KEY constraint
```

**Çözüm:**
```java
// Farklı bir email adresi kullanın
{
  "email": "yeni-email@example.com"
}
```

### Problem 3: "Event is full" - Etkinlik kapasitesi dolu
```
❌ Error: 409 Conflict
   Message: Etkinlik 'Maraton' dolu (Kapasite: 100)
```

**Çözüm:**
```java
// Etkinliğin kapasitesini artırın
PUT /api/events/1
{
  "capacity": 150
}
```

---

## 📊 Database Schema

Spring Boot ve Hibernate otomatik olarak tablolar oluşturacak:

```sql
-- Categories tablosu
CREATE TABLE Categories (
  id BIGINT PRIMARY KEY IDENTITY,
  name VARCHAR(100) NOT NULL UNIQUE,
  description VARCHAR(500)
);

-- Participants tablosu
CREATE TABLE Participants (
  id BIGINT PRIMARY KEY IDENTITY,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  phone_number VARCHAR(20)
);

-- Events tablosu
CREATE TABLE Events (
  id BIGINT PRIMARY KEY IDENTITY,
  title VARCHAR(200) NOT NULL,
  description VARCHAR(2000),
  start_date DATETIME NOT NULL,
  end_date DATETIME NOT NULL,
  location VARCHAR(300) NOT NULL,
  capacity INT,
  category_id BIGINT NOT NULL FOREIGN KEY REFERENCES Categories(id),
  status VARCHAR(50),
  created_at DATETIME DEFAULT GETDATE(),
  updated_at DATETIME
);

-- EventParticipants tablosu (Many-to-Many)
CREATE TABLE EventParticipants (
  id BIGINT PRIMARY KEY IDENTITY,
  event_id BIGINT NOT NULL FOREIGN KEY REFERENCES Events(id),
  participant_id BIGINT NOT NULL FOREIGN KEY REFERENCES Participants(id),
  status VARCHAR(50),
  registered_at DATETIME DEFAULT GETDATE(),
  UNIQUE (event_id, participant_id)
);
```

---

## 🚀 Sonraki Adımlar

1. ✅ **Authentication**: JWT ile login sistemi ekleyin
2. ✅ **Validation**: Daha detaylı input validasyonu
3. ✅ **Pagination**: Büyük listeleri sayfalamak
4. ✅ **Search Filters**: Gelişmiş arama seçenekleri
5. ✅ **File Upload**: Etkinlik afişi upload'u
6. ✅ **Notifications**: Email notification'ları
7. ✅ **Reports**: Katılımcı raporları
8. ✅ **Analytics**: İstatistikler dashboard'u

---

## 📚 Faydalı Kaynaklar

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA Guide](https://spring.io/projects/spring-data-jpa)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [REST API Best Practices](https://restfulapi.net/)

---

**Happy Coding! 🎉**
