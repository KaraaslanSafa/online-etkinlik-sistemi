# Events Management System - Backend API Dökümantasyonu

## 📋 Genel Bilgiler

Events Management System, etkinlik yönetimi için kapsamlı bir REST API uygulamasıdır. Sistem etkinlik oluşturma, katılımcı kayıt, kategori yönetimi ve katılımcı takibi gibi özellikleri içerir.

**Teknoloji Stack:**
- Java 25 LTS
- Spring Boot 4.0.5
- Spring Data JPA
- SQL Server
- Maven

---

## 🚀 Başlangıç

### Sistem Gereksinimler
- Java 25+
- SQL Server
- Maven 3.9+

### Kurulum

1. **Veritabanı Bağlantısını Ayarlayın** (`application.properties`):
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=EventsManagementSystem;encrypt=true;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=Salih.12345
```

2. **Uygulamayı Çalıştırın**:
```bash
cd demo
mvn clean install
mvn spring-boot:run
```

API şu adreste erişilebilir olacaktır: `http://localhost:8080`

---

## 📚 API Endpoints

### 1. KATEGORI YÖNETİMİ (Categories)

#### Kategori Oluştur
```
POST /api/categories
Content-Type: application/json

{
  "name": "Spor",
  "description": "Spor etkinlikleri"
}

Response (201 Created):
{
  "id": 1,
  "name": "Spor",
  "description": "Spor etkinlikleri"
}
```

#### Tüm Kategorileri Getir
```
GET /api/categories

Response (200):
[
  {
    "id": 1,
    "name": "Spor",
    "description": "Spor etkinlikleri"
  },
  {
    "id": 2,
    "name": "Teknoloji",
    "description": "Teknoloji etkinlikleri"
  }
]
```

#### Kategori Detaylarını Getir
```
GET /api/categories/{id}

Response (200):
{
  "id": 1,
  "name": "Spor",
  "description": "Spor etkinlikleri"
}
```

#### Kategori Güncelle
```
PUT /api/categories/{id}
Content-Type: application/json

{
  "name": "Spor ve Fitness",
  "description": "Spor ve fitness etkinlikleri"
}

Response (200):
{
  "id": 1,
  "name": "Spor ve Fitness",
  "description": "Spor ve fitness etkinlikleri"
}
```

#### Kategori Sil
```
DELETE /api/categories/{id}

Response (204 No Content)
```

---

### 2. KATILIMCI YÖNETİMİ (Participants)

#### Katılımcı Oluştur
```
POST /api/participants
Content-Type: application/json

{
  "firstName": "Ahmet",
  "lastName": "Yılmaz",
  "email": "ahmet@example.com",
  "phoneNumber": "05551234567"
}

Response (201 Created):
{
  "id": 1,
  "firstName": "Ahmet",
  "lastName": "Yılmaz",
  "email": "ahmet@example.com",
  "phoneNumber": "05551234567"
}
```

#### Tüm Katılımcıları Getir
```
GET /api/participants

Response (200):
[
  {
    "id": 1,
    "firstName": "Ahmet",
    "lastName": "Yılmaz",
    "email": "ahmet@example.com",
    "phoneNumber": "05551234567"
  }
]
```

#### Email ile Katılımcı Getir
```
GET /api/participants/email/{email}

Response (200):
{
  "id": 1,
  "firstName": "Ahmet",
  "lastName": "Yılmaz",
  "email": "ahmet@example.com",
  "phoneNumber": "05551234567"
}
```

#### Katılımcı Güncelle
```
PUT /api/participants/{id}
Content-Type: application/json

{
  "firstName": "Ahmet",
  "lastName": "Kaya",
  "email": "ahmet.kaya@example.com",
  "phoneNumber": "05551234568"
}

Response (200):
{
  "id": 1,
  "firstName": "Ahmet",
  "lastName": "Kaya",
  "email": "ahmet.kaya@example.com",
  "phoneNumber": "05551234568"
}
```

#### Katılımcı Sil
```
DELETE /api/participants/{id}

Response (204 No Content)
```

---

### 3. ETKİNLİK YÖNETİMİ (Events)

