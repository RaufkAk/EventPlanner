# Email Notification Data Sources

## Answer: "Mail bilgisini nerden alacak?" (Where does it get email info?)

This document explains the complete data flow for email notifications in the EventPlanner system.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Email Data Flow                             │
└─────────────────────────────────────────────────────────────────────┘

User Request (POST /api/bookings)
         │
         ▼
┌─────────────────────┐
│  API Gateway        │  (Port 8000)
│  (Spring Cloud      │
│   Gateway)          │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────────────────────────────────────────────────┐
│         BOOKING SERVICE (Port 8083) - Data Generator            │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. Receives POST /api/bookings request                         │
│     Input: { userId: 1, eventId: "507f...", status: "PENDING" }│
│                                                                  │
│  2. Validates with User Service (JWT token)                     │
│     ✓ User verified → userId confirmed                         │
│                                                                  │
│  3. Checks Event Service for stock                             │
│     ✓ Event exists → eventId confirmed                         │
│                                                                  │
│  4. Reserves seat in Event Service                             │
│     ✓ Seat reserved → seatCount = 1                            │
│                                                                  │
│  5. Processes payment via Payment Service                       │
│     ✓ Payment OK → booking persisted to MySQL                  │
│                                                                  │
│  6. ⚡ CREATES BOOKING EVENT WITH EMAIL DATA ⚡                 │
│     ┌──────────────────────────────────────────────────────┐  │
│     │ BookingCreatedEvent {                                │  │
│     │   bookingId: 123,                                    │  │
│     │   userId: 1,                                         │  │
│     │   userEmail: "user1@eventplanner.com" ◄──────────┐   │  │
│     │   eventId: "507f...",                           │   │  │
│     │   eventTitle: "Event 507f..." ◄────────────────┤   │  │
│     │   seatCount: 1,                                │   │  │
│     │   status: "CONFIRMED",                         │   │  │
│     │   bookingDate: "2026-01-05T14:54:00"          │   │  │
│     │ }                                              │   │  │
│     └──────────────────────────────────────────────────────┘  │
│                                           │                    │
│           EMAIL DATA SOURCES:             │                    │
│           • userEmail: userId-based      ─┘                    │
│           • eventTitle: eventId-based                          │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
           │
           ▼
    ┌─────────────────┐
    │   RabbitMQ      │
    │  (Message Broker)
    │  Port: 5672     │
    │  Queue Name:    │
    │  notificationQ. │
    └────────┬────────┘
             │
             ▼ (Async Message)
┌─────────────────────────────────────────────────────────────────┐
│    NOTIFICATION SERVICE (Port 8085) - Email Sender              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. @RabbitListener receives BookingCreatedEvent from queue    │
│                                                                  │
│  2. Extracts email info from event message:                    │
│     • Recipient: event.getUserEmail() → "user1@eventplanner.com"
│     • Event Title: event.getEventTitle() → "Event 507f..."    │
│     • Booking ID: event.getBookingId() → 123                  │
│                                                                  │
│  3. Calls EmailService.sendBookingConfirmation(event)          │
│                                                                  │
│  4. EmailService either:                                        │
│     ┌─────────────────────────────────────────────────────┐   │
│     │ SIMULATED MODE (Current):                           │   │
│     │ • Logs to console:                                  │   │
│     │   "=== EMAIL SENT ==="                              │   │
│     │   "To: user1@eventplanner.com"                      │   │
│     │   "Subject: Booking Confirmation - Event 507f..."  │   │
│     │   "Body: [HTML email content]"                      │   │
│     │ • Persists to MongoDB notification_logs collection  │   │
│     └─────────────────────────────────────────────────────┘   │
│          OR                                                      │
│     ┌─────────────────────────────────────────────────────┐   │
│     │ REAL EMAIL MODE (Ready to enable):                  │   │
│     │ • Connects to Gmail SMTP (smtp.gmail.com:587)      │   │
│     │ • Authenticates with credentials from              │   │
│     │   application.properties                            │   │
│     │ • Sends real email via JavaMailSender bean          │   │
│     │ • Persists to MongoDB notification_logs collection  │   │
│     └─────────────────────────────────────────────────────┘   │
│                                                                  │
│  5. Returns success/failure status logged in MongoDB            │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Current Implementation: Email Data Sources

