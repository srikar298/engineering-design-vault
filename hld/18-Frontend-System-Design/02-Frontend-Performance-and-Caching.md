# ⚡ 02 - Frontend Performance and Caching

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C130 |
| **Category** | Frontend Design |
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
*   **Two-Sentence Trigger:** Frontend performance and caching comprises asset compression, code optimization, resource prioritization hints, and multi-tiered caching strategies (Browser Cache, Service Worker, CDN) designed to minimize page rendering times. Architects implement these strategies to optimize Core Web Vitals, minimize bandwidth consumption, and deliver consistent, fast user experiences across varying network qualities and client devices.
*   **Scalability Dimension:** Primary: **Largest Contentful Paint (LCP)** & **Interaction to Next Paint (INP)**. Secondary: **Network Bandwidth Egress** & **Client Memory Footprint**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Core Web Vitals (CWVs)
1.  **Largest Contentful Paint (LCP):** Measures *loading performance*. Indicates when the main content of a page has likely loaded. **Target: < 2.5s**.
    *   *Optimization:* Compress images (WebP/AVIF), eliminate render-blocking JS/CSS, preload the hero image.
2.  **Interaction to Next Paint (INP):** Measures *interactivity and responsiveness* (replaced FID in March 2024). Assesses the latency of all user interactions (clicks, taps, keypresses) on a page. **Target: < 200ms**.
    *   *Optimization:* Yield to the browser main thread, break long tasks (> 50ms) using `requestIdleCallback` or Web Workers, optimize React/Vue state updates.
3.  **Cumulative Layout Shift (CLS):** Measures *visual stability*. Quantifies how much elements shift around the screen during loading. **Target: < 0.1**.
    *   *Optimization:* Set explicit `width` and `height` dimensions on images/videos, reserve space for dynamic ads/skeletons, avoid inserting content above existing content.

---

### Bundle Optimization & Payload Compression
*   **Code Splitting / Dynamic Imports:** Rather than loading a single monolithic JavaScript bundle, the application compiles routes and large components into separate files loaded on-demand via `import()`.
*   **Tree Shaking:** A build-time optimization (Vite/Webpack) that uses static analysis of ES6 module syntax (`import`/`export`) to eliminate unused code paths (dead code removal).
*   **Brotli vs. Gzip:** Brotli uses a dictionary-based compression algorithm that delivers **15% to 20% smaller files** than Gzip for text assets (JS, CSS, HTML). Brotli should be preferred for static assets, while dynamic responses can fall back to Gzip due to Gzip's lower compression CPU overhead.

---

### Caching Layers & HTTP Headers

```
[Browser Request] ──► 1. Browser Cache (HTTP Headers)
                             │
                             ▼ (Cache Miss)
                      2. Service Worker (Pre-cached Shell)
                             │
                             ▼ (Cache Miss)
                      3. CDN Edge (Brotli, Region cached)
                             │
                             ▼ (Cache Miss)
                      4. Origin Server (Backend App/BFF)
```

1.  **Hashed Assets (JS, CSS, Images):**
    *   *Header:* `Cache-Control: public, max-age=31536000, immutable`
    *   *Why:* Because the build output includes content hashes (e.g., `main.a98f12.js`), the file content never changes. The browser can cache it forever.
2.  **HTML Entry Point (`index.html`):**
    *   *Header:* `Cache-Control: no-cache` (or `no-cache, must-revalidate`)
    *   *Why:* `no-cache` forces the browser to check the origin server (using `ETag` or `Last-Modified`) to verify if a newer version of the page exists before serving the cached file. This ensures instant deployment updates.
3.  **Sensitive User Data APIs:**
    *   *Header:* `Cache-Control: no-store`
    *   *Why:* Prevents browsers or shared caches from writing sensitive information to disk.

---

### Browser Resource Hints

*   **`dns-prefetch`:** Resolves domain names in the background before the user clicks a link (saves ~50ms of DNS lookup).
*   **`preconnect`:** Resolves DNS, establishes TCP handshake, and negotiates TLS in the background. Use for critical third-party domains (e.g., Google Fonts, API Gateways).
*   **`preload`:** Instructs the browser to download a high-priority resource immediately (e.g., font files, above-the-fold hero images).
*   **`prefetch`:** Fetches resources in the background that are expected to be needed during subsequent page navigations (e.g., the JavaScript bundle for the next logical step in a user flow).

