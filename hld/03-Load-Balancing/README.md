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

## 🚫 The Interview Trap
**"I will just use Round Robin."**
Round Robin is a naive algorithm. In a real SDE-2 interview, if you have long-lived connections (like WebSockets) or uneven request sizes, Round Robin leads to **"Hot Spots"** (one server getting overwhelmed). 
*Better Answer:* "I'll use **Least Connections** or **Least Response Time** routing for more even distribution."

## 🚀 The SDE-3 Edge: Consistent Hashing
If the interviewer asks: *"How do you route a user to the exact same server every time (Session Stickiness) without storing session state centrally?"*

Do not say "Sticky Sessions via Cookies" (it breaks when servers scale up/down). 
Instead, talk about **Consistent Hashing**:
1. Map both the Servers and the User IDs (or IP) to a logical "Hash Ring".
2. A user is routed to the first server found moving clockwise on the ring.
3. If a server crashes, only a fraction of the users are re-routed, instead of a complete reshuffle. This is the foundation of distributed caches and databases like DynamoDB.
