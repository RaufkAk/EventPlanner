# EventPlanner — System Design (Component Design per Service)

This document contains component-level design for each microservice following the requested template.

**5.1 User Service**
- Purpose: Handles user identity management, secure registration, and authentication processes.
- Responsibilities:
  - Register new users and securely hash passwords (BCrypt).
  - Authenticate users and issue JWT (JSON Web Tokens).
  - Validate tokens for other services (Inter-service security).
  - Manage user profile information.
- Exposed APIs:
  - `POST /api/auth/register` (Create account)
  - `POST /api/auth/login` (Get JWT)
  - `GET /api/users/{id}` (Get profile)
- Data storage: PostgreSQL (users table) — Chosen for strict relational integrity and ACID compliance required for identity data.
- Dependencies: Config Server, Eureka Client.
- Scaling strategy: Stateless architecture; can be scaled horizontally behind the Gateway.

**5.2 Event Catalog Service**
- Purpose: Manages the creation, listing, and inventory tracking of events.
- Responsibilities:
  - Allow organizers to create and update events.
  - Serve high-performance read queries for event listings.
  - Manage ticket stock with concurrency controls for `availableSeats`.
- Exposed APIs:
  - `GET /api/events` (List all events)
  - `GET /api/events/{id}` (Get details)
  - `POST /api/events` (Create event - Organizer only)
  - `PUT /api/events/{id}/stock` (Internal: Update stock)
- Data storage: MongoDB (events collection) — Chosen for flexible schema (different event types) and high read throughput.
- Dependencies: Config Server, Eureka Client.
- Scaling strategy: Horizontal scaling with database sharding (if needed) for high read loads.

**5.3 Booking Service**
- Purpose: Acts as the central orchestrator for the ticket purchasing workflow.
- Responsibilities:
  - Coordinate the booking process (Saga pattern recommended for distributed transactions).
  - Communicate synchronously with User (validation), Event (stock), and Payment services.
  - Manage booking states (PENDING, CONFIRMED, CANCELLED).
  - Publish `BookingCreatedEvent` to RabbitMQ for asynchronous notifications.
- Exposed APIs:
  - `POST /api/bookings` (Place order)
  - `GET /api/bookings/{id}` (Check status)
- Data storage: MySQL (bookings table) — Relational DB used to ensure transactional consistency of orders.
- Dependencies: User Service, Event Catalog Service, Payment Service (via Feign/REST), RabbitMQ, Config Server.
- Scaling strategy: Stateless service; relies on external database and message broker for state. Use connection-pooling, idempotency keys and backoff/retry when calling external services.

**5.4 Payment Service**
- Purpose: Isolates financial transaction logic and simulates banking integrations.
- Responsibilities:
  - Process payment requests securely.
  - Record transaction logs and status (SUCCESS / FAILED).
  - Simulate external payment gateway latency/responses for testing.
- Exposed APIs:
  - `POST /api/payments/process` (Process transaction)
  - (Optional) `GET /api/payments/{id}` for queries
- Data storage: MySQL (transactions table).
- Dependencies: Config Server, Eureka Client.
- Scaling strategy: Horizontal scaling; ensure eventual consistency and safe retry logic for at-least-once processing.

**5.5 Notification Service**
- Purpose: Handles asynchronous user alerts to decouple communication latency from the main booking flow.
- Responsibilities:
  - Listen to and consume messages from `notificationQueue` (RabbitMQ).
  - Generate and send email confirmations (Simulated `JavaMailSender` or external provider).
  - Log notification history for audit purposes.
- Exposed APIs: None (Message Consumer).
- Data storage: MongoDB (notification_logs collection) — ideal for unstructured log data.
- Dependencies: RabbitMQ, Config Server.
- Scaling strategy: Scale the number of consumers based on queue depth (backpressure handling); ensure idempotent consumers and visibility timeouts.

---

Notes & Recommendations:
- Use centralized configuration (Config Server) to keep settings consistent across services.
- Use Eureka for service discovery and `lb://` URIs in the Gateway.
- Secure service-to-service communication: JWT propagation or mTLS. Prefer short-lived service tokens for inter-service calls.
- Observability: Add structured logging, distributed tracing (OpenTelemetry), and health checks for each service.
- Resilience: Circuit breakers (Resilience4j), retries and timeouts when calling other services.
