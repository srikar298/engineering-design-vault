# 🏗️ 02 - Scale From Zero (SDE-2 Refresher)

## 📖 The Concept
Scaling a system from 1 user to 10 million users requires transitioning from a single box (Monolith) to a distributed, stateless architecture.

## 📊 The SDE-2 Trade-off Table: Scaling Strategies

| Strategy | How it Works | Pros | Cons (The Trade-off) |
| :--- | :--- | :--- | :--- |
| **Vertical Scaling (Scale Up)** | Buy a bigger, faster server (more CPU/RAM). | Simple. No code changes required. | Hard limit (Hardware ceiling). Single Point of Failure. |
| **Horizontal Scaling (Scale Out)** | Add more servers to the pool. | Infinite scalability. High availability. | Complex. Requires Load Balancers and **Stateless** servers. |

## 🚫 The Interview Trap
**"I will horizontally scale my web servers to handle the load."**
You cannot just add servers if your web tier is **Stateful** (e.g., storing user session data in local server memory). If User A logs into Server 1, and their next request hits Server 2, they will be logged out.
*Better Answer:* "First, I will ensure the web tier is entirely **Stateless** by moving all session data to an external distributed cache like Redis. Then, I can horizontally scale the web servers behind a Load Balancer."

## 🚀 The SDE-3 Edge: Auto-Scaling with Predictive Metrics
When asked how to manage server counts dynamically:
Don't just say "Auto-scaling groups based on CPU." CPU is a *lagging* indicator. By the time CPU is at 90%, it takes 3-5 minutes to spin up a new EC2 instance; during that time, requests fail.
*The SDE-3 Answer:* "I would implement **Predictive Auto-Scaling** based on leading indicators (e.g., Queue Length or Request Rate). If the inbound request queue spikes, we scale out *before* the CPU maxes out."
