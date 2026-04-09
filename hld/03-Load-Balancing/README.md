# ⚖️ 03 - Load Balancing (The SDE-2 Perspective)

## 📖 The Concept
A Load Balancer distributes incoming network traffic across a group of backend servers. This ensures no single server bears too much demand, improving responsiveness and availability.

## 📊 The SDE-2 Trade-off Table: Layer 4 vs. Layer 7

| Feature | Layer 4 (Transport) | Layer 7 (Application) |
| :--- | :--- | :--- |
| **Data Evaluated** | IP, TCP/UDP Ports | HTTP Headers, Cookies, URL paths |
| **Speed** | Extremely Fast (No payload inspection) | Slower (Requires decryption/inspection) |
| **Routing Flexibility** | Low (Blind forwarding) | High (Can route based on `/api` vs `/images`) |
| **Use Case** | High-throughput TCP streams (Gaming, Video) | Web Applications, Microservice routing |

---

## 🛠️ 2. Load Balancing Algorithms

| Algorithm | How it Works | Best For |
| :--- | :--- | :--- |
| **Round Robin** | Sequential distribution. | Servers with identical specs and short requests. |
| **Least Connections** | Routes to the server with the fewest active tasks. | Long-lived connections (WebSockets, DB streams). |
| **Weighted WRR** | Round Robin but accounts for server capacity. | CPU/RAM-heavy workloads with mixed server power. |
| **IP Hash** | Hashing the client's IP to assign a server. | Basic session stickiness. |

---

## 🚀 The SDE-3 Edge: Advanced Consistent Hashing

Traditional Hashing (e.g., `hash(key) % N`) fails when $N$ changes—adding or removing one server causes a massive reshuffle of data/sessions.

**Consistent Hashing** solves this by mapping both servers and keys onto a circular hash ring ($0$ to $2^{32}-1$).

### The Virtual Nodes Strategy
A common mistake is drawing 3 points on a ring for 3 servers. If one server is more powerful or if the hash distribution is uneven, you get **Hot Spots**.

**The Senior Solution:** "We implement **Virtual Nodes**. Each physical server is mapped to *multiple* points on the ring (e.g., Server A maps to A1, A2, A3). This ensures the load is distributed much more uniformly across the server pool and makes scaling up/down seamless."

### Health Checks: The Silent Hero
A Load Balancer is only as good as its awareness of the fleet.
*   **Active Health Checks:** The LB periodically pings an `/health` endpoint on every server.
*   **Passive Health Checks:** The LB monitors traffic results; if a server returns 500 errors for 5 consecutive requests, it is marked "Down".

**Senior Signal:** "Our `/health` endpoint doesn't just return `200 OK`. it checks downstream dependencies (e.g., DB connection, Cache heart-beat). This prevents 'Zombie Servers' that are alive but can't serve requests."
