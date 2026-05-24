# 🏢 SD056 - Design an Enterprise Double-Entry Ledger

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Problem ID** | SD056 |
| **Category** | Finance |
| **Difficulty** | 🔴 Expert |
| **Interview Frequency** | 🔥 Must Know (2024–2026) |
| **Target Companies** | Stripe, Adyen, PayPal, Square, Uber |
| **SDE-2 Mandatory** | ✅ Yes |
| **Status** | Completed |
| **Times Practiced** | 1 |
| **Last Practiced** | 2026-05-24 |
| **Next Review** | 2026-06-24 |
| **Confidence** | 🟢 Applied |
| **Mastery** | 🟢 Expert |

---

## 📋 1. Core Requirements & Scale

### Functional Requirements
- Enforce strict double-entry bookkeeping: Every transaction consists of multiple journal entries where the sum of debits and credits is exactly zero.
- Track account balances in real-time.
- Enforce strict transaction ordering (no race conditions on balance updates).
- Support multi-currency ledger balances.

### Non-Functional Requirements
- **Strong Consistency & ACID Invariants**: No money created or lost due to concurrency anomalies or network partitions.
- **Idempotency**: Prevent double-charging on network retries.
- **Auditability**: Immutable ledger state; journal entries can never be updated or deleted, only reversed with a new transaction.
- **High Ingestion Performance**: Handle peaks up to 10k RPS.

### Scale Targets (Back-of-the-Envelope)
- **Daily Transactions**: 100M.
- **Peak Throughput**: 5,000 to 10,000 Transactions/second.
- **Ledger Storage**: Each transaction requires ~500 bytes. $100\text{M} \times 500\text{B} = 50\text{GB}$ storage growth per day.

---

## 📐 2. High-Level Architecture

```
                                    [ Payment Gateway / Client ]
                                                 │
                                                 ▼
                                        [ API Load Balancer ]
                                                 │
                                                 ▼
                                     [ Gateway / Authenticator ]
                                                 │
                                                 ▼
                     [ Idempotency Filter ] ◄────► [ Redis (Idempotency Key Lease Store) ]
                                                 │
                                                 ▼
                                     [ Ledger API Coordinator ]
                                                 │
                     ┌───────────────────────────┴───────────────────────────┐
                     ▼                                                       ▼
      [ Database Shard Cluster (PostgreSQL) ]                  [ Transactional Outbox Worker ]
      (Journal Entries & Account Balances)                                    │
                     │                                                        ▼
                     ▼                                               [ Message Queue (Kafka) ]
      [ Immutable Archival Storage (S3) ]                                     │
                                                                              ▼
                                                                  [ Downstream Event Handlers ]
                                                                   (Notifications / Reports)
```

---

## ⚖️ 3. Deep Dive & Core Components

### A. Double-Entry Bookkeeping Schema Design
To guarantee auditability, the database schema must follow strict accounting principles:
* **Accounts Table**: `account_id`, `name`, `type` (asset, liability, equity, revenue, expense).
* **Journals Table**: `journal_id`, `description`, `created_at`.
* **Journal Entries (Postings) Table**: `entry_id`, `journal_id`, `account_id`, `amount` (debits positive, credits negative), `currency`.
* **Database Constraint**: For any transaction, `SUM(postings.amount) = 0`. This is verified at the database level inside a transactional unit of work.

### B. Concurrency Control: Preventing Race Conditions on Balances
When 1,000 transactions hit the same account simultaneously (e.g., a popular merchant wallet), standard database updates cause lock contention or double-spending:
* **Optimistic Concurrency Control (OCC)**:
  * Accounts table has a `version` number. Balance updates check `WHERE version = current_version`.
  * **Trade-off**: Highly efficient for low-contention accounts, but causes massive write failures and retry loops under hot-account contention.
* **Log-Structured Writes (Accumulator Pattern)**:
  * Do not update a `balance` field in the account row directly. Instead, write new rows to the `postings` table and compute balances dynamically using a materialized view or read-cache aggregator.
  * **Trade-off**: Fast writes ($O(1)$ inserts), but increases read latency. We optimize this by caching balances in Redis and periodically writing balance checkpoints to the database.

### C. Transactional Outbox Pattern
Ledger states must sync with downstream systems (e.g. email receipt service, fraud checker, analytics engine).
* **The Problem**: Directly publishing to Kafka inside the database transaction causes dual-write failures if Kafka is down or slow, blocking database commits. Publishing *after* the commit can fail if the app crashes before the network call is made.
* **The Solution**: Write event payloads to an `outbox` table inside the same database transaction. A separate, lightweight background poller (Change Data Capture / Debezium) reads from the `outbox` table and publishes events to Kafka reliably.

---

## 🚫 4. Common Mistakes & Interview Playbook

### Common Mistakes (The "Junior" Signals)
- Storing balances as floating-point numbers (causes rounding errors like $0.0000001$ due to IEEE 754 precision limits). **Always use integers in the smallest currency unit (e.g., cents/micro-units).**
- Overwriting balance columns in database rows directly with `UPDATE accounts SET balance = balance + 10` without locking or isolation checks.
- Generating duplicate transactions during network retries because of a lack of gateway-level idempotency checks.

### Interview Tip (The "Strong Hire" Signal)
> *"We enforce double-entry constraints at the DB layer, representing monetary quantities as 64-bit bigints in the smallest currency unit. To scale under hot-wallet contention, we use log-structured journal writes (inserts only) rather than locks on account balance rows, and reconcile states downstream using a transactional outbox partition with CDC."*
