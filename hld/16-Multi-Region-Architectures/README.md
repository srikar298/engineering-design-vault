# 🌎 16 - Multi-Region & High Availability

## 📖 1. The Concept
What happens if an entire AWS Data Center (Region) goes offline? For global products, you must design for **Regional Resilience**.

---

## 📊 2. The SDE-2 Trade-off Table: Active-Passive vs. Active-Active

| Strategy | How it Works | Pros | Cons |
| :--- | :--- | :--- | :--- |
| **Active-Passive** | Region A serves all traffic. Region B is a "Hot Standby" with data replicated. | Simpler data consistency. | Waste of resources. High **RTO** (Recovery Time). |
| **Active-Active** | Traffic is routed to the nearest Region (A or B). | Lowest latency. No idle resources. | **Split Brain** risk. Extremely complex multi-master data sync. |

---

## 🏗️ 3. Routing: Geo-DNS vs. Anycast
- **Geo-DNS**: The DNS server returns an IP based on the requester's IP location. (Route53).
- **Anycast**: Multiple servers in different regions share the same IP. The network automatically routes to the closest one. (Cloudflare/Global Load Balancers).

---

## 🚀 4. The SDE-3 Edge: RTO and RPO
In a disaster recovery discussion, use these metrics:
1.  **RTO (Recovery Time Objective)**: How quickly must the system be back up? (e.g., 5 minutes).
2.  **RPO (Recovery Point Objective)**: How much data loss is acceptable? (e.g., "We can lose up to 30 seconds of data" -> Async replication).

**Senior Signal:** "We chose an Active-Active setup with a 30-second RPO using cross-region asynchronous replication to balance cost and global latency."
