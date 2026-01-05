# EventPlanner Data Flow Test Plan

## Test Scenario: Complete Booking Flow

### Prerequisites
- ✅ Docker: PostgreSQL, MySQL, MongoDB, RabbitMQ
- ⏳ Spring Boot Services: User, Event, Booking, Payment, Notification
- API Gateway: http://localhost:8000

---

## Test Steps

### 1️⃣ **User Registration & Login**

**Endpoint:** `POST http://localhost:8000/users/auth/register`

```bash
curl -X POST http://localhost:8000/users/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Rauf",
    "lastName": "Akyildiz",
    "username": "rauf",
    "email": "rauf@example.com",
    "password": "Test@123"
  }'
```

**Expected Response:**
```json
{
  "message": "User registered successfully",
  "userId": 1,
  "username": "rauf"
}
```

**Endpoint:** `POST http://localhost:8000/users/auth/login`

```bash
curl -X POST http://localhost:8000/users/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "rauf",
    "password": "Test@123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "rauf",
  "roles": ["USER"]
}
```

**Store JWT Token:** `TOKEN="<jwt_token_from_response>"`

---

### 2️⃣ **Event Creation (Event Service)**

**Endpoint:** `POST http://localhost:8000/events`

```bash
curl -X POST http://localhost:8000/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Tech Conference 2026",
    "description": "A great tech conference",
    "date": "2026-06-15T10:00:00",
    "location": "Istanbul, Turkey",
    "totalSeats": 100,
    "availableSeats": 100,
    "price": 99.99,
    "organizerId": 1
  }'
```

**Expected Response:**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "title": "Tech Conference 2026",
  "availableSeats": 100,
  "totalSeats": 100,
  "date": "2026-06-15T10:00:00"
}
```

**Store Event ID:** `EVENT_ID="507f1f77bcf86cd799439011"`

---

### 3️⃣ **Event Stock Check (Event Service)**

**Endpoint:** `GET http://localhost:8000/events/{eventId}`

```bash
curl http://localhost:8000/events/$EVENT_ID \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response:**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "title": "Tech Conference 2026",
  "availableSeats": 100,
  "totalSeats": 100,
  "price": 99.99
}
```

**✅ Verify:** `availableSeats > 0`

---

### 4️⃣ **Booking Creation (Booking Service - MAIN FLOW)**

**Endpoint:** `POST http://localhost:8000/bookings`

```bash
curl -X POST http://localhost:8000/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "userId": 1,
    "eventId": "507f1f77bcf86cd799439011",
    "numberOfTickets": 2
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "userId": 1,
  "eventId": "507f1f77bcf86cd799439011",
  "status": "CONFIRMED",
  "bookingDate": "2026-01-05T11:30:00",
  "totalAmount": 199.98
}
```

**Store Booking ID:** `BOOKING_ID="1"`

**Flow During This Request:**
```
Booking Service (Orchestrator)
  ├─ User Service: Validate user (JWT)
  ├─ Event Service: Check stock (availableSeats > 0)
  ├─ Event Service: Reserve seat (availableSeats -= 1)
  ├─ Payment Service: Process payment
  │   └─ MySQL: INSERT into payments
  ├─ MySQL: INSERT into bookings (Status: CONFIRMED)
  └─ RabbitMQ: Publish BookingCreatedEvent
     └─ Notification Service: Consume & Log
```

---

### 5️⃣ **Verify Booking Status**

**Endpoint:** `GET http://localhost:8000/bookings/{bookingId}`

```bash
curl http://localhost:8000/bookings/$BOOKING_ID \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response:**
```json
{
  "id": 1,
  "userId": 1,
  "eventId": "507f1f77bcf86cd799439011",
  "status": "CONFIRMED",
  "bookingDate": "2026-01-05T11:30:00"
}
```

---

## 🗄️ Database Verification

### PostgreSQL (User Service)
```bash
docker exec eventplanner-postgres psql -U admin -d user_db -c "SELECT id, username, email FROM users WHERE username='rauf';"
```

**Expected Result:** User record exists

---

### MongoDB (Event Service)
```bash
docker exec eventplanner-mongodb mongosh -u admin -p password --eval "db.getSiblingDB('event_db').events.findOne({title: 'Tech Conference 2026'})"
```

**Expected Result:** Event record with `availableSeats: 99` (reduced by 1)

---

### MySQL (Booking Service)
```bash
docker exec eventplanner-mysql mysql -u root -ppassword booking_db -e "SELECT id, user_id, event_id, status FROM bookings LIMIT 5;"
```

**Expected Result:** Booking record with `status = 'CONFIRMED'`

---

### MySQL (Payment Service)
```bash
docker exec eventplanner-mysql mysql -u root -ppassword payment_db -e "SELECT id, booking_id, amount, status FROM payments LIMIT 5;"
```

**Expected Result:** Payment record with `status = 'COMPLETED'`

---

### MongoDB (Notification Service)
```bash
docker exec eventplanner-mongodb mongosh -u admin -p password --eval "db.getSiblingDB('notification_db').notification_logs.find({}).pretty()"
```

**Expected Result:** Notification log entry

---

## ✅ Test Validation Checklist

- [ ] User registered successfully (PostgreSQL)
- [ ] JWT token obtained
- [ ] Event created (MongoDB)
- [ ] Event stock > 0
- [ ] Booking created (MySQL)
- [ ] Payment processed (MySQL)
- [ ] Stock reduced (MongoDB)
- [ ] Notification logged (MongoDB)
- [ ] All status = CONFIRMED/COMPLETED

---

## 🎯 Success Criteria

✅ **Data Flow Complete** if all steps pass:
1. User auth works
2. Event has stock
3. Booking created & confirmed
4. Payment processed
5. Stock updated
6. Notification recorded

