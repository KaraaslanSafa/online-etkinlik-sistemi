# 🎉 Online Etkinlik Yönetim Sistemi - Admin Onay Sistemi Dokümantasyonu

## 📋 Genel Bakış

Etkinlik Yönetim Sistemine **Admin Onay Sistemi** başarıyla uygulanmıştır. Organizatörler tarafından oluşturulan etkinlikler artık admin tarafından onaylanmadan önce siteye eklenmez.

---

## ✅ Uygulanmış Özellikler

### 1️⃣ BACKEND ÖZELLIKLERI (Java/Spring Boot)

#### 📄 Yeni Enum: `ApprovalStatus.java`
```java
public enum ApprovalStatus {
    PENDING("Onay Beklemede"),
    APPROVED("Onaylandı"),
    REJECTED("Reddedildi");
}
```

#### 🏗️ Event Entity Güncellemeleri
**Yeni Alanlar:**
- `approvalStatus` (ApprovalStatus): Etkinliğin onay durumu
- `approverAdminId` (Long): Onayan admin ID'si
- `approvedAt` (LocalDateTime): Onay tarihi
- `rejectionReason` (String): Reddetme nedeni (varsa)

#### 📊 EventDTO Güncellemeleri
```java
private String approvalStatus;
private Long approverAdminId;
private LocalDateTime approvedAt;
private String rejectionReason;
```

#### 🔧 EventService Interface - Yeni Metodlar
```java
// Admin Onay Metodları
List<EventDTO> getApprovalPendingEvents();      // Onay bekleyen etkinlikleri getir
void approveEvent(Long eventId, Long adminId);  // Etkinliği onayla
void rejectEvent(Long eventId, String reason);  // Etkinliği reddet
```

#### 🛠️ EventServiceImpl - Implementation
- `getApprovalPendingEvents()`: PENDING durumundaki etkinlikleri filtreler
- `approveEvent()`: Etkinliği APPROVED durumuna geçirir, admin bilgisini kaydeder
- `rejectEvent()`: Etkinliği REJECTED durumuna geçirir, reddetme nedenini kaydeder

#### 🌐 EventController - Yeni API Endpoints

**Admin Onay Endpoints:**
```
GET    /api/events/approval/pending              → Onay bekleyen etkinlikleri listele
POST   /api/events/{id}/approve?adminId={id}    → Etkinliği onayla
POST   /api/events/{id}/reject?reason={text}    → Etkinliği reddet
DELETE /api/events/{id}                          → Admin etkinliği sil
```

---

### 2️⃣ FRONTEND ÖZELLIKLERI (React)

#### 📱 Yeni Bileşen: `AdminApprovalPanel.js`
**Özellikler:**
- Onay bekleyen etkinliklerin listesi
- Her etkinlik için:
  - Başlık, açıklama, tarih, konum
  - Kapasite ve kategori bilgileri
- ✓ Onayla butonu (yeşil)
- ✗ Reddet butonu (kırmızı)
- Reddetme sebebi popup'ı

**Kullanım:**
Admin paneline erişmek için ana sayfada "⚙️ Admin Paneli Aç" butonunu tıklayın.

#### 🔄 Güncellenmiş Bileşenler

**EventForm.js - Etkinlik Oluşturma Formu**
```
Alanlar:
✓ Etkinlik Adı (zorunlu)
✓ Açıklama
✓ Başlangıç Tarihi (zorunlu)
✓ Bitiş Tarihi
✓ Konum (zorunlu)
✓ Şehir
✓ Kapasite
✓ Kategori ID
✓ Ücretsiz/Ücretli seçeneği
✓ Fiyat (ücretli seçilince)

Mesaj: "✓ Etkinlik başarıyla oluşturuldu! Admin onayını beklemektedir."
```

