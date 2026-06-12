# ⚡ 05 - RBAC vs. ABAC

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C134 |
| **Category** | Security Basics |
| **Difficulty** | 🟢 Easy |
| **Interview Frequency** | 🟡 Medium |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** RBAC (Role-Based Access Control) is a static authorization model where permissions are grouped into specific, coarse-grained roles (e.g., Admin, Reader) and assigned to users, while ABAC (Attribute-Based Access Control) is a dynamic model that evaluates permissions in real-time based on attributes of the subject, resource, action, and environment. It is triggered when designing access control frameworks for APIs and internal systems, selecting between simple role mappings and granular, context-aware security policies.
*   **Scalability Dimension:** Primary: **Policy Evaluation Performance (CPU/DB overhead) vs. Access Rule Granularity**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Dynamic Evaluation Flow: ABAC
Unlike RBAC, which only checks the user's role string, ABAC evaluates the context dynamically:
```
  [ Client Request ] ──► (user_id="Bob", IP="10.0.0.5", time="21:00", resource="ledger_v1")
                                 │
                                 ▼
                     [ Policy Decision Point ] ◄── Evaluates Rego / JSON Rules
                                 │
                 ┌───────────────┴───────────────┐
           (Deny Access)                   (Allow Access)
           Outside working hours           IP is in VPC, action is read
```

### RBAC vs. ABAC Comparison Matrix
| Dimension | Role-Based Access Control (RBAC) | Attribute-Based Access Control (ABAC) |
| :--- | :--- | :--- |
| **Evaluation Method** | Check static role mapping (e.g., `user.hasRole('Admin')`). | Evaluate attributes: `f(user, resource, action, environment)`. |
| **Granularity** | 🔴 Coarse-grained. Static. | 🟢 Fine-grained. Dynamic. |
| **Complexity** | 🟢 Low. Databases use simple mapping tables. | 🔴 High. Requires policy engines (like [Open Policy Agent](../24-components-library/06-Infrastructure/Security_IAM/L069-Open-Policy-Agent/README.md)). |
| **Rule Explosion** | 🔴 High risk. Roles multiply into variants (e.g., `US_Billing_Editor`). | 🟢 Low risk. Attributes are combined dynamically in a single rule. |
| **Evaluation Latency**| ⚡ Microseconds (simple SQL JOIN / cache read). | 🟡 Milliseconds (compiling and evaluating rules). |
| **Primary Use Case** | Small-to-mid enterprise systems with standard hierarchies. | Complex, regulated systems (e.g., finance, healthcare, government). |

---

### The Role Explosion Problem (RBAC Limit)
In a growing system, RBAC quickly degrades. Suppose a business requirement is introduced: *"Editors can modify files, but only if they belong to the same regional department."*
*   **The RBAC approach:** You must create new roles: `HR_Editor_US`, `HR_Editor_EU`, `Sales_Editor_US`... This is called **Role Explosion**, which makes user permission tables unmaintainable.
*   **The ABAC approach:** You write a single policy:
    ```rego
    allow {
        user.role == "Editor"
        user.department == resource.department
        user.region == resource.region
        input.action == "write"
    }
    ```
    Permissions remain decoupled from roles, and no new mappings are created.

---

## 🧠 3. Advanced SDE-3 Architectures: ReBAC (Google Zanzibar)
RBAC is based on *Who you are*. ABAC is based on *What your attributes are*. 
But modern collaborative systems like Google Docs, GitHub, and Notion use **ReBAC (Relationship-Based Access Control)**, based on *How you are related to the resource*.

### The "Google Docs" Problem
In Google Docs, you can grant "Viewer" access to a specific folder. That folder contains sub-folders, which contain files. The permission must inherit down the graph. ABAC and RBAC both fail here because evaluating nested hierarchies recursively in a SQL database at query-time is too slow.

### Google Zanzibar (SpiceDB)
Google solved this with Zanzibar (open-sourced as SpiceDB). It is a globally distributed graph database that stores raw relationships: `UserA -> is_viewer_of -> FolderB -> contains -> FileC`.
*   **The ReBAC Advantage:** Permissions are resolved instantly by traversing a highly-optimized graph, allowing millisecond latency for infinite nesting and inheritance.
*   **When to use:** If your system resembles Google Drive, GitHub (Organizations $\rightarrow$ Teams $\rightarrow$ Repos $\rightarrow$ Issues), or any deeply nested hierarchy, skip ABAC and use ReBAC.

---

## 💥 4. Resiliency & Operations

### Decoupled Policy-as-Code (OPA)
To prevent lock-in and keep APIs fast, modern architectures decouple authorization logic using **[Open Policy Agent (OPA)](../24-components-library/06-Infrastructure/Security_IAM/L069-Open-Policy-Agent/README.md)**:
*   Instead of writing complex authorization logic directly inside Java/Go microservices, the microservice sends request attributes to a local sidecar daemon running OPA via high-speed HTTP/gRPC.
*   OPA evaluates the rules in memory and returns a simple Boolean `allow: true/false`, keeping evaluation latency under 1ms.

---

## 🚫 5. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Hardcoding dynamic, attribute-like checks as static roles (e.g., creating a role named `User_Under_Working_Hours`).
*   Recommending ABAC for every simple system. If your app only has `User` and `Admin`, implementing an OPA engine adds massive unnecessary operational complexity.

### Interview Tip (The "Strong Hire" Signal)
> *"For our microservice API, we use a hybrid approach. We use RBAC for coarse-grained access, mapping users to general roles like 'Developer' or 'Manager' which we cache in their JWT tokens. For fine-grained, context-aware operations, we evaluate ABAC policies using Open Policy Agent (OPA) running as a sidecar, checking environmental attributes like client IP, access time, and resource department matching before permitting writes. If we need to support deep nested hierarchies like Google Drive, I would pivot to a ReBAC model like Google Zanzibar."*

---

## 💡 6. My Custom Study Notes & Whiteboard

### Architecture Pragmatism: The Modular Monolith Exception
While OPA and ABAC are the gold standards for distributed microservices, **they are an anti-pattern for Modular Monoliths**. 

For my projects built as Modular Monoliths, I use a highly-pragmatic **Permissions-Based Access Control** model:
1. **The Schema:** Seed the database with 3 tables: `Roles` (e.g., 13 roles), `Permissions` (granular actions), and `Role_Permissions` (the mapping).
2. **The Cache:** Because the database is local to the monolith, a sub-millisecond local DB query (or an in-memory application cache loaded at boot) fetches the permission matrix.
3. **The Code:** The application strictly checks permissions (`if user.hasPermission('delete_user')`), NEVER roles (`if user.role == 'Admin'`).

This entirely solves the "Role Explosion" problem of classic RBAC without introducing the massive operational overhead (network hops, Envoy sidecars, Rego compilation) of OPA. OPA should only be introduced if the monolith is ever fractured into polyglot microservices.
