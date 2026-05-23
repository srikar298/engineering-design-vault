# ⚡ 06 - Secrets Management

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C135 |
| **Category** | Security Basics |
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
*   **Two-Sentence Trigger:** Secrets Management is the cryptographic practice of securing, storing, distributing, and auditing sensitive credentials such as database passwords, API keys, SSH keys, and TLS certificates. It is triggered when configuring application deployment systems, replacing insecure hardcoded credentials and static environment variables with dedicated secret stores (like AWS Secrets Manager or HashiCorp Vault) that authorize access using platform IAM roles.
*   **Scalability Dimension:** Primary: **Secret Vault Network Latency Dependency vs. Credential Exposure Blast Radius**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Secret Storage Paradigms
Where do you store database credentials?
1. **Plain Text Config Files / Git:**
   * *Security:* 💀 Zero. Code leaks instantly compromise the system.
2. **Environment Variables:**
   * *How it works:* Credentials injected into container environment variables at runtime.
   * *Pros:* Simple, decoupled from code.
   * *Cons:* ⚠️ Moderately Insecure. Env variables can be leaked via server-side process dumps (`ps aux`), error stack logs, or container inspection endpoints (`/proc/self/environ`).
3. **Dedicated Vaults (AWS Secrets Manager / Vault):**
   * *How it works:* The application retrieves secrets dynamically via API calls authenticated by its runtime IAM role.
   * *Pros:* 🟢 Extremely Secure. Supports audit logging, automated rotation, and dynamic access.
   * *Cons:* Introduces a network dependency. If the secret vault goes down, your app cannot boot up or connect to the database.

---

### SDE-3 Patterns: Dynamic Secrets & Automated Rotation

#### 1. Dynamic Secrets
Instead of storing a static database password that lasts forever, HashiCorp Vault can generate a unique database user account on-the-fly for every app instance:
```
  [ App Pod ] ──► (Authenticates via IAM) ──► [ Vault Server ]
                                                     │
                                            (Auto-generates user)
                                                     ▼
  [ App Pod ] ◄── (Returns temporary credential) ◄── [ Postgres DB ]
                  (Expires in 1 Hour)
```
If an attacker compromises the app pod, the leaked credential is only valid for 1 hour and is tracked directly to that specific pod, minimizing the blast radius.

#### 2. Zero-Downtime Secret Rotation
To rotate database master passwords automatically without dropping client queries, we use a **two-step rotation strategy**:
1. **Create & Store:** Generate a new password ($P2$) and save it in the database as an active secondary user.
2. **Deploy:** Update the secret store. App servers retrieve the new secret ($P2$) and establish new connection pools. (Existing connections using $P1$ continue functioning).
3. **Revoke:** After a safety window (e.g., 24 hours), run a cron job to delete the old user credential ($P1$) from the database.

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **Secret Manager Rate Limiting:**
    *   *Problem:* Application servers query the secret manager on every single database query or API call, hitting API rate limits immediately and causing connection failures.
    *   *Mitigation:* Cache retrieved secrets locally in memory with a short TTL (e.g., 5-15 minutes), and use an in-memory decryption library.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Storing private keys or database passwords in environment variables directly without encryption or lifecycle management.
*   Suggesting manual rotation of passwords by database administrators (which guarantees operational delays and downtime).

### Interview Tip (The "Strong Hire" Signal)
> *"We secure our database credentials by using AWS Secrets Manager integrated with KMS. Our ECS containers authenticate via Task Execution IAM roles to retrieve secrets dynamically, caching them in memory with a 10-minute TTL to avoid hitting API rate limits. We implement zero-downtime secret rotation using Lambda functions that provision new secondary database credentials before deprecating old ones, ensuring zero dropped client transactions."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
