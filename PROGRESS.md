# BusGo Backend API - Development Progress & Unfiltered Planned Phases

## Current Progress
- **Phases 1, 2, 3, and 4 Complete**: 
  - Spring Boot project initialized (Java 17, Spring Boot 3.3.1).
  - Dependencies configured (MapStruct, Swagger/OpenAPI, JJWT, PostgreSQL).
  - Global Exception Handling & Swagger UI implemented.
  - Core JPA Database Entities (Users, Operators, Routes, Buses, Seats, Bookings) fully mapped.
  - JWT Security & Authentication Layer implemented.
  - Route Discovery & Seat Map APIs completed (Localized to Nigerian Mock Data).
- **Phase 5 Up Next**:
  - Booking Workflow & Seat Locking mechanism.

---

## UNFILTERED Planned Phases

### Phase 1: Project Initialization & Foundation (COMPLETED)
- Generate Spring Boot project (Web, JPA, Security, PostgreSQL, Validation, Lombok, Actuator)
- Configure `application.yml` and basic properties
- Setup `docker-compose.yml` for PostgreSQL
- Establish global exception handling framework
- Configure Swagger UI (Springdoc OpenAPI)

### Phase 2: Database Schema & Core Entities (COMPLETED)
- Create PostgreSQL migration scripts (Flyway)
- Setup core JPA Entities (User, Operator, Route, Bus, Schedule, Seat, Booking, Passenger, Payment, Ticket, Review, WalletTransaction, Notification, SeatLock)
- Set up Spring Data JPA repositories for all entities.

### Phase 3: JWT Security & User Profiles (COMPLETED)
- Implement Spring Security configuration for full app (JWT Filters).
- User registration and login with BCrypt password hashing.
- JWT token generation, parsing, and verification logic.
- User profile management REST endpoints (`GET /users/me`, `PUT /users/me`).

### Phase 4: Route Discovery & Search (COMPLETED)
- Implement Route and Schedule searching REST API (`GET /api/v1/search/schedules`).
- Schedule details endpoint (`GET /api/v1/schedules/:id`).
- Seat availability and layout endpoint (`GET /api/v1/schedules/:id/seats`).

### Phase 5: Booking Workflow & Seat Locking (UP NEXT)
- Implement `POST /bookings` endpoint to create a pending booking.
- Implement Seat Locking mechanism with a 10-minute TTL (optimistic locking/concurrency control).
- Scheduled background task to automatically release expired seat locks.
- Booking management endpoints (view user's bookings, cancel a booking).

### Phase 6: Payments, QR Tickets & Integrations
- Payment initiation logic and Webhook stubs for Stripe/Paystack/Flutterwave.
- Digital E-Ticket generation (QR code generation using ZXing).
- Link Email and SMS mock services to trigger upon successful payment and ticket generation.

### Phase 7: Advanced Features
- Wallet transactions (handling auto-credits on cancellations).
- Bus live tracking coordinates mock endpoint.
- Reviews and ratings submission logic.

### Phase 8: Polish & Testing
- Unit and integration tests (JUnit, Mockito, MockMvc).
- Final manual verification and walkthrough via Swagger UI.
