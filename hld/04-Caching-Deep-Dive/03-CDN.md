# ⚡ 03 - CDN (Content Delivery Network)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C024 |
| **Category** | Caching Topology |
| **Difficulty** | 🟢 Easy |
| **Interview Frequency** | 🔥 High |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** A Content Delivery Network (CDN) is a geographically distributed network of proxy servers (Points of Presence) that caches static media, assets, and APIs close to the client. It intercepts client requests, resolving them at the Edge to avoid round-trip latency to the origin server.
*   **Scalability Dimension:** Primary: **Global Read Latency** & **Origin Bandwidth Savings**.

---

## ⚖️ 2. Trade-offs & Deep Dive
| Push CDN | Pull CDN |
| :--- | :--- |
| **Push:** Content is uploaded directly to the CDN when updated (origin pushes it). | **Pull:** CDN grabs the resource from the origin server on the first cache miss. |
| *Pros:* Minimizes origin traffic. Content is always ready. | *Pros:* Easy setup, consumes less storage since only requested content is saved. |
| *Cons:* Wastes CDN storage on files that might never be downloaded. | *Cons:* The first user experiences slow load times (cache miss latency). |

*   **Ideal Use Cases:**
    *   High-traffic static resources (images, JS, CSS, video files, index.html).
    *   API response caching for non-personalized payloads (e.g., product catalog list).
*   **Anti-Patterns / When NOT to use:**
    *   Highly dynamic, real-time personalized data (e.g., a user's shopping cart or banking ledger) where caching at the edge causes stale, incorrect views.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Edge Cache Hit Rate`: The percentage of traffic served from the CDN instead of going to the origin.
    *   `Origin Bandwidth Ingress`: Spikes indicate CDN cache stampedes or invalidation loops.
    *   `Time to First Byte (TTFB)`: Elevated TTFB globally indicates edge routing problems.
*   **Blast Radius (The "Impact"):**
    *   A global CDN outage (e.g., Cloudflare BGP route drop) shuts down access to the app entirely or forces origin load balancers to crash from direct client asset requests.
*   **Numbers to Know:**
    *   Edge serving time: **10 - 30 ms**
    *   Origin serving time (cross-continent): **150 - 400 ms**

---

## 🚫 4. Interview Playbook
*   **Common Mistakes:**
    *   Underestimating asset versioning (e.g., using `logo.png` instead of `logo-a8fd92.png`). If you don't change the filename, clients see the old image until the CDN TTL expires.
    *   Not knowing how DNS resolves traffic to the closest PoP (Anycast routing).
*   **Interview Tip (The "Strong Hire" Signal):**
    *   Mention Anycast routing: *"I will configure DNS with Anycast so requests automatically route to the topologically closest CDN Edge Node, reducing initial handshake times."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
