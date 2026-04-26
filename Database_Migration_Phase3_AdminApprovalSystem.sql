-- ================================================================
-- EVENT MANAGEMENT SYSTEM - PHASE 2 MIGRATION
-- Admin Approval System & Member Controls
-- ================================================================

-- 1. Admin tablosu oluştur
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Admins')
BEGIN
    CREATE TABLE Admins (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        username NVARCHAR(100) NOT NULL UNIQUE,
        password NVARCHAR(255) NOT NULL,
        email NVARCHAR(100) NOT NULL UNIQUE,
        full_name NVARCHAR(200) NOT NULL,
        is_active BIT NOT NULL DEFAULT 1,
        is_super_admin BIT NOT NULL DEFAULT 0,
        approvals_count INT NOT NULL DEFAULT 0,
        rejections_count INT NOT NULL DEFAULT 0,
        deletions_count INT NOT NULL DEFAULT 0,
        created_at DATETIME NOT NULL DEFAULT GETDATE(),
        updated_at DATETIME,
        last_login_at DATETIME,
        notes NVARCHAR(500),
        CONSTRAINT UK_Admins_Username UNIQUE(username),
        CONSTRAINT UK_Admins_Email UNIQUE(email)
    );
    
    PRINT 'Admins tablosu başarıyla oluşturuldu';
END
ELSE
BEGIN
    PRINT 'Admins tablosu zaten var';
END

-- 2. Events tablosunda approval sistemi kontrol
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('Events') AND name = 'approval_status')
BEGIN
    ALTER TABLE Events ADD approval_status NVARCHAR(50) NOT NULL DEFAULT 'PENDING';
    ALTER TABLE Events ADD approver_admin_id BIGINT;
    ALTER TABLE Events ADD approved_at DATETIME;
    ALTER TABLE Events ADD rejection_reason NVARCHAR(500);
    
    PRINT 'Events tablosuna approval sistemleri başarıyla eklendi';
END
ELSE
BEGIN
    PRINT 'Events tablosunda approval sistemleri zaten var';
END

-- 3. Events ve Admins arasında foreign key ilişkisi
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FK_Events_Admins')
BEGIN
    ALTER TABLE Events 
    ADD CONSTRAINT FK_Events_Admins 
    FOREIGN KEY (approver_admin_id) REFERENCES Admins(id);
    
    PRINT 'Events ve Admins arasında ilişki başarıyla oluşturuldu';
END
ELSE
BEGIN
    PRINT 'Events ve Admins arasında ilişki zaten var';
END

-- 4. EventReviews tablosunda yapı kontrol
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'EventReviews')
BEGIN
    CREATE TABLE EventReviews (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        event_id BIGINT NOT NULL,
        participant_id BIGINT NOT NULL,
        rating INT NOT NULL,
        title NVARCHAR(500),
        comment NVARCHAR(2000),
        helpful_count INT NOT NULL DEFAULT 0,
        created_at DATETIME NOT NULL DEFAULT GETDATE(),
        updated_at DATETIME,
        CONSTRAINT UK_UserEventReview UNIQUE(event_id, participant_id),
        CONSTRAINT FK_EventReviews_Events FOREIGN KEY(event_id) REFERENCES Events(id) ON DELETE CASCADE,
        CONSTRAINT FK_EventReviews_Participants FOREIGN KEY(participant_id) REFERENCES Participants(id) ON DELETE CASCADE,
        CONSTRAINT CK_EventReviews_Rating CHECK (rating >= 1 AND rating <= 5)
    );
    
    CREATE INDEX IDX_EventReviews_EventId ON EventReviews(event_id);
    CREATE INDEX IDX_EventReviews_ParticipantId ON EventReviews(participant_id);
    CREATE INDEX IDX_EventReviews_Rating ON EventReviews(rating);
    
    PRINT 'EventReviews tablosu başarıyla oluşturuldu';
END
ELSE
BEGIN
    PRINT 'EventReviews tablosu zaten var';
END

-- 5. Default Admin oluştur (test için)
IF NOT EXISTS (SELECT * FROM Admins WHERE username = 'admin')
BEGIN
    INSERT INTO Admins (username, password, email, full_name, is_active, is_super_admin)
    VALUES ('admin', 'admin123', 'admin@example.com', 'Sistem Yöneticisi', 1, 1);
    
    PRINT 'Default admin kullanıcısı oluşturuldu (username: admin, password: admin123)';
END
ELSE
BEGIN
    PRINT 'Admin kullanıcısı zaten var';
END

-- 6. Mevcut etkinliklerin approval status'ünü kontrol et
UPDATE Events 
SET approval_status = 'APPROVED' 
WHERE approval_status IS NULL OR approval_status = '';

PRINT 'Mevcut etkinlikler başarıyla güncellendi';

-- ================================================================
-- Veritabanı İstatistikleri
-- ================================================================
PRINT '';
PRINT '========== BAŞARILI ==========';
PRINT 'Admin Approval System başarıyla kuruldu';
PRINT 'Toplam Admin Sayısı: ' + CAST((SELECT COUNT(*) FROM Admins) AS NVARCHAR);
PRINT 'Toplam Event Sayısı: ' + CAST((SELECT COUNT(*) FROM Events) AS NVARCHAR);
PRINT 'Beklemede Olan Etkinlik: ' + CAST((SELECT COUNT(*) FROM Events WHERE approval_status = 'PENDING') AS NVARCHAR);
PRINT 'Onaylı Etkinlik: ' + CAST((SELECT COUNT(*) FROM Events WHERE approval_status = 'APPROVED') AS NVARCHAR);
PRINT '=============================';
