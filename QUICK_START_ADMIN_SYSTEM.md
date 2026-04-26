# 🚀 ADMIN ONAY & ÜYE KONTROL - HIZLI BAŞLANGIÇ

## Sistem Özeti

Bu sistem 3 ana bileşenden oluşur:

1. **Admin Onay Sistemi** - Organizatörler tarafından eklenen etkinlikler admin onayına gidiyor
2. **Üye Kontrol** - Üyeler etkinlikleri yorumlayabiliyor, katılabiliyor, çıkabiliyor
3. **Admin Yönetimi** - Adminlerin etkinlik onayı/reddi/silme işlemleri

---

## ⚡ 5 Dakikalık Kurulum

### Adım 1: Veritabanı Migration
```bash
# SQL Server Management Studio aç
# Ctrl+O ile dosyayı aç
# Database_Migration_Phase3_AdminApprovalSystem.sql

# Veya Command Line:
sqlcmd -S YOUR_SERVER -d YOUR_DATABASE -i Database_Migration_Phase3_AdminApprovalSystem.sql
```

### Adım 2: Spring Boot Çalıştır
```bash
cd c:\Users\Kadir\EventManagementSystem\online-etkinlik-sistemi

# Maven ile build
mvn clean install

# Çalıştır
mvn spring-boot:run

# Veya:
java -jar target/online-etkinlik-sistemi-*.jar
```

### Adım 3: Swagger UI Aç
```
http://localhost:8080/swagger-ui.html
```

---

## 🧪 Basit Test Senaryosu

### 1️⃣ Admin Oluştur
```bash
curl -X POST http://localhost:8080/api/admins \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin_test",
    "password": "test123",
    "email": "admin@test.com",
    "fullName": "Test Admin",
    "isSuperAdmin": false
  }'

# Response:
# {
#   "id": 2,
#   "username": "admin_test",
#   "approvalsCount": 0,
#   ...
# }
```

### 2️⃣ Etkinlik Oluştur (Organizatör)
```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Etkinliği",
    "description": "Bu bir test etkinliğidir",
    "startDate": "2026-05-15T10:00:00",
    "endDate": "2026-05-15T12:00:00",
    "location": "İstanbul",
    "city": "İstanbul",
    "price": 0,
    "isFree": true,
    "capacity": 50,
    "categoryId": 1
  }'

# Response:
# {
#   "id": 1,
#   "approvalStatus": "PENDING",  ⚠️ ONAY BEKLEMEDE
#   ...
# }
```

### 3️⃣ Onay Bekleyen Etkinlikleri Listele (Admin)
```bash
curl http://localhost:8080/api/events/approval/pending
```

### 4️⃣ Etkinliği Onayla (Admin)
```bash
curl -X POST http://localhost:8080/api/events/1/approve?adminId=2
```

### 5️⃣ Etkinliğe Katıl (Üye)
```bash
curl -X POST "http://localhost:8080/api/event-participants/register?eventId=1&participantId=1"
```

### 6️⃣ Yorum Ekle (Üye)
```bash
curl -X POST "http://localhost:8080/api/event-reviews?eventId=1&participantId=1&rating=5&title=Harika&comment=Çok%20iyi%20bir%20etkinlikti"

# Response:
# {
#   "id": 1,
#   "rating": 5,
#   "title": "Harika",
#   "helpfulCount": 0,
#   ...
# }
```

### 7️⃣ Etkinlik Yorumlarını Listele
```bash
curl http://localhost:8080/api/event-reviews/event/1
```

### 8️⃣ Ortalama Puanı Getir
```bash
curl http://localhost:8080/api/event-reviews/event/1/average-rating

# Response: 5 (puan)
```

---

## 📚 Dosya Haritası

```
project/
├── src/main/java/com/example/demo/
│   ├── entity/
│   │   ├── Admin.java                          ✅ YENİ
│   │   ├── Event.java                          ✏️ GÜNCELLENDI
│   │   └── EventReview.java                    ✔️ VAR
│   │
│   ├── repository/
│   │   ├── AdminRepository.java                ✅ YENİ
│   │   └── EventRepository.java                ✔️ VAR
│   │
│   ├── dto/
│   │   ├── AdminDTO.java                       ✅ YENİ
│   │   └── EventReviewDTO.java                 ✔️ VAR
│   │
│   ├── service/
│   │   ├── AdminService.java                   ✅ YENİ
│   │   ├── AdminServiceImpl.java                ✅ YENİ
│   │   ├── EventService.java                   ✏️ GÜNCELLENDI
│   │   ├── EventServiceImpl.java                ✏️ GÜNCELLENDI
│   │   └── EventReviewService.java             ✔️ VAR
│   │
│   └── controller/
│       ├── AdminController.java                ✅ YENİ
│       ├── EventReviewController.java          ✅ YENİ
│       ├── EventController.java                ✏️ GÜNCELLENDI
│       └── EventParticipantController.java     ✔️ VAR
│
├── Database_Migration_Phase3_AdminApprovalSystem.sql    ✅ YENİ
├── ADMIN_APPROVAL_AND_MEMBER_CONTROLS_API.md          ✅ YENİ
└── IMPLEMENTATION_SUMMARY.md                           ✅ YENİ
```

