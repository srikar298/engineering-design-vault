# Saga Pattern for Distributed Transactions (C077)
- **Category**: Distributed Transactions
- **Difficulty**: 🔴 Hard
- **Frequency**: 🔥 High

---

## 1. The Core Concept

### The Transactional Problem
In monolithic systems, maintaining data consistency is a solved problem. We rely on the database's ACID properties (Atomicity, Consistency, Isolation, Durability) to wrap multiple updates in a single transaction. If any write fails, the database engine rolls back all modifications.

However, in a microservices architecture, a single business process often spans multiple services, each owning its private database (Database-per-Service pattern). For example, a typical e-commerce checkout flow might look like this:

```
[Order Service] ──(Create Order)──> [Payment Service] ──(Charge Card)──> [Inventory Service] ──(Reserve Items)
```

If the inventory reservation fails because items went out of stock after the payment was charged, we must undo the payment and mark the order as failed. A traditional monolithic database cannot coordinate this across three separate databases.

#### Why 2-Phase Commit (2PC) Doesn't Scale
To solve distributed transactions, database theorists designed **2-Phase Commit (2PC)**:
1. **Prepare Phase**: The coordinator service asks all participating databases (resource managers) if they are ready to commit. The databases acquire locks on the records and respond "Yes" or "No".
2. **Commit Phase**: If all databases respond "Yes", the coordinator sends a commit command. If any database responds "No" or times out, the coordinator sends a rollback command.

While 2PC provides strong serializability, it is highly problematic in modern high-throughput distributed systems:
* **Blocking Protocol**: 2PC is a blocking protocol. If the coordinator crashes during the commit phase, participants remain in limbo, holding locks indefinitely. This causes cascading resource exhaustion across the system.
* **Latency Overhead**: Locks are held on database rows during the entire two round-trips over the network. If network latency is 50ms, rows are locked for at least 100ms, severely degrading write throughput.
* **SaaS and NoSQL Incompatibility**: 2PC requires standard XA transaction support. Modern microservices frequently use NoSQL databases (Cassandra, DynamoDB) or third-party SaaS APIs (Stripe, Twilio) that do not support XA or 2PC protocols.
* **Tight Coupling**: Services must coordinate synchronously, breaking the operational independence of microservices.

### The Saga Solution
A **Saga** is a design pattern that manages distributed transactions through a sequence of **local transactions**. 
* Every step in the saga represents a local transaction in a specific microservice.
* Each local transaction updates the database and publishes an event or message to trigger the next step in the saga.
* If a local transaction fails (e.g., due to business logic violation, like insufficient funds or stock), the Saga executes a series of **compensating transactions** in reverse order to undo the changes made by preceding local transactions.

### Architectural Flavors: Orchestration vs. Choreography

#### 1. Orchestration Saga
A centralized service (the **Orchestrator**) coordinates the state transitions. It tells each participant what transaction to execute, listens for success/failure, and triggers compensations if necessary. The orchestrator maintains the current state of the transaction in a persistent **Saga Log**.

```
                           +----------------------+
                           |   Saga Orchestrator  |
                           |  (Manages Saga Log)  |
                           +--+---------------+--+
                              |               |
             1. CreateOrder   |               | 3. ChargePayment
             (and wait response)              | (and wait response)
                              v               v
                      +-------+---+       +---+-------+
                      |   Order   |       |  Payment  |
                      |  Service  |       |  Service  |
                      +-----------+       +-----------+
```

#### 2. Choreography Saga
There is no central coordinator. Instead, participating services listen to events emitted by other services and execute their local transactions accordingly. The system state transitions via a decentralized event loop.

```
+---------------+              +-----------------+              +-------------------+
| Order Service |              | Payment Service |              | Inventory Service |
+-------+-------+              +--------+--------+              +---------+---------+
        |                               ^                                 ^
        | 1. Emits "OrderCreated" event |                                 |
        +-----------------------------> |                                 |
                                        | 2. Emits "PaymentCharged" event |
                                        +-------------------------------> |
```

---

## 2. Deep Dive

