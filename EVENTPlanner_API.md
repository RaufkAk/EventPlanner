# EventPlanner — API Dokümantasyonu

Aşağıda proje içinde tespit ettiğim servisler, portlar, endpointler ve tipik kullanım akışları bulunmaktadır.

**Genel Mimari ve Portlar**
- discovery-server: 8761 (Eureka)
- config-server: 8888
- api-gateway: 8000
- user-service: 8081
- event-service: 8082
- booking-service: 8083
- payment-service: 8084
- notification-service: 8085

Not: Servisler Spring Cloud ile Eureka üzerinden keşfediliyor. API Gateway konfigürasyonu [api-gateway/src/main/resources/application.properties](api-gateway/src/main/resources/application.properties#L1-L20) dosyasında tanımlıdır.

**Gateway yönlendirmeleri (kısa)**
- `/users/**` -> `user-service`
- `/events/**` -> `event-service`
- `/bookings/**` -> `booking-service`
- `/payments/**` -> `payment-service`
(Örnek: Gateway `http://localhost:8000/bookings/api/bookings` isteğini booking-service'e iletir.)

**User Service (kullanıcı, kimlik doğrulama)** — base: `http://localhost:8081`
- `POST /api/auth/register` — kullanıcı kaydı. (AuthController) [user-service/src/main/java/com/yeditepe/controller/AuthController.java](user-service/src/main/java/com/yeditepe/controller/AuthController.java#L1-L120)
- `POST /api/auth/login` — login, JWT döner.
- `GET /api/users/{id}/validate` — kullanıcı var mı kontrolü. (UserController) [user-service/src/main/java/com/yeditepe/controller/UserController.java](user-service/src/main/java/com/yeditepe/controller/UserController.java#L1-L80)
- `GET /api/users/{id}` — kullanıcı bilgisi.

**Event Service (etkinlik yönetimi)** — base: `http://localhost:8082`
- `GET /api/events` — etkinlik listesi (filtreler: category, venue, from, to). [event-service/src/main/java/com/yeditepe/eventservice/controller/EventController.java](event-service/src/main/java/com/yeditepe/eventservice/controller/EventController.java#L1-L220)
- `GET /api/events/{id}` — etkinlik detay
- `POST /api/events` — etkinlik oluştur
- `PUT /api/events/{id}` — güncelle
- `DELETE /api/events/{id}` — sil
- `GET /api/events/{id}/stock` — mevcut boş koltuk/stock sorgulama (Booking servis tarafından Feign ile çağrılıyor)
- `PUT /api/events/{id}/reserve` — rezervasyon için koltuk ayırma
- `PUT /api/events/{id}/release` — rezervasyon iptali, koltuk serbest bırakma

**Booking Service (rezervasyonlar)** — base: `http://localhost:8083`
- `POST /api/bookings` — rezervasyon oluştur (roller: USER veya ADMIN). Akış:
  1. `UserServiceClient` ile `GET /api/users/{id}/validate` -> kullanıcı geçerli mi?
  2. `EventServiceClient` ile `GET /api/events/{eventId}/stock` -> stoğu kontrol et.
  3. `EventServiceClient` ile `PUT /api/events/{eventId}/reserve` -> koltuk ayır.
  4. `PaymentServiceClient` ile `POST /api/payments/process` -> ödeme işle.
  5. Rezervasyon kaydedilir ve `BookingEventPublisher` ile RabbitMQ'ya `BookingCreatedEvent` publish edilir.
- `GET /api/bookings/{id}` — (ADMIN) rezervasyon getir
- `GET /api/bookings/user/{userId}` — kullanıcının rezervasyonları
- `GET /api/bookings/event/{eventId}` — (ADMIN) etkinliğin rezervasyonları
(BookingController kaynak: [booking-service/src/main/java/com/yeditepe/bookingservice/controller/BookingController.java](booking-service/src/main/java/com/yeditepe/bookingservice/controller/BookingController.java#L1-L200))

**Payment Service** — base: `http://localhost:8084`
- `POST /api/payments/process` — ödeme işle (Booking servis Feign ile çağırıyor). [payment-service/src/main/java/com/yeditepe/paymentservice/controller/PaymentController.java](payment-service/src/main/java/com/yeditepe/paymentservice/controller/PaymentController.java#L1-L220)
- `GET /api/payments/{paymentId}`
- `GET /api/payments/booking/{bookingId}`
- `GET /api/payments/transaction/{transactionId}`
- `GET /api/payments/status/{status}`
- `POST /api/payments/{paymentId}/refund`
- Analitik endpointleri: `/analytics/total-completed` vb.

**Notification Service (RabbitMQ tüketici)** — base: `http://localhost:8085`
- `BookingEventConsumer` RabbitMQ kuyruğundan `BookingCreatedEvent` tüketir ve bildirim işler. (notification-service/src/main/java/com/yeditepe/notificationservice/consumer/BookingEventConsumer.java)

---

Örnek rezervasyon akışı (adım adım, gateway üzerinden veya servislere doğrudan):

1) Kullanıcı kaydı ve login
- Kaydol (doğrudan user-service):

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass","email":"u@e.com","firstName":"Foo","lastName":"Bar","roles":["USER"]}'
```

- Login (JWT al):

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass"}'
# JSON yanıt => { token: "<jwt>", ... }
```

2) Etkinlik stoğunu kontrol et (örnek):
- Doğrudan event-service:

```bash
curl http://localhost:8082/api/events/{eventId}/stock
```

- Veya gateway üzerinden:

```bash
curl http://localhost:8000/events/api/events/{eventId}/stock
```

3) Rezervasyon oluşturma (Booking akışı). Örnek JSON `BookingRequest`'a göre:

```bash
curl -X POST http://localhost:8000/bookings/api/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT>" \
  -d '{
    "userId": 1,
    "eventId": "evt-123",
    "seats": 2,
    "paymentInfo": { "cardNumber":"4242...", "expiry":"..." }
  }'
```

Booking servisi arka planda:
- user-service ile kullanıcı doğrulaması
- event-service ile stok kontrol + rezervasyon
- payment-service ile ödeme
- RabbitMQ'ya `BookingCreatedEvent` publish -> notification-service tüketir

4) Bildirimler
- notification-service RabbitMQ kuyruğundan mesaj alır ve e-posta/SMS veya log yoluyla bildirimi işler.

---

Hatalar ve güvenlik
- `BookingController` role tabanlı güvenlik (JWT ile Authorization header gerekir). `AuthController` JWT üretir.
- Service-to-service iletişim Feign client ile sağlanıyor (booking -> user/event/payment).
- Mesajlaşma RabbitMQ ile asenkron bildirim sağlanıyor.

---

Kaynak kod referansları (özet)
- Booking controller: [booking-service/src/main/java/com/yeditepe/bookingservice/controller/BookingController.java](booking-service/src/main/java/com/yeditepe/bookingservice/controller/BookingController.java#L1-L120)
- Event controller: [event-service/src/main/java/com/yeditepe/eventservice/controller/EventController.java](event-service/src/main/java/com/yeditepe/eventservice/controller/EventController.java#L1-L220)
- Payment controller: [payment-service/src/main/java/com/yeditepe/paymentservice/controller/PaymentController.java](payment-service/src/main/java/com/yeditepe/paymentservice/controller/PaymentController.java#L1-L220)
- User auth: [user-service/src/main/java/com/yeditepe/controller/AuthController.java](user-service/src/main/java/com/yeditepe/controller/AuthController.java#L1-L120)
- Booking event publisher: [booking-service/src/main/java/com/yeditepe/bookingservice/messaging/BookingEventPublisher.java](booking-service/src/main/java/com/yeditepe/bookingservice/messaging/BookingEventPublisher.java#L1-L80)
- Notification consumer: [notification-service/src/main/java/com/yeditepe/notificationservice/consumer/BookingEventConsumer.java](notification-service/src/main/java/com/yeditepe/notificationservice/consumer/BookingEventConsumer.java#L1-L120)

---

İlerisi için öneriler
- API Gateway'e auth (JWT doğrulama) ve `/api/auth` route ekleyin (kullanıcı login/register gateway üzerinden yapılsın).
- Swagger/OpenAPI ekleyerek endpoint dokümantasyonunu otomatikleştirin.
- Örnek `docker-compose.yml` içindeki servis portlarını kontrol edin; RabbitMQ, PostgreSQL gibi bağımlılıkların çalıştığından emin olun.

---

İsterseniz şimdi:
- Bu dokümantasyonu genişletip `README` veya Swagger/spec haline getireyim, veya
- Örnek bir `curl` çalıştırıp (localde servisler çalışıyorsa) rezervasyon akışını testi edeyim.

