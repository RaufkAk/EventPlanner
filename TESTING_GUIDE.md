# EventPlanner - Testing Guide

## 🎯 Proje Sunum Öncesi Test Prosedürü

Bu rehberi takip ederek tüm servisleri ve workflow'ları adım adım test edebilirsiniz.

---

## 📋 Ön Koşullar

---

### Option 2: Maven spring-boot:run ile Başlatma (Doğrudan)

#### 1. Her Servisi Ayrı Terminal'de Çalıştır

```bash
# Terminal 1: Discovery Server
cd /Users/raufkutayakyildiz/Desktop/ıntellijWs/EventPlanner/discovery-server
mvn spring-boot:run
```

```bash
# Terminal 2: User Service
cd /Users/raufkutayakyildiz/Desktop/ıntellijWs/EventPlanner/user-service
mvn spring-boot:run
```

```bash
# Terminal 3: Event Service
cd /Users/raufkutayakyildiz/Desktop/ıntellijWs/EventPlanner/event-service
mvn spring-boot:run
```

```bash
# Terminal 4: Booking Service
cd /Users/raufkutayakyildiz/Desktop/ıntellijWs/EventPlanner/booking-service
mvn spring-boot:run
```

```bash
# Terminal 5: Payment Service
cd /Users/raufkutayakyildiz/Desktop/ıntellijWs/EventPlanner/payment-service
mvn spring-boot:run
```

```bash
# Terminal 6: Notification Service
cd /Users/raufkutayakyildiz/Desktop/ıntellijWs/EventPlanner/notification-service
mvn spring-boot:run
```

**Avantajlar:**
- ✅ Doğrudan kaynak koddan çalışır
- ✅ Build adımı her çalışmada yapılır
- ✅ Değişiklikleri hemen test edebilirsiniz

**Dezavantajlar:**
- ❌ Her servinin kendi terminal'i gerekli
- ❌ Build süresi daha uzun

---

### Option 3: Maven ile Build + JAR ile Run (Önerilen)

#### Step 1: Root Klasöre Git

```bash
cd /Users/raufkutayakyildiz/Desktop/ıntellijWs/EventPlanner
```

#### Step 2: Tüm Projeleri Build Et

```bash
mvn clean install -DskipTests
```

**Çıktı:**
```
[INFO] BUILD SUCCESS
...
[INFO] Total time: 2.45 s
```

#### Step 3: Her Servisi Çalıştır (Ayrı Terminal'ler)

**Terminal 1 - Discovery Server:**
```bash
cd /Users/raufkutayakyildiz/Desktop/ıntellijWs/EventPlanner
java -jar discovery-server/target/discovery-server-0.0.1-SNAPSHOT.jar
```

**Terminal 2 - User Service:**
```bash
cd /Users/raufkutayakyildiz/Desktop/ıntellijWs/EventPlanner
java -jar user-service/target/user-service-0.0.1-SNAPSHOT.jar
```

**Terminal 3 - Event Service:**
```bash
cd /Users/raufkutayakyildiz/Desktop/ıntellijWs/EventPlanner
java -jar event-service/target/event-service-0.0.1-SNAPSHOT.jar
```

**Terminal 4 - Booking Service:**
```bash
cd /Users/raufkutayakyildiz/Desktop/ıntellijWs/EventPlanner
java -jar booking-service/target/booking-service-0.0.1-SNAPSHOT.jar
```

**Terminal 5 - Payment Service:**
```bash
cd /Users/raufkutayakyildiz/Desktop/ıntellijWs/EventPlanner
java -jar payment-service/target/payment-service-0.0.1-SNAPSHOT.jar
```

**Terminal 6 - Notification Service:**
```bash
cd /Users/raufkutayakyildiz/Desktop/ıntellijWs/EventPlanner
java -jar notification-service/target/notification-service-0.0.1-SNAPSHOT.jar
```

---

### Option 4: Komut Satırında Tüm Servisleri Başlat (Arka Planda)

