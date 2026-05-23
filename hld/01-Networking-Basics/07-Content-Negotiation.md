# ⚡ 07 - Content Negotiation

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C008 |
| **Category** | Networking |
| **Difficulty** | 🟢 Easy |
| **Interview Frequency** | 🟡 Medium |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Content Negotiation is the HTTP mechanism by which a client and server agree on the best representation of a resource — the client declares what formats, languages, encodings, and character sets it can accept; the server responds with the most appropriate representation it can provide. This enables a single endpoint to serve JSON to API clients, XML to legacy SOAP clients, `gzip`-compressed responses to bandwidth-conscious clients, and localized content to international users.
*   **Scalability Dimension:** Primary: **API Flexibility & Client Diversity** without proliferating endpoints.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Four Negotiation Axes
| Header (Client → Server) | Response Header (Server → Client) | What It Negotiates |
| :--- | :--- | :--- |
| `Accept` | `Content-Type` | Response format (JSON, XML, HTML, Protobuf). |
| `Accept-Encoding` | `Content-Encoding` | Compression (gzip, br, deflate, identity). |
| `Accept-Language` | `Content-Language` | Human language (en-US, fr, de). |
| `Accept-Charset` | `Content-Type` (charset param) | Character encoding (UTF-8, ISO-8859-1). |

### Example: API Serving Multiple Formats
```
// Client 1: Browser
GET /api/users/123
Accept: application/json

Server: Content-Type: application/json
{ "id": "123", "name": "Alice" }

// Client 2: Legacy SOAP Service
GET /api/users/123
Accept: application/xml

Server: Content-Type: application/xml
<user><id>123</id><name>Alice</name></user>

// Client 3: Mobile (bandwidth-sensitive)
GET /api/users/123
Accept: application/json
Accept-Encoding: gzip

Server: Content-Type: application/json
        Content-Encoding: gzip
[compressed bytes] ← 70-80% smaller payload
```

### Quality Values (q-factors)
Clients can express **preference ordering** using `q` values (0.0–1.0):
```
Accept: application/json;q=1.0, application/xml;q=0.8, text/html;q=0.5
```
The server picks the highest-q format it supports. This allows graceful degradation.

### API Versioning via Content Negotiation
Some APIs use custom media types for version negotiation instead of URL versioning:
```
Accept: application/vnd.myapi.v2+json
                              ^^
                         Version embedded in media type
```
*   *Pros:* Resources keep stable URLs. Versioning is a concern of content, not URL.
*   *Cons:* Much harder to discover, test, and document vs `/v2/users`.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `406 Not Acceptable Rate`: Server can't satisfy the client's `Accept` header — indicates a client/server capability mismatch.
    *   `Content-Encoding Distribution`: Proportion of responses sent compressed vs uncompressed (high compression = bandwidth savings).
*   **Blast Radius (The "Impact"):**
    *   Caching servers (CDN, Varnish) must store separate cache entries per `Accept`/`Accept-Encoding` combination. A cache that ignores `Vary: Accept-Encoding` will serve gzip-compressed content to a client that doesn't support it.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Not knowing the `Vary` HTTP header — when a server responds differently based on `Accept-Encoding`, it **must** include `Vary: Accept-Encoding` in the response so CDNs and caches store separate entries for each encoding variant.
*   Hardcoding `Content-Type: application/json` without checking the client's `Accept` header (breaks XML or Protobuf consumers).

### Interview Tip (The "Strong Hire" Signal)
> *"Our API gateway checks the `Accept-Encoding` header and compresses responses with Brotli (for modern browsers) or gzip (for legacy clients). We saw a 65% reduction in API response bandwidth after enabling compression. The gateway sets `Vary: Accept-Encoding` on all compressed responses so Cloudflare caches the correct variant per client type."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
