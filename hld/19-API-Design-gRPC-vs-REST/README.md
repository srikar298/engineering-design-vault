# 🔌 19 - API Design: gRPC, REST, GraphQL & Pagination

**Concepts Covered**: C015 (gRPC), C016 (REST vs GraphQL), C015-EXT (Pagination Strategies), C020 (Content Negotiation)  
**Category**: API Design  
**SDE-2 Importance**: 🔥 Critical — Every system design starts with "What's the API?"

---

## 📂 Files in This Module

| File | Concept | Difficulty | Frequency |
| :--- | :--- | :--- | :--- |
| [README.md](./README.md) | gRPC vs REST vs GraphQL + Idempotency + Content Negotiation | 🟡 Medium | 🔥 High |
| [02-Pagination-Strategies.md](./02-Pagination-Strategies.md) | Offset vs Cursor vs Keyset at Scale | 🔴 Hard | 🔥 Extremely High |

---

## 📖 1. The Concept — What Makes a Good API?

Every distributed system you design exposes interfaces. The API contract is the **single most consequential architectural decision** — it's harder to change than your database schema once clients are live.

```
┌────────────────────────────────────────────────────────────┐
│                   API DESIGN SPACE                         │
│                                                            │
│  REST          GraphQL         gRPC          WebSocket     │
│  ─────         ───────         ────          ─────────     │
│  Standard      Flexible        Fast          Realtime      │
│  stateless     client-driven   binary        persistent    │
│  HTTP verbs    single endpoint protobuf      bi-directional│
│                                                            │
│  Public APIs   Feed/Mobile     Internal      Chat/Gaming   │
│  CRUD          BFFs            Services      Live Events   │
└────────────────────────────────────────────────────────────┘
```

---

## 📊 2. REST vs gRPC vs GraphQL — Master Comparison Table

| Feature | REST (JSON) | gRPC (Protobuf) | GraphQL |
| :--- | :--- | :--- | :--- |
| **Protocol** | HTTP/1.1 or HTTP/2 | HTTP/2 only | HTTP/1.1 or HTTP/2 |
| **Payload format** | Text (JSON) — verbose | Binary (Protobuf) — compact | Text (JSON) — verbose |
| **Payload size** | Baseline 100% | ~60-70% smaller | ~80-90% (avoids over-fetch) |
| **Schema** | Optional (OpenAPI) | Mandatory (`.proto`) | Mandatory (SDL) |
| **Type safety** | Loose | Strict (generated client) | Strict (generated types) |
| **Streaming** | SSE / chunked (hacky) | Native bi-directional | Subscriptions |
| **Browser support** | ✅ Native | ❌ Needs gRPC-web proxy | ✅ Native |
| **Caching** | ✅ HTTP cache / CDN | ❌ Hard (all POST) | ❌ Hard (all POST) |
| **Learning curve** | Low | High | Medium |
| **Best for** | Public APIs, CRUD | Internal microservices | Mobile, BFF layer |
| **Real usage** | Stripe, Twilio, GitHub | Google internal, Uber | GitHub v4, Shopify, FB |

---

## 🏗️ 3. REST — Deep Dive

### The Resource Model
REST is not just "use HTTP". It's a **resource-oriented architecture**:

```
WRONG REST (RPC style):
  POST /getUser?id=123
  POST /createPayment
  POST /cancelOrder

CORRECT REST (Resource style):
  GET    /users/123           ← Read user
  POST   /payments            ← Create payment
  DELETE /orders/456          ← Cancel order
  PATCH  /orders/456          ← Partial update
  PUT    /orders/456/status   ← Full replace
```

### HTTP Status Codes (non-negotiable in interviews)

| Code | Meaning | When to use |
| :--- | :--- | :--- |
| `200 OK` | Success | GET, PATCH, PUT with body |
| `201 Created` | Resource created | POST that creates a resource |
| `204 No Content` | Success, no body | DELETE, PUT with no response body |
| `400 Bad Request` | Client sent garbage | Invalid input, missing fields |
| `401 Unauthorized` | Not authenticated | No token / bad token |
| `403 Forbidden` | Not authorized | Token valid, but no permission |
| `404 Not Found` | Resource missing | Wrong ID |
| `409 Conflict` | State conflict | Duplicate create, version mismatch |
| `422 Unprocessable` | Validation failed | Valid JSON, invalid business logic |
| `429 Too Many Requests` | Rate limited | Rate limiter fired |
| `500 Internal Server Error` | Our bug | Unhandled exceptions |
| `503 Service Unavailable` | Overloaded | Circuit breaker open, load shedding |

---

## ⚡ 4. gRPC — Deep Dive

### How Protobuf Works
```protobuf
// Define schema in a .proto file
syntax = "proto3";

service PaymentService {
  rpc ProcessPayment (PaymentRequest) returns (PaymentResponse);
  rpc StreamTransactions (UserId) returns (stream Transaction);  // server streaming
}

message PaymentRequest {
  string user_id    = 1;
  int64  amount     = 2;
  string currency   = 3;
}
```