```bash
cd /Users/raufkutayakyildiz/Desktop/ıntellijWs/EventPlanner && \
echo "Building all services..." && \
mvn clean install -DskipTests && \
echo "Starting services..." && \
java -jar discovery-server/target/discovery-server-0.0.1-SNAPSHOT.jar > /tmp/discovery.log 2>&1 & \
sleep 5 && \
java -jar user-service/target/user-service-0.0.1-SNAPSHOT.jar > /tmp/user.log 2>&1 & \
java -jar event-service/target/event-service-0.0.1-SNAPSHOT.jar > /tmp/event.log 2>&1 & \
java -jar booking-service/target/booking-service-0.0.1-SNAPSHOT.jar > /tmp/booking.log 2>&1 & \
java -jar payment-service/target/payment-service-0.0.1-SNAPSHOT.jar > /tmp/payment.log 2>&1 & \
java -jar notification-service/target/notification-service-0.0.1-SNAPSHOT.jar > /tmp/notification.log 2>&1 & \
sleep 10 && \
echo "✅ All services started!"
```

---

## 🛠️ Maven Komutları Referans

| Komut | Ne Yapar |
|-------|----------|
| `mvn clean` | Target klasörünü temizler |
| `mvn compile` | Sadece compile eder |
| `mvn test` | Testleri çalıştırır |
| `mvn install` | JAR/WAR oluşturur ve .m2'ye kaydeder |
| `mvn package` | JAR/WAR oluşturur |
| `mvn clean install` | Temizle + Build + Install |
| `mvn clean install -DskipTests` | Build et ama testleri atla |
| `mvn spring-boot:run` | Spring Boot uygulamasını çalıştır |
| `mvn clean package` | Temizle + JAR'ı oluştur |

---

## 📊 Seçim Rehberi

| Seçenek | Ne Zaman Kullan | Süre |
|--------|-----------------|------|
| **Option 1** | İlk test ve sunum | 3 min |
| **Option 2** | Geliştirme sırasında | 2 min |
| **Option 3** (Önerilen) | Sunum öncesi | 3 min |
| **Option 4** | Hızlı test | 3 min |

---

## 📋 Ön Koşullar



```bash
# Terminal 1: Discovery Server
java -jar discovery-server/target/discovery-server-0.0.1-SNAPSHOT.jar &

# Terminal 2: User Service
java -jar user-service/target/user-service-0.0.1-SNAPSHOT.jar &

# Terminal 3: Event Service
java -jar event-service/target/event-service-0.0.1-SNAPSHOT.jar &

# Terminal 4: Booking Service
java -jar booking-service/target/booking-service-0.0.1-SNAPSHOT.jar &

# Terminal 5: Payment Service
java -jar payment-service/target/payment-service-0.0.1-SNAPSHOT.jar &

# Terminal 6: Notification Service
java -jar notification-service/target/notification-service-0.0.1-SNAPSHOT.jar &
```

