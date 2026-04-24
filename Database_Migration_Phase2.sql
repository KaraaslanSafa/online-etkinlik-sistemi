-- ============================================================
-- PHASE 2: REVIEWS, RATINGS, ORGANIZERS & CAMPAIGNS
-- ============================================================

-- 1. EventOrganizers Tablosu (Organizatör Bilgileri)
CREATE TABLE EventOrganizers (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(200) NOT NULL,
    email VARCHAR(150),
    phone VARCHAR(20),
    bio VARCHAR(1000),
    founded_year INT,
    total_events INT DEFAULT 0,
    total_participants INT DEFAULT 0,
    website VARCHAR(255),
    average_rating DECIMAL(3,2) DEFAULT 0,
    review_count INT DEFAULT 0,
    is_verified BIT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME
);

-- 2. Events tablosuna organizer_id ve average_rating ekle
ALTER TABLE Events
ADD organizer_id BIGINT,
    average_rating DECIMAL(3,2) DEFAULT 0,
    review_count INT DEFAULT 0,
    FOREIGN KEY (organizer_id) REFERENCES EventOrganizers(id);

-- 3. EventReviews Tablosu (Etkinlik Değerlendirmeleri)
CREATE TABLE EventReviews (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    event_id BIGINT NOT NULL,
    participant_id BIGINT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    title VARCHAR(200),
    comment VARCHAR(2000),
    helpful_count INT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME,
    FOREIGN KEY (event_id) REFERENCES Events(id),
    FOREIGN KEY (participant_id) REFERENCES Participants(id),
    CONSTRAINT UK_UserEventReview UNIQUE(event_id, participant_id)
);

CREATE INDEX IX_EventReviews_EventId ON EventReviews(event_id);
CREATE INDEX IX_EventReviews_ParticipantId ON EventReviews(participant_id);
CREATE INDEX IX_EventReviews_Rating ON EventReviews(rating);

-- 4. OrganizerRatings Tablosu (Organizatör Değerlendirmeleri)
CREATE TABLE OrganizerRatings (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    organizer_id BIGINT NOT NULL,
    participant_id BIGINT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment VARCHAR(1000),
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (organizer_id) REFERENCES EventOrganizers(id),
    FOREIGN KEY (participant_id) REFERENCES Participants(id),
    CONSTRAINT UK_OrganizerParticipantRating UNIQUE(organizer_id, participant_id)
);

CREATE INDEX IX_OrganizerRatings_OrgId ON OrganizerRatings(organizer_id);
CREATE INDEX IX_OrganizerRatings_Rating ON OrganizerRatings(rating);

-- 5. Campaigns Tablosu (Kampanyalar ve Promosyonlar)
CREATE TABLE Campaigns (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    organizer_id BIGINT NOT NULL,
    event_id BIGINT,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    discount_percentage DECIMAL(5,2),
    discount_amount DECIMAL(10,2),
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    max_participants INT,
    used_count INT DEFAULT 0,
    campaign_code VARCHAR(50) UNIQUE,
    is_active BIT DEFAULT 1,
    campaign_type VARCHAR(50), -- DISCOUNT, EARLY_BIRD, SEASONAL, REFERRAL, BUNDLE
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME,
    FOREIGN KEY (organizer_id) REFERENCES EventOrganizers(id),
    FOREIGN KEY (event_id) REFERENCES Events(id)
);

CREATE INDEX IX_Campaigns_OrgId ON Campaigns(organizer_id);
CREATE INDEX IX_Campaigns_EventId ON Campaigns(event_id);
CREATE INDEX IX_Campaigns_IsActive ON Campaigns(is_active);

-- 6. PromotionListings Tablosu (Vitrindeki Promosyon Listesi)
CREATE TABLE PromotionListings (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    campaign_id BIGINT NOT NULL,
    featured_until DATETIME,
    featured_position INT, -- 1-10 position
    featured_image_url VARCHAR(500),
    featured_description VARCHAR(1000),
    view_count INT DEFAULT 0,
    click_count INT DEFAULT 0,
    conversion_count INT DEFAULT 0,
    display_priority INT DEFAULT 5, -- 1-10, higher = more visible
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME,
    FOREIGN KEY (campaign_id) REFERENCES Campaigns(id)
);

