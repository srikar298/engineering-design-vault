# ⚡ 03 - Network Security

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C049 |
| **Category** | Security Basics |
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
*   **Two-Sentence Trigger:** Network Security is the design and implementation of topological boundaries, access control lists, firewalls, and cryptographic protocols that protect distributed infrastructure from unauthorized network ingress and exploitation. It is triggered when launching cloud resources, segregating public traffic points (like load balancers) from private application nodes and database clusters using virtual networks, security groups, and service-to-service authentication.
*   **Scalability Dimension:** Primary: **Layer 7 Packet Inspection Latency Overhead vs. Boundary Isolation Depth**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Enterprise VPC Segmentation Topology
In a secure system, resources are segmented into subnets with varying levels of internet accessibility:
```
[ The Public Internet ]
         │
         ▼
[ WAF (Layer 7 Rules) ]  ◄── Blocks SQLi, XSS, and DDoS rate anomalies
         │
         ▼
[ Public Subnet ]        ◄── Contains: ALB (Application Load Balancer), NAT Gateway
         │
  (Security Group: Only allow port 443 outbound to App Subnet)
         ▼
[ Private Subnet ]       ◄── Contains: Application Microservices (No public IP address)
         │
  (Security Group: Only allow port 5432 inbound from App Subnet)
         ▼
[ Isolated Subnet ]      ◄── Contains: Primary/Replica Databases (Zero internet ingress/egress routes)
```

### Key Network Security Defenses
1. **Web Application Firewall (WAF):**
   * *What it does:* Inspects HTTP traffic at Layer 7. Blocks common web vulnerabilities (SQL Injection, XSS, Path Traversal) and filters traffic based on geographic IP ranges or request rates (DDoS mitigation).
   * *Trade-off:* Adds minor processing latency (5-15ms) to every incoming HTTP request.
2. **mTLS (Mutual TLS) & Zero-Trust Mesh:**
   * *The Concept:* Traditional networks assume "inside the VPC is safe." If an attacker compromises a single public container, they can query internal endpoints without authentication (**Lateral Movement**).
   * *The Solution:* Enforce a **Zero-Trust** model. Every microservice-to-microservice RPC call is encrypted and authenticated via mTLS, validating cryptographic client certificates on both sides before executing requests.
3. **VPC Endpoint / PrivateLink:**
   * *Use Case:* Connecting your app to a third-party managed database (e.g., Snowflake or MongoDB Atlas).
   * *Mechanism:* Bypasses public internet routing. Provisions a virtual network interface card (ENI) inside your private VPC, routing database packets over the cloud provider's internal fiber backbone directly to the host.

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **Lateral Attack Vectors (Flat Network Design):**
    *   *Problem:* Designing a "flat" VPC network where all microservices and databases sit in the same subnet with wide open security groups (`0.0.0.0/0`). If a vulnerability (like Log4j) compromises one microservice, the attacker immediately gains access to all databases.
    *   *Mitigation:* Apply strict **Microsegmentation**. Enforce Security Group rules at the container level (e.g., database security group *only* accepts ingress from app server security groups, blocking direct access from test pods).

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Deploying database nodes (Postgres/MySQL) in public subnets with public IP addresses, claiming "they are secure because we set a strong password."
*   Believing that an Application Load Balancer (ALB) is a sufficient firewall. ALBs distribute traffic; they do not perform deep packet inspection for Layer 7 attacks like WAFs do.

### Interview Tip (The "Strong Hire" Signal)
> *"We secure our distributed microservices using a Zero-Trust architecture. We isolate our application servers in private subnets and place our databases in isolated subnets with zero internet ingress routes. All service-to-service communication is authenticated and encrypted via mTLS. To prevent lateral movement, our Security Groups enforce the principle of least privilege, allowing database access only from designated application security groups on port 5432."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
