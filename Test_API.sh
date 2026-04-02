# Events Management System - Test API with curl
# Test Script for Backend API

## Start the application first:
# cd demo
# mvn spring-boot:run

## API Base URL
BASE_URL="http://localhost:8080"

echo "========================================"
echo "Events Management API - Test Suite"
echo "========================================"
echo ""

# ==========================================
# 1. GET ALL CATEGORIES
# ==========================================
echo "TEST 1: Get All Categories"
echo "Request: GET $BASE_URL/api/categories"
curl -X GET "$BASE_URL/api/categories" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 2. GET ALL PARTICIPANTS
# ==========================================
echo "TEST 2: Get All Participants"
echo "Request: GET $BASE_URL/api/participants"
curl -X GET "$BASE_URL/api/participants" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 3. GET CATEGORY BY ID (ID=1: Sports)
# ==========================================
echo "TEST 3: Get Category by ID (1)"
echo "Request: GET $BASE_URL/api/categories/1"
curl -X GET "$BASE_URL/api/categories/1" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 4. GET PARTICIPANT BY ID (ID=1: Ahmet Yilmaz)
# ==========================================
echo "TEST 4: Get Participant by ID (1)"
echo "Request: GET $BASE_URL/api/participants/1"
curl -X GET "$BASE_URL/api/participants/1" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 5. GET ALL EVENTS
# ==========================================
echo "TEST 5: Get All Events"
echo "Request: GET $BASE_URL/api/events"
curl -X GET "$BASE_URL/api/events" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 6. GET EVENT BY ID (ID=1: Istanbul Marathon)
# ==========================================
echo "TEST 6: Get Event by ID (1)"
echo "Request: GET $BASE_URL/api/events/1"
curl -X GET "$BASE_URL/api/events/1" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 7. GET EVENTS BY STATUS (PLANNED)
# ==========================================
echo "TEST 7: Get Events by Status (PLANNED)"
echo "Request: GET $BASE_URL/api/events/status/PLANNED"
curl -X GET "$BASE_URL/api/events/status/PLANNED" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 8. GET EVENTS BY CATEGORY (Category ID=2: Technology)
# ==========================================
echo "TEST 8: Get Events by Category (2)"
echo "Request: GET $BASE_URL/api/events/category/2"
curl -X GET "$BASE_URL/api/events/category/2" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 9. SEARCH EVENTS by keyword
# ==========================================
echo "TEST 9: Search Events (keyword='Python')"
echo "Request: GET $BASE_URL/api/events/search?keyword=Python&status=PLANNED"
curl -X GET "$BASE_URL/api/events/search?keyword=Python&status=PLANNED" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 10. GET EVENT PARTICIPANTS
# ==========================================
echo "TEST 10: Get Event Participants (Event ID=1)"
echo "Request: GET $BASE_URL/api/event-participants/event/1"
curl -X GET "$BASE_URL/api/event-participants/event/1" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 11. GET PARTICIPANT'S EVENTS
# ==========================================
echo "TEST 11: Get Participant's Events (Participant ID=1)"
echo "Request: GET $BASE_URL/api/event-participants/participant/1"
curl -X GET "$BASE_URL/api/event-participants/participant/1" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 12. GET EVENT PARTICIPANT COUNT
# ==========================================
echo "TEST 12: Get Event Participant Count (Event ID=1)"
echo "Request: GET $BASE_URL/api/event-participants/event/1/count"
curl -X GET "$BASE_URL/api/event-participants/event/1/count" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 13. CREATE NEW CATEGORY
# ==========================================
echo "TEST 13: Create New Category"
echo "Request: POST $BASE_URL/api/categories"
curl -X POST "$BASE_URL/api/categories" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Music and Entertainment",
    "description": "Concerts, music festivals and entertainment events"
  }' \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 14. CREATE NEW PARTICIPANT
# ==========================================
echo "TEST 14: Create New Participant"
echo "Request: POST $BASE_URL/api/participants"
curl -X POST "$BASE_URL/api/participants" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Kerem",
    "lastName": "Bulut",
    "email": "kerem.bulut@example.com",
    "phoneNumber": "05553333333"
  }' \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 15. CREATE NEW EVENT
# ==========================================
echo "TEST 15: Create New Event"
echo "Request: POST $BASE_URL/api/events"
curl -X POST "$BASE_URL/api/events" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Kubernetes Workshop",
    "description": "Container orchestration with Kubernetes",
    "startDate": "2024-06-10T09:00:00",
    "endDate": "2024-06-10T17:00:00",
    "location": "Istanbul, Tech Campus",
    "capacity": 100,
    "categoryId": 2
  }' \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 16. UPDATE EVENT STATUS
# ==========================================
echo "TEST 16: Update Event Status (Event 1 -> ONGOING)"
echo "Request: PATCH $BASE_URL/api/events/1/status?status=ONGOING"
curl -X PATCH "$BASE_URL/api/events/1/status?status=ONGOING" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 17. REGISTER PARTICIPANT TO EVENT (Participant 5 to Event 2)
# ==========================================
echo "TEST 17: Register Participant to Event"
echo "Request: POST $BASE_URL/api/event-participants/register?eventId=2&participantId=11"
curl -X POST "$BASE_URL/api/event-participants/register?eventId=2&participantId=11" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 18. UPDATE PARTICIPATION STATUS
# ==========================================
echo "TEST 18: Update Participation Status"
echo "Request: PATCH $BASE_URL/api/event-participants/1/status?status=ATTENDED"
curl -X PATCH "$BASE_URL/api/event-participants/1/status?status=ATTENDED" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 19. GET PARTICIPANT BY EMAIL
# ==========================================
echo "TEST 19: Get Participant by Email"
echo "Request: GET $BASE_URL/api/participants/email/ahmet.yilmaz@example.com"
curl -X GET "$BASE_URL/api/participants/email/ahmet.yilmaz@example.com" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# ==========================================
# 20. GET EVENTS BETWEEN DATES
# ==========================================
echo "TEST 20: Get Events between dates"
echo "Request: GET $BASE_URL/api/events/between?startDate=2024-04-01T00:00:00&endDate=2024-06-30T23:59:59"
curl -X GET "$BASE_URL/api/events/between?startDate=2024-04-01T00:00:00&endDate=2024-06-30T23:59:59" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

echo "========================================"
echo "All tests completed!"
echo "========================================"