### Choreography Saga Internals
In a choreography saga, services communicate asynchronously via a message broker (e.g., Apache Kafka, RabbitMQ). 

Let's detail the execution path of a choreography-based checkout saga:
1. **Order Service** creates a local database entry for the order in a `PENDING` state and publishes an `OrderCreated` event to Kafka.
2. **Payment Service** consumes the `OrderCreated` event, attempts to charge the customer's card, writes to its local DB, and publishes a `PaymentCharged` event (or `PaymentFailed` event).
3. **Inventory Service** consumes `PaymentCharged`, reserves the physical stock in its local DB, and publishes an `InventoryReserved` event.
4. **Order Service** consumes `InventoryReserved` and updates the order status to `CONFIRMED`.

#### Failure and Compensation Flow:
If the **Inventory Service** fails to reserve stock:
1. It publishes an `InventoryReservationFailed` event.
2. **Payment Service** consumes this event and executes a compensating transaction: it refunds the credit card charge in its database and calls the Stripe API to void the payment, then publishes a `PaymentRefunded` event.
3. **Order Service** consumes the failures and updates the order status to `CANCELLED`.

#### Drawbacks of Choreography:
* **Cognitive Load**: No single service contains the global state machine. To understand the workflow, developers must trace events across multiple codebases.
* **Cyclic Dependencies**: Services must subscribe to each other's events, which can easily create circular event paths.
* **Complex Testing**: Simulating a failure recovery scenario requires spinning up the entire event broker and all downstream consumer services.

---

### Orchestration Saga Internals
An Orchestration Saga uses a centralized controller class or service. The orchestrator must persist its state machine in a database (often called the **Saga Log** or **State Store**) before executing any downstream calls. This ensures that if the orchestrator crashes mid-saga, it can recover from its last-known state on reboot.

#### State Machine Representation
The orchestrator maintains a formal state machine:

```
[Idle] ──> [Creating Order] ──(Success)──> [Charging Payment] ──(Success)──> [Reserving Inventory] ─┐
  ^              |                                 |                                 |       │
  │            (Fail)                            (Fail)                            (Fail)    │ (Success)
  │              v                                 v                                 v       v
[Cancel] <── [Undo Order] <────────────────── [Refund Payment] <─────────────────────┴───── [Confirm]
```

#### Detailed Saga Log Schema
To track the progress of each step, the Saga Log table must capture the current execution state, context, and sequence. Here is a conceptual PostgreSQL schema for a Saga Log:

```sql
CREATE TABLE saga_instances (
    saga_id UUID PRIMARY KEY,
    saga_type VARCHAR(100) NOT NULL, -- e.g., 'E_COMMERCE_CHECKOUT'
    status VARCHAR(50) NOT NULL,     -- 'STARTED', 'COMPLETED', 'FAILED', 'COMPENSATING', 'COMPENSATED'
    payload JSONB NOT NULL,          -- Input payload for restarting the saga
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE saga_steps (
    step_id UUID PRIMARY KEY,
    saga_id UUID REFERENCES saga_instances(saga_id),
    step_name VARCHAR(100) NOT NULL, -- e.g., 'CHARGE_PAYMENT'
    sequence_order INT NOT NULL,
    status VARCHAR(50) NOT NULL,     -- 'PENDING', 'SUCCESS', 'FAILED', 'COMPENSATED'
    executed_at TIMESTAMP WITH TIME ZONE,
    compensated_at TIMESTAMP WITH TIME ZONE,
    error_message TEXT
);
```

#### Orchestrator Crash Recovery Steps
When an orchestrator node crashes, it must recover unfinished sagas. 
1. A background daemon polls for active instances:
   ```sql
   SELECT saga_id, saga_type, payload FROM saga_instances 
   WHERE status IN ('STARTED', 'COMPENSATING') 
   AND updated_at < NOW() - INTERVAL '1 minute';
   ```
2. For each active saga found, the daemon retrieves all logged steps:
   ```sql
   SELECT step_name, status, sequence_order FROM saga_steps 
   WHERE saga_id = :saga_id ORDER BY sequence_order ASC;
   ```
