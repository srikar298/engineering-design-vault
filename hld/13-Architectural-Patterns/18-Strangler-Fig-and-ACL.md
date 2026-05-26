# Strangler Fig Pattern & Anti-Corruption Layer

| Field       | Value                          |
|-------------|--------------------------------|
| Concept IDs | C099, C100, C101               |
| Category    | Architectural Patterns         |
| Difficulty  | 🟡 Medium                      |
| Frequency   | 🔥 High                        |
| Tags        | `migration`, `legacy`, `ddd`, `strangler-fig`, `acl`, `branch-by-abstraction`, `proxy`, `api-gateway` |

---

## 1. The Core Concept

### The Migration Problem — Why Big-Bang Rewrites Fail

You have a monolith. It's 8 years old. It handles 2 million requests per day. It's written in PHP 5.6, has no tests, and the only engineer who understood it left three years ago. Management wants it modernized into microservices. What do you do?

The tempting answer: **big-bang rewrite**. Stop the world, rebuild everything from scratch in Go with a clean architecture, then cut over on day X.

**History shows this approach fails approximately 80% of the time.** Famous examples:
- **Netscape 6.0**: Engineers convinced leadership to rewrite the browser from scratch. Took 4 years. By the time it shipped, Mozilla was years behind competitors. Market share never recovered.
- **Meltdown (Healthcare.gov)**: The original site launch was essentially a big-bang deployment of a completely new system. The result was catastrophic public failure under load.
- **Heatmiser**: Countless small startups that rewrote their Ruby app in Node "for performance." Burned months, lost business momentum, discovered the old system's quirks only after users started hitting edge cases.

**Why big-bang rewrites fail:**
1. The old system's behavior is the specification. Undocumented edge cases accumulate over years.
2. You don't know what you don't know until you're in production.
3. Development takes longer than estimated. The old system keeps evolving during the rewrite.
4. The cutover day is a single massive risk event. If it fails, rollback is equally painful.
5. Team morale collapses when the rewrite date keeps slipping.

**The solution:** migrate incrementally, in production, with zero downtime. The Strangler Fig pattern is the canonical approach.

---

## 2. Deep Dive — The Strangler Fig Pattern

### Naming and Metaphor

Martin Fowler coined the term in 2004, inspired by the **strangler fig tree** (Ficus aurea) native to the rainforests of southeast Asia.

The strangler fig begins life as a seed dropped by a bird high in the canopy of a host tree. It sends roots down to the ground and vines up around the trunk. Over decades, the fig's structure grows around the host. Eventually, the original host tree dies and decays — the fig's own structure has replaced it. To an observer, the transition is imperceptible. The tree is always alive and growing.

**Applied to software:**
- The **host tree** = your legacy monolith.
- The **fig** = your new microservices.
- The **roots** = the API gateway / proxy that intercepts traffic.
- The gradual entwining = incremental feature migration.

### The Three-Phase Strategy

```
Phase 1: Intercept
====================
All traffic flows through proxy. Proxy routes 100% to monolith.

[Clients] ---> [Proxy/Gateway] ---> [Monolith]

Phase 2: Branch
=================
New features go to new services. Old features still go to monolith.
Selected old features get migrated, verified, then routed to new service.

                                 +---> [Monolith]     (old features)
[Clients] ---> [Proxy/Gateway] --|
                                 +---> [User Service] (migrated)
                                 +---> [Order Service](new feature)

Phase 3: Strangle
==================
Monolith handles only residual features (or nothing at all).

                                 +---> [User Service]
[Clients] ---> [Proxy/Gateway] --+---> [Order Service]
                                 +---> [Payment Service]
                                 +---> [Notification Service]
             [Monolith decommissioned]
```

### Implementation Details

**Step 1: Install the Strangler Facade**

The facade (API Gateway or reverse proxy like nginx/Envoy) sits in front of the monolith. Initially it forwards 100% of traffic to the monolith — purely transparent passthrough. This is your foundation. Deploy it, verify it causes no latency regression, and leave it for a sprint.

