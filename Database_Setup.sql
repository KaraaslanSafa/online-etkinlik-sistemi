-- =====================================================
-- Events Management System - Database Schema & Sample Data
-- SQL Server Script
-- =====================================================

-- Drop existing tables if they exist
IF OBJECT_ID('EventParticipants', 'U') IS NOT NULL
    DROP TABLE EventParticipants;

IF OBJECT_ID('Events', 'U') IS NOT NULL
    DROP TABLE Events;

IF OBJECT_ID('Participants', 'U') IS NOT NULL
    DROP TABLE Participants;

IF OBJECT_ID('Categories', 'U') IS NOT NULL
    DROP TABLE Categories;

GO

-- =====================================================
-- 1. CREATE CATEGORIES TABLE
-- =====================================================
CREATE TABLE Categories (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    CONSTRAINT UK_Category_Name UNIQUE(name)
);

PRINT 'Categories table created successfully!';

-- =====================================================
-- 2. CREATE PARTICIPANTS TABLE
-- =====================================================
CREATE TABLE Participants (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    CONSTRAINT UK_Participant_Email UNIQUE(email)
);

PRINT 'Participants table created successfully!';

-- =====================================================
-- 3. CREATE EVENTS TABLE
-- =====================================================
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
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

PRINT 'Events table created successfully!';