3. If the saga status is `STARTED`, the recovery worker identifies the first step with status `PENDING` or `FAILED` and resumes execution from that point. If it was `COMPENSATING`, the worker resumes executing compensating steps in descending order.

#### Code Pattern for an Orchestration Saga Engine
Below is a conceptual Python implementation of an Orchestration Saga controller:

```python
import uuid
import logging
from abc import ABC, abstractmethod

class SagaStep(ABC):
    @abstractmethod
    def execute(self, payload) -> bool:
        pass

    @abstractmethod
    def compensate(self, payload) -> bool:
        pass

class CreateOrderStep(SagaStep):
    def execute(self, payload):
        print(f"Executing: Order created for payload: {payload}")
        return True
    
    def compensate(self, payload):
        print(f"Compensating: Cancelling order for payload: {payload}")
        return True

class ChargePaymentStep(SagaStep):
    def execute(self, payload):
        print(f"Executing: Charging payment for payload: {payload}")
        # Simulate payment failure
        if payload.get("fail_payment"):
            print("Payment failed due to insufficient funds.")
            return False
        return True
        
    def compensate(self, payload):
        print(f"Compensating: Refunding payment for payload: {payload}")
        return True

class ReserveInventoryStep(SagaStep):
    def execute(self, payload):
        print(f"Executing: Reserving inventory for payload: {payload}")
        # Simulate inventory failure
        if payload.get("fail_inventory"):
            print("Inventory reservation failed: Out of stock.")
            return False
        return True
        
    def compensate(self, payload):
        print(f"Compensating: Releasing inventory for payload: {payload}")
        return True

class SagaOrchestrator:
    def __init__(self, steps):
        self.steps = steps
        self.saga_id = None

    def execute(self, payload):
        self.saga_id = str(uuid.uuid4())
        print(f"\n--- Starting Saga {self.saga_id} ---")
        completed_steps = []
        
        for step in self.steps:
            step_name = step.__class__.__name__
            print(f"[SAGA LOG]: Recording step {step_name} status as PENDING")
            
            success = step.execute(payload)
            if success:
                completed_steps.append(step)
                print(f"[SAGA LOG]: Recording step {step_name} status as SUCCESS")
            else:
                print(f"[SAGA LOG]: Recording step {step_name} status as FAILED")
                print(f"Saga execution failed at step: {step_name}. Initiating compensation...")
                self._compensate(completed_steps, payload)
                return False
        print(f"Saga {self.saga_id} completed successfully.")
        return True

    def _compensate(self, completed_steps, payload):
        # Compensating in reverse order
        for step in reversed(completed_steps):
            step_name = step.__class__.__name__
            print(f"[SAGA LOG]: Recording step {step_name} status as COMPENSATING")
            
            comp_success = False
            retries = 3
            while not comp_success and retries > 0:
                try:
                    comp_success = step.compensate(payload)
                except Exception as e:
                    logging.error(f"Error during compensation of {step_name}: {e}")
                retries -= 1
            
            if not comp_success:
                # CRITICAL: Compensation failed. Must route to a Dead Letter Queue / human intervention.
                logging.critical(f"UNABLE TO COMPENSATE STEP {step_name}! MANUAL ALIGNMENT REQUIRED.")
            else:
                print(f"[SAGA LOG]: Recording step {step_name} status as COMPENSATED")

# Run Orchestration Saga
if __name__ == "__main__":
    steps = [CreateOrderStep(), ChargePaymentStep(), ReserveInventoryStep()]
    orchestrator = SagaOrchestrator(steps)
    
    # Test Success Path
    orchestrator.execute({"order_id": "123", "fail_payment": False, "fail_inventory": False})
    
    # Test Failure Path at Payment (Triggers Compensation for CreateOrder)
    orchestrator.execute({"order_id": "124", "fail_payment": True, "fail_inventory": False})

    # Test Failure Path at Inventory (Triggers Compensation for ChargePayment and CreateOrder)
    orchestrator.execute({"order_id": "125", "fail_payment": False, "fail_inventory": True})
```

---