CREATE INDEX IX_PromotionListings_FeaturedUntil ON PromotionListings(featured_until);
CREATE INDEX IX_PromotionListings_Priority ON PromotionListings(display_priority);

-- 7. View: Etkinlikler - Organizatör Bilgisi ile (Full Info)
CREATE VIEW EventsWithOrganizerView AS
SELECT 
    e.id,
    e.title,
    e.description,
    e.startDate,
    e.endDate,
    e.location,
    e.city,
    e.price,
    e.isFree,
    e.capacity,
    e.average_rating,
    e.review_count,
    e.status,
    e.created_at,
    e.category_id,
    c.name as category_name,
    e.organizer_id,
    eo.name as organizer_name,
    eo.average_rating as organizer_rating,
    eo.review_count as organizer_review_count,
    eo.is_verified as organizer_verified,
    eo.website as organizer_website
FROM Events e
LEFT JOIN Categories c ON e.category_id = c.id
LEFT JOIN EventOrganizers eo ON e.organizer_id = eo.id;

-- 8. View: Promosyonlar - Tam Detay
CREATE VIEW PromotionsDetailView AS
SELECT 
    pl.id as promotion_id,
    pl.featured_position,
    pl.featured_image_url,
    pl.featured_description,
    pl.view_count,
    pl.click_count,
    pl.conversion_count,
    pl.featured_until,
    pl.display_priority,
    c.id as campaign_id,
    c.title as campaign_title,
    c.description as campaign_description,
    c.campaign_type,
    c.discount_percentage,
    c.discount_amount,
    c.campaign_code,
    c.start_date,
    c.end_date,
    c.used_count,
    c.max_participants,
    c.event_id,
    e.title as event_title,
    e.city as event_city,
    e.startDate,
    e.price,
    e.organizer_id,
    eo.name as organizer_name,
    eo.average_rating as organizer_rating
FROM PromotionListings pl
INNER JOIN Campaigns c ON pl.campaign_id = c.id
LEFT JOIN Events e ON c.event_id = e.id
LEFT JOIN EventOrganizers eo ON c.organizer_id = eo.id
WHERE pl.featured_until > GETDATE() AND c.is_active = 1;

-- 9. Stored Procedure: Ortalama Rating Güncelle
CREATE PROCEDURE UpdateEventAverageRating
    @EventId BIGINT
AS
BEGIN
    UPDATE Events
    SET average_rating = (
        SELECT AVG(CAST(rating AS DECIMAL(3,2))) FROM EventReviews WHERE event_id = @EventId
    ),
    review_count = (
        SELECT COUNT(*) FROM EventReviews WHERE event_id = @EventId
    )
    WHERE id = @EventId;
END;

-- 10. Stored Procedure: Organizatör Rating Güncelle
CREATE PROCEDURE UpdateOrganizerAverageRating
    @OrganizerId BIGINT
AS
BEGIN
    UPDATE EventOrganizers
    SET average_rating = (
        SELECT AVG(CAST(rating AS DECIMAL(3,2))) FROM OrganizerRatings WHERE organizer_id = @OrganizerId
    ),
    review_count = (
        SELECT COUNT(*) FROM OrganizerRatings WHERE organizer_id = @OrganizerId
    )
    WHERE id = @OrganizerId;
END;

-- 11. Stored Procedure: Organizatör Istatistiklerini Güncelle
CREATE PROCEDURE UpdateOrganizerStats
    @OrganizerId BIGINT
AS
BEGIN
    UPDATE EventOrganizers
    SET 
        total_events = (SELECT COUNT(*) FROM Events WHERE organizer_id = @OrganizerId),
        total_participants = (
            SELECT ISNULL(SUM(ep.id), 0) FROM EventParticipants ep
            INNER JOIN Events e ON ep.event_id = e.id
            WHERE e.organizer_id = @OrganizerId
        )
    WHERE id = @OrganizerId;
END;
