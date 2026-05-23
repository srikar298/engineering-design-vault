# ⚡ 02 - JSON Web Tokens (JWT)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C132 |
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
*   **Two-Sentence Trigger:** A JSON Web Token (JWT) is an open standard (RFC 7519) defining a compact, self-contained method for securely transmitting claims between parties as a digitally signed JSON object. It is triggered when designing stateless user authentication and inter-service authorization systems, enabling application servers to verify user identity mathematically without querying a central session database on every request.
*   **Scalability Dimension:** Primary: **Stateless Server Processing Speed vs. Immediate Token Revocation Control**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### JWT Structure (Base64Url Encoded)
A JWT consists of three parts separated by dots (`.`):
```
  [ Header ] . [ Payload ] . [ Signature ]
  
  1. Header: Specifies the signature algorithm (e.g., {"alg": "RS256", "typ": "JWT"}).
  2. Payload: Stores user data and expiration claims (e.g., {"sub": "123", "role": "admin", "exp": 18000000}).
  3. Signature: Built by hashing Header + Payload using a secret key (HS256) or private key (RS256).
```
> [!WARNING]
> The Payload is only **Base64Url encoded**, not encrypted! Anyone who intercepts the token can read the user data. Never store passwords, SSNs, or sensitive PII inside a JWT payload.

### Stateless JWT vs. Stateful Database Session
| Dimension | Stateless JWT | Stateful Session (e.g., Session ID in Redis) |
| :--- | :--- | :--- |
| **Verification** | Cryptographic check (CPU-bound) by reading signature. | Database lookup (I/O-bound) of session ID. |
| **Scalability** | 🟢 Infinite. Servers require no shared database. | 🔴 Limited by cache/database I/O limits. |
| **Revocation** | 🔴 Hard. Token remains valid until `exp` time hits. | 🟢 Instant. Delete session key from Redis. |
| **Token Size** | Large (hundreds of bytes containing all claims). | Tiny (32-character session ID string). |

---

### Mitigating the Revocation & Storage Problem
1. **The Short-Expiry + Refresh Pattern:**
   * Configure access tokens (JWT) to expire in **15 minutes**.
   * Issue a stateful **Refresh Token** (stored in a database) to the client.
   * Every 15 minutes, the client exchanges the refresh token for a new access JWT. If a user is banned or logs out, delete the refresh token from the DB. The user's access is revoked within 15 minutes max.
2. **Where to Store the Token on the Client:**
   * *Memory (React State):* High security (immune to XSS theft), but token is lost on page refresh.
   * *LocalStorage/SessionStorage:* Easy to use, but highly vulnerable to **XSS (Cross-Site Scripting)** attacks. A malicious script can read the token and hijack the user session.
   * *HttpOnly Cookie (The Gold Standard):* Stored in browser cookies with `HttpOnly`, `Secure`, and `SameSite=Strict` flags. Javascript cannot read HttpOnly cookies, neutralizing XSS theft, and SameSite flags protect against **CSRF (Cross-Site Request Forgery)**.

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **The Compromised Signing Key:**
    *   *Problem:* If your JWT private signing key is leaked or compromised, attackers can forge admin-level JWT tokens locally, bypassing all authentication checks globally.
    *   *Mitigation:* Use asymmetric signing (RS256) where keys are rotated automatically. Expose the active public verification keys via a standard **JWKS (JSON Web Key Set)** endpoint. Resource servers cache JWKS keys and auto-fetch new keys when a token signed with an unknown key ID arrives.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Storing highly sensitive database keys or passwords in the JWT payload because they think "JWTs are encrypted and secure."
*   Creating JWT access tokens that are valid for 30 days without any revocation or blacklisting mechanism.

### Interview Tip (The "Strong Hire" Signal)
> *"For our user authentication, we enforce stateless JWTs signed with asymmetric RS256 keys, distributing public verification keys via an autoconfigured JWKS endpoint. To protect against XSS session theft, we store JWTs in HttpOnly, Secure cookies with SameSite=Strict flags. We mitigate token revocation issues by enforcing a short 15-minute access token lifespan, paired with a stateful database-backed refresh token rotation pattern to authorize session renewals."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
