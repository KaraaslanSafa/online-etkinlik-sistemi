# Database Setup and Testing Guide

## 📋 Database Schema Overview

The Events Management System uses **SQL Server** with 4 main tables:

### Table Structure

```
┌─────────────────────────────────────────────────────────────┐
│ CATEGORIES                                                  │
├─────────────────────────────────────────────────────────────┤
│ • id (PK)                                                   │
│ • name (VARCHAR, UNIQUE)                                    │
│ • description (VARCHAR)                                     │
└─────────────────────────────────────────────────────────────┘
         ▲
         │ (1:N)
         │
┌─────────────────────────────────────────────────────────────┐
│ EVENTS                                                      │
├─────────────────────────────────────────────────────────────┤
│ • id (PK)                                                   │
│ • title, description                                        │
│ • start_date, end_date                                      │
│ • location, capacity                                        │
│ • category_id (FK → Categories)                             │
│ • status (PLANNED, ONGOING, COMPLETED, CANCELLED)          │
│ • created_at, updated_at                                    │
└─────────────────────────────────────────────────────────────┘
         ▲                     ▲
         │                     │
         │ (1:N)               │ (N:M)
         │                     │
         │              ┌──────────────────────┐
         │              │ EVENT_PARTICIPANTS   │
         │              ├──────────────────────┤
         │              │ • id (PK)            │
         │              │ • event_id (FK)      │
         │              │ • participant_id(FK) │
         │              │ • status             │
         │              │ • registered_at      │
         │              └──────────────────────┘
         │                     ▲
         │                     │ (1:N)
         │                     │
┌─────────────────────────────────────────────────────────────┐
│ PARTICIPANTS                                                │
├─────────────────────────────────────────────────────────────┤
│ • id (PK)                                                   │
│ • first_name, last_name                                     │
│ • email (VARCHAR, UNIQUE)                                   │
│ • phone_number                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔧 Database Setup Steps

### Step 1: Verify SQL Server Installation

```bash
# Check if SQL Server is running
# Windows: Services.msc → SQL Server (SQLEXPRESS) → Status: Running

# Or use TDS Protocol
sqlcmd -S localhost -U sa -P Salih.12345 -Q "SELECT GETDATE()"
```

### Step 2: Run Database Setup Script

**Option A: Using SQL Command Line**

```bash
# Navigate to project directory
cd EventsManagementSystem\demo

# Run the setup script
sqlcmd -S localhost -U sa -P Salih.12345 -i Database_Setup_Clean.sql
```

**Option B: Using SQL Server Management Studio**

1. Open SQL Server Management Studio
2. Connect to `localhost\SQLEXPRESS` with:
   - Login: `sa`
   - Password: `Salih.12345`
3. New Query → Open `Database_Setup_Clean.sql`
4. Execute (F5)

### Step 3: Verify Database Creation

```sql
-- Run this query to verify tables exist
USE EventsManagementSystem

SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE'

-- Expected output:
-- Categories
-- Participants
-- Events
-- EventParticipants
```

---

## 📊 Sample Data Overview

### Categories (6 records)
| ID | Name | Description |
|---|---|---|
| 1 | Sports | Sports and fitness events |
| 2 | Technology | Software, AI and tech conferences |
| 3 | Education | Educational seminars and workshops |
| 4 | Arts and Culture | Art exhibitions, concerts, cultural events |
| 5 | Business and Entrepreneurship | Networking, startup events |
| 6 | Health | Health, wellness and medical conferences |

### Participants (12 records)
| ID | First Name | Last Name | Email |
|---|---|---|---|
| 1 | Ahmet | Yilmaz | ahmet.yilmaz@example.com |
| 2 | Fatima | Kara | fatima.kara@example.com |
| 3 | Mehmet | Topuz | mehmet.topuz@example.com |
| ... | ... | ... | ... |
| 12 | Filiz | Simsek | filiz.simsek@example.com |

### Events (10 records)
| ID | Title | Category | Status | Capacity |
|---|---|---|---|---|
| 1 | Istanbul Marathon 2024 | Sports | PLANNED | 500 |
| 2 | Python Advanced Workshop | Technology | PLANNED | 50 |
| 3 | Web3 and Blockchain Conference | Technology | PLANNED | 200 |
| 4 | English Speaking Course | Education | ONGOING | 30 |
| 5 | Turkish Painting Art Exhibition | Arts | ONGOING | 150 |
| 6 | First Steps in Entrepreneurship | Business | PLANNED | 80 |
| 7 | Blood Pressure Management Seminar | Health | PLANNED | 100 |
| 8 | National Bodybuilding Championship | Sports | PLANNED | 300 |
| 9 | React and Next.js Training | Technology | PLANNED | 40 |
| 10 | Digital Marketing Conference | Business | PLANNED | 250 |

### Event Registrations (42 records)
- Total registrations across all events
- Status: REGISTERED, ATTENDED, CANCELLED, NO_SHOW
- Example: Event 1 (Marathon) has 6 participants registered

---

## 🚀 Application Startup

### Start Spring Boot Application

```bash
cd EventsManagementSystem\demo

