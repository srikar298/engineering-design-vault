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

## 🏗️ 3. Performance Optimizations
1.  **Lazy Loading**: Loading JS bundles only when needed.
2.  **Asset Optimization**: Image compression, WebP, Font subsetting.
3.  **Virtualization**: Rendering only the visible rows in a long list (e.g., News Feed).
4.  **Debouncing & Throttling**: Controlling the rate of events (e.g., Search autocomplete).

---

## 🚀 4. The SDE-3 Edge: Micro-Frontends
**The Problem:** Your 10k user app has 50 developers. They keep breaking each other's code.
*   **The Solution:** Decompose the frontend into independent, deployable units (e.g., Header, Checkout, Product Details).
*   **Implementation:** Use **Module Federation** or **Iframes** (Legacy).
*   **Impact:** Team autonomy and independent scaling of frontend features.
