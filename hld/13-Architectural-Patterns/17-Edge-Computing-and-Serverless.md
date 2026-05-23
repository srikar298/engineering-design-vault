# ⚡ 17 - Edge Computing and Serverless

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C126 |
| **Category** | Architectural Patterns |
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
*   **Two-Sentence Trigger:** Edge computing distributes stateless computation and data storage closer to end-users (at CDN Point-of-Presence locations), while serverless abstracts underlying host management by running function code dynamically on demand. An architect implements this combined paradigm when building globally distributed web applications requiring sub-100ms response times for authentication, dynamic personalization, request routing, and real-time content aggregation without maintaining a centralized VM fleet.
*   **Scalability Dimension:** Primary: **Read/Write Latency (Time to First Byte - TTFB)** and Request Concurrency. Secondary: **Cold-start duration** and **Global replication convergence time**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Edge Workers (V8 Isolates) vs. Container Serverless (MicroVMs)
In traditional serverless (like AWS Lambda), functions run within containers or micro-virtual machines (e.g., AWS Firecracker). Edge computing relies on lightweight JavaScript V8 Isolates (e.g., Cloudflare Workers) to eliminate startup latency.

```
       [USER] ────── (Request: Sub-10ms latency) ──────► [CDN Edge PoP]
                                                             │
                                         ┌───────────────────┴───────────────────┐
                                         ▼                                       ▼
                              ┌─────────────────────┐                 ┌─────────────────────┐
                              │ Edge Worker         │                 │ Edge Key-Value      │
                              │ (V8 Isolate Engine) │ ◄─────────────► │ (Local Read Store)  │
                              └─────────────────────┘                 └─────────────────────┘
                                         │ (Cache Miss / Write)
                                         ▼ (High-Latency WAN)
                                  ┌──────────────┐
                                  │ Origin Cloud │
                                  │ (Central DB) │
                                  └──────────────┘
```

#### V8 Isolates (Edge Workers)
*   **Mechanism:** V8 engine isolates run within a single operating system process, relying on Chromium's sandboxing mechanism. Instead of launching a virtual machine, the system spins up a new JS execution context in under **5 milliseconds**.
*   **Pros:** Near-zero cold start; minimal memory footprint (3MB per isolate); extremely cheap runtimes.
*   **Cons:** Limited execution time (e.g., 50ms CPU time); restricted library ecosystems (cannot run standard C++ extensions or heavy native Node.js binaries); no direct filesystem access.

#### MicroVMs / Containers (Centralized Serverless)
*   **Mechanism:** Boots a stripped-down Linux kernel (e.g., Firecracker) inside a dedicated container.
*   **Pros:** Supports any runtime (Java, Go, Python, Docker); runs for up to 15 minutes; large memory capacity (up to 10GB).
*   **Cons:** Cold starts range from 200ms to several seconds; higher base memory overhead.

---

### Data Storage at the Edge
Running compute at the edge requires data to be present at the edge. Architects utilize three primary storage patterns:

1.  **Replicated Key-Value Stores (e.g., Cloudflare KV):** Designed for high-read, low-write configurations. Writes propagate globally over minutes (eventual consistency), but reads are served from local edge memory in under 15ms.
2.  **Edge SQL Databases (e.g., Turso, D1):** Embed lightweight SQLite databases directly at the edge PoP. Reads are local, while writes are synchronized using Raft consensus back to a primary origin database.
3.  **Conflict-Free Replicated Data Types (CRDTs):** Used to synchronize real-time multi-user modifications (like collaborative docs) without lock contention across PoPs.

---

### Architectural Comparison
| Metric / Feature | Centralized VMs (EC2) | Centralized Serverless (Lambda) | Edge Serverless (Workers) |
| :--- | :--- | :--- | :--- |
| **Cold Start** | ✅ Zero (Always running). | ❌ High (200ms - 5s). | ✅ Minimal (< 5ms). |
| **Execution Timeout** | ✅ Unlimited. | 🟡 Medium (15 minutes). | ❌ Short (50ms - 30s CPU time). |
| **Memory Capacity** | ✅ Up to Terabytes. | 🟡 Up to 10 GB. | ❌ Up to 128 MB. |
| **Global Latency** | ❌ High for distant users. | ❌ High for distant users. | ✅ Ultra-low (Local PoP). |
| **State Maintenance** | Stateful (in-memory sessions). | Stateless. | Stateless (local cache only). |

---

## 💥 3. Resiliency & Operations

*   **Observability (The "Signal"):**
    *   `Isolate CPU Duration`: The CPU execution time in milliseconds. If p99 CPU time approaches the platform threshold (e.g., 50ms), requests will be terminated with 504 Gateway errors.
    *   `Edge Cache Hit Ratio`: The proportion of requests resolved in-memory at the edge. A low hit ratio (< 50%) causes request storms to the central origin database.
    *   `Global Replication Delay`: The synchronization lag between the origin write database and edge read replicas.