```
nginx strangler facade (Phase 1 - passthrough):

upstream monolith {
    server legacy-app:8080;
}

server {
    location / {
        proxy_pass http://monolith;
    }
}
```

**Step 2: Extract and Verify a Single Feature**

Pick the simplest, most isolated feature to migrate first. Good candidates:
- Read-only endpoints (no writes = no data consistency risk)
- Features with clear boundaries (user profile fetch, product catalog)
- Features with high traffic (validates new service under load)

Build the new microservice. Deploy it alongside the monolith. **Shadow traffic** (also called dark launching): send traffic to both old and new, compare responses, use the old response as the actual response. This validates the new service without risking user impact.

```
Shadow/Dark Launch routing (nginx lua or Envoy):

User Request
     |
     v
[API Gateway]
     |
     |---- (100% response to user) ----> [Monolith]
     |
     +---- (fire-and-forget shadow) ----> [New User Service]
                                               |
                                        [Log differences]
                                        [Alert if divergent]
```

**Step 3: Route Traffic to New Service**

Once shadow mode shows equivalence, start routing real traffic. Use feature flags for controlled rollout:

```
# Feature flag configuration (LaunchDarkly / homegrown):
route_user_profile_to_new_service: true

# API Gateway routing logic:
if feature_flag("route_user_profile_to_new_service"):
    proxy_pass http://new-user-service
else:
    proxy_pass http://monolith
```

Start at 1% → 10% → 50% → 100%. Monitor error rates, latency, and business metrics at each step. Roll back instantly by flipping the flag.

**Step 4: Strangler Data Migration**

Migrating the service without migrating the data is incomplete. The new service needs its own datastore. Options:

| Strategy | Description | Risk |
|----------|-------------|------|
| **Dual writes** | New service writes to both old DB and new DB during transition | Medium — consistency between DBs |
| **CDC (Change Data Capture)** | Stream changes from old DB to new DB using Debezium/Kafka | Low — async, eventually consistent |
| **Bulk sync + CDC** | Bulk copy historical data, then apply CDC for ongoing changes | Low — industry standard |
| **Big-bang cutover** | Stop writes, migrate DB, start new service | High — downtime |

**Recommended:** CDC via Kafka. Use Debezium to stream changes from the legacy MySQL/Postgres to Kafka topics. The new service consumes these events to build its own read-optimized datastore.

### Real-World Strangler Fig Examples

**Amazon (2001-2006)**:
Amazon's famous transition from a monolith to microservices is the canonical strangler fig. Werner Vogels described it: teams started by putting APIs in front of existing functionality. Traffic was incrementally routed to new services. The monolith was never "rewrote" — it was eaten from the outside in, service by service, over years.

**UK Government Digital Service (gov.uk)**:
The GOV.UK website used the strangler fig pattern to move from a complex legacy CMS to a simpler, service-oriented architecture. The API gateway intercepted requests; new content services were deployed for specific government departments. The legacy CMS powered remaining departments until they were migrated.

**Shopify**:
Shopify's shift from a Rails monolith to a modular monolith (component-based "Pods") used strangler fig principles internally. Features were encapsulated in pods with clear APIs before being potentially split out.

---

## 3. Anti-Corruption Layer (ACL)

### The DDD Origin

The Anti-Corruption Layer is a term from Eric Evans' seminal book **Domain-Driven Design** (2003). It appears in the context of **bounded contexts** — the idea that different parts of a large system have their own domain model, language, and rules.

When two bounded contexts need to integrate, especially when one is a legacy system or third-party service with a poorly designed domain model, you must prevent the "corrupt" model from leaking into your clean domain.

**The corruption scenario:**

You're building a modern e-commerce platform. You integrate with a legacy ERP system that has been in production since 1998. The ERP's API returns:

