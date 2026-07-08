# BusGo Backend API Documentation

Welcome to the BusGo API! This document outlines all available `/v1` endpoints.

> **Live Testing**
> You can test all of these endpoints interactively using the live Swagger UI dashboard by navigating to `http://localhost:8080/swagger-ui.html` while the server is running.

---

## 1. Authentication Endpoints

### Register User
**`POST /v1/auth/register`**
Registers a new user and returns a JWT token.
**Body:**
```json
{
  "fullName": "John Doe",
  "email": "john@email.com",
  "phone": "+234800000000",
  "password": "SecurePassword123"
}
```

### Login
**`POST /v1/auth/login`**
Authenticates a user and returns a JWT token.
**Body:**
```json
{
  "email": "john@email.com",
  "password": "SecurePassword123"
}
```

### Refresh Token
**`POST /v1/auth/refresh`**
Uses a valid refresh token to issue a new set of access and refresh tokens.
**Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUz..."
}
```

### Webhook Verification
**`POST /v1/payments/webhook`**
(Secure endpoint for Paystack Server-to-Server callbacks)

### Forgot Password
**`POST /v1/auth/forgot-password`**
Generates a 6-digit reset code and emails it to the user.
**Body:**
```json
{
  "email": "user@example.com"
}
```

### Reset Password
**`POST /v1/auth/reset-password`**
Consumes the 6-digit reset code and sets a new password.
**Body:**
```json
{
  "token": "123456",
  "newPassword": "newSecurePassword123"
}
```

### OTP & Password Management (Mocks)
- **`POST /v1/auth/send-otp`** (Body: `{"phone": "+234..."}`)
- **`POST /v1/auth/verify-otp`** (Body: `{"phone": "...", "otp": "..."}`)

---

## 2. Procedural Search Engine

### Search Buses
**`GET /v1/buses/search`**
Procedurally generates deterministic bus schedules based on your search criteria.
**Query Parameters:**
- `origin` (string) - e.g., "Lagos"
- `destination` (string) - e.g., "Abuja"
- `date` (string) - e.g., "2026-07-15"
- `passengers` (int) - e.g., 1

### Get Seat Layout
**`GET /v1/buses/{busId}/seats`**
Generates a 41-seater layout for the specified bus, with randomized booked seats.

---

## 3. Bookings & Trips

> **Authentication Required**
> All endpoints below require a valid JWT token sent in the `Authorization: Bearer <token>` header.

### Create Booking
**`POST /v1/bookings`**
Creates a new booking reservation.
**Body:**
```json
{
  "busId": "bus_123456",
  "seatIds": ["seat_3_1"],
  "passengers": 1,
  "paymentMethod": "paystack"
}
```

### Cancel Booking
**`POST /v1/bookings/{bookingId}/cancel`**
Cancels an existing booking and initiates a refund.

### Get User Trips
**`GET /v1/trips`**
Returns the user's entire booking history.

---

## 4. Wallet System

### Get Balance
**`GET /v1/wallet/balance`**
Returns the user's current wallet balance (Mocked to ₦45,000).

### Get Transactions
**`GET /v1/wallet/transactions`**
Returns a ledger of all historical wallet credits and debits.

### Fund Wallet
**`POST /v1/wallet/fund`**
Adds funds to the user's wallet via Paystack mock integration.

---

## 5. Miscellaneous

### Supported Cities
**`GET /v1/cities`**
Returns the static list of all supported Nigerian cities for dropdown menus.

### Live Bus Tracking
**`GET /v1/tracking/{tripId}`**
Returns live GPS coordinates, speed, and driver information for an active trip.

---

## Global Error Handling
All errors across the entire API will be caught and returned in the exact following JSON shape:
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Detailed error message here",
    "details": null
  }
}
```