*   **Blast Radius (The "Impact"):**
    *   A syntax error or configuration bug deployed to an edge worker propagates globally within seconds, breaking the entire entry pipeline.
    *   *Mitigation:* Implement regional canary rollouts (deploying first to a low-traffic CDN region), configure fallback routing policies (if an edge worker returns a 5xx, the CDN automatically bypasses the worker and routes directly to the origin backend), and run local unit tests with Miniflare.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   **Running Heavy Compute at the Edge:** Proposing Edge Workers for heavy operations like video transcoding or ML model training, ignoring the strict 50ms CPU execution timeout.
*   **Assuming Strong Consistency Across the Edge:** Suggesting global inventory lock management using Edge KV. Because Edge KV is eventually consistent, it will lead to race conditions and double-booking.
*   **Ignoring Database Connection Pools:** Connecting Edge Workers directly to a centralized PostgreSQL database. Since edge requests can scale to 100k concurrent isolates, they will quickly exhaust PostgreSQL's connection limits. (Use connection pooling proxies like PgBouncer or HTTP-based databases).

### Interview Tip (The "Strong Hire" Signal)
> "To minimize global latency, we execute user requests at the closest CDN point of presence using V8 Isolate-based Edge Workers, avoiding the startup penalties of container-based microVMs. We authenticate users locally using JWT validation directly at the edge, retrieving roles from a local eventually consistent KV cache. For operations requiring transactional databases, we route requests through an HTTP connection proxy (like Prisma Accelerate or PgBouncer) to prevent the distributed isolates from overwhelming our central SQL origin server."

---

## 💡 5. My Custom Study Notes & Whiteboard

### Production-Grade Edge Worker: JWT Validation & Routing (JavaScript)
This script runs in a V8 Isolate at the CDN Edge, verifying user JSON Web Tokens (JWT) before letting the request pass to the backend or serving a cached resource.

```javascript
// Edge Worker Entrypoint
addEventListener('fetch', event => {
  event.respondWith(handleRequest(event.request, event));
});

// Configure constants
const JWT_SECRET = "jwt_shared_secret_string_128_bit_value";
const CACHE_TTL_SECONDS = 3600;

async function handleRequest(request, event) {
  const url = new URL(request.url);

  // 1. Static asset caching bypass
  if (url.pathname.startsWith('/static/')) {
    return fetch(request); // Standard CDN caching logic applies
  }

  // 2. Extract Authorization Header
  const authHeader = request.headers.get('Authorization');
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return new Response(JSON.stringify({ error: "Unauthorized: Missing Token" }), {
      status: 401,
      headers: { 'Content-Type': 'application/json' }
    });
  }

  const token = authHeader.split(' ')[1];

  // 3. Crypto-verify JWT at the Edge
  const isValid = await verifyJWT(token, JWT_SECRET);
  if (!isValid) {
    return new Response(JSON.stringify({ error: "Unauthorized: Invalid Signature" }), {
      status: 403,
      headers: { 'Content-Type': 'application/json' }
    });
  }

  // 4. Edge-based redirect or Rewrite routing
  const country = request.headers.get('cf-ipcountry') || 'US';
  if (url.pathname === '/') {
    // Dynamic rewrite based on user country location
    url.pathname = `/welcome/${country.toLowerCase()}`;
    return Response.redirect(url.toString(), 302);
  }

  // 5. Pass validated request to primary Origin backend
  const modifiedRequest = new Request(request, {
    headers: {
      ...Object.fromEntries(request.headers),
      'X-User-Validated': 'true'
    }
  });

  return fetch(modifiedRequest);
}

// Cryptographic JWT Verification helper using Web Crypto API
async function verifyJWT(token, secret) {
  const parts = token.split('.');
  if (parts.length !== 3) return false;

  const [header, payload, signature] = parts;
  const dataToSign = `${header}.${payload}`;

  // Import Key
  const encoder = new TextEncoder();
  const key = await crypto.subtle.importKey(
    'raw',
    encoder.encode(secret),
    { name: 'HMAC', hash: 'SHA-256' },
    false,
    ['verify']
  );

  // Decode Signature
  const signatureBytes = base64UrlToBytes(signature);

  // Verify
  return await crypto.subtle.verify(
    'HMAC',
    key,
    signatureBytes,
    encoder.encode(dataToSign)
  );
}

function base64UrlToBytes(base64Url) {
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const padLength = (4 - (base64.length % 4)) % 4;
  const paddedBase64 = base64 + '='.repeat(padLength);
  const binaryString = atob(paddedBase64);
  const bytes = new Uint8Array(binaryString.length);
  for (let i = 0; i < binaryString.length; i++) {
    bytes[i] = binaryString.charCodeAt(i);
  }
  return bytes;
}
```