```json
{
  "CUST_ID": "C-000234",
  "ACCT_NO": "ACC-9921-B",
  "CUST_NM": "John Smith",
  "ADDR_LINE1": "123 Main St",
  "DLVR_FLGS": "Y|N|N|Y",
  "ORD_HIST": [{ "ORD_NO": "0029183", "ORD_DT": "20190412" }]
}
```

If you allow this model to flow directly into your codebase, you'll have `CUST_ID`, `DLVR_FLGS`, and `ORD_HIST` appearing in your `OrderService`, your `ShippingService`, and your `NotificationService`. Your entire domain is polluted by a 1998 ERP's naming conventions and data shapes.

### ACL Structure

```
ACL Translation Layer:

Legacy ERP System                     Your Domain
┌─────────────────┐                  ┌──────────────────┐
│  CustomerRecord │                  │  User Entity     │
│ ─────────────── │                  │ ──────────────── │
│  CUST_ID        │                  │  id: UUID        │
│  CUST_NM        │  ┌─────────┐     │  name: String    │
│  ADDR_LINE1     │->│   ACL   │---> │  address: Address│
│  DLVR_FLGS      │  │(Adapter)│     │  deliveryPrefs:  │
│  ORD_HIST       │  └─────────┘     │   DeliveryPrefs  │
└─────────────────┘                  │  orderHistory:   │
                                     │   List<Order>    │
                                     └──────────────────┘
```

**ACL implementation (TypeScript example):**

```typescript
// Legacy ERP model — we cannot change this
interface ERPCustomerRecord {
  CUST_ID: string;          // "C-000234"
  ACCT_NO: string;          // "ACC-9921-B"
  CUST_NM: string;          // "John Smith"
  ADDR_LINE1: string;
  DLVR_FLGS: string;        // "Y|N|N|Y" (pipe-delimited booleans)
  ORD_HIST: Array<{ ORD_NO: string; ORD_DT: string }>;
}

// Your clean domain model
interface User {
  id: string;
  accountNumber: string;
  name: string;
  address: { line1: string };
  deliveryPreferences: {
    expressAllowed: boolean;
    weekendDelivery: boolean;
    signatureRequired: boolean;
    leaveAtDoor: boolean;
  };
  orderHistory: Array<{ orderId: string; orderDate: Date }>;
}

// Anti-Corruption Layer: translates ERP model → your domain
class ERPCustomerACL {
  translate(record: ERPCustomerRecord): User {
    const dlvrFlags = record.DLVR_FLGS.split('|').map(f => f === 'Y');

    return {
      id: this.normalizeCustomerId(record.CUST_ID),
      accountNumber: record.ACCT_NO,
      name: record.CUST_NM,
      address: { line1: record.ADDR_LINE1 },
      deliveryPreferences: {
        expressAllowed: dlvrFlags[0],
        weekendDelivery: dlvrFlags[1],
        signatureRequired: dlvrFlags[2],
        leaveAtDoor: dlvrFlags[3],
      },
      orderHistory: record.ORD_HIST.map(o => ({
        orderId: o.ORD_NO,
        orderDate: this.parseERPDate(o.ORD_DT), // "20190412" → Date
      })),
    };
  }

  private normalizeCustomerId(erpId: string): string {
    // Strip "C-" prefix and zero-pad to UUID format
    return erpId.replace('C-', '').padStart(8, '0');
  }

  private parseERPDate(erpDate: string): Date {
    // ERP uses YYYYMMDD format
    const year = parseInt(erpDate.substring(0, 4));
    const month = parseInt(erpDate.substring(4, 6)) - 1;
    const day = parseInt(erpDate.substring(6, 8));
    return new Date(year, month, day);
  }
}
```

### ACL Design Principles

**1. The ACL is a boundary, not a leaky membrane.**
Your domain code should never import or reference the legacy model types. The ACL is the only place where `ERPCustomerRecord` is mentioned. This keeps the legacy model change-isolated.

**2. ACL = Adapter + Facade + Translator.**
- **Adapter**: converts one interface to another.
- **Facade**: hides the complexity of the legacy API.
- **Translator**: maps between domain languages.