The `.proto` file is compiled to generate **type-safe client and server code** in any language (Java, Go, Python, C++). The client and server never need to manually serialize/deserialize.

### Why gRPC is Faster

```
REST/JSON:
  { "user_id": "abc123", "amount": 5000, "currency": "USD" }
  = 54 bytes (text)

gRPC/Protobuf:
  [field1=abc123][field2=5000][field3=USD]
  = ~15 bytes (binary, varint-encoded)

Plus HTTP/2 multiplexing = no head-of-line blocking across concurrent calls.
```

### 4 Types of gRPC Streaming

| Type | Pattern | Use Case |
| :--- | :--- | :--- |
| Unary | Request → Response | Standard call (like REST) |
| Server Streaming | Request → Stream | Live scores, stock prices |
| Client Streaming | Stream → Response | File upload, batch insert |
| Bi-directional | Stream ↔ Stream | Chat, collaborative editing |

---

## 🎯 5. Content Negotiation (C020)

Often overlooked but asked at senior levels. Content negotiation is the mechanism by which a client and server agree on the **format** of the response.

### How It Works

```
Client Request:
  GET /api/report
  Accept: application/json, application/xml;q=0.8, text/html;q=0.5
  Accept-Language: en-US, en;q=0.9
  Accept-Encoding: gzip, br

  Translation: "I prefer JSON. If not available, XML is ok (80% preference).
               HTML is last resort (50%). Prefer English. Support gzip/Brotli."

Server Response (picks best match):
  200 OK
  Content-Type: application/json
  Content-Language: en-US
  Content-Encoding: gzip
  Vary: Accept, Accept-Language   ← tells CDN to cache separately per variant
```

### The `q` Factor (Quality Value)
`Accept: application/json, application/xml;q=0.8`  
Default `q=1.0`. Lower q = lower preference. Range: 0.0-1.0.

### Why `Vary` Header Matters for Caching
```
Without Vary:
  CDN caches /api/report as JSON.
  Spanish user requests /api/report.
  CDN serves cached English JSON → wrong!

With Vary: Accept, Accept-Language:
  CDN maintains separate cache entries per (Accept, Accept-Language) combination.
  Spanish user gets Spanish JSON from cache correctly.
```

### Proactive vs Reactive Negotiation
- **Proactive (Server-driven)**: Server picks format based on `Accept` header (most common).
- **Reactive (Agent-driven)**: Server returns `300 Multiple Choices` with links to all variants. Client picks. Rarely used.

---

## 🚀 6. The SDE-3 Edge: Idempotency Keys

If an API call fails due to a network timeout, should the client retry?

```
Problem:
  Client → POST /payments → [timeout]
  Was the payment processed or not?
  
  If client retries → double charge!
  If client doesn't retry → lost payment!

Solution: Idempotency Keys
  Client → POST /payments
           X-Idempotency-Key: uuid-abc-123   ← client generates once and stores

Server:
  1. Hash the key: redis.get("idempotency:uuid-abc-123")
  2. If found → return the cached response (no re-processing)
  3. If not found → process payment → store result in Redis with key (TTL: 24h)
  4. Return response
```

**Properties:**
- `GET`, `PUT`, `DELETE` — naturally idempotent by REST semantics
- `POST` — NOT idempotent by default. Always add `X-Idempotency-Key`
- Stripe, Adyen, PayPal all require `Idempotency-Key` on payment creation

---

## 🎯 7. Interview Scripts

### "When would you choose gRPC over REST?"

> *"For public-facing APIs, I'd always use REST — browser compatibility, CDN caching, and ecosystem familiarity are non-negotiable. But for internal microservice-to-microservice communication, gRPC gives significant advantages: Protobuf reduces payload size by ~60%, HTTP/2 multiplexing eliminates head-of-line blocking, and generated clients eliminate serialization bugs. I'd use gRPC for any internal service that communicates at high frequency — payment processing, inventory checks, recommendation scoring — where latency and throughput matter."*

### "REST vs GraphQL for a mobile app?"

> *"For mobile apps, GraphQL's biggest win is eliminating over-fetching and under-fetching. A mobile newsfeed card needs 5 fields from a User, 3 from a Post, and 2 from a Comment. With REST I'd either hit 3 endpoints (under-fetching) or build a custom BFF endpoint. With GraphQL the client declares exactly what it needs in one request. The trade-off is that GraphQL is harder to cache since everything goes through POST, and N+1 query problems require careful resolver design with DataLoader."*

---

## ✅ 8. SDE-2+ Readiness Checklist

- [ ] Can explain why HTTP/2 multiplexing matters for gRPC performance
- [ ] Can explain the difference between `401` and `403` without hesitation
- [ ] Can design idempotency key handling for a payment API
- [ ] Knows what the `Vary` header does for CDN caching
- [ ] Can explain GraphQL N+1 problem and DataLoader solution
- [ ] Can name one real company using each: REST, gRPC, GraphQL
- [ ] Can explain `Accept: application/json;q=1.0, */*;q=0.1` without looking it up
