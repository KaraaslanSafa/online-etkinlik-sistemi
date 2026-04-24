-- Events Management System - Database Setup
-- SQL Server Script (UTF-8 Compatible)

-- Drop existing tables if they exist
IF OBJECT_ID('EventParticipants', 'U') IS NOT NULL
    DROP TABLE EventParticipants;

IF OBJECT_ID('Events', 'U') IS NOT NULL
    DROP TABLE Events;

IF OBJECT_ID('Participants', 'U') IS NOT NULL
    DROP TABLE Participants;1. Etkinlik Kapasite ve Çakışma Kontrolü (Business Logic)
Hocalar sadece veritabanına kayıt atmaya bakmaz, kodun içinde bir "mantık" ararlar.

Özellik: Bir kullanıcı aynı saatte olan iki farklı etkinliğe bilet alamasın. Veya etkinlik kapasitesi 50 kişiyse, 51. kişiye "Kontenjan Doldu" uyarısı versin.

Neden Etkileyici? Bu, sizin sadece kod yazmadığınızı, aynı zamanda projenin senaryosunu (iş mantığını) düşündüğünüzü gösterir.

2. Dinamik Filtreleme ve Arama Motoru
Sistemdeki yüzlerce etkinliği tek tek aratmak yerine, hocaya şunu gösterebilirsiniz:

Özellik: "Sadece Ankara'daki konserleri getir" veya "Ücretsiz olan eğitimleri göster" gibi filtreler.

Teknik Detay: Spring Data JPA'nın findByCityAndPriceFree gibi özel sorgu metodlarını kullanarak bunu çok kolay yapabilirsiniz.

3. Otomatik Mail Hatırlatıcı (Bonus Puan)
Eğer hocayı gerçekten şaşırtmak istiyorsanız:

Özellik: Bir kullanıcı etkinliğe kayıt olduğunda, sistem arka planda ona "Biletiniz onaylandı!" diye bir e-posta göndersin.

Teknik Detay: Spring Boot'un içinde gelen spring-boot-starter-mail kütüphanesini kullanarak Gmail üzerinden bile mail attırabilirsiniz.

Projeyi "Görsel" Hale Getirmek İçin Küçük Bir İpucu
Şu an projeniz sadece arka planda (backend) çalışıyor. Hocaya sonuçları terminalden göstermek biraz sönük kalabilir. Eğer frontend (arayüz) yazmaya vaktiniz yoksa bile şu aracı mutlaka kullanın:

Swagger (OpenAPI) Ekleme:
Sadece bir kütüphane ekleyerek, projenizin tüm API uçlarını şık bir arayüzde görebilir ve hocaya oradan "Bakın buradan etkinliği ekliyorum, buradan da biletleri listeliyorum" diyerek test ettirebilirsiniz.

IF OBJECT_ID('Categories', 'U') IS NOT NULL
    DROP TABLE Categories;

GO

-- Create Categories Table
CREATE TABLE Categories (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500)
);

-- Create Participants Table
CREATE TABLE Participants (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone_number VARCHAR(20)
);

-- Create Events Table
CREATE TABLE Events (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    location VARCHAR(300) NOT NULL,
    capacity INT,
    category_id BIGINT NOT NULL,
    status VARCHAR(50) DEFAULT 'PLANNED',
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME,
    CONSTRAINT FK_Events_CategoryId FOREIGN KEY(category_id) REFERENCES Categories(id)
);

-- Create EventParticipants Table (Many-to-Many)
CREATE TABLE EventParticipants (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    event_id BIGINT NOT NULL,
    participant_id BIGINT NOT NULL,
    status VARCHAR(50) DEFAULT 'REGISTERED',
    registered_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_EventParticipants_EventId FOREIGN KEY(event_id) REFERENCES Events(id),
    CONSTRAINT FK_EventParticipants_ParticipantId FOREIGN KEY(participant_id) REFERENCES Participants(id),
    CONSTRAINT UK_EventParticipant UNIQUE(event_id, participant_id)
);

