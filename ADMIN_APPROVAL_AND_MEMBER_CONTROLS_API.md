# ADMIN ONAY SİSTEMİ VE ÜYE KONTROL KÖD ÖRNEKLERİ

## 1. ADMİN YÖNETIMI

### 1.1 Yeni Admin Oluştur
```java
// AdminController - POST /api/admins
AdminDTO newAdmin = new AdminDTO();
newAdmin.setUsername("admin2");
newAdmin.setPassword("secure_password");
newAdmin.setEmail("admin2@example.com");
newAdmin.setFullName("İkinci Yönetici");
newAdmin.setIsSuperAdmin(false);

// Request örneği:
POST /api/admins
Content-Type: application/json

{
    "username": "admin2",
    "password": "secure_password",
    "email": "admin2@example.com",
    "fullName": "İkinci Yönetici",
    "isSuperAdmin": false
}

// Response:
HTTP/1.1 201 Created
{
    "id": 2,
    "username": "admin2",
    "email": "admin2@example.com",
    "fullName": "İkinci Yönetici",
    "isActive": true,
    "isSuperAdmin": false,
    "approvalsCount": 0,
    "rejectionsCount": 0,
    "deletionsCount": 0,
    "createdAt": "2026-04-26T10:30:00"
}
```

### 1.2 Admin Bilgisi Getir
```
// GET /api/admins/{id}
GET /api/admins/2
Authorization: Bearer token

// Response:
HTTP/1.1 200 OK
{
    "id": 2,
    "username": "admin2",
    "email": "admin2@example.com",
    "fullName": "İkinci Yönetici",
    "isActive": true,
    "isSuperAdmin": false,
    "approvalsCount": 15,
    "rejectionsCount": 3,
    "deletionsCount": 2,
    "createdAt": "2026-04-26T10:30:00",
    "updatedAt": "2026-04-26T14:20:00",
    "lastLoginAt": "2026-04-26T14:15:00"
}
```

### 1.3 Tüm Adminleri Listele
```
GET /api/admins
Authorization: Bearer token

// Response:
HTTP/1.1 200 OK
[
    {
        "id": 1,
        "username": "admin",
        "email": "admin@example.com",
        "fullName": "Sistem Yöneticisi",
        ...
    },
    {
        "id": 2,
        "username": "admin2",
        "email": "admin2@example.com",
        "fullName": "İkinci Yönetici",
        ...
    }
]
```

---

## 2. ETKİNLİK ONAY SİSTEMİ

### 2.1 Onay Bekleyen Etkinlikleri Listele
```
GET /api/events/approval/pending
Authorization: Bearer admin_token

// Response:
HTTP/1.1 200 OK
[
    {
        "id": 5,
        "title": "Yazılım Konferansı 2026",
        "description": "Türkiye'nin en büyük yazılım konferansı",
        "startDate": "2026-05-15T09:00:00",
        "endDate": "2026-05-15T17:00:00",
        "location": "İstanbul Kongre Merkezi",
        "city": "İstanbul",
        "capacity": 500,
        "approvalStatus": "PENDING",
        "approverAdminId": null,
        "approvedAt": null,
        "rejectionReason": null,
        "participantCount": 0
    },
    {
        "id": 6,
        "title": "Spor Festivali",
        "approvalStatus": "PENDING",
        ...
    }
]
```

### 2.2 Etkinliği Onayla
```
// POST /api/events/{id}/approve?adminId={adminId}
POST /api/events/5/approve?adminId=1
Authorization: Bearer admin_token

// Response:
HTTP/1.1 200 OK

// Event başarıyla onaylandı. Admin istatistikleri otomatik olarak güncellendi:
// approvalsCount: 15 -> 16
```

### 2.3 Etkinliği Reddet
```
// POST /api/events/{id}/reject?rejectionReason={reason}
POST /api/events/5/reject?rejectionReason=Etkinlik%20bilgileri%20eksik.%20Lütfen%20düzenleyiniz.
Authorization: Bearer admin_token

// Response:
HTTP/1.1 200 OK

// Event reddedildi. Organizatör bilgilendirilir.
// rejectionReason: "Etkinlik bilgileri eksik. Lütfen düzenleyiniz."
```

