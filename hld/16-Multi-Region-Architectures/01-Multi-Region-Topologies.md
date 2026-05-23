# ⚡ 01 - Multi-Region Topologies

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C065 |
| **Category** | Multi-Region |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🔥 High |
| **Understanding** | 🔴 None / 🟡 Conceptual / 🟢 Applied |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | 2026-06-01 |
| **Mastery** | 🔴 Familiar / 🟡 Competent / 🟢 Expert |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Multi-Region Topologies distribute computational workloads and database storage across geographically separate cloud data centers (e.g., US-East-1, EU-West-1, AP-Southeast-1). Architects trigger these designs when building global applications that require survival of entire cloud data center outages (high disaster resilience), compliance with local data sovereignty laws (e.g., GDPR), and sub-100ms response times for global users.
*   **Scalability Dimension:** Primary: **Read/Write Latency (User Proximity)**. Secondary: **Network Transit Cost** and **Cross-Region Consistency Overhead**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Topologies

#### 1. Active-Passive (Hot / Cold Standby)
In this setup, a single "Active" region handles all writes and reads. An asynchronous replication stream synchronizes data changes to a secondary "Passive" region. 
*   **Hot Standby:** The passive region runs a scaled-down version of the application servers. If the primary region fails, traffic is routed to the secondary, and the database replica is promoted to primary.
*   **Cold Standby:** The passive region contains only backup storage or machine images (AMIs). In a disaster, servers must be provisioned and configured before traffic can resume, causing high Recovery Time.

```
                    [ User Traffic ]
                           │
                 (Route53 / Geo-Routing)
                           │
             ┌─────────────┴─────────────┐
             ▼ (Active)                  ▼ (Passive / Standby)
     ┌───────────────┐           ┌───────────────┐
     │ US-East-1 App │           │ EU-West-1 App │
     └───────┬───────┘           └───────────────┘
             │                                   
             ▼ (Read / Write)                    
     ┌───────────────┐   (Async Replica) ┌───────────────┐
     │  Primary DB   │──────────────────►│  Replica DB   │
     └───────────────┘                   └───────────────┘
```

#### 2. Active-Active (Multi-Primary)
Multiple regions actively accept read and write traffic. Data written in Region A is replicated to Region B asynchronously, and vice versa. 
*   **Benefits:** Zero-downtime failover (if Region A dies, all traffic is redirected to Region B). Low latency since users read and write to the nearest physical region.
*   **Challenges:** Risk of **Split Brain** (regions isolated by network partition accept conflicting writes) and complex conflict resolution.

```
             [ User US ]                 [ User EU ]
                  │                           │
              (Anycast)                   (Anycast)
                  │                           │
                  ▼                           ▼
          ┌───────────────┐           ┌───────────────┐
          │ US-East-1 App │           │ EU-West-1 App │
          └───────┬───────┘           └───────┬───────┘
                  │                           │
                  ▼ (Read/Write Local)        ▼ (Read/Write Local)
          ┌───────────────┐   (Bi-directional)┌───────────────┐
          │  Master DB-A  │◄─────────────────►│  Master DB-B  │
          └───────────────┘     Async Replica └───────────────┘
```

---

### Global Routing Strategies
1.  **Geo-DNS Routing:**
    *   The DNS server inspects the requester's IP and resolves the domain name to the server IP located in the closest region (e.g., AWS Route 53 Geolocation routing).
    *   *Trade-off:* DNS records are heavily cached by client operating systems and ISP resolver caches (ignoring TTLs). If a region fails, traffic will continue hitting the dead region for 10-30 minutes until client DNS caches expire.
2.  **Anycast IP Routing:**
    *   Multiple regional load balancers advertise the exact same public IP address using **BGP (Border Gateway Protocol)**. The internet routing infrastructure automatically sends the user's packets to the closest physical server.
    *   *Trade-off:* Instantaneous failover. If Region A goes down, its BGP advertisements cease, and internet routers redirect packets to Region B within seconds. However, it requires premium global network infrastructure (e.g., Cloudflare, AWS Global Accelerator).

---

### Topologies Comparison Table

