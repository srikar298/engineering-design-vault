# ⚡ 14 - Service Discovery

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C058 |
| **Category** | Microservice Infrastructure |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🟡 Medium |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Service Discovery is the mechanism by which microservices dynamically locate the network addresses (IP + port) of other services they need to call, without hardcoding addresses that change with every container restart or auto-scaling event. It consists of a Service Registry (a database of service → address mappings) and a discovery mechanism for services to register themselves and query for others.
*   **Scalability Dimension:** Primary: **Dynamic Cluster Elasticity** & **Zero-Downtime Scaling**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Client-Side vs Server-Side Discovery

**Client-Side Discovery:**
1. Service A queries the Registry (e.g., Consul, etcd) directly for Service B's available instances.
2. Service A runs a client-side load balancer (e.g., Netflix Ribbon) to pick one instance.
3. Service A connects directly to Service B's IP.
*   *Pros:* Fewer network hops. Client controls load balancing algorithm.
*   *Cons:* Each language/runtime needs its own discovery client library (polyglot overhead).

**Server-Side Discovery:**
1. Service A sends request to a Load Balancer or API Gateway.
2. The LB/Gateway queries the Registry for healthy Service B instances.
3. LB routes request to one instance.
*   *Pros:* Language-agnostic — works with any client. Centralized routing.
*   *Cons:* Extra hop through the LB (added latency). LB is a SPOF if not HA.

### Service Registry Options
| Tool | Mechanism | Consistency | Use Case |
| :--- | :--- | :--- | :--- |
| **Consul** | Gossip (membership) + Raft (consistency). Built-in health checks. | CP | General-purpose. Most popular. |
| **etcd** | Raft consensus. Used by Kubernetes internally. | CP | Kubernetes-native. |
| **Eureka** | AP — prioritizes availability over consistency. | AP | Netflix OSS stack. Java-centric. |
| **Kubernetes DNS** | Built-in. `service-name.namespace.svc.cluster.local`. | CP (etcd-backed) | Kubernetes environments (simplest). |

---

## 💥 3. Resiliency & Operations
*   **Observability (The "Signal"):**
    *   `Registry Stale Entry Count`: Stale entries from crashed instances that didn't deregister (causes requests to dead IPs).
    *   `Service Registration Latency`: Time between a new instance starting and being discoverable.
*   **Blast Radius (The "Impact"):**
    *   If the Service Registry cluster crashes, services can no longer discover new instances. Existing connections continue (cached addresses), but new connections and scaling events fail.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Not knowing that services must both **register on startup** and **deregister on shutdown** (or have TTL-based leases that expire automatically on crash).
*   Using DNS-based discovery without accounting for DNS TTL caching — a scaled-down instance's IP stays in DNS cache for the TTL duration, causing a percentage of requests to fail.

### Interview Tip (The "Strong Hire" Signal)
> *"In our Kubernetes cluster, we use native Kubernetes Service DNS for service discovery — it's zero-ops and backed by etcd. For services outside Kubernetes, we use Consul with agent-side health checks. Instances register with a 10s TTL; if a health check fails 3 times, Consul removes the instance from the registry within 30 seconds."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
