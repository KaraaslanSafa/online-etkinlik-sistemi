# 🎉 Etkinlik Yönetim Sistemi - Yeni Özellikler

Bu dosya projeye eklenen tüm yeni ve gelişmiş özellikleri açıklamaktadır.

---

## 📋 Özet

Projeye aşağıdaki **3 ana özellik** ve **1 görsel araç** eklenmiştir:

1. ✅ **Etkinlik Kapasite ve Zaman Çakışması Kontrolü** - İş Mantığı
2. ✅ **Dinamik Filtreleme ve Arama Motoru** - Esnek Sorgu Systemleri
3. ✅ **Otomatik E-posta Hatırlatıcı** - Email İntegrasyonu
4. ✅ **Swagger API Dokumentasyonu** - Görsel REST API Arayüzü

---

## 🔴 1. ETKİNLİK KAPASİTE VE ZAMAN ÇAKIŞMASI KONTROLÜ

### 📌 Özellik Açıklama

Sistem, katılımcıların aynı saatte iki farklı etkinliğe bilet almasını **engeller** ve etkinlik **kapasitesi dolduğunda** uyarı gösterir.

### 💻 Teknik Detay

#### A. Kapasite Kontrolü
```java
// EventParticipantServiceImpl.java
if (!event.isAvailable()) {
    throw new EventFullException(event.getTitle(), event.getCapacity());
}
```

**Event Entity:**
- `capacity` (Integer): Etkinliğin maksimum katılımcı sayısı
- `eventParticipants.size()`: Şu anki kayıtlı katılımcı sayısı

**Error Response:**
```json
{
    "status": 409,
    "message": "Etkinlik dolu: Test Event (Kapasite: 50)",
    "timestamp": "2026-04-16T11:30:00"
}
```

#### B. Zaman Çakışması Kontrolü
```java
// EventRepository.java
@Query("SELECT e FROM Event e WHERE e.id != :eventId AND " +
       "NOT (e.endDate <= :startDate OR e.startDate >= :endDate)")
List<Event> findConflictingEvents(@Param("eventId") Long eventId, 
                                 @Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate);
```

**Kontrol Mantığı:**
- Yeni kayıt yapılırken, katılımcının aynı zaman aralığında başka etkinliğe kayıtlı olup olmadığı kontrol edilir
- Çakışan etkinlik bulunursa → `TimeConflictException` fırlatılır

**Error Response:**
```json
{
    "status": 409,
    "message": "Zaman çakışması: Katılımcı ID 5 zaten aynı saatinde başka bir etkinliğe kayıtlıdır.",
    "timestamp": "2026-04-16T11:30:00"
}
```

### 🧪 Test Senaryosu

```bash
# 1. Etkinlik oluştur (15:00 - 17:00)
POST /api/events
{
    "title": "Python Kursu",
    "startDate": "2026-05-01T15:00:00",
    "endDate": "2026-05-01T17:00:00",
    "capacity": 2,
    ...
}

# 2. Katılımcı 1'i kaydet
POST /api/event-participants/register?eventId=1&participantId=1
✅ Başarı

# 3. Katılımcı 2'yi kaydet
POST /api/event-participants/register?eventId=1&participantId=2
✅ Başarı

# 4. Katılımcı 3'ü kaydet (Etkinlik dolu)
POST /api/event-participants/register?eventId=1&participantId=3
❌ 409 Conflict - "Etkinlik dolu"

# 5. Aynı zaman aralığında başka etkinlik (15:30 - 16:30) oluştur
POST /api/events
{
    "title": "Java Kursu",
    "startDate": "2026-05-01T15:30:00",
    "endDate": "2026-05-01T16:30:00",
    ...
}

# 6. Katılımcı 1'i yeni etkinliğe kaydetmeye çalış
POST /api/event-participants/register?eventId=2&participantId=1
❌ 409 Conflict - "Zaman çakışması"
```

---

## 🟢 2. DİNAMİK FİLTRELEME VE ARAMA MOTORU

### 📌 Özellik Açıklama

Kullanıcılar etkinlikleri **şehir, fiyat, durum** gibi ölçütlere göre **hızlıca filtreleyebilir**.

### 💻 Teknik Detay

#### A. Yeni Database Fields

**Event Entity'ye eklenen alanlar:**
```java
@Column(length = 100)
private String city;  // Etkinliğin şehri

@Column(name = "price_amount")
private Double price;  // Etkinlik fiyatı

@Column(columnDefinition = "BIT DEFAULT 0")
private Boolean isFree;  // Ücretsiz mi?
```