### 1. **User Email Address** → `userEmail` field
**Source:** Generated from userId (placeholder approach)

```java
// BookingService.java - Line 110
String userEmail = "user" + savedBooking.getUserId() + "@eventplanner.com";
// Result: "user1@eventplanner.com"
```

**Current Status:** Placeholder
- ✅ Works immediately
- ✅ Prevents null pointer exceptions
- ⚠️ Not real email addresses

**Future Enhancement:** Real User Service Integration
```java
// Would be:
UserResponse userResponse = userService.getUserById(savedBooking.getUserId(), token);
String userEmail = userResponse.getEmail();  // Actual email from database
```

---

### 2. **Event Title** → `eventTitle` field
**Source:** Generated from eventId (placeholder approach)

```java
// BookingService.java - Line 114
String eventTitle = "Event " + savedBooking.getEventId();
// Result: "Event 507f1f77bcf86cd799439011"
```

**Current Status:** Placeholder
- ✅ Works immediately
- ✅ Provides context in emails
- ⚠️ Not descriptive

**Future Enhancement:** Real Event Service Integration
```java
// Would be:
EventStockResponse eventResponse = eventService.checkEventStock(eventId);
String eventTitle = eventResponse.getName();  // Actual event name from database
```

---

### 3. **Booking ID & Seat Count** → `bookingId`, `seatCount` fields
**Source:** From saved Booking entity

```java
// BookingService.java
.bookingId(savedBooking.getId())      // From MySQL: booking.id
.seatCount(1)                          // Always 1 (current system design)
```

**Status:** ✅ Complete and accurate

---

### 4. **Booking Status & Date** → `status`, `bookingDate` fields
**Source:** From saved Booking entity

```java
// BookingService.java
.status(savedBooking.getStatus().name())           // CONFIRMED
.bookingDate(savedBooking.getBookingDate())        // 2026-01-05T14:54:00
```

**Status:** ✅ Complete and accurate

---

## Step-by-Step Email Sending Flow

### When a Booking is Created:

```
1. User calls POST /api/bookings with {userId: 1, eventId: "507f..."}
   ↓
2. BookingService.createBooking() executes:
   • Validates user exists
   • Checks event stock
   • Reserves seat
   • Processes payment
   • Saves booking to MySQL ✓
   ↓
3. Constructs BookingCreatedEvent with:
   - bookingId: 123 (from saved booking)
   - userId: 1 (from request)
   - userEmail: "user1@eventplanner.com" (generated from userId)
   - eventId: "507f..." (from request)
   - eventTitle: "Event 507f..." (generated from eventId)
   - seatCount: 1 (hardcoded)
   - status: "CONFIRMED" (booking status)
   - bookingDate: 2026-01-05T14:54:00 (from saved booking)
   ↓
4. Publishes event to RabbitMQ notificationQueue
   ↓
5. NotificationService listens and receives event
   ↓
6. Extracts email data from event:
   To: event.getUserEmail()       → "user1@eventplanner.com"
   Subject: "Booking Confirmation - " + event.getEventTitle()
   Subject: "Booking Confirmation - Event 507f..."
   ↓
7. EmailService.sendBookingConfirmation(event):
   • If SIMULATED: Logs to console + saves to MongoDB
   • If REAL: Connects to Gmail SMTP + sends actual email + saves to MongoDB
   ↓
8. Notification persisted to MongoDB notification_logs collection:
   {
     bookingId: "123",
     recipient: "user1@eventplanner.com",
     subject: "Booking Confirmation - Event 507f...",
     status: "SUCCESS" or "FAILED",
     sentAt: 2026-01-05T14:54:05,
     response: "Email sent successfully" or error details
   }
```

---

## Current Status & Configuration

### ✅ What's Working:
- Email address generation from userId
- Event title generation from eventId
- Event construction with all fields
- RabbitMQ message publishing
- NotificationService consumer listening
- Email simulation and logging
- MongoDB persistence of email logs

### ⚠️ What's Ready to Enable:
- **Real Email Sending**: EmailService has JavaMailSender bean ready
- **SMTP Configuration**: Gmail configured in application.properties
- **Only Missing**: Actual Gmail credentials (currently placeholder)

