# ⚡ 06 - REST vs GraphQL

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C009 |
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
*   **Two-Sentence Trigger:** REST (Representational State Transfer) structures APIs around resources and HTTP methods (`GET /users/123`), with a fixed response shape defined by the server — clients must make multiple calls and receive over-fetched data they don't need. GraphQL is a query language where the client specifies exactly which fields it needs in a single request, eliminating over-fetching and under-fetching at the cost of query complexity, caching difficulty, and server-side execution overhead.
*   **Scalability Dimension:** Primary: **API Flexibility vs Operational Simplicity**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Over-Fetching & Under-Fetching Problem

**Over-fetching (REST):**
```
Client needs: user name + profile picture only
REST response: { id, name, email, phone, address, bio, preferences, ... }
              ← Sends 2KB of data; client uses 100 bytes ❌
```

**Under-fetching (REST — N+1 Problem):**
```
Client needs: user + their posts + each post's comments
REST calls:
  GET /users/123          → 1 request
  GET /users/123/posts    → 1 request
  GET /posts/1/comments   → 1 request per post
  GET /posts/2/comments   → 1 request per post
  ... = N+1 requests ❌
```

**GraphQL Solution:**
```graphql
query {
  user(id: "123") {
    name
    profilePicture        # Only what we need
    posts {
      title
      comments { text }  # All in one request ✅
    }
  }
}
```

### REST vs GraphQL Comparison
| Feature | REST | GraphQL |
| :--- | :--- | :--- |
| **Over-fetching** | ✅ Common — server defines shape. | ❌ Eliminated — client defines shape. |
| **Under-fetching** | ✅ Common — N+1 requests for related data. | ❌ Eliminated — single query fetches nested data. |
| **Caching** | ✅ Easy — HTTP-level cache (CDN, `Cache-Control`). | ❌ Hard — all queries are POST to `/graphql`. Cache-busting requires persisted queries or custom layer. |
| **Schema / Type Safety** | Partial (OpenAPI). Not enforced. | ✅ Strongly typed schema — introspectable by clients. |
| **File Upload** | ✅ Native `multipart/form-data`. | ❌ Non-standard (requires Apollo Upload or REST endpoint). |
| **Learning Curve** | Low — REST is ubiquitous. | High — N+1 resolver problem, DataLoader patterns, batching. |
| **Versioning** | Via URL (`/v1/users`). | Via field deprecation — additive schema evolution. |

### The N+1 Resolver Problem in GraphQL
Naively resolving nested GraphQL fields causes N+1 database queries:
```
Query: { posts { author { name } } }

For each post (N=100):
  → SELECT * FROM users WHERE id = post.author_id  // 100 DB queries!

Solution: DataLoader (batching)
  → Collects all author_ids in one request tick
  → SELECT * FROM users WHERE id IN (1, 2, 3, ...) // 1 DB query ✅
```

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Query Complexity Score`: Unlimited query depth can cause O(n^k) resolver chains. Must set query depth limits and complexity limits.
    *   `Resolver Execution Time`: Identify slow resolvers causing query latency.
*   **Blast Radius (The "Impact"):**
    *   Without query depth/complexity limits, a malicious client can craft a deeply nested GraphQL query that causes exponential database load — effectively a DoS attack.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Saying GraphQL is always better (it's operationally complex — N+1 problems, caching difficulty, security limits). REST with a BFF is often simpler.
*   Not knowing that GraphQL caching is inherently hard because all queries go to `POST /graphql` with different bodies — HTTP caches can't key on the body.

### Interview Tip (The "Strong Hire" Signal)
> *"We use REST for our external public API (it's cacheable, widely understood, simple to document) and GraphQL internally for our mobile app's BFF layer. Mobile has 50+ different screen shapes needing different data — GraphQL eliminates the N+1 round trips and over-fetching that were killing our mobile performance. We use DataLoader in our resolvers and query complexity limits to prevent abuse."*

---

## 💡 5. My Custom Study Notes & Whiteboard
```
When to choose what:
REST:      Public API, simple CRUD, heavy caching needed, small team.
GraphQL:   Mobile/web app with diverse clients, many entity relationships,
           client teams need autonomy over data shape.
gRPC:      Internal microservice-to-microservice (not browser-facing).

Key GraphQL risks to always mention:
1. N+1 problem → DataLoader
2. Caching difficulty → persisted queries / edge caching
3. Query complexity DoS → depth + complexity limits
```