#### B. Spring Data JPA Custom Queries

```java
// EventRepository.java

// Şehire göre etkinlikleri getir
List<Event> findByCity(String city);

// Ücretsiz etkinlikleri getir
List<Event> findByIsFreeTrue();

// Belirtilen fiyattan daha ucuz etkinlikleri getir
@Query("SELECT e FROM Event e WHERE e.price < :price")
List<Event> findByPriceLessThan(@Param("price") Double price);

// Şehir ve fiyat kombinasyonu
@Query("SELECT e FROM Event e WHERE e.city = :city AND e.price < :maxPrice")
List<Event> findByCityAndMaxPrice(@Param("city") String city, @Param("maxPrice") Double maxPrice);
```

### 🔗 API Endpoints

| Endpoint | HTTP | Açıklama |
|----------|------|----------|
| `/api/events/city/{city}` | GET | Belirtilen şehirdeki tüm etkinlikler |
| `/api/events/free-events` | GET | Tüm ücretsiz etkinlikler |
| `/api/events/city/{city}/free` | GET | Belirtilen şehirdeki ücretsiz etkinlikler |
| `/api/events/max-price?maxPrice=100` | GET | 100 TL'den daha ucuz etkinlikler |
| `/api/events/search?keyword=yoga` | GET | Anahtar kelimeyle arama |

### 🧪 Örnek Sorgular

```bash
# Ankara'daki tüm etkinlikleri getir
GET http://localhost:8080/api/events/city/Ankara

# Ücretsiz etkinlikleri getir
GET http://localhost:8080/api/events/free-events

# İstanbul'daki ücretsiz yoga etkinliklerini getir
GET http://localhost:8080/api/events/city/Istanbul/free

# 50 TL'den daha ucuz etkinlikleri getir
GET http://localhost:8080/api/events/max-price?maxPrice=50

# "Konseri" içeren etkinlikleri ara
GET http://localhost:8080/api/events/search?keyword=Konser&status=PLANNED
```

---

## 📧 3. OTOMATİK E-POSTA HATIRLATICI

### 📌 Özellik Açıklama

Bir katılımcı etkinliğe kayıt olduğunda, sistem **otomatik olarak onay e-postası** gönderir.

### 💻 Teknik Detay

#### A. Email Service Implementation

```java
@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendEventRegistrationConfirmation(String participantEmail, 
                                                   String participantName, 
                                                   String eventTitle, 
                                                   String eventDate) {
        // E-posta oluştur ve gönder
    }
}
```

#### B. Otomatik Email Tetiklemesi

```java
// EventParticipantServiceImpl.java - registerParticipant() metodu içinde
eventParticipantRepository.save(eventParticipant);

// 📧 KAYIT ONAY E-POSTASI GÖNDER
emailService.sendEventRegistrationConfirmation(
    participant.getEmail(),
    participantFullName,
    event.getTitle(),
    eventDate
);
```

#### C. Email Şablonu

```
Sayın Mehmet Kara,

Etkinlik kaydınız başarıyla onaylanmıştır.

Etkinlik Detayları:
- Etkinlik Adı: Python Programlama Kursu
- Tarih / Saat: 01/05/2026 15:00

Lütfen saatinde etkinliğe katılmayı unutmayınız.

Sorularınız için bizimle iletişime geçebilirsiniz.

Saygılarımızla,
Etkinlik Yönetim Sistemi
```

### ⚙️ Gmail Yapılandırması

`application.properties` dosyasında:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Not:** Üretim ortamında bu bilgiler `.env` dosyasında veya environment variables'da güvenli şekilde saklanmalıdır.

---

## 🎨 4. SWAGGER / OPENAPI DOKUMENTASYONU

### 📌 Özellik Açıklama

Tüm API uç noktalarını **etkileşimli bir web arayüzünde** test edebilir, izler ve kullanabilirsiniz.

### 🌐 Erişim

**URL:** `http://localhost:8080/swagger-ui.html`

veya 

**OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

### 🎯 Swagger Config

```java
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Etkinlik Yönetim Sistemi API")
                .version("1.0.0")
                .description("Etkinlikler, katılımcılar ve kayıtları yönetmek için kapsamlı REST API")
                .contact(new Contact()
                    .name("Destek Ekibi")
                    .email("support@eventmanagement.com"))
                .license(new License()
                    .name("MIT Açık Kaynak Lisansı")))
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Geliştirme Sunucusu"),
                new Server().url("https://api.eventmanagement.com").description("Üretim Sunucusu")
            ));
    }
}
```

