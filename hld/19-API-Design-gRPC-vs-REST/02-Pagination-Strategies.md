# 📄 Pagination Strategies at Scale

**Concept ID**: C015-EXT  
**Category**: API Design  
**Difficulty**: 🟡 Medium → 🔴 Hard (at scale)  
**Interview Frequency**: 🔥 Extremely High — asked in virtually every feed/search/list API design

---

## 📖 1. The Core Problem

You have a table with **500 million rows**. A client requests "give me the next 20 results." Three fundamental questions arise:

1. **How do you tell the DB where to start?** (Stateless between requests)
2. **What happens if rows are inserted/deleted between pages?** (Consistency)
3. **How fast is the DB lookup for page N?** (Performance at depth)

The choice of pagination strategy is a **first-class architectural decision** — it impacts API contract, DB indexing strategy, caching, and client implementation.

---

## ⚔️ 2. The Four Strategies — Comparison Table

```
┌─────────────────┬──────────────┬─────────────────┬──────────────┬─────────────────┐
│   Strategy      │  How It Works│  Performance    │  Consistency │  Real Usage     │
├─────────────────┼──────────────┼─────────────────┼──────────────┼─────────────────┤
│ OFFSET/LIMIT    │ SKIP N rows  │ ❌ O(N) scan    │ ❌ Drifts    │ Admin panels    │
│                 │ at DB level  │ Slow at depth   │ on live data │ Internal tools  │
├─────────────────┼──────────────┼─────────────────┼──────────────┼─────────────────┤
│ PAGE NUMBER     │ page=3&      │ ❌ Same as      │ ❌ Same as   │ Search results  │
│                 │ size=20      │ OFFSET          │ OFFSET       │ (Google, Bing)  │
├─────────────────┼──────────────┼─────────────────┼──────────────┼─────────────────┤
│ CURSOR-BASED    │ after=<token>│ ✅ O(log N)     │ ✅ Stable    │ Twitter, GitHub │
│ (Keyset)        │ DB uses index│ Index seek      │ on live data │ Stripe, Slack   │
├─────────────────┼──────────────┼─────────────────┼──────────────┼─────────────────┤
│ SEEK METHOD     │ WHERE id >   │ ✅ O(log N)     │ ✅ Stable    │ Internal APIs   │
│ (Explicit)      │ last_seen_id │ Index seek      │              │ High-perf DBs   │
└─────────────────┴──────────────┴─────────────────┴──────────────┴─────────────────┘
```

---

## ❌ 3. Why OFFSET/LIMIT Breaks at Scale

```
SELECT * FROM posts ORDER BY created_at DESC LIMIT 20 OFFSET 50000;
```

### Problem 1: The DB still reads 50,020 rows
The database engine cannot teleport to row 50,000. It scans from the beginning, counts 50,000 rows, discards them, and returns the next 20.

```
OFFSET 0     → scans 20 rows    → fast   ✅
OFFSET 1000  → scans 1020 rows  → ok     🟡
OFFSET 10000 → scans 10020 rows → slow   🟠
OFFSET 50000 → scans 50020 rows → broken 🔴
```

**At 500M rows with OFFSET 499,999,980 → the DB scans almost the entire table.**

### Problem 2: Live data causes ghost rows and duplicates

```
Timeline:
  Client fetches page 1: rows [1, 2, 3, 4, 5]  (OFFSET 0)
  [New post inserted at position 1]
  Client fetches page 2: rows [5, 6, 7, 8, 9]  (OFFSET 5)
                                ↑ Row 5 appears TWICE!

OR:
  Client fetches page 1: rows [1, 2, 3, 4, 5]  (OFFSET 0)
  [Row 3 is deleted]
  Client fetches page 2: rows [7, 8, 9, 10, 11] (OFFSET 5)
                                ↑ Row 6 is SKIPPED entirely (ghost)
```

**This is why Twitter's old offset-based feed had duplicate tweets appearing during scroll.**

---

## ✅ 4. Cursor-Based Pagination (Keyset) — The Gold Standard

### Core Idea
Instead of telling the DB **"skip N rows"**, tell it **"start from after this specific row"**.

The cursor encodes the **position** of the last seen item. The DB uses an **index seek** directly to that position.