**Bekleme süresi:** ~15 saniye (tüm servislerin Eureka'ya kayıtlanması için)

---

## 🧪 Test Adımları

### STEP 0: User Bilgilerini Kontrol Et

**Port:** 8081  
**Method:** GET  
**Endpoint:** `/api/users/{userId}`

```bash
curl http://localhost:8081/api/users/1 | jq '.'
```

**Beklenen Response:**
```json
{
  "username": "user1",
  "email": "user1@eventplanner.com",
  "firstName": "User",
  "lastName": "One"
}
```

**Neler test ediyoruz:**
- ✅ User Service çalışıyor mu?
- ✅ Kullanıcı bilgileri döndürülüyor mu?
- ✅ User validation işlevi var mı?

**Not:** Farklı userId'ler deneyin: 1, 5, 10, 15 vb.

---

### STEP 1: Event Oluştur

**Port:** 8082  
**Method:** POST  
**Endpoint:** `/api/events`

```bash
curl -X POST http://localhost:8082/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Spring Conference 2026",
    "description": "Largest Spring conference in Turkey",
    "date": "2026-05-20T09:00:00",
    "location": "Istanbul, Turkey",
    "capacity": 1000,
    "availableSeats": 1000,
    "price": 500.00
  }' | jq '.'
```

**Beklenen Response:**
```json
{
  "id": "UUID-XXXXX",
  "title": "Spring Conference 2026",
  "date": "2026-05-20T09:00:00",
  "availableSeats": 1000,
  "price": 500.00
}
```

**Neler test ediyoruz:**
- ✅ Event Service çalışıyor mu?
- ✅ Veritabanına kayıt yapılıyor mu?
- ✅ UUID generation çalışıyor mu?

---

### STEP 2: Event Listele

**Port:** 8082  
**Method:** GET  
**Endpoint:** `/api/events`

```bash
curl http://localhost:8082/api/events | jq '.'
```

**Beklenen Response:**
```json
[
  {
    "id": "UUID-XXXXX",
    "title": "Spring Conference 2026",
    ...
  }
]
```

**Neler test ediyoruz:**
- ✅ Event listesi döndürülüyor mu?
- ✅ Oluşturduğumuz event var mı?

---

### STEP 3: Booking Oluştur (Ana Test)

**Port:** 8083  
**Method:** POST  
**Endpoint:** `/api/bookings`

```bash
# EVENT_ID'yi STEP 1'den aldığınız UUID ile değiştirin
curl -X POST http://localhost:8083/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "eventId": "BURAYA-UUID-GEÇİN",
    "numberOfTickets": 2
  }' | jq '.'
```

**Beklenen Response:**
```json
{
  "id": 1,
  "userId": 1,
  "eventId": "UUID-XXXXX",
  "status": "CONFIRMED",
  "bookingDate": "2026-01-05T19:30:12.305688"
}
```

**Neler test ediyoruz:**
- ✅ Booking Service RabbitMQ ile haberleşiyor mu?
- ✅ Payment Service başarıyla yanıt veriyor mu?
- ✅ Status PENDING → CONFIRMED geçişi yapılıyor mu?
- ✅ Database'e yazılıyor mu?

---

### STEP 4: Email Gönderimini Kontrol Et

**Port:** 8085  
**Method:** LOG CHECK  
**Command:**

```bash
# Notification Service loglarını takip et
tail -50 /tmp/notification.log | grep -iE "email|sent|successfully"
```

**Beklenen Çıktı:**
```
✅ Email SUCCESSFULLY SENT to: user1@eventplanner.com
📧 Subject: Booking Confirmation - Event Spring Conference 2026
✅ Email başarıyla gönderildi!
```

**Neler test ediyoruz:**
- ✅ RabbitMQ mesajı başarıyla konsumed mi?
- ✅ Notification Service email gönderdiği mi?
- ✅ Mailtrap SMTP entegrasyonu çalışıyor mu?
- ✅ MongoDB'ye log kaydediliyor mu?

---

### STEP 5: Payment İşlemini Kontrol Et

**Port:** 8084  
**Method:** GET  
**Endpoint:** `/api/payments/booking/{bookingId}`

```bash
curl http://localhost:8084/api/payments/booking/1 | jq '.'
```

**Beklenen Response:**
```json
{
  "id": 1,
  "bookingId": 1,
  "amount": 100.0,
  "status": "COMPLETED",
  "paymentMethod": "CREDIT_CARD",
  "transactionId": "TXN-XXXXXX",
  "paymentDate": "2026-01-05T19:30:12.351811"
}
```

**Neler test ediyoruz:**
- ✅ Payment Service işlem yapıyor mu?
- ✅ Transaction ID oluşturuluyor mu?
- ✅ Status COMPLETED mı?

---

### STEP 6: Booking Detaylarını Kontrol Et

**Port:** 8083  
**Method:** GET  
**Endpoint:** `/api/bookings/{bookingId}`

```bash
curl http://localhost:8083/api/bookings/1 | jq '.'
```

**Beklenen Response:**
```json
{
  "id": 1,
  "userId": 1,
  "eventId": "UUID-XXXXX",
  "status": "CONFIRMED",
  "bookingDate": "2026-01-05T19:30:12.305688"
}
```

**Neler test ediyoruz:**
- ✅ Booking database'de kaydedilmiş mi?
- ✅ Status CONFIRMED mu?

---

### STEP 7: Eureka Service Discovery Kontrol Et

**Port:** 8761  
**Method:** GET  
**Endpoint:** `/eureka/apps`

```bash
curl http://localhost:8761/eureka/apps | grep "<name>" | head -10
```

**Beklenen Çıktı:**
```
<name>BOOKING-SERVICE</name>
<name>EVENT-SERVICE</name>
<name>NOTIFICATION-SERVICE</name>
<name>PAYMENT-SERVICE</name>
<name>USER-SERVICE</name>
```

**Neler test ediyoruz:**
- ✅ Tüm servislerin Discovery'ye kaydedilmiş mi?
- ✅ Service locator çalışıyor mu?

---

## 📊 Akış Kontrol Listesi

### End-to-End Workflow
- [ ] Event oluşturuldu
- [ ] Event listede görünüyor
- [ ] Booking oluşturuldu
- [ ] Status CONFIRMED
- [ ] Payment işlendi (COMPLETED)
- [ ] Email gönderildi
- [ ] MongoDB'ye log kaydedildi
- [ ] Eureka'da tüm servisleri görüyoruz

### Database Kontrolleri
- [ ] PostgreSQL: Bookings tablosu
- [ ] PostgreSQL: Events tablosu
- [ ] MongoDB: notification_logs collection

### Async Message Flow
- [ ] RabbitMQ: Message published
- [ ] RabbitMQ: Message consumed
- [ ] Notification Service: Email sent
- [ ] Mailtrap: Email görünüyor

---

## ⚠️ Sorun Giderme

### Email Gelmedi
```bash
# Notification Service loglarını kontrol et
tail -100 /tmp/notification.log | grep -iE "error|exception"
```

### Booking Status PENDING kaldı
```bash
# Payment Service loglarını kontrol et
tail -100 /tmp/payment.log | grep -iE "payment|error"
```

### Service Discovery'de kayıt yok
```bash
# Eureka loglarını kontrol et
tail -50 discovery-server.log | grep -iE "registered|error"
```

### RabbitMQ mesaj almadı
```bash
# Booking Service loglarını kontrol et
tail -100 /tmp/booking.log | grep -iE "rabbitmq|published"
```

---

## 🎬 Demo Sırasında Söylenecek Şeyler

1. **Event Creation (STEP 1-2)**
   > "Önce bir etkinlik oluşturuyoruz. Event Service PostgreSQL'de kaydediliyor ve UUID ile kimlik alıyor."

2. **Booking Creation (STEP 3)**
   > "Kullanıcı bilet satın almak istiyor. Booking Service'e istek gidiyor. Sistem otomatik olarak ödeme işliyor."

3. **Payment Processing**
   > "Ödeme başarılı olursa, booking status PENDING'den CONFIRMED'a geçiyor."

4. **RabbitMQ Publishing**
   > "Booking CONFIRMED olunca, asenkron bir event RabbitMQ'ya gönderiliyor."

5. **Email Notification**
   > "Notification Service bu event'i tüketiyor ve Mailtrap üzerinden email gönderiliyor."

6. **Service Discovery**
   > "Tüm servislerin Eureka'da dinamik olarak kayıtlandığı görülüyor. API Gateway gerektiğinde onları bulabiliyor."

---

## ✅ Başarılı Demo Göstergeleri

- ✅ Tüm servisleri başlatmak 15 saniye
- ✅ Event oluşturmak 1 saniye
- ✅ Booking oluşturmak 3-5 saniye
- ✅ Email gelmek 2-3 saniye
- ✅ Tüm endpoint'ler 200 OK response dönüyor
- ✅ Eureka'da 5 servis görünüyor
- ✅ RabbitMQ mesaj akışı hatasız

---

## 🚀 Quick Start Script

```bash
#!/bin/bash

echo "🚀 Starting all services..."
java -jar discovery-server/target/discovery-server-0.0.1-SNAPSHOT.jar > /tmp/discovery.log 2>&1 &
sleep 5
java -jar user-service/target/user-service-0.0.1-SNAPSHOT.jar > /tmp/user.log 2>&1 &
java -jar event-service/target/event-service-0.0.1-SNAPSHOT.jar > /tmp/event.log 2>&1 &
java -jar booking-service/target/booking-service-0.0.1-SNAPSHOT.jar > /tmp/booking.log 2>&1 &
java -jar payment-service/target/payment-service-0.0.1-SNAPSHOT.jar > /tmp/payment.log 2>&1 &
java -jar notification-service/target/notification-service-0.0.1-SNAPSHOT.jar > /tmp/notification.log 2>&1 &

sleep 10
echo "✅ Services started. Ready for testing!"
```

---

**İyi sunumlar! 🎯**