### 2.4 Etkinlik Oluştur (Organizatör)
```java
// EventDTO oluştur - Approval PENDING durumunda başlar
EventDTO newEvent = new EventDTO();
newEvent.setTitle("Web Geliştirme Atölyesi");
newEvent.setDescription("React ve Node.js ile web uygulaması geliştirme");
newEvent.setStartDate(LocalDateTime.of(2026, 6, 1, 14, 0));
newEvent.setEndDate(LocalDateTime.of(2026, 6, 1, 18, 0));
newEvent.setLocation("Ankara");
newEvent.setCity("Ankara");
newEvent.setPrice(150.0);
newEvent.setIsFree(false);
newEvent.setCapacity(30);
newEvent.setCategoryId(3L);

// POST /api/events
POST /api/events
Content-Type: application/json

{
    "title": "Web Geliştirme Atölyesi",
    "description": "React ve Node.js ile web uygulaması geliştirme",
    "startDate": "2026-06-01T14:00:00",
    "endDate": "2026-06-01T18:00:00",
    "location": "Ankara",
    "city": "Ankara",
    "price": 150.0,
    "isFree": false,
    "capacity": 30,
    "categoryId": 3
}

// Response:
HTTP/1.1 201 Created
{
    "id": 7,
    "title": "Web Geliştirme Atölyesi",
    "approvalStatus": "PENDING",  // ⚠️ Admin onayı bekliyor
    "approverAdminId": null,
    "approvedAt": null,
    "rejectionReason": null,
    ...
}
```

---

## 3. ÜYE KONTROL SİSTEMİ (Yorumlama, Ekleme, Çıkarma)

### 3.1 Etkinliğe Katılır (Ekleme)
```
// POST /api/event-participants/register?eventId={eventId}&participantId={participantId}
POST /api/event-participants/register?eventId=5&participantId=10
Authorization: Bearer user_token

// Response:
HTTP/1.1 201 Created
{
    "id": 45,
    "eventId": 5,
    "participantId": 10,
    "status": "REGISTERED",
    "registeredAt": "2026-04-26T10:35:00"
}

// Zaman çakışması varsa:
HTTP/1.1 409 Conflict
{
    "error": "Zaman çakışması! Aynı saatte başka bir etkinliğe kaydlısınız."
}

// Etkinlik dolu ise:
HTTP/1.1 409 Conflict
{
    "error": "Maalesef etkinlik dolu. Kontenjanı sınırı aşıldı."
}
```

### 3.2 Etkinlikten Çıkar (Silme)
```
// DELETE /api/event-participants/unregister?eventId={eventId}&participantId={participantId}
DELETE /api/event-participants/unregister?eventId=5&participantId=10
Authorization: Bearer user_token

// Response:
HTTP/1.1 204 No Content

// Kayıt başarıyla silindir. Kontenjan boş olur.
```

### 3.3 Etkinlik için Yorum/Değerlendirme Ekle
```
// POST /api/event-reviews?eventId={id}&participantId={id}&rating={1-5}&title={title}&comment={comment}
POST /api/event-reviews?eventId=5&participantId=10&rating=5&title=Mükemmel%20etkinlik&comment=Çok%20iyi%20organizasyondu
Authorization: Bearer user_token

// Response:
HTTP/1.1 201 Created
{
    "id": 1,
    "eventId": 5,
    "eventTitle": "Yazılım Konferansı 2026",
    "participantId": 10,
    "participantName": "A***",  // Privacy: İsim maskelendi
    "rating": 5,
    "title": "Mükemmel etkinlik",
    "comment": "Çok iyi organizasyondu",
    "helpfulCount": 0,
    "createdAt": "2026-04-26T10:40:00"
}

// Aynı etkinliğe tekrar yorum yapmaya çalışırsa:
HTTP/1.1 409 Conflict
{
    "error": "Bu etkinlik için zaten bir değerlendirme yaptınız"
}

// Puan geçersiz ise (1-5 dışı):
HTTP/1.1 400 Bad Request
{
    "error": "Rating must be between 1 and 5"
}
```