**EventList.js - Onaylı Etkinliklerin Listesi**
- API'dan tüm etkinlikleri çeker
- Sadece APPROVED durumundaki etkinlikleri gösterir
- Grid düzeninde etkinlik kartları
- Admin silme butonu
- Etkinlik detaylarına gitme butonu

**ParticipantList.js - Katılımcı Yönetimi**
- Etkinliğe kayıtlı katılımcıları listeler
- Katılımcı adı, soyadı ve email
- Katılımcı çıkarma butonu (✕ Çıkar)
- API entegrasyonu

**App.js - Ana Uygulama**
- Admin paneli toggle butonu
- API'dan etkinlik çekme
- Geliştirilmiş UI/UX
- Mesaj sistemi

---

## 🔄 İş Akışı (Workflow)

### Etkinlik Oluşturma Süreci:

```
1. ORGANIZATÖR
   ↓ EventForm.js'de etkinlik oluşturur
   ↓ POST /api/events (EventDTO gönderir)
   ↓ Backend: Event kaydedilir (approvalStatus = PENDING)
   
2. DATABASE
   ↓ Etkinlik PENDING durumda saklanır
   
3. ADMIN
   ↓ AdminApprovalPanel.js'de Onay Bekleyen Etkinlikler'i görür
   ↓ "✓ Onayla" veya "✗ Reddet" seçeneğini tıklar
   
4. ONAYLAMA DURUMUNDA:
   ↓ POST /api/events/{id}/approve
   ↓ approvalStatus = APPROVED
   ↓ approverAdminId = {admin ID}
   ↓ approvedAt = {şu anki zaman}
   
5. REDDETME DURUMUNDA:
   ↓ POST /api/events/{id}/reject?reason=...
   ↓ approvalStatus = REJECTED
   ↓ rejectionReason = {belirtilen neden}
   
6. ÜYELERİN GÖRECEĞI:
   ↓ EventList.js sadece APPROVED etkinlikleri gösterir
   ↓ Üyeler etkinliğe katılabilir
```

---

## 📲 API Endpoint Detayları

### Etkinlik Oluşturma
**Endpoint:** `POST /api/events`
**Status:** PENDING (default olarak)
```json
{
  "title": "React Eğitimi",
  "description": "İleri seviye React",
  "startDate": "2026-06-01T14:00",
  "endDate": "2026-06-01T18:00",
  "location": "İstanbul Konferans Merkezi",
  "city": "İstanbul",
  "capacity": 50,
  "categoryId": 1,
  "isFree": true,
  "price": 0.0
}
```

### Onay Bekleyen Etkinlikleri Getir
**Endpoint:** `GET /api/events/approval/pending`
**Response:** Tüm PENDING etkinliklerin listesi

### Etkinliği Onayla
**Endpoint:** `POST /api/events/{eventId}/approve?adminId={adminId}`
**Sonuç:** approvalStatus = APPROVED

### Etkinliği Reddet
**Endpoint:** `POST /api/events/{eventId}/reject?rejectionReason={reason}`
**Sonuç:** approvalStatus = REJECTED

### Etkinliği Sil (Admin)
**Endpoint:** `DELETE /api/events/{eventId}`
**İzin:** Sadece Admin

---

## 👥 Kullanıcı Rollerine Göre İzinler

### 👨‍💼 Organizatör
- ✅ Etkinlik oluşturma (PENDING durumunda)
- ✅ Etkinlik düzenleme
- ✅ Kendi etkinliklerini görme

### 🛡️ Admin
- ✅ Onay bekleyen etkinlikleri görme
- ✅ Etkinlikleri onaylama
- ✅ Etkinlikleri reddetme (neden belirtmek zorunlu)
- ✅ Etkinlikleri silme
- ✅ Tüm etkinlikleri yönetme

### 👤 Üyeler
- ✅ Etkinliklere katılabilir (APPROVED etkinliklere)
- ✅ Katılımcıları yönetebilir
- ✅ Yorum yapabilir
- ✅ Etkinlikler hakkında bilgi alabilir

---

