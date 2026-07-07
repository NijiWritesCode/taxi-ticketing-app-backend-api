# Bruzo API Contract Specification

> Hand this document to whoever is building the Spring Boot backend.  
> Every endpoint, request body, and response shape the mobile app expects is documented here.

---

## Base Configuration

| Key | Value |
|-----|-------|
| **Base URL** | `https://api.bruzo.ng/v1` (or `http://localhost:8080/v1` for local dev) |
| **Auth** | Bearer token in `Authorization` header |
| **Content-Type** | `application/json` |
| **Currency** | All monetary values in **Naira as integers** (₦25,000 = `25000`). |

---

## 1. Authentication

### `POST /auth/register`

Register a new user account.

**Request:**
```json
{
  "fullName": "Adebayo Johnson",
  "email": "adebayo@email.com",
  "phone": "+2348012345678",
  "password": "Secure123"
}
```

**Response (201):**
```json
{
  "user": {
    "id": "usr_abc123",
    "fullName": "Adebayo Johnson",
    "email": "adebayo@email.com",
    "phone": "+2348012345678",
    "avatarUrl": null,
    "nextOfKinPhone": null,
    "nextOfKinName": null,
    "nin": null,
    "dateOfBirth": null,
    "gender": null,
    "kycComplete": false
  },
  "token": "eyJhbGciOiJIUzI1NiIs..."
}
```

---

### `POST /auth/login`

**Request:**
```json
{
  "email": "adebayo@email.com",
  "password": "Secure123"
}
```

**Response (200):**
```json
{
  "user": { /* same User shape as above */ },
  "token": "eyJhbGciOiJIUzI1NiIs..."
}
```

---

### `POST /auth/send-otp`

Send OTP to phone for verification.

**Request:**
```json
{
  "phone": "+2348012345678"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "OTP sent successfully"
}
```

---

### `POST /auth/verify-otp`

**Request:**
```json
{
  "phone": "+2348012345678",
  "otp": "123456"
}
```

**Response (200):**
```json
{
  "user": { /* User shape */ },
  "token": "eyJhbGciOiJIUzI1NiIs..."
}
```

---

### `POST /auth/forgot-password`

**Request:**
```json
{
  "email": "adebayo@email.com"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Password reset link sent to your email"
}
```

---

## 2. User Profile

### `GET /user/profile`

🔒 Requires Bearer token.

**Response (200):**
```json
{
  "id": "usr_abc123",
  "fullName": "Adebayo Johnson",
  "email": "adebayo@email.com",
  "phone": "+2348012345678",
  "avatarUrl": "https://...",
  "nextOfKinPhone": "+2348099998888",
  "nextOfKinName": "Folake Johnson",
  "nin": "12345678901",
  "dateOfBirth": "1995-03-15",
  "gender": "male",
  "kycComplete": true
}
```

---

### `PATCH /user/profile`

🔒 Requires Bearer token. Update any subset of profile fields.

**Request:**
```json
{
  "nextOfKinPhone": "+2348099998888",
  "nextOfKinName": "Folake Johnson",
  "nin": "12345678901"
}
```

**Response (200):** Returns the full updated `User` object.

---

## 3. Bus Search (⭐ Procedural Generation Endpoint)

### `GET /buses/search`

This is the **core endpoint** that uses procedural generation on the backend. The Spring Boot service should hash `(origin + destination + date)` into a seed and deterministically generate varied results.

**Query Parameters:**

| Param | Type | Required | Example |
|-------|------|----------|---------|
| `origin` | string | ✅ | `Lagos` |
| `destination` | string | ✅ | `Abuja` |
| `date` | string (YYYY-MM-DD) | ✅ | `2026-07-15` |
| `passengers` | integer | ✅ | `1` |

**Example:** `GET /buses/search?origin=Lagos&destination=Abuja&date=2026-07-15&passengers=1`

