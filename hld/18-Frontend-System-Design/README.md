# 🎨 18 - Frontend System Design

## 📖 1. The Concept
System design doesn't stop at the API. For 10k+ concurrent users, the Frontend must be optimized for **Network Efficiency**, **State Management**, and **Perceived Performance**.

---

## 📊 2. The SDE-2 Trade-off Table: Rendering Strategies

| Strategy | Where it Renders | Pros | Cons |
| :--- | :--- | :--- | :--- |
| **CSR** (Client-Side) | Browser (React/Vue). | Rich interactions. | Slow initial load (FCP). Poor SEO. |
| **SSR** (Server-Side) | Server (Next.js). | Fast initial load. Great SEO. | Higher server load (Cost). |
| **ISR/SSG** (Static) | Build Time. | Fastest. Zero runtime cost. | Data can be stale. |

---

## 🏗️ 3. Performance & Web Vitals

A "Senior" candidate knows exactly what metrics to measure:
- **LCP (Largest Contentful Paint):** Measures loading performance. Target: **< 2.5s**.
- **FID (First Input Delay):** Measures interactivity. Target: **< 100ms**.
- **CLS (Cumulative Layout Shift):** Measures visual stability. Target: **< 0.1**.

### Optimization Toolkit:
1.  **Image Optimization:** Using WebP and **CDN-edge resizing** to serve the exact pixels needed for the device.
2.  **Code Splitting:** Using Dynamic Imports to keep the initial JS bundle small.
3.  **Prefetching:** Loading the "Next Page" JS while the user is still on the current page to make transitions feel instant.

---

## 🚀 4. The SDE-3 Edge: BFF & Data Fetching

### BFF (Backend for Frontend) Pattern
Instead of the frontend calling 10 different microservices, it calls one **BFF** service.
- **Why?** It aggregates data, handles protocol translation (e.g., gRPC to JSON), and formats responses specifically for the device (Mobile vs Web).
- **Benefit:** Reduces the number of expensive mobile network round-trips.

### GraphQL vs. REST
| Feature | REST | GraphQL |
| :--- | :--- | :--- |
| **Fetching** | Under-fetching (Multiple calls). | One call for everything. |
| **Payload** | Over-fetching (Extra data). | Exactly what you asked for. |
| **Caching** | Easy (Native HTTP caching). | Hard (Requires Client-side cache like Apollo). |

**Senior Signal:** "For our high-traffic dashboard, we chose **GraphQL** to solve the over-fetching problem, but we implemented **Persisted Queries** to prevent attackers from sending massive, nested queries that could DDoS our backend."

---