#### Etkinlik Oluştur
```
POST /api/events
Content-Type: application/json

{
  "title": "İstanbul Maratonu 2024",
  "description": "Şehir içi 42km maraton yarışı",
  "startDate": "2024-05-15T06:00:00",
  "endDate": "2024-05-15T12:00:00",
  "location": "İstanbul, Taksim - Florya",
  "capacity": 5000,
  "categoryId": 1
}

Response (201 Created):
{
  "id": 1,
  "title": "İstanbul Maratonu 2024",
  "description": "Şehir içi 42km maraton yarışı",
  "startDate": "2024-05-15T06:00:00",
  "endDate": "2024-05-15T12:00:00",
  "location": "İstanbul, Taksim - Florya",
  "capacity": 5000,
  "categoryId": 1,
  "status": "PLANNED",
  "participantCount": 0
}
```

#### Tüm Etkinlikleri Getir
```
GET /api/events

Response (200):
[
  {
    "id": 1,
    "title": "İstanbul Maratonu 2024",
    ...
  }
]
```

#### Etkinlik Detaylarını Getir
```
GET /api/events/{id}

Response (200):
{
  "id": 1,
  "title": "İstanbul Maratonu 2024",
  ...
}
```

#### Duruma Göre Etkinlikleri Getir
```
GET /api/events/status/{status}

Status değerleri: PLANNED, ONGOING, COMPLETED, CANCELLED

Response (200):
[...]
```

#### Kategoriye Göre Etkinlikleri Getir
```
GET /api/events/category/{categoryId}

Response (200):
[...]
```

#### Etkinlik Ara
```
GET /api/events/search?keyword=maraton&status=PLANNED

Response (200):
[...]
```

#### Tarih Aralığında Etkinlikleri Getir
```
GET /api/events/between?startDate=2024-05-01T00:00:00&endDate=2024-05-31T23:59:59

Response (200):
[...]
```

#### Etkinlik Güncelle
```
PUT /api/events/{id}
Content-Type: application/json

{
  "title": "İstanbul Maratonu 2024 - Güncellenmiş",
  "capacity": 6000,
  ...
}

Response (200):
{...}
```

#### Etkinlik Durumunu Güncelle
```
PATCH /api/events/{id}/status?status=ONGOING

Response (200)
```

#### Etkinlik Sil
```
DELETE /api/events/{id}

Response (204 No Content)
```

---

### 4. ETKİNLİK KATILIMCI YÖNETİMİ (Event Participants)

#### Katılımcıyı Etkinliğe Kaydet
```
POST /api/event-participants/register?eventId=1&participantId=1

Response (201 Created):
{
  "id": 1,
  "eventId": 1,
  "participantId": 1,
  "participantName": "Ahmet Yılmaz",
  "participantEmail": "ahmet@example.com",
  "status": "REGISTERED",
  "registeredAt": "2024-04-02T10:30:00"
}
```

#### Etkinliğin Katılımcılarını Getir
```
GET /api/event-participants/event/{eventId}

Response (200):
[
  {
    "id": 1,
    "eventId": 1,
    "participantId": 1,
    "participantName": "Ahmet Yılmaz",
    "participantEmail": "ahmet@example.com",
    "status": "REGISTERED",
    "registeredAt": "2024-04-02T10:30:00"
  }
]
```

#### Katılımcının Etkinliklerini Getir
```
GET /api/event-participants/participant/{participantId}

Response (200):
[...]
```

#### Etkinlik Katılımcı Sayısını Getir
```
GET /api/event-participants/event/{eventId}/count

Response (200):
42
```

#### Katılımcının Katılım Durumunu Güncelle
```
PATCH /api/event-participants/{eventParticipantId}/status?status=ATTENDED

Status değerleri: REGISTERED, ATTENDED, CANCELLED, NO_SHOW

Response (200)
```

#### Katılımcıyı Etkinlikten Çıkar
```
DELETE /api/event-participants/unregister?eventId=1&participantId=1

Response (204 No Content)
```

---

## 🔍 Veri Modelleri

### Category (Kategori)
```
{
  "id": Long (PK),
  "name": String (Unique, Required),
  "description": String
}
```

### Participant (Katılımcı)
```
{
  "id": Long (PK),
  "firstName": String (Required),
  "lastName": String (Required),
  "email": String (Unique, Email Format, Required),
  "phoneNumber": String
}
```