-- Create Indexes
CREATE INDEX IX_Events_CategoryId ON Events(category_id);
CREATE INDEX IX_Events_Status ON Events(status);
CREATE INDEX IX_EventParticipants_EventId ON EventParticipants(event_id);
CREATE INDEX IX_EventParticipants_ParticipantId ON EventParticipants(participant_id);

GO

-- Insert Categories
INSERT INTO Categories (name, description) VALUES
('Sports', 'Sports and fitness events'),
('Technology', 'Software, AI and technology conferences'),
('Education', 'Educational seminars and workshops'),
('Arts and Culture', 'Art exhibitions, concerts and cultural events'),
('Business and Entrepreneurship', 'Networking, business presentations and startup events'),
('Health', 'Health, wellness and medical conferences');

-- Insert Participants
INSERT INTO Participants (first_name, last_name, email, phone_number) VALUES
('Ahmet', 'Yilmaz', 'ahmet.yilmaz@example.com', '05551234567'),
('Fatima', 'Kara', 'fatima.kara@example.com', '05552345678'),
('Mehmet', 'Topuz', 'mehmet.topuz@example.com', '05553456789'),
('Zeynep', 'Aksoy', 'zeynep.aksoy@example.com', '05554567890'),
('Can', 'Ozdemir', 'can.ozdemir@example.com', '05555678901'),
('Ayse', 'Demir', 'ayse.demir@example.com', '05556789012'),
('Burak', 'Kilic', 'burak.kilic@example.com', '05557890123'),
('Gul', 'Sahin', 'gul.sahin@example.com', '05558901234'),
('Cem', 'Gunay', 'cem.gunay@example.com', '05559012345'),
('Dilan', 'Aydin', 'dilan.aydin@example.com', '05550123456'),
('Eren', 'Coskun', 'eren.coskun@example.com', '05551111111'),
('Filiz', 'Simsek', 'filiz.simsek@example.com', '05552222222');

-- Insert Events
INSERT INTO Events (title, description, start_date, end_date, location, capacity, category_id, status) VALUES
('Istanbul Marathon 2024', 'City-wide 42km marathon race', '2024-05-15 06:00:00', '2024-05-15 12:00:00', 'Istanbul, Taksim - Florya', 500, 1, 'PLANNED'),
('Python Advanced Workshop', 'Advanced Python programming with async and decorators', '2024-04-10 09:00:00', '2024-04-10 17:00:00', 'Ankara, Tech Park', 50, 2, 'PLANNED'),
('Web3 and Blockchain Conference', 'Blockchain, smart contracts and Web3 ecosystem', '2024-05-20 10:00:00', '2024-05-20 18:00:00', 'Izmir, Culture Center', 200, 2, 'PLANNED'),
('English Speaking Course', 'Professional English speaking skills development', '2024-04-15 14:00:00', '2024-04-15 17:00:00', 'Istanbul, Language Center', 30, 3, 'ONGOING'),
('Turkish Painting Art Exhibition', 'Contemporary Turkish painters and sculptors works', '2024-04-05 11:00:00', '2024-05-05 18:00:00', 'Ankara, Art Museum', 150, 4, 'ONGOING'),
('First Steps in Entrepreneurship', 'Startup guidance from idea to funding', '2024-04-25 09:00:00', '2024-04-25 16:00:00', 'Istanbul, Startup Hub', 80, 5, 'PLANNED'),
('High Blood Pressure Management Seminar', 'Doctor-led seminar on hypertension causes and treatment', '2024-04-18 15:00:00', '2024-04-18 17:00:00', 'Istanbul, Hospital', 100, 6, 'PLANNED'),
('National Bodybuilding Championship', 'Bodybuilding and fitness championship', '2024-06-01 18:00:00', '2024-06-01 22:00:00', 'Ankara, Sports Hall', 300, 1, 'PLANNED'),
('React and Next.js Training', 'Modern web development with SSR, SSG and API routes', '2024-05-01 10:00:00', '2024-05-01 16:00:00', 'Izmir, Coding Academy', 40, 2, 'PLANNED'),
('Digital Marketing Conference', 'SEO, SEM, SMM and influencer marketing strategies', '2024-05-10 09:00:00', '2024-05-10 17:00:00', 'Istanbul, Grand Hotel', 250, 5, 'PLANNED');

