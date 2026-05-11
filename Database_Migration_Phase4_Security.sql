-- Users Tablosu
CREATE TABLE Users (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(100) UNIQUE NOT NULL,
    password_hash NVARCHAR(255) NOT NULL,
    email NVARCHAR(150) UNIQUE NOT NULL,
    first_name NVARCHAR(100),
    last_name NVARCHAR(100),
    is_active BIT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE()
);

-- Roles Tablosu
CREATE TABLE Roles (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(50) NOT NULL UNIQUE
);

-- UserRoles Tablosu
CREATE TABLE UserRoles (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (role_id) REFERENCES Roles(id)
);

-- AuditLogs Tablosu
CREATE TABLE AuditLogs (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT,
    action NVARCHAR(100),
    entity_type NVARCHAR(50),
    entity_id BIGINT,
    details NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE()
);

-- Default Roller
INSERT INTO Roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER'), ('ROLE_ORGANIZER');