| Metric | Active-Passive (Cold) | Active-Passive (Hot) | Active-Active (Read-Local) | Active-Active (Multi-Master) |
| :--- | :--- | :--- | :--- | :--- |
| **RTO (Recovery Time)** | ❌ Hours (rebuild servers). | 🟡 Minutes (DNS swap, DB promotion). | 🟢 Seconds (automatic read failover). | 🟢 Zero (instantaneous routing). |
| **RPO (Data Loss)** | ❌ High (last backup age). | 🟡 Low (async replication lag, ~1-5s). | 🟡 Low (async replication lag, ~1-5s). | 🟡 Low (async replication lag, ~1-5s). |
| **Write Latency** | 🟡 Medium (depends on user proximity to primary). | 🟡 Medium (depends on user proximity to primary). | 🟡 Medium (writes sent to primary region). | 🟢 Extremely Low (writes local to user region). |
| **Consistency** | Strong Consistency. | Strong Consistency. | Strong Consistency for writes; eventual for read replicas. | Eventual Consistency (risk of write conflicts). |
| **Cost** | 🟢 Low (standby idle/offline). | ❌ High (duplicate running servers). | ❌ High (duplicate active nodes). | 🔴 Extremely High (multi-master licensing + traffic sync cost). |

---

## 💥 3. Resiliency & Operations

*   **Observability (The "Signal"):**
    *   `Cross-Region Replication Lag (Seconds)`: Represents the latency of data sync between databases. A lag $>5$ seconds indicates a database bottleneck or inter-region network degradation.
    *   `Anycast Routing Path Flaps`: Monitor the stability of BGP advertisements to ensure traffic isn't constantly switching regions, which invalidates local session caches.
*   **Blast Radius (The "Impact"):**
    *   If Region A crashes and 50% of global traffic shifts to Region B, Region B may suffer a **Cascading Outage** due to CPU/Memory exhaustion if it was not pre-scaled to handle the combined load.
    *   **Mitigation:** Always implement **Degraded Mode Flags**. When a regional failover occurs, disable non-essential features (e.g., recommendation carousels, analytics logging) to reduce CPU load on the surviving region by 30-40%.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Claiming that Active-Passive with DNS routing provides instant failover. A senior engineer explains that DNS TTL caching on client browsers and local ISPs will bypass any server-side DNS changes for several minutes, meaning users will experience errors during that window.
*   Suggesting an Active-Active multi-master write database across continents for a banking ledger without explaining the speed-of-light limitations. (Light in fiber takes ~67ms to travel from New York to London and back, making synchronous multi-master consensus too slow for sub-50ms writes).

### Interview Tip (The "Strong Hire" Signal)
> *"For our reservation system, we chose an Active-Passive topology for writes but Active-Active for reads. Reads are routed locally using Anycast IP and served from read-replicas in US, EU, and Asia. Writes are forwarded to the primary region in US-East. This design avoids write-conflict complexity while providing sub-50ms read latency for 95% of our global traffic."*

---

## 💡 5. My Custom Study Notes & Whiteboard

### Speed of Light Latency Constraints (Ocean Transit Math)
*   **Distance (NY to London):** $\sim 5,600 \text{ km}$
*   **Speed of light in fiber optic glass:** $\sim 200,000 \text{ km/sec}$
*   **One-way physical delay:** $5,600 / 200,000 = 28 \text{ ms}$
*   **Minimum Round-Trip Time (RTT):** $56 \text{ ms}$ (physical limit without network switch overheads).
*   **Conclusion:** Synchronous multi-region ACID writes (e.g., using Paxos or Raft across regions) require at least $1 \times \text{RTT}$ for a quorum consensus. Thus, any multi-region synchronous write will incur a minimum physical latency penalty of $60-80 \text{ ms}$.

### Terraform Route 53 Active-Passive DNS Configuration
```hcl
# Primary Region Record
resource "aws_route53_record" "primary" {
  zone_id = aws_route53_zone.main.zone_id
  name    = "api.myglobalapp.com"
  type    = "A"

  failover_routing_policy {
    type = "PRIMARY"
  }

  set_identifier = "us-east-1-primary"
  alias {
    name                   = aws_lb.us_east_1.dns_name
    zone_id                = aws_lb.us_east_1.zone_id
    evaluate_target_health = true # Route53 automatically shifts to secondary if LB health check fails
  }
}

# Secondary/Passive Region Record
resource "aws_route53_record" "secondary" {
  zone_id = aws_route53_zone.main.zone_id
  name    = "api.myglobalapp.com"
  type    = "A"

  failover_routing_policy {
    type = "SECONDARY"
  }

  set_identifier = "eu-west-1-secondary"
  alias {
    name                   = aws_lb.eu_west_1.dns_name
    zone_id                = aws_lb.eu_west_1.zone_id
    evaluate_target_health = true
  }
}
```