| Hint | Scope | Timing | Use Case |
| :--- | :--- | :--- | :--- |
| **`dns-prefetch`** | DNS lookup only. | Low priority. | Pre-resolving third-party link domains. |
| **`preconnect`** | DNS + TCP + TLS. | High priority. | Third-party API domains or CDN assets. |
| **`preload`** | Download & Cache. | Critical path. | Above-the-fold hero image, custom web fonts. |
| **`prefetch`** | Download & Cache. | Low priority. | Next-page bundle (loaded when main thread is idle). |

---

## 💥 3. Resiliency & Operations

### Observability (The "Signal")
*   **RUM (Real User Monitoring) Metrics:** Instruments the browser using the `web-vitals` library to send telemetry to a monitoring endpoint (e.g., Datadog, Sentry, or custom API). Tracks LCP, INP, and CLS across actual user devices.
*   **CDN Cache Hit Ratio (CHR):** Measures the efficiency of CDN caching. A drop indicates inappropriate cache key configurations (e.g., query parameter variance) resulting in origin server traffic spikes.
*   **JavaScript Error Rate:** Tracks unhandled runtime errors in production via `window.onerror`.

### Blast Radius (The "Impact")
*   **Immutable Cache Lockout:** Caching `index.html` with a long `max-age` and `immutable` header prevents users from getting software updates. Hotfixes will not load unless the user manually clears their browser cache.
*   **Mitigation:**
    1.  **Never cache `index.html` permanently:** Always use `no-cache` for HTML entry points.
    2.  **Preload Limits:** Limit `preload` links to 3-5 critical assets. Over-preloading clogs the browser's parallel connection pool (which is limited to 6 concurrent requests per origin over HTTP/1.1).
    3.  **Active Service Worker Lifecycle:** Implement an update check on Service Workers that prompts users to refresh the page when a new app version is available.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   **Caching `index.html`:** Recommending long cache periods for the HTML entry file, leaving no mechanism to deploy bug fixes or revert broken code without forcing users to clear their cache.
*   **Preloading Everything:** Suggesting `preload` or `preconnect` for dozens of resources, which conflicts with browser prioritization logic and degrades page load performance.
*   **Confusing `no-cache` and `no-store`:** Believing `no-cache` means "do not cache." In reality, `no-cache` means "cache, but revalidate with the server before using." Only `no-store` completely prevents caching.

### Interview Tip (The "Strong Hire" Signal)
> "When optimizing frontend performance, I separate assets into static, hashed dependencies and dynamic, entry point assets. Static assets are served with `Cache-Control: public, max-age=31536000, immutable` and pre-compressed with Brotli at the CDN edge. The main `index.html` is served with `Cache-Control: no-cache` to force immediate ETag validation, preventing deployment cache locks. To minimize INP, I keep the main execution thread unblocked by dynamically splitting code on route levels, offloading heavy processing (like analytics parsers) to Web Workers, and utilizing `requestIdleCallback` to defer non-essential scripts."

---

## 💡 5. My Custom Study Notes & Whiteboard

### HTTP Cache-Control Flowchart

```
                          [Request Asset]
                                 │
                 ┌───────────────┴───────────────┐
                 ▼ (Is it in Browser Cache?)     ▼ (No)
        ┌────────┴────────┐               [Fetch from Network]
        ▼ (Cache Valid?)  ▼ (Expired)            │
    [Serve Local Cache]   │                      ▼
                          ▼             [Check Cache Headers]
                  [Verify with ETag]             │
                         │             ┌─────────┴─────────┐
             ┌───────────┴───────────┐ ▼ (no-store)        ▼ (no-cache)
             ▼ (304 Not Modified)    ▼ (200 OK)         [Validate with Origin]
         [Serve Local Cache]    [Overwrite Cache]
```

*   **ETag Check Example:**
    *   *Request:* `If-None-Match: "33a64df551425fcc55e4d42a148795d9f25f89d4"`
    *   *Response:* `304 Not Modified` (no body, saving bandwidth) or `200 OK` (with the new asset payload).