### 📝 Swagger Annotations

Tüm Controller metodlarına `@Operation` ve `@ApiResponse` annotations'ları eklenmiştir:

```java
@PostMapping("/register")
@Operation(summary = "Etkinliğe katılımcı kaydet", 
           description = "Bir katılımcıyı etkinliğe kaydeder. Zaman çakışması ve kontenjan kontrolleri yapılır.")
@ApiResponse(responseCode = "201", description = "Kayıt başarıyla yapıldı")
@ApiResponse(responseCode = "409", description = "Zaman çakışması veya etkinlik dolu")
public ResponseEntity<EventParticipantDTO> registerParticipant(
        @RequestParam Long eventId,
        @RequestParam Long participantId) {
    // ...
}
```

### 🖼️ Swagger UI Özellikleri

- ✅ Tüm endpoint'ler listelenir
- ✅ Her endpoint ile ilgili açıklamalar ve parametreler gösterilir
- ✅ Doğrudan tarayıcıdan API test edilebilir
- ✅ Request/Response örnekleri otomatik oluşturulur
- ✅ Error kodları açıklanır

---

## 📦 POM.xml Yeni Bağımlılıklar

```xml
<!-- Swagger / OpenAPI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>

<!-- Email Support -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- Lombok (Optional) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

---

## 🧪 Proje Build ve Çalıştırma

### Build Komut
```bash
cd demo
mvnw.cmd clean install
```

### Çalıştırma Komut
```bash
mvnw.cmd spring-boot:run
```

Uygulama başladıktan sonra:
- **REST API:** `http://localhost:8080/api/...`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

## 📊 Uygulama Mimarisi

```
┌─────────────────────────────────────────────────────────┐
│                   REST API Clients                        │
│           (Postman, Swagger UI, Frontend)                │
└────────────────────┬────────────────────────────────────┘
                     │
         ┌───────────┼───────────┐
         │           │           │
    ┌────▼────┐ ┌───▼────┐ ┌───▼────┐
    │EventCtrl │ │EventPartCtrl │ │CategoryCtrl │
    └────┬────┘ └───┬────┘ └───────┘
         │           │
    ┌────▼───────────▼────┐
    │   Service Layer     │
    │ ┌──────────────────┐│
    │ │EventService      ││
    │ │EventPartService  ││
    │ │EmailService ⭐   ││
    │ │                  ││
    │ └──────────────────┘│
    └────┬────────────────┘
         │
    ┌────▼──────────────────┐
    │ Repository Layer      │
    │ ┌──────────────────┐  │
    │ │EventRepository   │  │ ⭐ Dinamik filtreleme
    │ │ParticipantRepo   │  │
    │ │EventPartRepo     │  │
    │ │CategoryRepo      │  │
    │ └──────────────────┘  │
    └────┬──────────────────┘
         │
    ┌────▼──────────────────┐
    │  SQL Server Database  │
    │ - Events             │
    │ - Participants       │
    │ - EventParticipants  │
    │ - Categories         │
    └──────────────────────┘
```

---

## ✨ Yeni Exception Tipleri

1. **TimeConflictException** - Zaman çakışması
2. **EventFullException** - Etkinlik dolu
3. **DuplicateRegistrationException** - İkili kayıt
4. **ResourceNotFoundException** - Kaynak bulunamadı

Tüm exceptionlar `GlobalExceptionHandler` tarafından merkezi olarak yönetilir ve tutarlı JSON response'lar döner.

---

## 🔒 Güvenlik ve Best Practices

- ✅ Input validation (Hibernate Validator)
- ✅ CORS aktif
- ✅ SQL injection protection (Parameterized queries)
- ✅ Exception handling
- ✅ Logging
- ✅ Email config security (externalize credentials)

---

## 📈 İleri Geliştirmeler (Future Roadmap)

- [ ] JWT Authentication
- [ ] Role-Based Access Control
- [ ] Event ratings/reviews
- [ ] Notification service with queue (RabbitMQ/Kafka)
- [ ] Payment integration
- [ ] Calendar view
- [ ] PDF ticket generation
- [ ] Admin dashboard

---

## 📞 İletişim ve Destek

Sorularınız için: support@eventmanagement.com

---

**Son Güncelleme:** 16 Nisan 2026
**Versiyon:** 1.0.0