# Option 1: Using Maven
mvn spring-boot:run

# Option 2: Using IDE
# Right-click DemoApplication.java → Run As → Java Application

# Expected output:
# Started DemoApplication in 2.5 seconds (JVM running for 3.2s)
# Tomcat started on port(s): 8080 (http)
```

### Verify Application is Running

```bash
# Check if API is responding
curl http://localhost:8080/api/categories

# Or visit in browser:
# http://localhost:8080/api/categories
```

---

## 🧪 Testing the API

### Option 1: Using PowerShell Test Script (Recommended for Windows)

```bash
# Navigate to project directory
cd EventsManagementSystem\demo

# Run PowerShell test script
.\Test_API.ps1

# This will run 20 comprehensive tests
```

### Option 2: Using Postman

**Import Configuration:**

1. Open Postman
2. Create new Collection: "Events Management API"
3. Add folder: "Categories", "Participants", "Events", "Event Participants"

**Sample Requests:**

#### Get All Categories
```
GET http://localhost:8080/api/categories
Headers: Content-Type: application/json
```

#### Get All Events
```
GET http://localhost:8080/api/events
Headers: Content-Type: application/json
```

#### Register Participant to Event
```
POST http://localhost:8080/api/event-participants/register?eventId=1&participantId=1
Headers: Content-Type: application/json
```

#### Create New Event
```
POST http://localhost:8080/api/events
Headers: Content-Type: application/json

Body:
{
  "title": "New Event Title",
  "description": "Event description",
  "startDate": "2024-06-15T09:00:00",
  "endDate": "2024-06-15T17:00:00",
  "location": "Event Location",
  "capacity": 100,
  "categoryId": 1
}
```

### Option 3: Using curl

```bash
# Get all categories
curl -X GET http://localhost:8080/api/categories

# Get event by ID
curl -X GET http://localhost:8080/api/events/1

# Search events
curl -X GET "http://localhost:8080/api/events/search?keyword=Python&status=PLANNED"

# Register participant
curl -X POST "http://localhost:8080/api/event-participants/register?eventId=1&participantId=1"

# Create new category
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name":"Music","description":"Music events"}'
```

---

## 📈 Database Query Examples

### Find All Planned Events with Participant Count

```sql
SELECT 
    e.id,
    e.title,
    c.name as category,
    COUNT(ep.id) as participants,
    e.capacity,
    (e.capacity - COUNT(ep.id)) as available_spots
FROM Events e
LEFT JOIN Categories c ON e.category_id = c.id
LEFT JOIN EventParticipants ep ON e.id = ep.event_id
WHERE e.status = 'PLANNED'
GROUP BY e.id, e.title, c.name, e.capacity
ORDER BY available_spots DESC
```

### Find Participants for Specific Event

```sql
SELECT 
    p.id,
    p.first_name,
    p.last_name,
    p.email,
    ep.status,
    ep.registered_at
FROM EventParticipants ep
JOIN Participants p ON ep.participant_id = p.id
WHERE ep.event_id = 1
ORDER BY ep.registered_at
```

### Find Technology Category Events

```sql
SELECT 
    e.id,
    e.title,
    e.start_date,
    e.location,
    COUNT(ep.id) as registered_count