**Response (200):**
```json
{
  "origin": "Lagos",
  "destination": "Abuja",
  "date": "2026-07-15",
  "results": [
    {
      "id": "bus_a7f3x2",
      "operator": {
        "id": "op_gigm",
        "name": "GIGM",
        "logoUrl": null,
        "rating": 4.8,
        "totalReviews": 1245
      },
      "departureTime": "06:30 AM",
      "arrivalTime": "05:00 PM",
      "duration": "10h 30m",
      "price": 25000,
      "currency": "₦",
      "busType": "AC Jet",
      "totalSeats": 41,
      "availableSeats": 12,
      "amenities": {
        "ac": true,
        "wifi": false,
        "usb": true,
        "toilet": false,
        "food": false,
        "blanket": false,
        "water": false
      },
      "isFastest": true,
      "isCheapest": false
    }
  ]
}
```

> **Procedural generation rules for the backend:**
> 1. Hash `origin + destination + date` → deterministic seed
> 2. Use seed to pick 3–8 buses from a pool of ~10 operators
> 3. Scale prices by approximate distance between cities
> 4. Vary departure times across the day (6 AM – 10 PM)
> 5. Mark one as `isFastest` (shortest duration) and one as `isCheapest` (lowest price)
> 6. Same query = same results every time (deterministic)

---

## 4. Seat Layout

### `GET /buses/{busId}/seats`

Returns the seat layout for a specific bus.

> This can also be procedurally generated from the `busId` + `totalSeats` from the search result. Use the bus ID as a seed to mark 30-70% of seats as "booked".

**Response (200):**
```json
{
  "busId": "bus_a7f3x2",
  "layout": {
    "id": "layout_1",
    "name": "Toyota Hiace Standard",
    "rows": 12,
    "columns": 5,
    "seats": [
      {
        "id": "seat_1a",
        "row": 0,
        "column": 0,
        "status": "available",
        "type": "driver",
        "price": 0,
        "label": "Driver"
      },
      {
        "id": "seat_2a",
        "row": 1,
        "column": 0,
        "status": "booked",
        "type": "standard",
        "price": 25000,
        "label": "1A"
      },
      {
        "id": "seat_2b",
        "row": 1,
        "column": 1,
        "status": "available",
        "type": "standard",
        "price": 25000,
        "label": "1B"
      }
    ]
  }
}
```

**Seat Status values:** `"available"` | `"booked"`  
**Seat Type values:** `"standard"` | `"vip"` | `"driver"` | `"empty"`

---

## 5. Booking / Checkout

### `POST /bookings`

🔒 Requires Bearer token. Creates a new booking after payment.

**Request:**
```json
{
  "busId": "bus_a7f3x2",
  "seatIds": ["seat_3a"],
  "passengers": 1,
  "promoCode": null,
  "paymentMethod": "paystack",
  "paymentReference": "PSK_ref_abc123xyz"
}
```

**Response (201):**
```json
{
  "booking": {
    "id": "trip_x9f2k",
    "pnr": "BRZ-8F2X9A",
    "status": "upcoming",
    "origin": {
      "city": "Lagos",
      "code": "LOS",
      "time": "06:30 AM",
      "date": "15 Jul 2026"
    },
    "destination": {
      "city": "Abuja",
      "code": "ABV",
      "time": "05:00 PM",
      "date": "15 Jul 2026"
    },
    "duration": "10h 30m",
    "ticketDetails": {
      "terminal": "T1",
      "gate": "A",
      "seatNumber": "3A",
      "class": "Economy"
    },
    "passenger": {
      "id": "usr_abc123",
      "fullName": "Adebayo Johnson",
      "email": "adebayo@email.com",
      "phone": "+2348012345678"
    },
    "farePaid": 26250,
    "colorScheme": "red"
  }
}
```

> The `colorScheme` field (`"red"` | `"blue"` | `"lime"` | `"darkGreen"`) is used by the frontend for visual card theming. The backend can assign it randomly or cycle through the options.

