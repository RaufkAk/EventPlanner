# EventPlanner Test Planı

## Test Adımları (Sequence Diagram'a göre)

### 1. Event Oluştur
```bash
curl -X POST http://localhost:8082/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Concert 2025",
    "description": "Amazing concert",
    "venue": "Stadium",
    "category": "Music",
    "eventDate": "2025-06-15",
    "totalSeats": 100,
    "bookedSeats": 0,
    "price": 50.0
  }'
```
**Beklenen:** Event ID döner (örn: 65a3f...)

### 2. Event Stock'u Kontrol Et
```bash
curl http://localhost:8082/api/events/{EVENT_ID}/stock
```
**Beklenen:** `{"availableSeats": 100, "hasStock": true}`

### 3. User Oluştur (İsteğe bağlı)
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 4. Booking Yap (Ana Test)
```bash
curl -X POST http://localhost:8083/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "eventId": "{EVENT_ID}",
    "numberOfSeats": 2,
    "totalPrice": 100.0
  }'
```
**Beklenen İşlemler:**
- ✓ Event Service'e stock check -> `availableSeats >= 2`
- ✓ Payment Service'e payment request
- ✓ Event Service'e seat reservation
- ✓ RabbitMQ'ya event publish
- ✓ Notification Service consume message ve email gönder

**Beklenen Cevap:** `{"bookingId": "...", "status": "CONFIRMED"}`

### 5. Booking Doğrula
```bash
curl http://localhost:8083/api/bookings/{BOOKING_ID}
```

### 6. Event Stock'u Tekrar Kontrol Et
```bash
curl http://localhost:8082/api/events/{EVENT_ID}/stock
```
**Beklenen:** `{"availableSeats": 98, "hasStock": true}`

---

## Hata Ayıklama

### API Gateway 403 Hatası
- API Gateway (port 8000) CSRF korumasına sahip
- Şimdilik backend servisleri doğrudan port üzerinden test et

### Port Kontrolleri
- Eureka: 8761
- Config Server: 8888
- User Service: 8081
- Event Service: 8082
- Booking Service: 8083
- Payment Service: 8084
- Notification Service: 8085
- **API Gateway: 8000**

### Service Başlatma
```bash
cd /Users/raufkutayakyildiz/Desktop/ıntellijWs/EventPlanner

# Discovery Server + Config Server
mvn -pl discovery-server spring-boot:run &
sleep 8
mvn -pl config-server spring-boot:run &
sleep 5

# Backend Services (parallel)
mvn -pl user-service spring-boot:run &
mvn -pl event-service spring-boot:run &
mvn -pl payment-service spring-boot:run &
mvn -pl notification-service spring-boot:run &
mvn -pl booking-service spring-boot:run &
```

### Health Check
```bash
for port in 8761 8888 8081 8082 8083 8084 8085; do
  echo -n "Port $port: "
  curl -s http://localhost:$port/health 2>/dev/null | jq .status || echo "DOWN"
done
```
