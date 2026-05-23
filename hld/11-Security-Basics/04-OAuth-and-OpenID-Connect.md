# ⚡ 04 - OAuth 2.0 & OpenID Connect (OIDC)

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C133 |
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
*   **Two-Sentence Trigger:** OAuth 2.0 is a delegated authorization framework allowing third-party applications to obtain limited access to HTTP resources on behalf of a user, while OpenID Connect (OIDC) is an identity layer built on top of OAuth 2.0 to provide federated authentication. It is triggered when implementing Single Sign-On (SSO) systems (e.g., "Login with Google") or permitting external applications to access API resources safely without sharing master user passwords.
*   **Scalability Dimension:** Primary: **Federated Authentication Scope Management vs. Authorization Flow Latency**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### OAuth 2.0 Authorization Code Grant Flow
This is the gold standard flow for server-side web applications:
```
Client Browser                  App Server                    Identity Provider (IdP)
      │                              │                                  │
      │── 1. /login (Click SSO) ────►│                                  │
      │◄── 2. HTTP 302 Redirect ─────│                                  │
      │      (To IdP Auth endpoint)  │                                  │
      │                              │                                  │
      │── 3. Authenticate & Approve Scope ─────────────────────────────►│
      │◄── 4. Redirect to App Server with Auth Code ────────────────────│
      │                                                                 │
      │── 5. POST Auth Code ────────►│                                  │
      │                              │── 6. Exchange Code for Tokens ──►│
      │                              │      (Via Private Client Secret) │
      │                              │◄── 7. Returns: Access & ID Token─│
```

### OAuth 2.0 vs. OpenID Connect
*   **OAuth 2.0 (Authorization):** Handles *permitting access* to resources. It issues an **Access Token** (typically an opaque string or JWT) that a client sends in the `Authorization: Bearer <token>` header to query APIs. It knows nothing about *who* the user is, only that they have permission.
*   **OpenID Connect (Authentication):** Built on top of OAuth 2.0. It handles *verifying identity*. It issues an **ID Token** (always a JWT) containing standard claims about the user (e.g., `iss` (issuer), `sub` (subject/user ID), `email`, `exp`).

---

### Key Security Protocols: PKCE & Token Rotation
1. **PKCE (Proof Key for Code Exchange) (Pronounced "Pixie"):**
   * *The Problem:* In mobile apps or Single Page Apps (SPAs), there is no secure backend to hide the Client Secret. Attackers can intercept the Authorization Code from the browser history or mobile OS redirects and exchange it for tokens.
   * *The Solution:* The client generates a random secret verifier ($A$) and sends its SHA-256 hash ($B$) in the authorization request. When exchanging the code for tokens, the client sends the raw verifier ($A$). The IdP hashes $A$ and checks if it matches $B$. This guarantees only the entity that initiated the login can retrieve the tokens. **PKCE is now mandatory for all web/mobile clients.**
2. **Refresh Token Rotation:**
   * *Mechanism:* Every time a client uses a Refresh Token to get a new Access Token, the Authorization Server invalidates the old Refresh Token and issues a new one.
   * *Mitigation:* If an attacker steals a refresh token and uses it, the next time the legitimate user attempts to renew their session with that same token, the server detects reuse and invalidates the **entire token family**, logging out all sessions immediately.

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **IdP Availability Dependency:**
    *   *Problem:* If your SSO provider (e.g., Okta or Auth0) goes down, users cannot log in.
    *   *Mitigation:* Cache public signing keys (JWKS) locally inside the application to verify existing active tokens without calling Okta, and use fallback local credentials for critical operational administrators.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Saying "We use OAuth 2.0 to authenticate our users." OAuth 2.0 is an *authorization* framework. You must mention OpenID Connect (OIDC) to show you understand identity federating.
*   Suggesting the Implicit Flow (which returns access tokens directly in the redirect URL fragment) for modern SPAs. The Implicit Flow is deprecated due to token leakage risks; always recommend **Auth Code Flow with PKCE**.

### Interview Tip (The "Strong Hire" Signal)
> *"For our user login and API authorization, we implement OpenID Connect using the Authorization Code Flow with PKCE. We verify user identity via the ID Token JWT, and authorize API queries using short-lived Access Tokens. To protect our mobile and SPA clients from authorization hijacking without a client secret, we enforce PKCE verifiers, and we implement Refresh Token Rotation to detect and immediately revoke compromised session families."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