## 🗂️ Dosya Yapısı

### Backend Dosyaları:
```
src/main/java/com/example/demo/
├── entity/
│   ├── ApprovalStatus.java          (NEW)
│   └── Event.java                   (MODIFIED)
├── dto/
│   └── EventDTO.java                (MODIFIED)
├── service/
│   ├── EventService.java            (MODIFIED)
│   └── EventServiceImpl.java         (MODIFIED)
└── controller/
    └── EventController.java         (MODIFIED)
```

### Frontend Dosyaları:
```
frontend/src/
├── AdminApprovalPanel.js            (NEW)
├── App.js                           (MODIFIED)
├── EventForm.js                     (MODIFIED)
├── EventList.js                     (MODIFIED)
└── ParticipantList.js               (MODIFIED)
```

---

## 🚀 Başlatma Talimatları

### Backend:
```bash
cd demo
./mvnw spring-boot:run
# API çalışacak: http://localhost:8080
```

### Frontend:
```bash
cd demo/frontend
npm install
npm start
# Frontend çalışacak: http://localhost:3000
```

### Admin Paneline Erişim:
1. Frontend açılınca, Ana sayfada "⚙️ Admin Paneli Aç" butonunu tıklayın
2. Onay bekleyen etkinlikleri göreceksiniz
3. "✓ Onayla" veya "✗ Reddet" butonlarını kullanın

---

## 📝 Yorum Sistemi (Mevcut)

Üyeler tarafından etkinlikler hakkında yorum yapılabilir:

**EventReviewService Metodları:**
- `createReview()`: Yorum oluşturma
- `getReviewsByEvent()`: Etkinlikteki yorumları getirme
- `deleteReview()`: Yorum silme
- `updateReview()`: Yorum güncelleme
- `markAsHelpful()`: Yararlı işareti

---

## ✨ Gelecek Geliştirmeler

- [ ] Email bildirimleri (etkinlik onaylandığında organizatöre)
- [ ] Reddetme nedeni organizatöre gönderilmesi
- [ ] Admin raporları
- [ ] Etkinlik düzenleme için onay sistemi
- [ ] Role-based access control (RBAC)
- [ ] Onay iptal etme özelliği

---

## 📊 Veritabanı Schema Güncellemeleri

**Events Tablosuna Eklenen Alanlar:**
```sql
ALTER TABLE Events ADD COLUMN approval_status VARCHAR(20) DEFAULT 'PENDING';
ALTER TABLE Events ADD COLUMN approver_admin_id BIGINT;
ALTER TABLE Events ADD COLUMN approved_at DATETIME;
ALTER TABLE Events ADD COLUMN rejection_reason VARCHAR(500);
```

---

## 🔐 Güvenlik Notları

⚠️ **Geliştirilmesi Gereken Noktalar:**
- Admin endpoint'lerine role-based authorization eklenmelidir
- Admin ID'si şu anda query parametresinden alınmaktadır (güvenlik riski)
- Spring Security ile admin rolü doğrulaması yapılmalıdır
- Reddetme nedemi ve diğer sensitive bilgiler log'lanmalıdır

---

## 🐛 Hata Giderme

### "Etkinlik oluştururken hata oluştu"
- Backend'in çalışıyor olduğunu kontrol edin
- API URL'sini kontrol edin (http://localhost:8080)
- Browser console'da hata mesajını kontrol edin

### Admin paneli etkinlikleri göstermiyor
- GET /api/events/approval/pending endpoint'ine istek atın
- Veritabanında PENDING etkinlikleri kontrol edin
- Network tab'ında response'ı kontrol edin

### Etkinlik silinemiyor
- Admin izni olup olmadığını kontrol edin
- DELETE endpoint'inin çalışıp çalışmadığını kontrol edin

---

**Commit Hash:** `15b4ec3`  
**Tarih:** 26 Nisan 2026  
**Durum:** ✅ Tamamlandı