### ❌ What Needs Integration:
- **Real User Email**: Call UserService to get actual email addresses
- **Real Event Title**: Call EventService to get actual event names
- **Error Handling**: Add retry logic for failed email sends

---

## How to Enable Real Email Sending

### Step 1: Generate Gmail App Password
```
1. Go to myaccount.google.com
2. Select "Security" from left menu
3. Enable 2-Step Verification
4. Go to App passwords → Generate
5. Select Mail and Windows Computer
6. Copy the 16-character password
```

### Step 2: Update application.properties
```properties
spring.mail.username=your-actual-email@gmail.com
spring.mail.password=xxxx xxxx xxxx xxxx  # 16-char app password
```

### Step 3: Uncomment Real Implementation in EmailService
```java
// notification-service/src/main/java/.../EmailService.java

// FROM THIS (simulated):
log.info("=== EMAIL SENT ===");
log.info("To: {}", event.getUserEmail());

// TO THIS (real):
SimpleMailMessage message = new SimpleMailMessage();
message.setTo(event.getUserEmail());
message.setFrom("your-email@gmail.com");
message.setSubject("Booking Confirmation - " + event.getEventTitle());
message.setText(buildEmailBody(event));
mailSender.send(message);
```

### Step 4: Restart Notification Service
```bash
cd notification-service
mvn spring-boot:run
```

---

## Database Records Showing Email Trail

### MySQL (booking-service)
```sql
SELECT id, user_id, event_id, status, booking_date 
FROM booking 
WHERE id = 123;

Result:
id  │ user_id │ event_id           │ status    │ booking_date
123 │ 1       │ 507f1f77bcf... │ CONFIRMED │ 2026-01-05 14:54:00
```

### MongoDB (notification_db / notification_logs)
```javascript
db.notification_logs.findOne({bookingId: "123"})

Result:
{
  _id: ObjectId(...),
  bookingId: "123",
  recipient: "user1@eventplanner.com",
  subject: "Booking Confirmation - Event 507f1f77bcf86cd799439011",
  status: "SUCCESS",
  sentAt: ISODate("2026-01-05T14:54:05.123Z"),
  response: "Email sent successfully or simulated",
  createdAt: ISODate("2026-01-05T14:54:05.123Z")
}
```

---

## Architecture Diagram: Where Each Component Gets Email Info

```
                    BOOKING SERVICE
                   Generates Email Data
                           │
                ┌──────────┼──────────┐
                │          │          │
                ▼          ▼          ▼
          UserEmail   EventTitle  BookingID
           (userId)   (eventId)   (Saved DB)
                │          │          │
                └──────────┼──────────┘
                           │
                   BookingCreatedEvent
                   {
                     userId: 1
                     userEmail: "user1@eventplanner.com"
                     eventTitle: "Event 507f..."
                     bookingId: 123
                   }
                           │
                           ▼
                      RabbitMQ Queue
                   (notificationQueue)
                           │
                           ▼
                NOTIFICATION SERVICE
               Receives Email Data From Event
                           │
                   ┌───────┴───────┐
                   │               │
                   ▼               ▼
              EmailService    MongoDB Logs
              Sends To:        Persists:
            event.user        recipient: email
            Email()           subject: title
                              status: success/fail
```

---

## Summary

**User's Question:** "Mail bilgisini nerden alacak?" (Where will it get the email info?)

**Answer:**
1. **User Email** → Generated from userId: `"user" + userId + "@eventplanner.com"`
2. **Event Title** → Generated from eventId: `"Event " + eventId`
3. **Booking Details** → From MySQL: bookingId, status, date
4. **All Combined** → Packaged in BookingCreatedEvent and sent via RabbitMQ
5. **NotificationService** → Extracts email info and either:
   - **Simulates**: Logs to console (current)
   - **Real**: Connects to Gmail and sends actual email (ready to enable)

**To Enable Real Email:**
- Provide Gmail app password
- Update application.properties
- Uncomment real email code
- Restart Notification Service

**To Improve:**
- Integrate User Service to get real emails
- Integrate Event Service to get real event names
- Add retry logic for failed email sends
