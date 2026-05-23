# ⚡ 03 - DNS Resolution

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C005 |
| **Category** | Networking |
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
*   **Two-Sentence Trigger:** DNS (Domain Name System) is the internet's distributed phone book — translating human-readable hostnames (e.g., `api.myapp.com`) into IP addresses that routers can forward packets to. In system design, DNS is not just name resolution but a powerful **traffic steering layer** — enabling geo-routing, weighted failover, health-check-based routing, and blue/green deployments at the global network edge.
*   **Scalability Dimension:** Primary: **Global Traffic Routing** & **Multi-Region Failover**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Full DNS Resolution Chain
```
User types: api.myapp.com

1. Browser Cache          (TTL-based, typically 60s)
         ↓ (miss)
2. OS Cache / /etc/hosts  (OS-level cache)
         ↓ (miss)
3. Router / ISP Resolver  (Recursive resolver — does the heavy lifting)
         ↓ (miss)
4. Root Name Server       (Knows TLD servers: .com → 13 root servers worldwide)
         ↓
5. TLD Name Server (.com) (Knows authoritative server for myapp.com)
         ↓
6. Authoritative NS       (Route53 / Cloudflare — returns actual A/CNAME record)
         ↓
   Returns: 203.0.113.42
   ← Cached at each level for TTL duration →
```

### DNS Record Types
| Record | Purpose | Example |
| :--- | :--- | :--- |
| **A** | Hostname → IPv4 address. | `api.myapp.com → 1.2.3.4` |
| **AAAA** | Hostname → IPv6 address. | `api.myapp.com → 2001:db8::1` |
| **CNAME** | Hostname → another hostname (alias). | `www.myapp.com → myapp.com` |
| **MX** | Mail exchange servers for a domain. | `myapp.com → mail.myapp.com` |
| **TXT** | Arbitrary text (used for SPF, DKIM, domain ownership verification). | |
| **NS** | Nameserver records — which DNS servers are authoritative. | |
| **SOA** | Start of Authority — primary NS, admin email, TTL defaults. | |

### DNS as Traffic Steering
| Routing Policy | Mechanism | Use Case |
| :--- | :--- | :--- |
| **Latency-Based** | Route53 measures latency to each region; returns IP of lowest-latency region. | Global API: users auto-routed to nearest region. |
| **Geo-DNS** | Returns different IPs based on client's geolocation. | Data residency compliance (EU users → EU region). |
| **Weighted** | Split traffic by weight (e.g., 90%/10%). | Canary deployments, blue/green traffic shifting. |
| **Failover** | Primary record + health-check; switch to secondary if primary fails. | Active-passive DR with automatic DNS failover. |

### The TTL Trade-off
*   **Low TTL (30s):** Faster failover during incidents. High DNS query load on authoritative servers.
*   **High TTL (300s):** Fewer queries, cached longer. During failover, clients keep hitting the old IP for up to TTL seconds.
*   **Pre-failover tip:** Lower TTL to 30s hours **before** planned maintenance → faster switch when you do it.

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `DNS Resolution Latency`: Spikes indicate resolver cache misses or authoritative NS slowness.
    *   `NXDOMAIN Rate`: "Non-existent domain" responses. Spikes may indicate misconfiguration or DGA malware.
*   **Blast Radius (The "Impact"):**
    *   **DNS is the single most critical internet dependency.** A misconfigured DNS record with a 5-minute TTL takes 5 minutes to propagate globally. A wrong IP causes 100% request failure for all affected regions.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Stopping at "DNS returns an IP" without mentioning the caching hierarchy — TTL is the key operational concept that determines how quickly changes propagate.
*   Not knowing that CNAME records **cannot be used at the zone apex** (e.g., `myapp.com` root domain cannot be a CNAME — use Route53 ALIAS records as a workaround).

### Interview Tip (The "Strong Hire" Signal)
> *"For our multi-region deployment, we use Route53 Latency-Based Routing with health checks on each regional endpoint. When `us-east-1` becomes unhealthy, Route53 automatically stops returning that IP and routes all global traffic to `eu-west-1` within 30 seconds — our TTL. We pre-lower TTL to 30s before deployments."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