---

## 🎯 Ana Endpoint'ler

### Admin İşlemleri
```
POST   /api/admins                      - Admin oluştur
GET    /api/admins                      - Tüm adminleri listele
GET    /api/admins/{id}                 - Admin getir
PUT    /api/admins/{id}                 - Admin güncelle
DELETE /api/admins/{id}                 - Admin sil
```

### Etkinlik Onay Sistemi
```
GET    /api/events/approval/pending     - Onay bekleyen etkinlikler
POST   /api/events/{id}/approve         - Etkinlik onayla
POST   /api/events/{id}/reject          - Etkinlik reddet
DELETE /api/events/{id}/admin           - Admin olarak sil
```

### Yorum Yönetimi
```
POST   /api/event-reviews               - Yorum ekle
GET    /api/event-reviews/event/{id}    - Etkinlik yorumlarını listele
PUT    /api/event-reviews/{id}          - Yorumu güncelle
DELETE /api/event-reviews/{id}          - Yorumu sil
POST   /api/event-reviews/{id}/mark-helpful  - Faydalı işaretle
GET    /api/event-reviews/event/{id}/average-rating  - Ortalama puan
```

### Üye Katılım
```
POST   /api/event-participants/register     - Etkinliğe katıl
DELETE /api/event-participants/unregister   - Etkinlikten çık
```

---

## 🔍 Durum Kodu Referansı

| Kod | Anlamı | Örnek |
|-----|--------|-------|
| **200 OK** | İşlem başarılı | Etkinlik onaylandı |
| **201 Created** | Yeni kayıt oluşturuldu | Admin oluşturuldu |
| **204 No Content** | İşlem başarılı (boş response) | Etkinlik silindi |
| **400 Bad Request** | Geçersiz veri | Puan 1-5 dışında |
| **404 Not Found** | Kaynak bulunamadı | Admin ID yanlış |
| **409 Conflict** | Çakışma var | Zaman çakışması, duplicate yorum |
| **500 Server Error** | Sunucu hatası | Database bağlantı sorunu |

---

## ⚙️ Ortam Değişkenleri

`application.properties` dosyasında:

```properties
# Database
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=EventManagementDB
spring.datasource.username=sa
spring.datasource.password=YourPassword
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect

# Server
server.port=8080
```

---

## 🐛 Sık Sorulan Problemler

### Problem: "Admin bulunamadı" hatası
**Çözüm:** Admin ID'nin doğru olduğundan emin olun
```bash
GET /api/admins  # Tüm adminleri listele
```

### Problem: "Event not found" hatası
**Çözüm:** Event ID'nin var olduğundan emin olun
```bash
GET /api/events
```

### Problem: "Zaman çakışması" hatası
**Çözüm:** Üye aynı saatte başka bir etkinliğe kayıtlı
```bash
GET /api/event-participants/participant/{participantId}
```

### Problem: "Duplicate yorum" hatası
**Çözüm:** Aynı etkinliğe zaten yorum yapılmış
- Mevcut yorumu güncelle: PUT `/api/event-reviews/{id}`
- Veya sil sonra yeniden ekle

---

## 📊 SQL Queries

### Admin İstatistikleri
```sql
SELECT username, 
       approvalsCount, 
       rejectionsCount, 
       deletionsCount
FROM Admins
ORDER BY approvalsCount DESC;
```

### Onay Bekleyen Etkinlikler
```sql
SELECT e.title, 
       e.approvalStatus, 
       e.createdAt
FROM Events e
WHERE e.approvalStatus = 'PENDING'
ORDER BY e.createdAt ASC;
```

### Etkinlik Yorumları ve Ortalama
```sql
SELECT 
    e.title,
    AVG(er.rating) as average_rating,
    COUNT(er.id) as review_count
FROM Events e
LEFT JOIN EventReviews er ON e.id = er.event_id
WHERE e.id = @eventId
GROUP BY e.id, e.title;
```

---

## 📞 Yardım

**Dokümantasyon:**
- `ADMIN_APPROVAL_AND_MEMBER_CONTROLS_API.md` - Detaylı API referansı
- `IMPLEMENTATION_SUMMARY.md` - Teknik detaylar

**Swagger UI:**
- Interactive API testing: `http://localhost:8080/swagger-ui.html`

**Database:**
- Migration script: `Database_Migration_Phase3_AdminApprovalSystem.sql`

---

## ✨ Sonraki Öneriler

1. **Authentication** ekle (Spring Security)
2. **Email notifications** ekle (onay/red bilgilendirmesi)
3. **Rate limiting** ekle
4. **Audit logging** ekle
5. **Frontend** componentleri geliştir
6. **Unit tests** yaz