### Failure Recovery Modes: Forward vs. Backward Recovery

In distributed system design, you must choose how the Saga recovers from errors.

#### 1. Backward Recovery
When a step fails, the system executes compensating transactions in reverse order to return the system to its initial state.
* **When to use**: Business failures where continuation is impossible (e.g., payment rejected, account closed, inventory unavailable).
* **Requirements**: Every step must have a corresponding, reliable compensating action designed to undo its side effects.

#### 2. Forward Recovery
When a step fails, the Saga does not roll back. Instead, it retries the failed step or routes to an alternative step until the entire transaction succeeds.
* **When to use**: Technical failures (e.g., network timeout, service temporarily unavailable) where the step is guaranteed to succeed eventually, or when the cost of rollback is too high.
* **Requirements**: Highly idempotent operations and a reliable retry mechanism with backoff configurations.

---

### Designing Compensating Transactions

A compensating transaction is **not** a traditional database rollback. It is an explicit new transaction that undoes the semantic effects of a previous transaction. Because of this, it has strict design constraints:

#### 1. Idempotency
Because network calls can fail, the orchestrator or event broker will inevitably retry sending messages. A compensation command might be delivered multiple times.
* **Implementation**: The compensating service must track transaction IDs or idempotency keys. If it receives a second refund request for `order_123`, it must return success (`HTTP 200 OK`) and perform no database mutations.

#### 2. Commutativity (Handling Out-of-Order Execution)
In highly asynchronous networks, a compensation message (e.g., `CancelOrder`) can arrive at a service *before* the original execution message (e.g., `CreateOrder`). This is known as the **Out-of-Order Delivery** problem.
* **If not handled**: The service executes `CancelOrder`, finds no order, and does nothing. A few seconds later, `CreateOrder` arrives and inserts the order record. The order remains active forever, resulting in a zombie resource.
* **Solution**: Implement a "Cancellation Record". When `CancelOrder` arrives, write a tombstone record to the database for `order_123` stating it is pre-cancelled. When `CreateOrder` eventually arrives, it checks for this tombstone and declines the insert.

#### 3. The Pivot Step
The **Pivot Step** is the point of no return in a Saga.
* **Pre-Pivot**: Steps that occur before the pivot step. All pre-pivot steps *must* be compensable.
* **The Pivot**: Once this step succeeds, the saga *cannot* be aborted or compensated backward. The transaction is committed.
* **Post-Pivot**: Steps that occur after the pivot step. These steps *cannot* fail due to business logic (only due to temporary infrastructure issues). They must recover using **Forward Recovery** (endless retries).
* *Example*: In an order system, charging the credit card is the Pivot. If card charge succeeds, we must ship the items eventually. If shipping fails temporarily (truck breaks down), we retry shipping; we do not refund the money automatically unless we have a complex post-pivot refund saga.

#### 4. Workflow Replays and Deterministic Execution (e.g., Temporal)
Orchestrator platforms like Temporal avoid storing the state machine explicitly as database status codes. Instead, they use **Event Sourcing** on the workflow execution history.
* Whenever a step (called an *Activity* in Temporal) completes, its result is appended to the history log.
* If the orchestrator container crashes and reboots, it recreates the state by **replaying the workflow code** from the beginning.
* During replay, when the code reaches a step that has already run, the framework intercepts the call, reads the result from the history log, and returns it instantly without re-executing the network call.
* **Constraint**: Workflow execution code must be **strictly deterministic**. You cannot use random numbers, read system time (`datetime.now()`), or execute non-deterministic network queries inside the main workflow logic, as they will cause the replay path to diverge, throwing a `NonDeterministicWorkflowError`.

---

### Code-First Orchestration (Temporal) vs. State-Machine-Based Orchestration (AWS Step Functions / Conductor)

When designing a centralized orchestration system, engineers generally choose between two architecture types:

#### A. Code-First Orchestration (e.g., Temporal, Cadence)
Workflows are defined in standard programming languages (Python, Go, Java, TypeScript).
* **Execution**: Virtual actors replay deterministic code blocks, using history events to bypass already-executed steps.
* **Pros**: Full programming expressiveness (loops, try-catch, dynamic branches), code can be unit-tested directly, version control fits naturally in git repository.
* **Cons**: Strict determinism requirements require developers to learn specific library patterns.