**3. The ACL is bidirectional if needed.**
If you write back to the ERP, you need a reverse translation: `User` → `ERPCustomerRecord`. Keep the two translations in the same ACL class.

**4. Validate at the ACL boundary.**
The legacy system's data may be dirty. Your ACL is the right place to validate, sanitize, and raise domain errors for invalid data — before it enters your clean domain.

### Real-World ACL Examples

**Payment Gateway Integration (Stripe, Braintree):**
Your internal order domain has `Order`, `LineItem`, `Customer`. Stripe has `PaymentIntent`, `PaymentMethod`, `Charge`. The ACL translates:
- `Order` → `CreatePaymentIntentRequest`
- `Stripe PaymentIntent` → your `PaymentTransaction` entity

Your service layer never knows about `PaymentIntent` — it only knows `PaymentTransaction`. If you switch from Stripe to Braintree tomorrow, you only rewrite the ACL.

**Google Maps / Address Validation APIs:**
External APIs return address structures in their own format. Your ACL normalizes them to your `Address` value object before persisting.

**External Identity Providers (Auth0, Okta):**
JWT claims from Auth0 look different from Okta's. Your ACL translates both to your internal `AuthenticatedUser` context object.

---

## 4. Branch by Abstraction

An alternative migration strategy when you can't easily use a proxy (e.g., internal library migration rather than an HTTP endpoint migration).

**The Algorithm:**

```
Step 1: Create an abstraction (interface) for the functionality to be replaced.

interface UserRepository {
  findById(id: string): Promise<User>;
  save(user: User): Promise<void>;
}

Step 2: Have the existing code implement the abstraction.

class MonolithUserRepository implements UserRepository { ... }

Step 3: Replace callers to use the abstraction (not the concrete impl).

// Before: const repo = new MonolithUserRepository();
// After:  const repo: UserRepository = repositoryFactory.create();

Step 4: Build the new implementation behind the abstraction.

class MicroserviceUserRepository implements UserRepository { ... }

Step 5: Route some/all callers to the new implementation via feature flag.

const repo = featureFlags.isEnabled('new-user-service')
  ? new MicroserviceUserRepository(httpClient)
  : new MonolithUserRepository(db);

Step 6: Once new impl is proven, delete the old impl and remove the abstraction.
```

**Branch by Abstraction vs. Strangler Fig:**

| Dimension | Strangler Fig | Branch by Abstraction |
|-----------|---------------|----------------------|
| Level | Network/HTTP | Code/Library |
| Mechanism | Proxy routing | Interface + feature flag |
| Language agnostic | Yes | No (requires refactoring source) |
| Best for | Service extraction | In-process library replacement |
| Traffic splitting | URL/route based | Code-level |
| Rollback | Flag flip or route change | Flag flip |
| Requires source access | No (proxy only) | Yes |

---

## 5. Pattern Comparison

| Pattern | Problem Solved | Mechanism | When to Use |
|---------|---------------|-----------|-------------|
| **Strangler Fig** | Migrate monolith → microservices safely | Proxy intercepts + gradual rerouting | Replacing HTTP endpoints incrementally |
| **ACL** | Integrate with legacy/third-party without domain pollution | Adapter + translator at boundary | Any external integration with different domain model |
| **Branch by Abstraction** | Swap internal implementations safely | Interface + feature flag | Migrating in-process libraries or components |
| **Dark Launch / Shadow** | Validate new service without user impact | Parallel execution, results compared | Before routing real traffic to a new service |
| **Feature Flags** | Control rollout granularity | Runtime configuration | Complement to strangler fig and branch by abstraction |
| **Big-Bang Rewrite** | ~~Replace everything at once~~ | ~~Stop old, start new~~ | **Never. Don't do this.** |

---

## 6. SDE-2 Interview Script

**Interviewer: "How would you migrate our 10-year-old monolith to microservices without downtime?"**

---

**Opening — challenge the premise:**

