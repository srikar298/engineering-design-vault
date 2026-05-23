# ⚡ 05 - gRPC

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C010 |
| **Category** | Networking |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🔥 High |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** gRPC is a high-performance, open-source RPC (Remote Procedure Call) framework by Google that uses **Protocol Buffers** (binary serialization) over **HTTP/2** for transport — enabling strongly-typed, code-generated, low-latency service-to-service communication across any language. It natively supports 4 communication patterns: unary, server streaming, client streaming, and bidirectional streaming — making it ideal for internal microservice communication.
*   **Scalability Dimension:** Primary: **Low Latency Internal RPC** & **Strong Contract / Type Safety**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### gRPC vs REST (Detailed)
| Feature | gRPC | REST (JSON/HTTP) |
| :--- | :--- | :--- |
| **Protocol** | HTTP/2 | HTTP/1.1 (usually), HTTP/2 (optionally) |
| **Serialization** | Protocol Buffers (binary, ~10× smaller than JSON). | JSON (text, human-readable). |
| **Schema** | Strongly typed `.proto` file. Breaking changes caught at compile time. | Optional (OpenAPI/Swagger). No enforcement. |
| **Code Generation** | ✅ Auto-generates client/server stubs in 10+ languages. | ❌ Must write client manually or use generator. |
| **Streaming** | ✅ 4 modes: Unary, Server, Client, Bidirectional. | ❌ Limited (polling, SSE for server-push). |
| **Latency** | Lower — binary encoding + HTTP/2 multiplexing. | Higher — JSON parsing overhead + HTTP/1.1 HOLB. |
| **Browser Support** | ❌ Not natively (requires gRPC-Web proxy). | ✅ Native everywhere. |
| **Human Debuggability** | ❌ Binary — need tools like grpcurl, Postman. | ✅ Curl-friendly, readable JSON. |

### The 4 gRPC Communication Patterns
```
1. Unary (Standard RPC):
   Client ──── GetUser(user_id) ──────────────────────────► Server
   Client ◄─── UserResponse ────────────────────────────── Server

2. Server Streaming:
   Client ──── GetFeed(user_id) ──────────────────────────► Server
   Client ◄─── FeedItem1 ─── FeedItem2 ─── FeedItem3 ───── Server
   (Server streams multiple responses; client reads stream)

3. Client Streaming:
   Client ──── DataChunk1 ─── DataChunk2 ─── DataChunk3 ──► Server
   Client ◄─── UploadResponse ────────────────────────────── Server

4. Bidirectional Streaming:
   Client ◄──────────────────────────────────────────────► Server
   (Both sides stream concurrently — real-time chat, collaborative editing)
```

### Protocol Buffers (The Wire Format)
```protobuf
// user.proto
syntax = "proto3";

service UserService {
  rpc GetUser(GetUserRequest) returns (User);
  rpc StreamUserEvents(GetUserRequest) returns (stream UserEvent);
}

message GetUserRequest { string user_id = 1; }
message User {
  string user_id = 1;
  string name = 2;
  int64 created_at = 3;
}
```
*   Field numbers (not names) are encoded in binary → renaming a field is safe; changing a field number breaks compatibility.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `gRPC Status Codes`: Unlike HTTP 4xx/5xx, gRPC has its own status codes: `OK`, `NOT_FOUND`, `UNAVAILABLE`, `DEADLINE_EXCEEDED`.
    *   `gRPC Deadline Propagation`: Set per-call deadlines, which automatically propagate through the entire call chain.
*   **Blast Radius (The "Impact"):**
    *   A `.proto` schema change that removes or renumbers a field breaks all clients that use that field — **never remove or renumber fields; only add new ones** (additive-only evolution).

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Proposing gRPC for a **public-facing API** (no browser support natively; gRPC-Web adds proxy complexity — REST/GraphQL are better choices for external APIs).
*   Not knowing that gRPC uses **HTTP/2** — this means all the HTTP/2 benefits (multiplexing, header compression) apply automatically.

### Interview Tip (The "Strong Hire" Signal)
> *"Between microservices, we use gRPC with Protocol Buffers. The generated stubs give us compile-time type safety across Go, Java, and Python services — we catch schema mismatches in CI, not in production. For external APIs consumed by browsers and mobile, we expose REST/JSON via an API Gateway that translates to internal gRPC calls (grpc-gateway)."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