-- Insert Event Participants
INSERT INTO EventParticipants (event_id, participant_id, status, registered_at) VALUES
(1, 1, 'REGISTERED', '2024-03-01 10:00:00'),
(1, 2, 'REGISTERED', '2024-03-02 14:00:00'),
(1, 3, 'ATTENDED', '2024-03-05 09:00:00'),
(1, 4, 'REGISTERED', '2024-03-06 11:00:00'),
(1, 5, 'CANCELLED', '2024-03-10 15:00:00'),
(1, 6, 'REGISTERED', '2024-03-12 13:00:00'),
(2, 2, 'REGISTERED', '2024-03-15 10:00:00'),
(2, 5, 'REGISTERED', '2024-03-16 11:00:00'),
(2, 7, 'REGISTERED', '2024-03-17 09:00:00'),
(2, 9, 'ATTENDED', '2024-03-18 08:00:00'),
(3, 3, 'REGISTERED', '2024-03-20 15:00:00'),
(3, 7, 'REGISTERED', '2024-03-21 10:00:00'),
(3, 8, 'REGISTERED', '2024-03-22 14:00:00'),
(3, 10, 'REGISTERED', '2024-03-23 11:00:00'),
(3, 11, 'REGISTERED', '2024-03-24 13:00:00'),
(4, 1, 'REGISTERED', '2024-03-25 10:00:00'),
(4, 4, 'ATTENDED', '2024-03-26 14:00:00'),
(4, 6, 'ATTENDED', '2024-03-27 09:00:00'),
(4, 8, 'REGISTERED', '2024-03-28 11:00:00'),
(5, 2, 'ATTENDED', '2024-03-01 15:00:00'),
(5, 6, 'ATTENDED', '2024-03-02 10:00:00'),
(5, 9, 'REGISTERED', '2024-03-03 14:00:00'),
(5, 12, 'REGISTERED', '2024-03-04 11:00:00'),
(6, 1, 'REGISTERED', '2024-03-10 10:00:00'),
(6, 5, 'REGISTERED', '2024-03-11 15:00:00'),
(6, 7, 'REGISTERED', '2024-03-12 09:00:00'),
(6, 10, 'REGISTERED', '2024-03-13 14:00:00'),
(6, 11, 'REGISTERED', '2024-03-14 11:00:00'),
(7, 3, 'REGISTERED', '2024-03-20 10:00:00'),
(7, 4, 'REGISTERED', '2024-03-21 14:00:00'),
(7, 8, 'REGISTERED', '2024-03-22 09:00:00'),
(8, 1, 'REGISTERED', '2024-04-01 10:00:00'),
(8, 3, 'REGISTERED', '2024-04-02 14:00:00'),
(8, 5, 'REGISTERED', '2024-04-03 11:00:00'),
(9, 7, 'REGISTERED', '2024-04-05 10:00:00'),
(9, 9, 'REGISTERED', '2024-04-06 15:00:00'),
(9, 11, 'REGISTERED', '2024-04-07 09:00:00'),
(10, 2, 'REGISTERED', '2024-04-10 10:00:00'),
(10, 4, 'REGISTERED', '2024-04-11 14:00:00'),
(10, 6, 'REGISTERED', '2024-04-12 11:00:00'),
(10, 7, 'REGISTERED', '2024-04-13 15:00:00'),
(10, 10, 'REGISTERED', '2024-04-14 09:00:00');

GO

-- Verification Queries
SELECT 'Categories: ' + CAST(COUNT(*) AS VARCHAR(10)) FROM Categories;
SELECT 'Participants: ' + CAST(COUNT(*) AS VARCHAR(10)) FROM Participants;
SELECT 'Events: ' + CAST(COUNT(*) AS VARCHAR(10)) FROM Events;
SELECT 'Event Registrations: ' + CAST(COUNT(*) AS VARCHAR(10)) FROM EventParticipants;

GO