#### B. State-Machine-Based Orchestration (e.g., AWS Step Functions, Netflix Conductor)
Workflows are defined in JSON or YAML formats representing a Directed Acyclic Graph (DAG).
* **Execution**: An engine parses the JSON/YAML and evaluates input/output paths to execute transition nodes.
* **Pros**: Simple UI builders, clean visual DAG views, easy configuration of parallel states without code compilation.
* **Cons**: JSON/YAML definitions become extremely verbose and difficult to maintain for complex business logic containing nested loops, error routing, and data filtering.

---

### Edge Case Troubleshooting & Operational Anti-Patterns

* **Compensations That Take Too Long / Handled Manually**: When a compensation step fails repeatedly (e.g., refund fails due to expired credit card), the saga cannot progress. The orchestrator must not block infinitely. It should route the saga state to a DLQ (Dead Letter Queue) or administrative dashboard where support agents can manually override the state or issue a check.
* **Saga Orchestrator Database Scaling**: Since every workflow state transition writes to the Saga Log, the state store database can become a hot-spot bottleneck. To scale this, use a distributed key-value store (like Cassandra/ScyllaDB) partition-keyed by `saga_id`, or run highly optimized relational databases with regular archiving scripts that migrate completed saga instances to cold storage.

---

### FAQ & Common Pitfalls

#### Q1: What happens if a compensating transaction fails?
A compensating transaction must never fail due to business logic validation. If it fails due to network or infrastructure issues, it must be retried with exponential backoff. If it still fails, it must trigger alerts for manual operational intervention to prevent data inconsistency.

#### Q2: Should we use Saga for all microservice updates?
No. Sagas add significant complexity and testing overhead. Use Sagas only when a single business operation requires atomicity across physically separated datastores where a single database transaction is impossible.

#### Q3: Can Sagas guarantee read consistency?
No. Because Sagas commit changes to databases immediately in local transactions, intermediate states are visible to other users (soft state). If strong read consistency is required, you must implement application-level checks or use locking states.

---

### Lack of Isolation (ACID vs. BASE)
Sagas are **BASE** (Basically Available, Soft state, Eventual consistency), not ACID. Specifically, they lack **Isolation**.
Because local transactions commit immediately to their respective databases, their changes are visible to other concurrent transactions *before* the entire Saga completes. This introduces three consistency risks:

1. **Lost Updates**: Saga $A$ updates record $X$. Before Saga $A$ finishes, Saga $B$ overwrites record $X$. Later, Saga $A$ fails and triggers compensation, rolling back record $X$ to its initial value. Saga $B$'s update is silently lost.
2. **Dirty Reads**: Saga $A$ updates a user balance. Saga $B$ reads the updated balance and lets the user buy an item. Saga $A$ fails and compensates, reverting the balance. The user has now purchased an item they cannot afford.
3. **Non-Repeatable Reads**: A service reads a record, and before the Saga completes, another transaction updates the same record, causing a subsequent read in the Saga to see different data.

#### Isolation Anomaly Countermeasures:
* **Semantic Lock**: Set a status field like `PENDING_RESERVATION` or `LOCKED_BY_SAGA_XYZ` on the record. Other transactions must check this state and decide whether to ignore, block, or bypass this record.
* **Commutative Updates**: Design operations that do not care about order. For example, instead of absolute values (`SET balance = 100`), use delta adjustments (`ADD balance, 10`).
* **Pessimistic Locking / Pivot Comparison**: Read values and perform strict validation before execution, committing changes only at the pivot point.
* **Value-by-value comparison**: If a compensation attempts to roll back a record, it must check if the value was modified by another process. If so, it raises an alert instead of overwriting the concurrent change.

---

## 3. Comparison Table