### 3.4 Etkinlik Yorumlarını Listele
```
// GET /api/event-reviews/event/{eventId}
GET /api/event-reviews/event/5
Authorization: Bearer token

// Response:
HTTP/1.1 200 OK
[
    {
        "id": 1,
        "eventId": 5,
        "eventTitle": "Yazılım Konferansı 2026",
        "participantId": 10,
        "participantName": "A***",
        "rating": 5,
        "title": "Mükemmel etkinlik",
        "comment": "Çok iyi organizasyondu",
        "helpfulCount": 5,
        "createdAt": "2026-04-26T10:40:00"
    },
    {
        "id": 2,
        "eventId": 5,
        "participantId": 11,
        "participantName": "B***",
        "rating": 4,
        "title": "Harika bir gün",
        "comment": "Organizasyon çok iyiydi",
        "helpfulCount": 3,
        "createdAt": "2026-04-26T11:00:00"
    }
]
```

### 3.5 Etkinliğin Ortalama Puanını Getir
```
// GET /api/event-reviews/event/{eventId}/average-rating
GET /api/event-reviews/event/5/average-rating
Authorization: Bearer token

// Response:
HTTP/1.1 200 OK
4.5

// Event'in tüm yorumlarına göre ortalama: 4.5
```

### 3.6 Yorumu Güncelle (Yazar tarafından)
```
// PUT /api/event-reviews/{id}?rating={newRating}&title={title}&comment={comment}&participantId={id}
PUT /api/event-reviews/1?rating=4&title=İyi%20etkinlik&comment=Biraz%20daha%20organize%20olabilirdi&participantId=10
Authorization: Bearer user_token

// Response:
HTTP/1.1 200 OK
{
    "id": 1,
    "rating": 4,  // 5'den 4'e değişti
    "title": "İyi etkinlik",
    "comment": "Biraz daha organize olabilirdi",
    "updatedAt": "2026-04-26T10:50:00"
}
```

### 3.7 Yorumu Sil (Yazar veya Admin)
```
// DELETE /api/event-reviews/{id}?participantId={id}
DELETE /api/event-reviews/1?participantId=10
Authorization: Bearer user_token

// Response:
HTTP/1.1 204 No Content

// Yorum silinir ve event rating'i otomatik olarak güncellenir
```

### 3.8 Yorumu Faydalı Olarak İşaretle
```
// POST /api/event-reviews/{id}/mark-helpful
POST /api/event-reviews/1/mark-helpful
Authorization: Bearer user_token

// Response:
HTTP/1.1 204 No Content

// Yorum helpfulCount'u 1 artar
```

### 3.9 En Faydalı Yorumları Getir
```
// GET /api/event-reviews/event/{eventId}/most-helpful
GET /api/event-reviews/event/5/most-helpful
Authorization: Bearer token

// Response:
HTTP/1.1 200 OK
[
    {
        "id": 2,
        "rating": 4,
        "helpfulCount": 8,  // En çok beğenilen
        ...
    },
    {
        "id": 1,
        "rating": 5,
        "helpfulCount": 5,
        ...
    }
]
```

### 3.10 Minimum Puana Göre Yorumları Getir
```
// GET /api/event-reviews/event/{eventId}/min-rating?minRating={puan}
GET /api/event-reviews/event/5/min-rating?minRating=4
Authorization: Bearer token

// Response:
HTTP/1.1 200 OK
[
    {
        "rating": 5,  // 4+ olan yorumlar
        ...
    },
    {
        "rating": 4,
        ...
    }
]
```

---

## 4. ADMİN SİLME İŞLEMİ

### 4.1 Etkinliği Admin Olarak Sil
```
// DELETE /api/events/{id}/admin?adminId={adminId}
DELETE /api/events/5/admin?adminId=1
Authorization: Bearer admin_token

// Response:
HTTP/1.1 204 No Content

// Event silinir ve Admin istatistikleri güncellenir:
// Admin 1: deletionsCount: 2 -> 3
```

