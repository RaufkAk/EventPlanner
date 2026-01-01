# EventPlanner - Microservices Event Management System

A scalable, cloud-native web application designed to facilitate the creation, discovery, and booking of events using microservices architecture.

## Project Overview

EventPlanner is built on a **Microservices Architecture** using Spring Boot, with the following core services:

- **API Gateway** (Port 8000): Single entry point for all client requests
- **User Service** (Port 8081): Identity management and authentication
- **Event Catalog Service** (Port 8082): Event creation, listing, and inventory
- **Booking Service** (Port 8083): Orchestration of ticket purchasing workflow
- **Payment Service** (Port 8084): Financial transaction processing (simulated)
- **Notification Service** (Port 8085): Asynchronous email notifications via RabbitMQ
- **Discovery Server** (Port 8761): Service registration and discovery (Eureka)
- **Config Server** (Port 8888): Centralized configuration management

## Prerequisites

- **Java 17+** (OpenJDK or Oracle JDK)
- **Maven 3.6+**
- **Docker & Docker Compose** (for infrastructure)
- **Git**

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | Spring Boot | 3.2.0 |
| Cloud | Spring Cloud | 2023.0.0 |
| Service Discovery | Netflix Eureka | Latest |
| API Gateway | Spring Cloud Gateway | Latest |
| Messaging | RabbitMQ | 3-management |
| Databases | PostgreSQL, MySQL, MongoDB | Latest |
| Build Tool | Maven | 3.6+ |
| JDK | OpenJDK/Oracle | 17 |

## Project Structure

```
EventPlanner/
├── api-gateway/                 # API Gateway Service
├── user-service/                # User Identity Service
├── event-service/               # Event Catalog Service
├── booking-service/             # Booking Orchestration Service
├── payment-service/             # Payment Processing Service
├── notification-service/        # Notification Service
├── discovery-server/            # Eureka Discovery Server
├── config-server/               # Config Server
├── docker-compose.yml           # Infrastructure containers
├── pom.xml                      # Parent POM
├── README.md                    # This file
└── SYSTEM_DESIGN.md             # System design documentation
```

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/RaufkAk/EventPlanner.git
cd EventPlanner
```

### 2. Build All Services

```bash
# Clean and build all modules
mvn clean package -DskipTests

# Or for detailed output:
mvn clean install -DskipTests
```

### 3. Start Infrastructure (Docker)

```bash
# Start PostgreSQL, MySQL, MongoDB, and RabbitMQ
docker-compose up -d

# Verify containers are running
docker-compose ps
```

### 4. Start Services in Order

Start the services in the following order:

#### A. Discovery Server (Eureka)
```bash
cd discovery-server
mvn spring-boot:run
# OR
java -jar target/discovery-server-0.0.1-SNAPSHOT.jar
```
Access Eureka Dashboard: [http://localhost:8761](http://localhost:8761)

#### B. Config Server
```bash
cd config-server
mvn spring-boot:run
# OR
java -jar target/config-server-0.0.1-SNAPSHOT.jar
```

#### C. Other Services (start in any order)
```bash
# User Service
java -jar user-service/target/user-service-0.0.1-SNAPSHOT.jar

# Event Catalog Service
java -jar event-service/target/event-service-0.0.1-SNAPSHOT.jar

# Booking Service
java -jar booking-service/target/booking-service-0.0.1-SNAPSHOT.jar

# Payment Service
java -jar payment-service/target/payment-service-0.0.1-SNAPSHOT.jar

# Notification Service
java -jar notification-service/target/notification-service-0.0.1-SNAPSHOT.jar

# API Gateway (start last)
java -jar api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar
```

## API Endpoints

All endpoints go through the **API Gateway** (port 8000):

### Authentication
- `POST /users/auth/register` - Register new user
- `POST /users/auth/login` - Login and get JWT

### Events
- `GET /events` - List all events
- `GET /events/{id}` - Get event details
- `POST /events` - Create new event (requires JWT)
- `PUT /events/{id}/stock` - Update event stock

### Bookings
- `POST /bookings` - Book ticket
- `GET /bookings/{id}` - Check booking status

### Payments
- `POST /payments` - Process payment

## Database Configuration

### PostgreSQL (User Service)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/user_db
spring.datasource.username=admin
spring.datasource.password=password
```

