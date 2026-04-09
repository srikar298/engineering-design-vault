# 🔐 11 - Security Basics (The SDE-2 Standard)

Security is often an afterthought in System Design interviews, but ignoring it is a red flag. You must design systems that are "Secure by Default."

---

## 🔑 1. Authentication vs. Authorization

- **Authentication (Who are you?)**: Verifying identity via Password, MFA, or Biometrics.
- **Authorization (What can you do?)**: Permissions managed via **RBAC** (Role-Based Access Control) or **ABAC** (Attribute-Based).

### Token-based Auth (JWT)
- **Pro**: Stateless, easy to scale.
- **Con**: Hard to revoke (must wait for expiry or use a blacklist in Redis).

---

## 🛰️ 2. Networking Security

- **HTTPS (TLS)**: Encrypts data in transit. Mandatory for all APIs.
- **VPC (Virtual Private Cloud)**: Isolates your infrastructure from the public internet.
- **Private Subnets**: Keep your DBs and internal services here. They should NOT have public IPs.
- **Firewalls / Security Groups**: Only allow traffic on specific ports (e.g., allow 443 to Load Balancer, but only allow LB to App Server).

---

## 🔒 3. Data Security

- **Encryption at Rest**: Encrypting data before it hits the disk (AES-256).
- **Secrets Management**: Never hardcode API keys or DB passwords. Use a vault (e.g., AWS Secrets Manager, HashiCorp Vault).
- **Rate Limiting**: Protects against Brute-force and DDoS attacks.

---

## 🚀 The SDE-2 Interview Tip
When drawing your diagram, mention: **"All communication between services happens over mTLS (Mutual TLS), and our database resides in a private subnet with no public access."** This immediately signals a high level of seniority.
