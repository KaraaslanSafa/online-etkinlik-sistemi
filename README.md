# Online Etkinlik Yönetim Sistemi

Bu proje, **İleri Web Uygulamaları** dersi kapsamında geliştirilmiş, modern bir web mimarisine sahip kapsamlı bir etkinlik yönetim platformudur. Sistem; kullanıcıların etkinlikleri keşfetmesini, kayıt olmasını, bilet almasını ve organizatörlerin etkinlik süreçlerini yönetmesini sağlar.

## 🚀 Proje Amacı
Projenin temel amacı; MVC (Model-View-Controller) mimarisi üzerine kurulu, güvenli, ölçeklenebilir ve kullanıcı dostu bir etkinlik yönetim sistemi sunmaktır. Proje, tam kapsamlı bir oturum yönetimi, rol bazlı yetkilendirme ve modern bir veritabanı yapısı içermektedir.

## 🛠 Kullanılan Teknolojiler

### Backend
- **Java 17+** & **Spring Boot**
- **Spring Security** (JWT tabanlı kimlik doğrulama)
- **Spring Data JPA** (Hibernate)
- **Maven** (Bağımlılık yönetimi)
- **Swagger / OpenAPI 3** (API Dokümantasyonu)
- **Lombok** (Boilerplate kod azaltımı)

### Frontend
- **React.js**
- **Axios** (API istekleri için)
- **CSS3** (Modern ve şık arayüz tasarımı)

### Veritabanı & Deployment
- **Microsoft SQL Server** (Üretim ortamı)
- **Azure App Services** (Backend Deployment)
- **Vercel** (Frontend Deployment)

## ✨ Öne Çıkan Özellikler

- **Güvenli Kimlik Doğrulama:** JWT (JSON Web Token) altyapısı ile stateless oturum yönetimi.
- **OTP Doğrulama:** E-posta ve telefon üzerinden Tek Kullanımlık Şifre (OTP) ile hesap doğrulama.
- **Rol Bazlı Yetkilendirme:** `ADMIN`, `USER` ve `ORGANIZER` rolleri ile gelişmiş yetki kontrolü.
- **Gelişmiş Veritabanı Yapısı:** Yönergeye uygun olarak 12'den fazla ilişkili tablo (Event, Participant, Ticket, Campaign, AuditLog vb.).
- **Admin Paneli:** Etkinlik onaylama, kullanıcı yönetimi ve sistem loglarını izleme.
- **Bilet & Ödeme:** Farklı bilet türleri (VIP, Standart) ve ödeme durumu takibi.
- **Kampanya Sistemi:** Etkinliklere özel kampanya ve promosyon tanımlama.

## 📁 Proje Yapısı (MVC)

- **Model:** `com.example.demo.entity` altındaki varlık sınıfları veritabanı şemasını tanımlar.
- **View:** `frontend/src` dizinindeki React bileşenleri kullanıcı arayüzünü oluşturur.
- **Controller:** `com.example.demo.controller` paketindeki sınıflar API uç noktalarını yönetir.
- **Service:** İş mantığı ve veritabanı manipülasyonları servis katmanında yürütülür.

## ⚙️ Kurulum ve Çalıştırma

### 1. Gereksinimler
- JDK 17 veya üzeri
- Node.js (v16+)
- Maven
- SQL Server

### 2. Backend Kurulumu
\`\`\`bash
cd online-etkinlik-sistemi
mvn clean install
mvn spring-boot:run
\`\`\`
API varsayılan olarak `http://localhost:8080` adresinde çalışacaktır. Swagger dokümantasyonuna `/swagger-ui.html` üzerinden erişebilirsiniz.

### 3. Frontend Kurulumu
\`\`\`bash
cd frontend
npm install
npm start
\`\`\`
Frontend varsayılan olarak `http://localhost:3000` adresinde çalışacaktır.

## 👥 Geliştirici Ekibi
Bu proje aşağıdaki ekip üyeleri tarafından geliştirilmiştir:
- **Tahir Buğrahan Gök**
- **Kadir Avcı**
- **Muhammed Safa Karaaslan**

## 📄 Lisans
Bu proje eğitim amaçlı geliştirilmiş olup tüm hakları saklıdır.