### MySQL (Payment Service)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/booking_db
spring.datasource.username=root
spring.datasource.password=password
```

### MongoDB (Event & Notification Services)
```properties
spring.data.mongodb.uri=mongodb://admin:password@localhost:27017/event_db?authSource=admin
```

### RabbitMQ (Messaging)
```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=password
```

## Workflow Example: Ticket Booking

1. User logs in via **User Service** → receives JWT token
2. User browses events via **Event Catalog Service**
3. User books ticket via **API Gateway** → **Booking Service**
4. **Booking Service** orchestrates:
   - Validates user (User Service)
   - Checks stock (Event Service)
   - Processes payment (Payment Service)
5. Upon success, **Booking Service** publishes event to RabbitMQ
6. **Notification Service** consumes message and sends confirmation email

## Monitoring

### Service Discovery Dashboard
- URL: [http://localhost:8761](http://localhost:8761)
- View all registered microservices
- Check health status

### RabbitMQ Management Console
- URL: [http://localhost:15672](http://localhost:15672)
- Username: `admin`
- Password: `password`

### Actuator Health Endpoints
- `http://localhost:{port}/actuator/health` - Service health
- `http://localhost:{port}/actuator/metrics` - Metrics

## Testing

### Run Unit Tests
```bash
mvn clean test
```

### Run Integration Tests
```bash
mvn clean verify
```

## Configuration Management

The project uses **Spring Cloud Config Server** for centralized configuration. 

### Config Repository
- Git URI: `https://github.com/RaufkAk/EventPlanner-Config.git`
- Configuration files are fetched at application startup

### Override Local Properties
Create `application-local.properties` in each service's `src/main/resources`:
```properties
spring.cloud.config.enabled=false
```

## Deployment

### Render Cloud Deployment

1. Push code to GitHub
2. Create web services on Render for each microservice
3. Configure environment variables:
   ```
   SPRING_DATASOURCE_URL=postgresql://...
   SPRING_DATASOURCE_USERNAME=...
   SPRING_DATASOURCE_PASSWORD=...
   EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=https://discovery-server.render.com/eureka
   ```

### Docker Deployment

Build Docker images:
```bash
mvn clean package
docker build -t eventplanner:latest .
docker run -p 8000:8000 eventplanner:latest
```

## Security

- **JWT Authentication**: All protected endpoints require valid JWT token
- **HTTPS/TLS**: Recommended for production deployment
- **Secrets Management**: Use environment variables for sensitive data
- **CORS**: Configured at API Gateway level

## Troubleshooting

### Port Already in Use
```bash
# Find and kill process using port
lsof -i :{port}
kill -9 {PID}
```

### Service Not Registering with Eureka
- Check Discovery Server is running (port 8761)
- Verify `eureka.client.service-url.defaultZone` in application.properties
- Check network connectivity

### RabbitMQ Connection Failed
- Ensure RabbitMQ container is running: `docker-compose ps`
- Check credentials in `application.properties`
- Test connection: `docker-compose exec rabbitmq rabbitmqctl status`

### MongoDB Connection Issues
- Verify MongoDB is running: `docker-compose logs mongodb`
- Check authentication credentials
- Try connecting directly: `mongosh mongodb://admin:password@localhost:27017`

## Performance Targets (NFRs)

- **Availability**: 99.9% uptime
- **Response Time**: < 300ms for read operations
- **Async Processing**: < 5 seconds for email notifications
- **Scalability**: Horizontal scaling support for all services

## File Structure After Build

```
{service}/target/
├── {service}-0.0.1-SNAPSHOT.jar       # Executable JAR
├── {service}-0.0.1-SNAPSHOT.jar.original
├── classes/                           # Compiled classes
├── generated-sources/                 # Generated code
└── maven-archiver/                    # Maven metadata
```

## Development Notes

- **H2 Database**: Used for local development in Booking, Payment, and Event services
- **MongoDB In-Memory**: Embedded for local testing
- **Mock Email**: Notifications logged to console (not actual emails)
- **Simulated Payments**: No real banking integration

## Contributing

1. Create a feature branch: `git checkout -b feature/your-feature`
2. Commit changes: `git commit -am 'Add new feature'`
3. Push to branch: `git push origin feature/your-feature`
4. Submit a Pull Request

## License

This project is licensed under the MIT License.

## Contact

**Project Team:**
- Rauf Kutay Akyıldız - Booking Service
- Samet Laçin - User Service
- Erdoğan Efe Güner - Payment Service
- Selim Can Aydın - Event Catalog Service
- Ömer Yiğit Kartal - Notification Service

**Course**: COMP 301 - Fall 2025
**University**: Yeditepe University

---

**Last Updated**: 2026-01-01
**Status**: ✅ Production Ready
