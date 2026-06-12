# Single Responsibility Principle (SRP) - Deep Dive

> "A class should have one, and only one, reason to change." — Robert C. Martin

## 📋 Table of Contents
1. [Overview](#-overview)
2. [The "Reason to Change" Rule](#-the-reason-to-change-rule)
3. [Common Smells](#-common-smells)
4. [Before & After (Refactoring)](#-before--after-refactoring)
5. [Senior-Level Interview Questions](#-senior-level-interview-questions)

---

## 🔍 Overview
The **Single Responsibility Principle (SRP)** is about **Change Management**. While often simplified as "doing one thing," for a Senior Engineer, the true meaning is:

> "A class (or module) should have only one reason to change."

### 🎯 The "Stakeholder" Lens
A "reason to change" is almost always driven by a **Stakeholder** or an **Actor**.
- If the **Finance Team** wants to change how taxes are calculated, that's one responsibility.
- If the **Infra Team** wants to change how data is saved to a database, that's another responsibility.
- If both requirements live in the same class, SRP is violated because two independent stakeholders can force a change (and a potential break) on the same unit of code.

### Why it matters for Google SDE-2/Senior Interviews:
- **Engineering Judgment**: You aren't just splitting classes; you are identifying **Change Vectors**.
- **Ripple Effects**: SRP minimizes the chance that a change in pricing logic accidentally breaks your database connection code.
- **Decoupling**: It identifies the boundaries where your system should be able to evolve independently.

---

## 🚦 The "Reason to Change" Rule (Mental Model)
Ask these questions during an interview:
1. **"Who asked for this change?"** (A responsibility = a reason for change).
2. **"If pricing rules change, do I touch database code?"** (If yes, SRP is broken).
3. **"Is this class sensitive to more than one axis of change?"**

---

## 🏗️ Stakeholder Mapping Example
Consider an `InvoiceService`:
- `calculateTotal()` -> Owned by **Finance** (Business Logic)
- `saveToDatabase()` -> Owned by **DBA/Infra** (Persistence)
- `generatePdf()` -> Owned by **Product/Compliance** (Reporting)

**3 Stakeholders = 3 Reasons to Change = 3 Responsibilities.**

---

## ☣️ Common Smells
- **God Classes**: Huge classes that seem to do everything.
- **High Entanglement**: Changing one method requires changing five others.
- **Difficult Naming**: If you find it hard to name a class (e.g., `UserManagerAndLoggerAndPersistence`), it probably does too much.

---

## 🛠️ Before & After Examples (The Roadmap)
Check these Java examples for a practical deep dive into SRP violations and their refactored counterparts:

### 🔰 Level 1: User Management (Basic God Class)
- ☣️ **Violation**: [UserViolation.java](01-User_Management/UserViolation.java) (Class mix: Logic + Persistence + Logging)
- ✅ **Refactored**: [UserRefactored.java](01-User_Management/UserRefactored.java)
- **Stakeholders**: Finance vs. Infra vs. Security.

### 🥈 Level 2: Invoice Processing (The Dependency Trap)
- ☣️ **Violation**: [InvoiceViolation.java](02-Invoice_Processing/InvoiceViolation.java) (Class mix: Tax Logic + PDF Rendering)
- ✅ **Refactored**: [InvoiceRefactored.java](02-Invoice_Processing/InvoiceRefactored.java)
- **Stakeholders**: Finance (Tax Rules) vs. Product/UX (Branding/Layout).

### 🥇 Level 3: Payment Orchestration (Complex/Multi-Stakeholder)
- ☣️ **Violation**: [SRPPaymentViolation.java](03-Payment_Orchestration/SRPPaymentViolation.java) (Class mix: Risk + Provider + Audit)
- ✅ **Refactored**: [SRPPaymentRefactored.java](03-Payment_Orchestration/SRPPaymentRefactored.java)
- **Stakeholders**: Risk (Validation), Finance (Processor), Compliance (Audit).

---

## 🏆 The 10/10 Scorecard: SRP Mastery
*To hit a 10/10 in a Senior interview, you must address these advanced nuances.*

### 1. Cohesion vs. Coupling
- **High Cohesion**: All methods in a class should be strictly related to the responsibility.
- **Low Coupling**: The class should know as little as possible about other responsibilities.
- **Senior Insight**: SRP is about **"Finding the right grain size."** Too small (anaemic classes), and you have high coupling between too many units. Too large (God classes), and you lose cohesion.

### 2. When NOT to Split (Pragmatism)
Don't be a "Design Pattern Zealot."
- **Small Systems**: If the system is tiny and unlikely to grow, over-splitting increases "Mental Overhead" for no benefit.
- **Natural Grouping**: If two pieces of data always change together and are always used together, they might belong in the same "Information Expert" class.
- **Code Example**: [SRPPragmatic.java](04-Pragmatic_Example/SRPPragmatic.java) - Sometimes a single class is perfectly fine for cohesive operations in smaller contexts.

### 3. Transactional Integrity (The Edge Case)
When you split a class (e.g., `PaymentService` into `AccountDebiter` and `OrderUpdater`), you face a new problem: **Atomicity**.
- **The Problem**: What if the account is debited but the order update fails?
- **The 10/10 Solution**: Mention the **Unit of Work** pattern or **Distributed Transactions** (Saga) as a trade-off for splitting these responsibilities.

### 4. Performance & Object Overhead
- In performance-critical systems (like real-time trading), splitting into too many objects can cause GC pressure.
- **Summary**: Acknowledge that while SRP is good for code, you must monitor the **Runtime impact**.

---

## ❌ Junior Mistakes vs. ✅ Senior Solutions

| ❌ The Junior Approach | ✅ The Senior/LLD Approach | 🧠 Why it matters |
| :--- | :--- | :--- |
| One `UserService` class with `login()`, `saveToDb()`, `sendEmail()`, `exportCsv()`. | Separate `AuthService`, `UserRepository`, `EmailService`, `ReportExporter`. | Each class has exactly one stakeholder. Changing email templates never risks breaking DB logic. |
| Splitting by method count ("this class has 20 methods so I'll cut it in half"). | Splitting by **change vector** — who is the stakeholder forcing this change? | Arbitrary splitting creates anemic micro-classes that are tightly coupled right back together. |
| Mixing SQL/HTTP infrastructure with domain logic in the same method. | Infrastructure adapters implement domain interfaces. The domain never knows it's talking to SQL. | Domain tests run in milliseconds with no real DB. |

---

## 🏗️ Real-World Application (System Design)
In an **E-Commerce Order Processing System**:
- `OrderPricingEngine` → owned by **Finance** (taxes, discounts, currency)
- `OrderRepository` → owned by **DBA/Infra** (SQL schema, ORM, connection pool)
- `OrderNotifier` → owned by **Product** (email templates, push, SMS routing)
- `OrderAuditLogger` → owned by **Compliance** (immutable audit trail, GDPR)

If all four live in one `OrderService`, a Compliance audit requiring a new log field forces you to touch the same class as the Finance team's tax change — a direct collision requiring coordination between two unrelated teams on one file.

---

## 💥 FAANG / MNC Interview Preparation

### Q1: "SRP says 'one reason to change' — what exactly is a 'reason to change'?"
**The Senior Answer:**
A "reason to change" maps to a **Stakeholder** or **Actor**. The Finance team demanding new tax rules, the Infra team demanding a new DB driver, and the Product team demanding a new notification format are three independent actors — three independent reasons. Any class with more than one Actor can be forced to change for two unrelated reasons simultaneously, creating regression risk and team coordination overhead.

### Q2: "How do you find the right granularity for SRP? How do you avoid over-splitting?"
**The Senior Answer:**
The right grain is: "Will these two things ever need to change independently, driven by different stakeholders?" If they always change together — same Actor, same decision — they belong together (high cohesion). Over-splitting signals when a single business operation requires coordinating 10+ tiny anemic classes — that is procedural code wearing class-shaped wrappers.

### Q3: "How does SRP interact with transactional integrity when you split services?"
**The Senior Answer:**
This is the key trade-off. Splitting `PaymentService` into `AccountDebiter` and `OrderUpdater` loses the simple in-process transaction boundary. Solutions: (1) **Unit of Work** — collect all changes and commit atomically. (2) **Saga Pattern** — compensating transactions in a distributed system to roll back failed steps.

---

## 🪜 SRP Exercise Ladder
*Try these exercises to build your SRP muscles from Junior to Staff level.*

### Level 1: Recognition
- **Exercise**: Identify stakeholders for an `OrderService` containing `calculateTotal()`, `saveOrder()`, and `sendEmail()`.
- **Insight**: Many changes (taxes, service charges) can still be ONE responsibility if they are driven by the SAME decision-maker (Finance).

### Level 2: Mental Models
- **Exercise**: If `PasswordHasher` later adds password strength validation, is it still SRP-compliant?
- **Insight**: No. Validation rules (Product) and hashing algorithms (Security) are different change vectors.

### Level 3: Senior-Level LLD
- **Exercise**: Design a URL Shortener using only SRP-compliant classes.
- **Responsibilities**: Token generation, Persistence, Redirection, Analytics.

---

## 🎓 Interview-Grade Summary (Memorize This)
> "SRP means a class should be affected by only one category of change. If business rules and infrastructure rules both force modifications to the same class, SRP is violated. It’s not about counting methods; it’s about identifying independent change authority."

---

## 🚀 Final Sanity Check
Ask yourself: **"If this breaks, who will complain?"**
If the answer is more than one department or group, your class violates SRP.

---

## 📚 Further Reading / Patterns Linked
- SRP directly enables the **Facade Pattern** (a single clean interface hiding multiple backend responsibilities).
- Splitting by stakeholder maps to **Domain-Driven Design (DDD)** Bounded Contexts.
- Transactional integrity across split services uses the **Unit of Work** and **Saga Patterns**.