| Feature / Metric | 2-Phase Commit (2PC) | Orchestration Saga | Choreography Saga |
|:---|:---|:---|:---|
| **Data Consistency** | Strong Consistency (ACID) | Eventual Consistency (BASE) | Eventual Consistency (BASE) |
| **Locks** | Held across all phases on all systems | Only held briefly during local transactions | Only held briefly during local transactions |
| **SPOF (Single Point of Failure)**| Yes (Coordinator) | Yes (Orchestrator service/log, needs high availability) | No (Fully decentralized event broker handles recovery) |
| **Network Overhead** | High (blocking round trips) | Medium (central coordinator queries) | Low (asynchronous pub-sub events) |
| **Complexity to Develop**| Low (handled by DBMS drivers) | Medium (state machine design) | High (difficult to trace and debug) |
| **Dependency Coupling** | High coupling | Low-Medium (orchestrator depends on services) | Low (fully decoupled via message broker) |
| **Scalability** | Poor (throttled by slowest participant) | High | Extremely High |
| **SaaS/Third-Party API Support**| No | Yes (via custom code wrapper) | Yes (via event adapter layers) |
| **Observability** | Good (standard system logs) | High (centralized visualization of saga status) | Low (requires distributed tracing like OpenTelemetry) |

---

## 4. Real-World Usage

### 1. Netflix (Conductor)
Netflix uses a centralized orchestration engine called **Netflix Conductor** to coordinate microservices for media workflows. For example, when a movie is uploaded, it must be transcoded into multiple bitrates, localized subtitles must be matched, and metadata must be populated. Since transcoding takes hours and can fail midway, Netflix uses Orchestration Sagas with forward and backward recovery to coordinate these steps.

### 2. Uber (Cadence / Temporal)
Uber developed **Cadence** (which later spawned the open-source fork **Temporal**) to orchestrate complex stateful transactions. A user hailing a ride initiates a multi-step Saga involving: finding a driver, reserving the trip, executing credit card authorization, and notifying the passenger. If the driver cancels, Cadence orchestrates the compensation flow (releasing the driver lock, finding another driver, or adjusting the authorization capture).

### 3. E-Commerce Checkout Flow
A modern e-commerce platform using Cassandra for order history, Postgres for inventory, and Stripe for payments uses a Saga. Because Stripe is a external SaaS API, 2PC is impossible. They implement an Orchestration Saga using Temporal to guarantee that if inventory reservation fails after payment, Stripe's API is invoked to issue a refund.

---

## 5. SDE-2+ Interview Script

### Scenario
The interviewer asks the candidate to design a transaction system for a ride-sharing app when a rider requests a trip.

#### Interview Dialogue
**Interviewer**: Let’s design the system for booking a ride on our app. When a user requests a ride, we need to: 
1. Reserve a driver.
2. Authorize payment on the user's credit card.
3. Update the trip status to "En Route". 
How would you design the transactional flow across these microservices?

**Candidate**: To handle this across microservices, I would avoid 2-Phase Commit (2PC) because it would hold database locks across the payment gateway API call, which is a slow external network dependency. Instead, I would implement the **Saga Pattern** using an **Orchestration** approach.

**Interviewer**: Why chose Orchestration over Choreography?

**Candidate**: Choreography gets complicated fast. With Choreography, the Rider service would publish a `TripRequested` event, Driver service would reserve the driver and publish `DriverReserved`, Payment service would charge and publish `PaymentAuthorized`. If a failure occurs, say the driver rejects the ride last-minute, managing the reverse workflow through cascading events becomes hard to trace. 
By using an Orchestrator (like Temporal or a custom state engine), we define the workflow in a single location. The Orchestrator manages the state store—the Saga Log—guaranteeing that if it crashes mid-saga, it can recover and resume execution or compensation.

**Interviewer**: Walk me through how you handle a failure where the payment authorization succeeds, but the driver cancels before accepting the ride.