```
─── Page 1 ─────────────────────────────
GET /api/posts?limit=20

Response:
{
  "data": [...20 posts...],
  "next_cursor": "eyJpZCI6IDEwMjMsICJ0cyI6IDE2OTUwMDAwMDB9"
                  ↑ Base64 encoded {"id": 1023, "ts": 1695000000}
}

─── Page 2 ─────────────────────────────
GET /api/posts?limit=20&after=eyJpZCI6IDEwMjMsICJ0cyI6IDE2OTUwMDAwMDB9

DB Query (what happens internally):
SELECT * FROM posts
WHERE (created_at, id) < (1695000000, 1023)  ← Index seek! Not a scan.
ORDER BY created_at DESC, id DESC
LIMIT 20;
```

### Why the DB Query is O(log N) instead of O(N)

```
OFFSET approach:         Cursor approach:
┌──────────────┐         ┌──────────────┐
│ Row 1        │ ← scan  │ B-Tree Index │
│ Row 2        │ ← scan  │      │       │
│ ...          │ ← scan  │    Node      │
│ Row 50,000   │ ← scan  │   /    \     │
│ [DISCARD]    │         │ Node  [1023] │← seek directly here
│ Row 50,001   │ ← return│              │
└──────────────┘         └──────────────┘
  Full table scan           B-Tree O(log N) seek
```

---

## 🔐 5. Cursor Encoding — Making it Opaque

**Never expose raw DB column values** as cursors (`after=1023`). This leaks your schema, allows enumeration attacks, and couples clients to your DB internals.

**Use opaque Base64-encoded tokens:**

```java
// Encoding (server-side, before sending response)
public String encodeCursor(long id, long timestamp) {
    String raw = String.format("{\"id\":%d,\"ts\":%d}", id, timestamp);
    return Base64.getUrlEncoder()
                 .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
}

// Decoding (server-side, when cursor arrives in next request)
public CursorPayload decodeCursor(String cursor) {
    byte[] decoded = Base64.getUrlDecoder().decode(cursor);
    String json = new String(decoded, StandardCharsets.UTF_8);
    // parse {"id":1023,"ts":1695000000}
    return objectMapper.readValue(json, CursorPayload.class);
}
```

**What the client sees**: `eyJpZCI6MTAyMywidHMiOjE2OTUwMDAwMDB9`  
**What it actually is**: `{"id":1023,"ts":1695000000}`  
**Client behavior**: Treat it as an opaque string. Do not parse. Just pass it back.

---

## 🏗️ 6. DB Index Design Requirement

Cursor pagination **only works** if the cursor columns form a composite index. Otherwise you just moved the O(N) scan into the WHERE clause.

```sql
-- The query the cursor generates:
WHERE (created_at, id) < (:ts, :id)
ORDER BY created_at DESC, id DESC

-- The required index:
CREATE INDEX idx_posts_cursor ON posts (created_at DESC, id DESC);
--                                      ↑ Must match ORDER BY exactly
```

### Multi-column cursors (for stable sort)
Always include a **unique tiebreaker** column (usually `id`) alongside the sort column. Without it, rows with identical `created_at` values produce ambiguous cursor positions.

```
posts with same timestamp:
  {id: 1021, created_at: 1695000000, content: "A"}
  {id: 1022, created_at: 1695000000, content: "B"}
  {id: 1023, created_at: 1695000000, content: "C"}

Cursor on (created_at) alone → ambiguous. Which of the 3?
Cursor on (created_at, id) → always unambiguous. Use id as tiebreaker.
```

---

## 🌍 7. Real-World Implementations

### Twitter (X) — `next_token`
```json
GET /2/tweets/search/recent?query=cats&max_results=10
Response:
{
  "data": [...],
  "meta": {
    "next_token": "7140dibdnow9c7btw3w29n4v1mtb5e2ai0b4pb11"
  }
}
```

### GitHub — `Link` Header (RFC 5988)
```
Link: <https://api.github.com/repos/octocat/Hello-World/issues?page=2>; rel="next",
      <https://api.github.com/repos/octocat/Hello-World/issues?page=34>; rel="last"
```

### Stripe — `starting_after` / `ending_before`
```
GET /v1/charges?limit=10&starting_after=ch_1MtJ3n2eZvKYlo2CRTVtfUy3

Response:
{
  "object": "list",
  "data": [...],
  "has_more": true,
  "url": "/v1/charges"
}
```
Stripe uses the **object ID itself** as the cursor — no encoding needed because IDs already have no schema information.