### Event (Etkinlik)
```
{
  "id": Long (PK),
  "title": String (Required),
  "description": String,
  "startDate": LocalDateTime (Required),
  "endDate": LocalDateTime (Required),
  "location": String (Required),
  "capacity": Integer,
  "categoryId": Long (FK to Category, Required),
  "status": EventStatus (PLANNED, ONGOING, COMPLETED, CANCELLED),
  "participantCount": Integer,
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### EventParticipant
```
{
  "id": Long (PK),
  "eventId": Long (FK to Event),
  "participantId": Long (FK to Participant),
  "status": ParticipationStatus (REGISTERED, ATTENDED, CANCELLED, NO_SHOW),
  "registeredAt": LocalDateTime
}
```

---

## ⚠️ Hata Yönetimi

### Hata Yanıtları

#### 404 Not Found
```json
{
  "status": 404,
  "message": "Etkinlik bulunamadı: 999",
  "timestamp": "2024-04-02T10:30:00"
}
```

#### 409 Conflict (Etkinlik Dolu)
```json
{
  "status": 409,
  "message": "Etkinlik 'İstanbul Maratonu' dolu (Kapasite: 100)",
  "timestamp": "2024-04-02T10:30:00"
}
```

#### 409 Conflict (Çift Kayıt)
```json
{
  "status": 409,
  "message": "Katılımcı zaten bu etkinliğe kayıtlı. Event: 1, Participant: 1",
  "timestamp": "2024-04-02T10:30:00"
}
```

#### 400 Bad Request (Doğrulama Hatası)
```json
{
  "status": 400,
  "message": "Doğrulama hatası",
  "timestamp": "2024-04-02T10:30:00",
  "errors": {
    "email": "Geçerli bir email adresi girin",
    "firstName": "Ad boş olamaz"
  }
}
```

---

## 📊 Proje Yapısı

```
src/main/java/com/example/demo/
├── controller/           # REST API Controllers
│   ├── CategoryController
│   ├── ParticipantController
│   ├── EventController
│   └── EventParticipantController
├── service/             # Business Logic Layer
│   ├── CategoryService(Impl)
│   ├── ParticipantService(Impl)
│   ├── EventService(Impl)
│   └── EventParticipantService(Impl)
├── repository/          # Data Access Layer
│   ├── CategoryRepository
│   ├── ParticipantRepository
│   ├── EventRepository
│   └── EventParticipantRepository
├── entity/              # Database Models
│   ├── Category
│   ├── Participant
│   ├── Event
│   ├── EventParticipant
│   ├── EventStatus (Enum)
│   └── ParticipationStatus (Enum)
├── dto/                 # Data Transfer Objects
│   ├── CategoryDTO
│   ├── ParticipantDTO
│   ├── EventDTO
│   └── EventParticipantDTO
├── exception/           # Exception Handling
│   ├── ResourceNotFoundException
│   ├── EventFullException
│   ├── DuplicateRegistrationException
│   ├── GlobalExceptionHandler
│   ├── ErrorResponse
│   └── ValidationErrorResponse
└── DemoApplication      # Main Application
```

---

## 🧪 Test Örnekleri

### cURL ile API Test Etme

#### Kategori Oluştur
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name":"Spor","description":"Spor etkinlikleri"}'
```

#### Katılımcı Oluştur
```bash
curl -X POST http://localhost:8080/api/participants \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Ahmet","lastName":"Yılmaz","email":"ahmet@example.com","phoneNumber":"05551234567"}'
```

#### Etkinlik Oluştur
```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "title":"Maraton",
    "description":"Şehir maratonu",
    "startDate":"2024-05-15T06:00:00",
    "endDate":"2024-05-15T12:00:00",
    "location":"İstanbul",
    "capacity":5000,
    "categoryId":1
  }'
```

---

## 📝 Notlar

- Tüm endpoint'ler CORS desteğine sahiptir
- Doğrulama otomatik olarak yapılır
- Hata mesajları Türkçe dilindedir
- Tarih/Saat UTC+3 timezone'ında işlenir
- Tüm POST/PUT istekleri `Content-Type: application/json` gerektirir

---

## 🔐 Güvenlik Önerileri (Üretim için)

1. **Authentication & Authorization**: JWT veya OAuth2 ekleyin
2. **Rate Limiting**: İstek sayısını sınırlayın
3. **Input Validation**: Daha katı doğrulama kuralları ekleyin
4. **HTTPS**: Üretimde HTTPS kullanın
5. **Logging**: Duyarlı bilgileri log'a yazmayın
6. **Database**: Şifreyi environment variable'dan okuyun

---

## 📞 İletişim ve Destek

Herhangi bir soru veya sorun için lütfen iletişime geçiniz.

**Yapım Tarihi**: 2 Nisan 2024
**Sürüm**: 1.0.0