**Candidate**: That involves **Backward Recovery**. 
1. The Orchestrator initiates step 1: `ReserveDriver`. The Driver Service returns success.
2. The Orchestrator persists this step to the Saga Log and proceeds to step 2: `AuthorizePayment`. The Payment Service successfully holds the funds on the credit card.
3. Next, the driver cancels the ride assignment. The Driver Service updates its state and returns a failure response to the orchestrator.
4. The Orchestrator identifies this as a failure. It looks at the Saga Log, sees that `AuthorizePayment` completed successfully, and executes the compensating step: `ReleasePaymentAuthorization` (or void capture).
5. Once the Payment service releases the hold, the Orchestrator marks the Saga as `CANCELLED` and notifies the client.

**Interviewer**: What if the network drops while the Orchestrator is calling the payment compensation step, and it fails? How do you prevent double-refunding or leaking money?

**Candidate**: The compensation step must be designed to be **idempotent**. If the orchestrator times out waiting for the payment service response, it will retry calling `ReleasePaymentAuthorization`. 
To handle this safely:
* The payment service must track request IDs. If it receives a second request with the same `Saga ID`, it must recognize that it already voided that transaction, bypass the API call to Stripe, and immediately return `HTTP 200 OK`.
* Additionally, we must handle the **Out-of-Order Delivery** anomaly. If the payment authorization call was delayed on the network and the compensation request arrives *first*, the payment service must write a "Cancellation Tombstone" for that Saga ID. When the late authorization request finally arrives, it checks the database, sees the tombstone, and rejects the authorization.

**Interviewer**: Excellent. How do you handle database isolation anomalies, since Sagas commit local transactions immediately? For instance, what if two sagas try to reserve the same driver?

**Candidate**: We apply a **Semantic Lock**. When the driver is reserved in Step 1, the Driver Service doesn't just change the driver's status to `BUSY`. It sets it to `PENDING_SAGA_RIDE_XYZ`. If another ride request queries drivers, it ignores drivers with a semantic lock. If the saga completes successfully, the status changes to `OCCUPIED`. If the saga fails and compensates, the status reverts back to `AVAILABLE`. This prevents other transactions from dirty-reading the driver's availability.

**Interviewer**: What happens if the Orchestrator itself crashes? How does it remember where it was?

**Candidate**: If the orchestrator runs on a framework like Temporal, it uses event sourcing of the workflow history. Every completed activity is logged. Upon restart, the orchestrator replays the code. When it reaches a step that has already run, it returns the stored result from the database rather than executing the network call again. This relies on the workflow execution code being completely deterministic, which means no raw timestamps or random number generators can be run directly inside the workflow logic.

**Interviewer**: What about parallel steps? Can the orchestrator reserve a driver and authorize payment at the same time? How do we compensate if one of the parallel steps fails?

**Candidate**: Yes, we can design the orchestrator to execute independent branches concurrently (e.g., executing `ReserveDriver` and `AuthorizePayment` concurrently). If `ReserveDriver` fails but `AuthorizePayment` succeeds, the orchestrator awaits the completion of all outstanding parallel steps before executing the compensations. This guarantees we don't attempt to compensate an action that is still actively processing or hasn't finished registering in its local datastore.

---

## 6. SDE-2+ Readiness Checklist

- [ ] I understand why **2-Phase Commit (2PC)** is a blocking protocol and how it degrades throughput in microservice architectures.
- [ ] I can articulate the key differences between **Orchestration** and **Choreography** sagas, including their trade-offs regarding coupling and complexity.
- [ ] I know how to design **compensating transactions** that are idempotent and handle out-of-order execution anomalies (using cancellation tombstones).
- [ ] I can identify the **Pivot Step** in a Saga design and structure the workflow to use **Forward Recovery** post-pivot.
- [ ] I understand the isolation anomalies of Sagas (Dirty Reads, Lost Updates) and can implement mitigation strategies like **Semantic Locks** or **Commutative Updates**.
- [ ] I know how a **Saga Log** is used by an orchestrator to resume state recovery after system crashes.
- [ ] I understand deterministic execution constraints and workflow code replays inside engines like **Temporal**.
- [ ] I am familiar with real-world orchestrator runtimes like **Temporal**, **Cadence**, or **Netflix Conductor**.
- [ ] I can design parallel execution workflows in a Saga Orchestrator and safely handle failure cascades.