-- =====================================================
-- 4. CREATE EVENT_PARTICIPANTS TABLE (Many-to-Many)
-- =====================================================
CREATE TABLE EventParticipants (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    event_id BIGINT NOT NULL,
    participant_id BIGINT NOT NULL,
    status VARCHAR(50) DEFAULT 'REGISTERED',
    registered_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_EventParticipants_EventId FOREIGN KEY(event_id) REFERENCES Events(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT FK_EventParticipants_ParticipantId FOREIGN KEY(participant_id) REFERENCES Participants(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT UK_EventParticipant UNIQUE(event_id, participant_id)
);

PRINT 'EventParticipants table created successfully!';

-- =====================================================
-- CREATE INDEXES FOR BETTER PERFORMANCE
-- =====================================================
CREATE INDEX IX_Events_CategoryId ON Events(category_id);
CREATE INDEX IX_Events_Status ON Events(status);
CREATE INDEX IX_EventParticipants_EventId ON EventParticipants(event_id);
CREATE INDEX IX_EventParticipants_ParticipantId ON EventParticipants(participant_id);

PRINT 'Indexes created successfully!';

GO

-- =====================================================
-- SAMPLE DATA INSERTION
-- =====================================================

-- =====================================================
-- 1. INSERT CATEGORIES
-- =====================================================
PRINT '';
PRINT 'Inserting sample categories...';

INSERT INTO Categories (name, description) VALUES
('Spor', 'Spor ve fitness etkinlikleri'),
('Teknoloji', 'Yazılım, AI ve teknoloji konferansları'),
('Eğitim', 'Eğitim seminer ve workshop\'ları'),
('Sanat ve Kültür', 'Sanat sergileri, konserler ve kültürel etkinlikler'),
('İş ve Girişimcilik', 'Networking, iş sunumları ve girişimcilik etkinlikleri'),
('Sağlık', 'Sağlık, wellness ve tıbbi konferanslar');

PRINT 'Categories inserted: 6 records';

-- =====================================================
-- 2. INSERT PARTICIPANTS
-- =====================================================
PRINT '';
PRINT 'Inserting sample participants...';

INSERT INTO Participants (first_name, last_name, email, phone_number) VALUES
('Ahmet', 'Yılmaz', 'ahmet.yilmaz@example.com', '05551234567'),
('Fatima', 'Kara', 'fatima.kara@example.com', '05552345678'),
('Mehmet', 'Topuz', 'mehmet.topuz@example.com', '05553456789'),
('Zeynep', 'Aksoy', 'zeynep.aksoy@example.com', '05554567890'),
('Can', 'Özdemir', 'can.ozdemir@example.com', '05555678901'),
('Ayşe', 'Demir', 'ayse.demir@example.com', '05556789012'),
('Burak', 'Kılıç', 'burak.kilic@example.com', '05557890123'),
('Gül', 'Şahin', 'gul.sahin@example.com', '05558901234'),
('Cem', 'Günay', 'cem.gunay@example.com', '05559012345'),
('Dilan', 'Aydın', 'dilan.aydin@example.com', '05550123456'),
('Eren', 'Coşkun', 'eren.coskun@example.com', '05551111111'),
('Filiz', 'Şimşek', 'filiz.simsek@example.com', '05552222222');

PRINT 'Participants inserted: 12 records';

-- =====================================================
-- 3. INSERT EVENTS
-- =====================================================
PRINT '';
PRINT 'Inserting sample events...';

INSERT INTO Events (title, description, start_date, end_date, location, capacity, category_id, status) VALUES
(
    'İstanbul Maratonu 2024',
    'Şehir içi 42km maraton yarışı. Tüm şehri gezip finish çizgisine ulaşın!',
    '2024-05-15 06:00:00',
    '2024-05-15 12:00:00',
    'İstanbul, Taksim Meydanı - Florya Sahili',
    500,
    1,
    'PLANNED'
),
(
    'Python Advanced Workshop',
    'Python ile ileri seviye programlama. Async programming, decorators, metaprogramming.',
    '2024-04-10 09:00:00',
    '2024-04-10 17:00:00',
    'Ankara, Teknoloji Parkı',
    50,
    2,
    'PLANNED'
),
(
    'Web3 ve Blockchain Konferansı',
    'Blockchain teknolojisi, akıllı sözleşmeler ve Web3 ekosistemine giriş.',
    '2024-05-20 10:00:00',
    '2024-05-20 18:00:00',
    'İzmir, Alsancak Kültür Merkezi',
    200,
    2,
    'PLANNED'
),
(
    'İngilizce Konuşma Kursu',
    'Profesyonel ortamda İngilizce konuşma becerilerini geliştirin.',
    '2024-04-15 14:00:00',
    '2024-04-15 17:00:00',
    'İstanbul, Language Center',
    30,
    3,
    'ONGOING'
),
(
    'Türk Resim Sanatı Sergisi',
    'Çağdaş Türk ressam ve heykeltıraşlarının eserleri.',
    '2024-04-05 11:00:00',
    '2024-05-05 18:00:00',
    'Ankara, Sanat Müzesi',
    150,
    4,
    'ONGOING'
),
(
    'Girişimcilik Yolunda İlk Adımlar',
    'Startup kurmak isteyenlere rehberlik. İdea\'dan yatırım alanına kadar.',
    '2024-04-25 09:00:00',
    '2024-04-25 16:00:00',
    'İstanbul, Startup Hub',
    80,
    5,
    'PLANNED'
),
(
    'Yüksek Tansiyon Yönetimi Semineri',
    'Doktor eşliğinde yüksek tansiyon nedenleri ve tedavi yöntemleri.',
    '2024-04-18 15:00:00',
    '2024-04-18 17:00:00',
    'İstanbul, Acibadem Hastanesi',
    100,
    6,
    'PLANNED'
),
(
    'Vücut Geliştirme Şampiyonası',
    'Ulusal vücut geliştirme ve fitness şampiyonası.',
    '2024-06-01 18:00:00',
    '2024-06-01 22:00:00',
    'Ankara, Spor Salonu',
    300,
    1,
    'PLANNED'
),
(
    'React ve Next.js Eğitimi',
    'Modern web geliştirme frameworks\'ı. SSR, SSG ve API routes.',
    '2024-05-01 10:00:00',
    '2024-05-01 16:00:00',
    'İzmir, Kodlama Akademisi',
    40,
    2,
    'PLANNED'
),
(
    'Dijital Pazarlama Konferansı',
    'SEO, SEM, SMM ve influencer marketing stratejileri.',
    '2024-05-10 09:00:00',
    '2024-05-10 17:00:00',
    'İstanbul, Grand Hyatt',
    250,
    5,
    'PLANNED'
);

PRINT 'Events inserted: 10 records';

-- =====================================================
-- 4. INSERT EVENT_PARTICIPANTS (Sample Registration)
-- =====================================================
PRINT '';
PRINT 'Inserting sample event registrations...';

INSERT INTO EventParticipants (event_id, participant_id, status, registered_at) VALUES
-- Marathon registrations
(1, 1, 'REGISTERED', '2024-03-01 10:00:00'),
(1, 2, 'REGISTERED', '2024-03-02 14:00:00'),
(1, 3, 'ATTENDED', '2024-03-05 09:00:00'),
(1, 4, 'REGISTERED', '2024-03-06 11:00:00'),
(1, 5, 'CANCELLED', '2024-03-10 15:00:00'),
(1, 6, 'REGISTERED', '2024-03-12 13:00:00'),

-- Python Workshop registrations
(2, 2, 'REGISTERED', '2024-03-15 10:00:00'),
(2, 5, 'REGISTERED', '2024-03-16 11:00:00'),
(2, 7, 'REGISTERED', '2024-03-17 09:00:00'),
(2, 9, 'ATTENDED', '2024-03-18 08:00:00'),

-- Blockchain Conference
(3, 3, 'REGISTERED', '2024-03-20 15:00:00'),
(3, 7, 'REGISTERED', '2024-03-21 10:00:00'),
(3, 8, 'REGISTERED', '2024-03-22 14:00:00'),
(3, 10, 'REGISTERED', '2024-03-23 11:00:00'),
(3, 11, 'REGISTERED', '2024-03-24 13:00:00'),

-- English Speaking Course
(4, 1, 'REGISTERED', '2024-03-25 10:00:00'),
(4, 4, 'ATTENDED', '2024-03-26 14:00:00'),
(4, 6, 'ATTENDED', '2024-03-27 09:00:00'),
(4, 8, 'REGISTERED', '2024-03-28 11:00:00'),

-- Art Exhibition
(5, 2, 'ATTENDED', '2024-03-01 15:00:00'),
(5, 6, 'ATTENDED', '2024-03-02 10:00:00'),
(5, 9, 'REGISTERED', '2024-03-03 14:00:00'),
(5, 12, 'REGISTERED', '2024-03-04 11:00:00'),

-- Entrepreneurship Course
(6, 1, 'REGISTERED', '2024-03-10 10:00:00'),
(6, 5, 'REGISTERED', '2024-03-11 15:00:00'),
(6, 7, 'REGISTERED', '2024-03-12 09:00:00'),
(6, 10, 'REGISTERED', '2024-03-13 14:00:00'),
(6, 11, 'REGISTERED', '2024-03-14 11:00:00'),

-- Blood Pressure Management
(7, 3, 'REGISTERED', '2024-03-20 10:00:00'),
(7, 4, 'REGISTERED', '2024-03-21 14:00:00'),
(7, 8, 'REGISTERED', '2024-03-22 09:00:00'),

-- Bodybuilding Championship
(8, 1, 'REGISTERED', '2024-04-01 10:00:00'),
(8, 3, 'REGISTERED', '2024-04-02 14:00:00'),
(8, 5, 'REGISTERED', '2024-04-03 11:00:00'),

-- React Training
(9, 7, 'REGISTERED', '2024-04-05 10:00:00'),
(9, 9, 'REGISTERED', '2024-04-06 15:00:00'),
(9, 11, 'REGISTERED', '2024-04-07 09:00:00'),

-- Digital Marketing Conference
(10, 2, 'REGISTERED', '2024-04-10 10:00:00'),
(10, 4, 'REGISTERED', '2024-04-11 14:00:00'),
(10, 6, 'REGISTERED', '2024-04-12 11:00:00'),
(10, 7, 'REGISTERED', '2024-04-13 15:00:00'),
(10, 10, 'REGISTERED', '2024-04-14 09:00:00');

PRINT 'Event Participants inserted: 37 records';

GO

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================
PRINT '';
PRINT '========== DATA VERIFICATION ==========';
PRINT '';

PRINT 'Total Categories:';
SELECT COUNT(*) as [Category Count] FROM Categories;

PRINT 'Total Participants:';
SELECT COUNT(*) as [Participant Count] FROM Participants;

PRINT 'Total Events:';
SELECT COUNT(*) as [Event Count] FROM Events;

PRINT 'Total Event Registrations:';
SELECT COUNT(*) as [Registration Count] FROM EventParticipants;

PRINT '';
PRINT '========== EVENT STATISTICS ==========';
PRINT '';

SELECT 
    e.title as [Event Title],
    c.name as [Category],
    COUNT(ep.id) as [Registered Count],
    e.capacity as [Total Capacity],
    (e.capacity - COUNT(ep.id)) as [Available Spots]
FROM Events e
LEFT JOIN EventParticipants ep ON e.id = ep.event_id
LEFT JOIN Categories c ON e.category_id = c.id
GROUP BY e.id, e.title, c.name, e.capacity
ORDER BY e.id;

PRINT '';
PRINT '========== SETUP COMPLETED SUCCESSFULLY ==========';
PRINT 'All tables created and populated with sample data!';