### GraphQL — Relay Cursor Connections Spec
```graphql
query {
  posts(first: 10, after: "cursor_opaque_string") {
    edges {
      cursor        # per-edge cursor (position of this specific item)
      node {
        id
        title
      }
    }
    pageInfo {
      hasNextPage
      endCursor    # cursor to pass as `after` for next page
    }
  }
}
```
Relay establishes the canonical GraphQL pagination contract used by Facebook, Shopify, GitHub GraphQL API.

---

## 🔄 8. Bidirectional Pagination

Supporting both **forward** (`after`) and **backward** (`before`) pagination is needed for infinite scroll with "pull to refresh" (e.g., Twitter timeline).

```
API Contract:
  GET /posts?after=<cursor>   → next page  (scroll down)
  GET /posts?before=<cursor>  → prev page  (pull to refresh)
  GET /posts?limit=20         → first page (no cursor = start)

DB Query for `before`:
  SELECT * FROM posts
  WHERE (created_at, id) > (:ts, :id)   ← flipped inequality
  ORDER BY created_at ASC, id ASC        ← flipped ORDER
  LIMIT 20;
  -- then reverse the result array before returning
```

**Response shape for bidirectional:**
```json
{
  "data": [...],
  "pagination": {
    "next_cursor": "eyJpZCI6MTAyMywidHMiOjE2OTUwMDAwMDB9",
    "prev_cursor": "eyJpZCI6MTAwNCwidHMiOjE2OTQwMDAwMDB9",
    "has_next": true,
    "has_prev": true
  }
}
```

---

## 📊 9. When to Use Which — Decision Matrix

```
                    Is data live/changing?
                    YES              NO
                     │               │
        Is page     ─┤               ├─ Use OFFSET (simple)
        depth > 1k? │               │   (search results, reports)
                YES  │               │
                 ┌───┴───┐           │
          Use CURSOR     │           │
          PAGINATION     │           │
         (Twitter feed,  │           │
          GitHub issues, │           │
          Stripe charges)│           │
                         │           │
        NO (shallow)     │           │
         ┌───────────────┘           │
         │                           │
    OFFSET is fine                OFFSET is fine
    (admin panels,                (static datasets,
     small datasets)               analytics exports)
```

---

## 🎯 10. SDE-2 Interview Script

**Question**: *"How would you implement pagination for a Twitter-like feed API?"*

> *"I'd use cursor-based keyset pagination rather than OFFSET/LIMIT. OFFSET has two fatal problems at scale: it degrades to an O(N) table scan as users scroll deeper, and it produces duplicates or gaps on live data when new posts are inserted between page fetches.*
>
> *The cursor encodes the position of the last seen item — typically a composite of `(created_at, id)`. I'd Base64-encode it to make it opaque to clients. The DB query becomes a keyset seek: `WHERE (created_at, id) < (:ts, :id) ORDER BY created_at DESC LIMIT 20`, which uses the B-tree index directly in O(log N).*
>
> *I'd also design the index to match the ORDER BY exactly: `CREATE INDEX ON posts(created_at DESC, id DESC)`. And I'd include both `next_cursor` and `prev_cursor` in the response for bidirectional scroll support."*

**Follow-up**: *"What if users want to jump to page 50?"*

> *"Cursor-based pagination intentionally doesn't support random page jumps — that's a known trade-off. If arbitrary seek is required (like Google search page 1-10 links), I'd use OFFSET but with a strict depth limit (e.g., max 10,000 rows) and a warning to clients. Alternatively, for analytics use cases, Elasticsearch's `search_after` parameter handles deep pagination efficiently."*

---

## ✅ 11. SDE-2+ Readiness Checklist

- [ ] Can explain why `OFFSET 50000` is slow even with an index?
- [ ] Can design a composite cursor for multi-column sort stability?
- [ ] Can implement opaque Base64 cursor encoding/decoding?
- [ ] Knows the `Link` header RFC and Relay Connections GraphQL spec?
- [ ] Can design bidirectional pagination (forward + backward)?
- [ ] Can identify when OFFSET is acceptable vs. when cursor is mandatory?
