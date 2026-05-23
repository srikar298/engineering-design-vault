# 🔐 11 - Security Basics (C049-C136)

## 🧭 Security & Identity Study Path
Use this structured path aligned with your **Google Sheet Tracker** to master cryptography, tokens, network security boundaries, and authorization protocols:

### 🟢 1. Cryptographic & Token Foundations
*   [C131 - Hashing vs. Encryption](./01-Hashing-vs-Encryption.md)
*   [C132 - JSON Web Tokens (JWT)](./02-JWT.md)

### 🟡 2. Access Control & Authorization Delegations
*   [C133 - OAuth 2.0 & OpenID Connect (OIDC)](./04-OAuth-and-OpenID-Connect.md)
*   [C134 - RBAC vs. ABAC](./05-RBAC-vs-ABAC.md)

### 🔴 3. Network Boundaries & TLS Security
*   [C049 - Network Security](./03-Network-Security.md)
*   [C136 - TLS/SSL Handshake](./07-TLS-SSL-Handshake.md)

### 🟣 4. Secrets Lifecycle & Operations
*   [C135 - Secrets Management](./06-Secrets-Management.md)

---

## 🔑 1. Authentication vs. Authorization

- **Authentication (Who are you?)**: Verifying identity via Password, MFA, or Biometrics.
- **Authorization (What can you do?)**: Permissions managed via **RBAC** (Role-Based Access Control) or **ABAC** (Attribute-Based).

### Token-based Auth (JWT)
- **Pro**: Stateless, easy to scale.
- **Con**: Hard to revoke (must wait for expiry or use a blacklist in Redis).

---

## 🛰️ 2. Networking Security

| Strategy | Characteristic | Security Level |
| :--- | :--- | :--- |
| **TLS (HTTPS)** | Encrypts data from Client to Server. | Standard. |
| **mTLS (Mutual TLS)** | Both Client and Server verify each other's certificates. | **Gold Standard** (Service-to-Service). |
| **VPC / Subnets** | Isolates DB and App servers from the public internet. | Mandatory. |

**The Senior Signal:** "We implement a **Zero Trust** architecture where every service-to-service call is authenticated via **mTLS**. Even if an attacker breaches the perimeter, they cannot perform lateral movement because they lack the necessary client certificates."

---

## 🔒 3. Data Security & Secrets

### Secrets Management
- **The Trap:** Storing API keys or DB credentials in **Environment Variables**. These can be leaked through log dumps or process listings.
- **The Gold Standard:** Use a dedicated provider (AWS Secrets Manager / Vault). 
- **Secret Rotation:** Automatically rotate master passwords every 30-90 days without downtime.

**The SDE-3 Edge:** "We use **Dynamic Secrets**. Instead of a static DB password, our application requests a temporary credential from Vault that is valid only for 1 hour and is specific to that instance. This minimizes the 'Blast Radius' of a credential leak."

---

## 🚫 The Interview Trap
When asked about security, don't just say "I'll use a firewall." Talk about **The Principle of Least Privilege**. Every service should only have the absolute minimum permissions (IAM roles) required to do its job. 

**Senior Signal:** "Our API servers have an IAM role that allows reading from an S3 bucket but *cannot* delete or list other buckets in the account."

---