FROM Events e
LEFT JOIN EventParticipants ep ON e.id = ep.event_id
WHERE e.category_id = 2  -- Technology
GROUP BY e.id, e.title, e.start_date, e.location
```

### Get Events Attended by Participant

```sql
SELECT 
    e.id,
    e.title,
    e.start_date,
    ep.registered_at,
    ep.status as participation_status
FROM EventParticipants ep
JOIN Events e ON ep.event_id = e.id
WHERE ep.participant_id = 1
AND ep.status = 'ATTENDED'
ORDER BY e.start_date DESC
```

---

## ⚠️ Common Issues and Solutions

### Issue 1: Connection Refused

**Error:** `Communications link failure - Connection refused`

**Solution:**
```bash
# Verify SQL Server is running
tasklist | findstr sqlservr.exe

# Start SQL Server service
net start MSSQL$SQLEXPRESS

# Test connection
sqlcmd -S localhost -U sa -P Salih.12345 -Q "SELECT 1"
```

### Issue 2: Login Failed

**Error:** `Login failed for user 'sa'`

**Solution:**
```
1. Verify username: sa
2. Verify password: Salih.12345 (in application.properties)
3. Check SQL Server Authentication is enabled
4. Check SQL Server is in Mixed Mode (not Windows Auth only)
```

### Issue 3: Database Already Exists

**Error:** `Msg 1801, Level 16, State 1: Database 'EventsManagementSystem' already exists`

**Solution:**
```sql
-- Drop existing database
DROP DATABASE IF EXISTS EventsManagementSystem;

-- Then run the setup script again
```

### Issue 4: Duplicate Entry

**Error:** `Violation of UNIQUE KEY constraint`

**Solution:**
```bash
# Use different email or delete existing record
# Example: Use "newemail@example.com" instead
```

### Issue 5: Event Capacity Exceeded

**Error:** `Etkinlik 'Event Title' dolu (Kapasite: 100)`

**Solution:**
```bash
# Option 1: Choose different event
# Option 2: Update event capacity
PUT /api/events/1
{
  "capacity": 150  # Increase capacity
}
```

---

## 📝 Data Validation Rules

### Categories
- `name`: Required, must be unique, max 100 characters
- `description`: Optional, max 500 characters

### Participants
- `firstName`: Required, max 100 characters
- `lastName`: Required, max 100 characters
- `email`: Required, must be valid email format, must be unique
- `phoneNumber`: Optional, max 20 characters

### Events
- `title`: Required, max 200 characters
- `description`: Optional, max 2000 characters
- `startDate`: Required, DateTime format
- `endDate`: Required, DateTime format (must be after startDate)
- `location`: Required, max 300 characters
- `capacity`: Required, must be positive integer
- `categoryId`: Required, must reference existing category

### Event Participants
- `eventId`: Required, must reference existing event
- `participantId`: Required, must reference existing participant
- `status`: Optional (default: REGISTERED)
  - Valid values: REGISTERED, ATTENDED, CANCELLED, NO_SHOW
- Constraint: One participant cannot register twice for same event

---

## 🔄 Database Refresh

To reset database to initial state:

```bash
# Using SQL Command Line
sqlcmd -S localhost -U sa -P Salih.12345 -i Database_Setup_Clean.sql

# Using SQL Query
DROP DATABASE IF EXISTS EventsManagementSystem;
-- Then run setup script again
```

---

## 📊 Performance Tips

### Add More Indexes (Optional)

```sql
-- For frequently searched fields
CREATE INDEX IX_Events_Title ON Events(title);
CREATE INDEX IX_Participants_Email ON Participants(email);
CREATE INDEX IX_EventParticipants_Status ON EventParticipants(status);
```

### Query Optimization

```sql
-- Use EXISTS instead of IN for large datasets
SELECT * FROM events e
WHERE EXISTS (
    SELECT 1 FROM EventParticipants ep
    WHERE ep.event_id = e.id
)
```

---

## 📞 Support and Documentation

- **API Documentation**: See `API_DOCUMENTATION.md`
- **Getting Started Guide**: See `GETTING_STARTED.md`
- **Setup Summary**: See `SETUP_SUMMARY.md`

---

**Database Setup Date**: April 2, 2024  
**Sample Data**: 6 Categories, 12 Participants, 10 Events, 42 Registrations  
**Status**: ✅ Ready for Development and Testing