---

### `POST /bookings/{bookingId}/cancel`

🔒 Requires Bearer token.

**Response (200):**
```json
{
  "id": "trip_x9f2k",
  "status": "cancelled",
  "refundAmount": 26250,
  "refundStatus": "processing"
}
```

---

## 6. Trips (User's Booking History)

### `GET /trips`

🔒 Requires Bearer token. Returns all trips for the authenticated user.

**Query Parameters:**

| Param | Type | Required | Example |
|-------|------|----------|---------|
| `status` | string | ❌ | `upcoming`, `completed`, `cancelled` |

**Response (200):**
```json
{
  "trips": [
    {
      "id": "trip_x9f2k",
      "pnr": "BRZ-8F2X9A",
      "status": "upcoming",
      "origin": {
        "city": "Lagos",
        "code": "LOS",
        "time": "07:00 AM",
        "date": "24 Jun 2026"
      },
      "destination": {
        "city": "Abuja",
        "code": "ABV",
        "time": "06:00 PM",
        "date": "24 Jun 2026"
      },
      "duration": "11h 00m",
      "ticketDetails": {
        "terminal": "T1",
        "gate": "A",
        "seatNumber": "12A",
        "class": "Business"
      },
      "passenger": { /* User shape */ },
      "farePaid": 11950,
      "colorScheme": "red"
    }
  ]
}
```

---

## 7. Wallet

### `GET /wallet/balance`

🔒 Requires Bearer token.

**Response (200):**
```json
{
  "balance": 45000,
  "currency": "NGN"
}
```

---

### `GET /wallet/transactions`

🔒 Requires Bearer token.

**Query Parameters:**

| Param | Type | Required | Example |
|-------|------|----------|---------|
| `page` | integer | ❌ | `1` |
| `limit` | integer | ❌ | `20` |

**Response (200):**
```json
{
  "transactions": [
    {
      "id": "txn_abc123",
      "type": "DEBIT",
      "title": "Ticket Purchase — Lagos → Abuja",
      "date": "2026-07-10T09:30:00Z",
      "amount": 25000,
      "status": "SUCCESS"
    },
    {
      "id": "txn_def456",
      "type": "CREDIT",
      "title": "Wallet Top-up",
      "date": "2026-07-09T14:00:00Z",
      "amount": 50000,
      "status": "SUCCESS"
    }
  ],
  "page": 1,
  "totalPages": 3,
  "totalItems": 42
}
```

**Transaction type:** `"CREDIT"` | `"DEBIT"`  
**Transaction status:** `"SUCCESS"` | `"PENDING"` | `"FAILED"`

---

### `POST /wallet/fund`

🔒 Requires Bearer token. Initialize a wallet top-up.

**Request:**
```json
{
  "amount": 50000,
  "paymentMethod": "paystack",
  "paymentReference": "PSK_ref_xyz789"
}
```

**Response (200):**
```json
{
  "balance": 95000,
  "transaction": {
    "id": "txn_ghi789",
    "type": "CREDIT",
    "title": "Wallet Top-up",
    "date": "2026-07-10T15:30:00Z",
    "amount": 50000,
    "status": "SUCCESS"
  }
}
```

---

## 8. Live Tracking

### `GET /tracking/{tripId}`

🔒 Requires Bearer token. Returns the current live location and status of a bus for a given trip.

> For the demo, the backend can return mock coordinates that slowly move along the route path. Use the current server time to calculate a position along the route.

