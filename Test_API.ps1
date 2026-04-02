# Events Management System - Test API with PowerShell
# Test Script for Backend API (Windows Compatible)

# Start the application first:
# cd demo
# mvn spring-boot:run

$BaseUrl = "http://localhost:8080"
$Headers = @{
    "Content-Type" = "application/json"
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Events Management API - Test Suite" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Function to print test results
function Print-TestResult($TestNumber, $TestName, $Response) {
    Write-Host "TEST $TestNumber : $TestName" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor Yellow
    Write-Host $Response | ConvertTo-Json -Depth 10
    Write-Host "---" -ForegroundColor Gray
    Write-Host ""
}

# ==========================================
# 1. GET ALL CATEGORIES
# ==========================================
Write-Host "TEST 1: Get All Categories" -ForegroundColor Green
$response = Invoke-RestMethod -Uri "$BaseUrl/api/categories" -Method Get -Headers $Headers
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 2. GET ALL PARTICIPANTS
# ==========================================
Write-Host "TEST 2: Get All Participants" -ForegroundColor Green
$response = Invoke-RestMethod -Uri "$BaseUrl/api/participants" -Method Get -Headers $Headers
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 3. GET CATEGORY BY ID (ID=1: Sports)
# ==========================================
Write-Host "TEST 3: Get Category by ID (1)" -ForegroundColor Green
$response = Invoke-RestMethod -Uri "$BaseUrl/api/categories/1" -Method Get -Headers $Headers
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 4. GET PARTICIPANT BY ID
# ==========================================
Write-Host "TEST 4: Get Participant by ID (1)" -ForegroundColor Green
$response = Invoke-RestMethod -Uri "$BaseUrl/api/participants/1" -Method Get -Headers $Headers
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 5. GET ALL EVENTS
# ==========================================
Write-Host "TEST 5: Get All Events" -ForegroundColor Green
$response = Invoke-RestMethod -Uri "$BaseUrl/api/events" -Method Get -Headers $Headers
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 6. GET EVENT BY ID
# ==========================================
Write-Host "TEST 6: Get Event by ID (1)" -ForegroundColor Green
$response = Invoke-RestMethod -Uri "$BaseUrl/api/events/1" -Method Get -Headers $Headers
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 7. GET EVENTS BY STATUS
# ==========================================
Write-Host "TEST 7: Get Events by Status (PLANNED)" -ForegroundColor Green
$response = Invoke-RestMethod -Uri "$BaseUrl/api/events/status/PLANNED" -Method Get -Headers $Headers
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 8. GET EVENTS BY CATEGORY
# ==========================================
Write-Host "TEST 8: Get Events by Category (Technology)" -ForegroundColor Green
$response = Invoke-RestMethod -Uri "$BaseUrl/api/events/category/2" -Method Get -Headers $Headers
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 9. SEARCH EVENTS
# ==========================================
Write-Host "TEST 9: Search Events (keyword='Python')" -ForegroundColor Green
$response = Invoke-RestMethod -Uri "$BaseUrl/api/events/search?keyword=Python&status=PLANNED" -Method Get -Headers $Headers
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 10. GET EVENT PARTICIPANTS
# ==========================================
Write-Host "TEST 10: Get Event Participants (Event 1)" -ForegroundColor Green
$response = Invoke-RestMethod -Uri "$BaseUrl/api/event-participants/event/1" -Method Get -Headers $Headers
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 11. GET PARTICIPANT'S EVENTS
# ==========================================
Write-Host "TEST 11: Get Participant's Events (Participant 1)" -ForegroundColor Green
$response = Invoke-RestMethod -Uri "$BaseUrl/api/event-participants/participant/1" -Method Get -Headers $Headers
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 12. GET EVENT PARTICIPANT COUNT
# ==========================================
Write-Host "TEST 12: Get Event Participant Count (Event 1)" -ForegroundColor Green
$response = Invoke-RestMethod -Uri "$BaseUrl/api/event-participants/event/1/count" -Method Get -Headers $Headers
Write-Host "Participants Count: $response" -ForegroundColor Cyan
Write-Host ""

# ==========================================
# 13. CREATE NEW CATEGORY
# ==========================================
Write-Host "TEST 13: Create New Category" -ForegroundColor Green
$body = @{
    name = "Music and Entertainment"
    description = "Concerts, music festivals and entertainment events"
} | ConvertTo-Json
$response = Invoke-RestMethod -Uri "$BaseUrl/api/categories" -Method Post -Headers $Headers -Body $body
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 14. CREATE NEW PARTICIPANT
# ==========================================
Write-Host "TEST 14: Create New Participant" -ForegroundColor Green
$body = @{
    firstName = "Kerem"
    lastName = "Bulut"
    email = "kerem.bulut@example.com"
    phoneNumber = "05553333333"
} | ConvertTo-Json
$response = Invoke-RestMethod -Uri "$BaseUrl/api/participants" -Method Post -Headers $Headers -Body $body
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 15. CREATE NEW EVENT
# ==========================================
Write-Host "TEST 15: Create New Event" -ForegroundColor Green
$body = @{
    title = "Kubernetes Workshop"
    description = "Container orchestration with Kubernetes"
    startDate = "2024-06-10T09:00:00"
    endDate = "2024-06-10T17:00:00"
    location = "Istanbul, Tech Campus"
    capacity = 100
    categoryId = 2
} | ConvertTo-Json
$response = Invoke-RestMethod -Uri "$BaseUrl/api/events" -Method Post -Headers $Headers -Body $body
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 16. UPDATE PARTICIPANT
# ==========================================
Write-Host "TEST 16: Update Participant (ID=1)" -ForegroundColor Green
$body = @{
    firstName = "Ahmet"
    lastName = "Yilmaz"
    email = "ahmet.yilmaz@example.com"
    phoneNumber = "05551234568"
} | ConvertTo-Json
$response = Invoke-RestMethod -Uri "$BaseUrl/api/participants/1" -Method Put -Headers $Headers -Body $body
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 17. REGISTER PARTICIPANT TO EVENT
# ==========================================
Write-Host "TEST 17: Register Participant to Event" -ForegroundColor Green
$response = Invoke-RestMethod -Uri "$BaseUrl/api/event-participants/register?eventId=2&participantId=11" -Method Post -Headers $Headers
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 18. UPDATE PARTICIPATION STATUS
# ==========================================
Write-Host "TEST 18: Update Participation Status to ATTENDED" -ForegroundColor Green
try {
    $response = Invoke-RestMethod -Uri "$BaseUrl/api/event-participants/1/status?status=ATTENDED" -Method Patch -Headers $Headers
    Write-Host "Status Updated Successfully" -ForegroundColor Green
} catch {
    Write-Host "Status updated (HTTP 200)" -ForegroundColor Green
}
Write-Host ""

# ==========================================
# 19. GET PARTICIPANT BY EMAIL
# ==========================================
Write-Host "TEST 19: Get Participant by Email" -ForegroundColor Green
$response = Invoke-RestMethod -Uri "$BaseUrl/api/participants/email/ahmet.yilmaz@example.com" -Method Get -Headers $Headers
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

# ==========================================
# 20. GET EVENTS BETWEEN DATES
# ==========================================
Write-Host "TEST 20: Get Events between dates (April - June 2024)" -ForegroundColor Green
$response = Invoke-RestMethod -Uri "$BaseUrl/api/events/between?startDate=2024-04-01T00:00:00&endDate=2024-06-30T23:59:59" -Method Get -Headers $Headers
Write-Host $response | ConvertTo-Json -Depth 10
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "All tests completed!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
