# EventPlanner - Quick Start Guide

## 🚀 Fast Setup (5 minutes)

### Step 1: Build All Services
```bash
cd EventPlanner
mvn clean package -DskipTests
```
**Expected Output**: BUILD SUCCESS ✅

### Step 2: Start Infrastructure (if Docker available)
```bash
docker-compose up -d
```

### Step 3: Start Services (in separate terminals)

**Terminal 1 - Discovery Server:**
```bash
java -jar discovery-server/target/discovery-server-0.0.1-SNAPSHOT.jar
# Waits for startup message: "Eureka Server started"
```

**Terminal 2 - Config Server:**
```bash
java -jar config-server/target/config-server-0.0.1-SNAPSHOT.jar
# Config server reads from GitHub
```

**Terminal 3 - User Service:**
```bash
java -jar user-service/target/user-service-0.0.1-SNAPSHOT.jar
```

**Terminal 4 - Event Service:**
```bash
java -jar event-service/target/event-service-0.0.1-SNAPSHOT.jar
```

**Terminal 5 - Booking Service:**
```bash
java -jar booking-service/target/booking-service-0.0.1-SNAPSHOT.jar
```

**Terminal 6 - Payment Service:**
```bash
java -jar payment-service/target/payment-service-0.0.1-SNAPSHOT.jar
```

**Terminal 7 - Notification Service:**
```bash
java -jar notification-service/target/notification-service-0.0.1-SNAPSHOT.jar
```

**Terminal 8 - API Gateway (start last):**
```bash
java -jar api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar
```

## ✅ Verify Setup

### Check Service Registration
```bash
curl http://localhost:8761/eureka/apps
```

### Check Gateway Health
```bash
curl http://localhost:8000/actuator/health
```

### Check All Services
```bash
curl http://localhost:8000/api/users/health
curl http://localhost:8000/api/events/health
curl http://localhost:8000/api/bookings/health
curl http://localhost:8000/api/payments/health
```

## 🧪 Test Booking Flow

### 1. Register User
```bash
curl -X POST http://localhost:8000/users/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "role": "USER"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8000/users/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```
**Save the JWT token from response**

### 3. Create Event
```bash
TOKEN="<your-jwt-token>"

curl -X POST http://localhost:8000/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Tech Conference 2026",
    "description": "Annual tech conference",
    "date": "2026-06-15T09:00:00",
    "location": "Istanbul Convention Center",
    "availableSeats": 500,
    "price": 99.99
  }'
```

### 4. List Events
```bash
curl http://localhost:8000/events
```

### 5. Book Ticket
```bash
TOKEN="<your-jwt-token>"

curl -X POST http://localhost:8000/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "userId": 1,
    "eventId": "event-001",
    "quantity": 2
  }'
```

### 6. Check Booking Status
```bash
TOKEN="<your-jwt-token>"

curl http://localhost:8000/bookings/{bookingId} \
  -H "Authorization: Bearer $TOKEN"
```

## 🔍 Monitoring Dashboards

| Service | URL | Purpose |
|---------|-----|---------|
| Eureka | http://localhost:8761 | Service Discovery |
| RabbitMQ | http://localhost:15672 | Message Broker |
| H2 Console | http://localhost:8081/h2-console | Database View |

**RabbitMQ Credentials**: admin / password

## ⚠️ Common Issues

### "Connection refused" - Eureka Server not running
- Ensure Terminal 1 (Discovery Server) is running
- Check port 8761 is free

### "Service not registered" - Services not visible
- Wait 30-60 seconds for registration
- Check service logs for errors

### "JWT validation failed" - Authentication issues
- Ensure User Service is running
- Verify JWT token from login response
- Check token header format: `Bearer <token>`

### "RabbitMQ connection failed" - Messaging issues
- Start Docker containers: `docker-compose up -d`
- Or comment out RabbitMQ config in notification-service

## 🛑 Shutdown Services

```bash
# Kill all Java processes
pkill -f "java -jar"

# Or stop Docker containers
docker-compose down
```

## 📊 Service Port Reference

| Service | Port | Notes |
|---------|------|-------|
| API Gateway | 8000 | Entry point for all requests |
| User Service | 8081 | PostgreSQL (localhost) |
| Event Service | 8082 | MongoDB (docker) |
| Booking Service | 8083 | MySQL/H2 (docker) |
| Payment Service | 8084 | H2 (in-memory) |
| Notification Service | 8085 | MongoDB + RabbitMQ |
| Discovery Server | 8761 | Eureka dashboard |
| Config Server | 8888 | Config distribution |

## 🎯 Architecture Diagram

```
┌─────────────┐
│  Frontend   │
└──────┬──────┘
       │ HTTP/REST
       ▼
┌─────────────────────────┐
│   API Gateway (8000)    │ ◄─── Load Balancing & Routing
└────┬────────────────────┘
     │ Service-to-Service Calls (via Eureka)
     │
     ├──────────────────► User Service (8081)      ◄─── PostgreSQL
     ├──────────────────► Event Service (8082)     ◄─── MongoDB
     ├──────────────────► Booking Service (8083)   ◄─── MySQL/H2
     ├──────────────────► Payment Service (8084)   ◄─── H2
     ├──────────────────► Notification (8085)      ◄─── MongoDB + RabbitMQ
     │
     ├──────────────────► Discovery Server (8761)  ◄─── Eureka Registry
     └──────────────────► Config Server (8888)     ◄─── GitHub Config Repo
```

## 📝 Next Steps

1. Explore [SYSTEM_DESIGN.md](SYSTEM_DESIGN.md) for architecture details
2. Read [README.md](README.md) for comprehensive documentation
3. Check individual service README files for service-specific details
4. Review API documentation in [EVENTPlanner_API.md](EVENTPlanner_API.md)

---

**Happy Hacking!** 🚀