**Response (200):**
```json
{
  "tripId": "trip_x9f2k",
  "status": "in_transit",
  "bus": {
    "currentLocation": {
      "latitude": 7.6,
      "longitude": 4.5
    },
    "speed": 85,
    "heading": 45
  },
  "driver": {
    "name": "Oluwaseun Adebayo",
    "phone": "+2348000000000",
    "avatarUrl": "https://images.unsplash.com/photo-1506277886164-e25aa3f4ef7f?w=100"
  },
  "vehicle": {
    "type": "Toyota Hiace",
    "plateNumber": "KJA-123-XY"
  },
  "eta": {
    "minutes": 42,
    "distanceKm": 58.3,
    "arrivalTime": "05:00 PM"
  },
  "route": [
    { "latitude": 6.5244, "longitude": 3.3792 },
    { "latitude": 6.9, "longitude": 3.8 },
    { "latitude": 7.6, "longitude": 4.5 },
    { "latitude": 8.5, "longitude": 6.2 },
    { "latitude": 9.0765, "longitude": 7.3986 }
  ]
}
```

**Tracking status:** `"not_started"` | `"boarding"` | `"in_transit"` | `"arrived"`

---

## 9. Cities (For Location Search)

### `GET /cities`

Returns the list of supported cities for the origin/destination search.

**Response (200):**
```json
{
  "cities": [
    { "name": "Lagos", "code": "LOS", "state": "Lagos" },
    { "name": "Abuja", "code": "ABV", "state": "FCT" },
    { "name": "Port Harcourt", "code": "PHC", "state": "Rivers" },
    { "name": "Kano", "code": "KAN", "state": "Kano" },
    { "name": "Ibadan", "code": "IBD", "state": "Oyo" },
    { "name": "Benin City", "code": "BNI", "state": "Edo" },
    { "name": "Enugu", "code": "ENU", "state": "Enugu" },
    { "name": "Calabar", "code": "CAL", "state": "Cross River" },
    { "name": "Uyo", "code": "UYO", "state": "Akwa Ibom" },
    { "name": "Owerri", "code": "OWR", "state": "Imo" },
    { "name": "Warri", "code": "WAR", "state": "Delta" },
    { "name": "Abakaliki", "code": "ABK", "state": "Ebonyi" },
    { "name": "Jos", "code": "JOS", "state": "Plateau" },
    { "name": "Kaduna", "code": "KAD", "state": "Kaduna" },
    { "name": "Onitsha", "code": "ONT", "state": "Anambra" }
  ]
}
```

---

## Error Response Format

All error responses follow this shape:

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Email is already registered",
    "details": null
  }
}
```

**Common error codes:**

| Code | HTTP Status | Meaning |
|------|-------------|---------|
| `VALIDATION_ERROR` | 400 | Invalid input |
| `UNAUTHORIZED` | 401 | Missing/invalid token |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `NOT_FOUND` | 404 | Resource not found |
| `CONFLICT` | 409 | Duplicate resource |
| `INTERNAL_ERROR` | 500 | Server error |

---

## Summary of All Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/auth/register` | ❌ | Register new user |
| `POST` | `/auth/login` | ❌ | Login |
| `POST` | `/auth/send-otp` | ❌ | Send OTP |
| `POST` | `/auth/verify-otp` | ❌ | Verify OTP |
| `POST` | `/auth/forgot-password` | ❌ | Reset password |
| `GET` | `/user/profile` | 🔒 | Get profile |
| `PATCH` | `/user/profile` | 🔒 | Update profile |
| `GET` | `/cities` | ❌ | List supported cities |
| `GET` | `/buses/search` | ❌ | Search buses (procedural) |
| `GET` | `/buses/{busId}/seats` | ❌ | Get seat layout |
| `POST` | `/bookings` | 🔒 | Create booking |
| `POST` | `/bookings/{id}/cancel` | 🔒 | Cancel booking |
| `GET` | `/trips` | 🔒 | Get user trips |
| `GET` | `/wallet/balance` | 🔒 | Get wallet balance |
| `GET` | `/wallet/transactions` | 🔒 | Get transactions |
| `POST` | `/wallet/fund` | 🔒 | Fund wallet |
| `GET` | `/tracking/{tripId}` | 🔒 | Live bus tracking |