---

## 5. SERVİS ÖZET TABLOSU

| İşlem | Endpoint | Method | Rol | Durum |
|-------|----------|--------|-----|-------|
| **Yorum Ekle** | `/api/event-reviews` | POST | Üye | ✅ |
| **Yorum Güncelle** | `/api/event-reviews/{id}` | PUT | Üye | ✅ |
| **Yorum Sil** | `/api/event-reviews/{id}` | DELETE | Üye/Admin | ✅ |
| **Yorum Listele** | `/api/event-reviews/event/{id}` | GET | Herkese Açık | ✅ |
| **Etkinliğe Katıl** | `/api/event-participants/register` | POST | Üye | ✅ |
| **Etkinlikten Çık** | `/api/event-participants/unregister` | DELETE | Üye | ✅ |
| **Onay Bekleyen** | `/api/events/approval/pending` | GET | Admin | ✅ |
| **Etkinlik Onayla** | `/api/events/{id}/approve` | POST | Admin | ✅ |
| **Etkinlik Reddet** | `/api/events/{id}/reject` | POST | Admin | ✅ |
| **Admin Sil** | `/api/events/{id}/admin` | DELETE | Admin | ✅ |
| **Admin Oluştur** | `/api/admins` | POST | SuperAdmin | ✅ |

---

## 6. ERRORs ve İSTİSNALAR

### 6.1 Yorum Alanında Hata
```json
{
    "timestamp": "2026-04-26T10:45:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Rating must be between 1 and 5",
    "path": "/api/event-reviews"
}
```

### 6.2 Kaynak Bulunamadı
```json
{
    "status": 404,
    "error": "Not Found",
    "message": "Event not found with id: 999"
}
```

### 6.3 Zaman Çakışması
```json
{
    "status": 409,
    "error": "Conflict",
    "message": "Zaman çakışması! Aynı saatte başka bir etkinliğe kayıtlısınız."
}
```

---

## 7. VERİTABANI TABLOLARI

### Admins Tablosu
```sql
CREATE TABLE Admins (
    id BIGINT PRIMARY KEY,
    username NVARCHAR(100) UNIQUE NOT NULL,
    password NVARCHAR(255) NOT NULL,
    email NVARCHAR(100) UNIQUE NOT NULL,
    full_name NVARCHAR(200) NOT NULL,
    is_active BIT DEFAULT 1,
    is_super_admin BIT DEFAULT 0,
    approvals_count INT DEFAULT 0,
    rejections_count INT DEFAULT 0,
    deletions_count INT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME,
    last_login_at DATETIME,
    notes NVARCHAR(500)
);
```

### Events - Approval Alanları
```
approval_status NVARCHAR(50) - PENDING, APPROVED, REJECTED
approver_admin_id BIGINT - Onaylayan admin ID
approved_at DATETIME - Onay tarihi
rejection_reason NVARCHAR(500) - Red nedeni
```

### EventReviews Tablosu
```sql
CREATE TABLE EventReviews (
    id BIGINT PRIMARY KEY,
    event_id BIGINT FOREIGN KEY,
    participant_id BIGINT FOREIGN KEY,
    rating INT (1-5),
    title NVARCHAR(500),
    comment NVARCHAR(2000),
    helpful_count INT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME,
    UNIQUE(event_id, participant_id)
);
```

---

## 8. KURULUŞ KONTROL LİSTESİ

- [ ] Database_Migration_Phase3_AdminApprovalSystem.sql çalıştırıldı
- [ ] Admin entity oluşturuldu (Admin.java)
- [ ] AdminRepository oluşturuldu
- [ ] AdminService & AdminServiceImpl oluşturuldu
- [ ] AdminController oluşturuldu
- [ ] EventReviewController oluşturuldu
- [ ] EventService.deleteEventByAdmin metodu eklendi
- [ ] EventServiceImpl AdminService dependency'si eklendi
- [ ] EventController delete endpoints'leri güncellendi
- [ ] Application başlatıldı ve testler yapıldı