> "Before I dive into the how, I want to flag something important: this question comes up a lot, and the most common mistake I've seen is attempting a big-bang rewrite. Netscape did this with version 6 and lost years of market share. The safer and more reliable approach is the Strangler Fig pattern — incremental migration in production. Let me walk you through how I'd structure this."

**Phase 1 — Install the facade:**

> "First, I'd install an API Gateway or reverse proxy — let's say Kong or Envoy — in front of the monolith. Day one, it's a transparent passthrough. No routing logic yet. This gives us the infrastructure foundation without any risk. We run this for a sprint, verify there's no latency regression, and then we have our strangler facade in place."

**Phase 2 — Pick the right first target:**

> "For the first feature to extract, I'd pick something small, isolated, and ideally read-heavy. User profile lookup is a classic example — it has a clear boundary, well-understood behavior, and we can shadow it safely. I'd build the new User Service, deploy it alongside the monolith, and run it in shadow mode: 100% of requests still go to the monolith, but we fire-and-forget copies to the new service and compare responses. We log any divergence. This validates correctness without any risk."

**Phase 3 — Gradual cutover:**

> "Once shadow mode shows we're at 99.9% equivalence, I'd start routing real traffic. We use a feature flag — 1% of traffic to the new User Service. Monitor error rates, p99 latency, and business metrics. At 10%, 50%, 100%. If anything goes wrong at any stage, we flip the flag back in seconds."

**ACL for external integrations:**

> "Now, during this migration, when the new services need to talk to the old monolith's database or to third-party systems, I'd use an Anti-Corruption Layer. For example, if User Service needs data from our legacy ERP that returns customer records in a 1998-era format with cryptic field names, I'd build an ACL class that translates the ERP model into our clean User entity. This way, the legacy system's model never pollutes our new domain."

**Data migration:**

> "The trickiest part is the database. The monolith uses a shared database — that has to change. For each extracted service, I'd use Change Data Capture via Debezium and Kafka to stream changes from the monolith's Postgres to the new service's dedicated store. The service builds its read model by consuming these events. We run dual-write during transition, verify consistency, and then cut over reads to the new store."

**Wrap-up:**

> "The key principle throughout is: always be releasable. Every step should be reversible with a feature flag or route change. The monolith should always be running and handling fallback. We strangle it slowly until it's empty — then decommission it. This entire process might take 12-18 months, but we never have a big-bang cutover day, and we never take the system down."

---

## 7. SDE-2+ Readiness Checklist

- [ ] Can explain why big-bang rewrites fail (80% failure rate, undocumented edge cases, scope creep, single risk event).
- [ ] Can name the Strangler Fig tree and explain the metaphor correctly.
- [ ] Can describe the three phases: Intercept, Branch, Strangle.
- [ ] Can explain the role of the proxy/API Gateway as the strangler facade.
- [ ] Can describe shadow/dark launching and why it's done before real traffic routing.
- [ ] Knows how feature flags enable granular traffic splitting (1% → 10% → 50% → 100%).
- [ ] Can explain the database migration challenge: why shared databases must be decomposed.
- [ ] Can describe CDC (Change Data Capture) using Debezium/Kafka for data migration.
- [ ] Can explain the Anti-Corruption Layer from DDD and when to use it.
- [ ] Can give a real example of ACL: payment gateway integration (Stripe model ≠ internal Order model).
- [ ] Can write an ACL class that translates between two domain models.
- [ ] Can explain Branch by Abstraction and contrast it with Strangler Fig.
- [ ] Can articulate when ACL is needed vs. when a simple adapter suffices.
- [ ] Knows real examples: Amazon monolith → microservices, UK GDS modernization.
- [ ] Can explain what "bounded context" means and how ACL enforces bounded context boundaries.
- [ ] Can discuss the trade-off between migration velocity and operational overhead of running two systems simultaneously.
- [ ] Knows the difference between feature flags for traffic routing vs. feature flags for user experimentation.
