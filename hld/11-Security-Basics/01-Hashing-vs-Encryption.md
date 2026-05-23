# ⚡ 01 - Hashing vs. Encryption

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C131 |
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
*   **Two-Sentence Trigger:** Hashing is a one-way cryptographic function that maps an input of arbitrary size to a fixed-length string of bytes (the hash) which cannot be mathematically reversed to recover the original input. Encryption is a two-way function that scrambles plaintext into ciphertext using a key, allowing authorized entities holding the corresponding key to decrypt the ciphertext back into plaintext. It is triggered when designing database schemas and data transit paths, dictating how to protect passwords (one-way hashing) vs. how to secure API payloads and databases (two-way encryption).
*   **Scalability Dimension:** Primary: **Cryptographic CPU Computation Latency vs. Data Confidentiality & Integrity**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Cryptographic Techniques Matrix
| Dimension | One-Way Hashing (e.g., Bcrypt, SHA-256) | Symmetric Encryption (e.g., AES-256) | Asymmetric Encryption (e.g., RSA, ECC) |
| :--- | :--- | :--- | :--- |
| **Reversibility** | ❌ No. Irreversible by design. | ✅ Yes. Decryptable using the same key. | ✅ Yes. Decryptable using the private key. |
| **Keys Required** | None (uses optional Salt). | 1 Shared Secret Key. | 2 Keys: Public (encrypt) & Private (decrypt). |
| **Performance** | Variable (bcrypt is intentionally slow; SHA-256 is fast). | 🟢 Fast (highly optimized in modern CPU hardware). | 🔴 Slow (heavy mathematical modular exponentiation). |
| **Primary Use** | Password storage, data integrity, checksums. | Data-at-rest encryption (DB, S3 files), session tokens. | Secure key exchange (TLS handshake), digital signatures. |
| **Data Length** | Always fixed (e.g., SHA-256 is always 256 bits). | Variable (scales with plaintext size). | Variable (limited by key size). |

---

### Deep Dive: Password Hashing vs. Data Integrity Check
1. **Password Hashing (Slow & Resource Intensive):**
   * *The Problem:* If a database is hacked, attackers use GPUs to run billions of guesses per second against standard fast hashes (MD5, SHA-256) to crack passwords.
   * *The Solution:* Use CPU/Memory-hard algorithms like **bcrypt** or **Argon2id**. They include a **Work Factor (cost parameter)** which intentionally slows down execution (e.g., making it take 100ms per check), defending against brute-force attacks.
   * *Salts:* A random string appended to the password before hashing. This ensures two users with the password "password123" have completely different hashes, neutralizing **Rainbow Tables** (pre-calculated hash lists).
2. **Data Integrity Hashing (Fast):**
   * SHA-256 is designed to verify that a file or block of data has not been modified (e.g., Git commits or block storage checksums). It is optimized to be as fast as possible, making it unsafe for passwords.

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **CPU Exhaustion via bcrypt Work Factors:**
    *   *Problem:* Setting the bcrypt work factor too high (e.g., cost = 14, taking 1.5 seconds per hash). During peak traffic or a DDoS attack on the `/login` endpoint, application servers will suffer immediate CPU exhaustion, causing timeouts across all other endpoints.
    *   *Mitigation:* Benchmark and tune the work factor to take exactly 100-250ms on production-class hardware, and apply strict rate-limiting (WAF/Gateway) to the login route.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Stating they will "encrypt passwords in the database using AES-256." If the database is compromised, the attacker often finds the encryption keys in environment variables, allowing them to decrypt every password. Passwords must always be *hashed* with a slow salt algorithm.
*   Suggesting SHA-256 is a good choice for password hashing.

### Interview Tip (The "Strong Hire" Signal)
> *"We secure our data tiers using the principle of least exposure. For password storage, we use one-way hashing with Argon2id, incorporating random per-user salts to render precomputed Rainbow Tables useless. For our data at rest in PostgreSQL and S3, we use symmetric AES-256 encryption because of its high throughput, managing our encryption keys securely using AWS KMS HSMs."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
