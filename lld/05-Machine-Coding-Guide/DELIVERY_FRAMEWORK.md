# ⏱️ LLD Interview Delivery Framework (SDE-2+ Edition)

> **"Speed is a signal of confidence. Correctness is a signal of seniority."**

In a 35-45 minute LLD round, you don't have time to be perfect. You have time to be **Structured.** This framework ensures you cover the "Senior Signal" markers while delivering a working solution.

---

## 1. Requirements & Discovery (~5 Minutes)
*The prompt is a trap.* It's intentionally vague. Your job is to define the boundaries.

### 🛠️ Execution:
1.  **Primary Capabilities:** What are the 3-4 core flows? (e.g., "User books seat," "Admin adds movie").
2.  **Scale & Concurrency (Senior Move):** Ask: *"Are we designing for 10k concurrent users? Should I handle race conditions for seat selection?"*
3.  **Invariants:** What rules can *never* be broken? (e.g., "No double booking," "Balance cannot be negative").
4.  **2025 AI Twist:** Ask: *"Is this system agent-ready? Should I expose structured JSON/gRPC or just a simple CLI?"*
5.  **Out of Scope:** Explicitly list what you are NOT building (UI, Auth, Persistence).

---

## 2. Entities & Relationships (~3 Minutes)
*Don't draw every noun. Identify the "Brain" of the system.*

### 🛠️ Execution:
1.  **Identify the Orchestrator:** Which class drives the workflow? (e.g., `BookingService`, `GameEngine`).
2.  **The "Death Test" (Lifecycle):** 
    *   **Composition:** If the Car dies, the Engine dies. (Strongest coupling).
    *   **Aggregation:** If the Team dies, the Player lives. (Weak coupling).
3.  **UML Lite:** Use boxes and arrows. Don't waste time on formal UML symbols unless specifically asked.

---

## 3. Class Design & API (~10 Minutes)
*Define the "Contract" before the "Logic".*

### 🛠️ Execution:
1.  **Interfaces First:** Write the `interface` definitions. This proves you think in abstractions (DIP).
2.  **"Tell, Don't Ask":** Design methods that perform actions (`bookSeat(id)`) rather than exposing data (`getSeats().setOccupied(true)`).
3.  **SDE-2+ Move:** Use **Java Records** (Java 17+) for DTOs and **Sealed Classes** for state (Success/Failure).
4.  **Pattern Selection:** Mentally map 1-2 patterns. (e.g., *"I'll use **Strategy** for different pricing rules"*).

---

## 4. Implementation (~12 Minutes)
*Get it running early. Layer the complexity.*

### 🛠️ Execution:
1.  **Phase A: [INTERVIEW_MVP]:** Implement the "Happy Path" using simple collections (`HashMap`). Get it to compile and run.
2.  **Phase B: [PRODUCTION_ENHANCEMENT]:** Add the "Senior" features:
    *   **Concurrency:** Switch to `ConcurrentHashMap` or add `ReentrantLock`.
    *   **Validation:** Throw custom exceptions (`SeatAlreadyBookedException`).
    *   **Patterns:** Implement the Strategy or State classes you mentioned.
3.  **Vibe Coding Move:** If allowed, use AI to generate getters/setters/DTOs, but **manually write the Locks and Pattern logic.**

---

## 5. Verification & Walkthrough (~5 Minutes)
*Don't just say "it's done." Prove it.*

### 🛠️ Execution:
1.  **Trace a Scenario:** Walk through a non-trivial case. *"User A and User B both hit 'Book' at the same time. Here is how my lock prevents the race condition..."*
2.  **Extensibility Talk:** If they ask "What if we add X?", point to your Interface or Pattern. *"Because I used the Strategy pattern for pricing, we just add a new `HolidayStrategy` class—no change to the core code."*

---

## ⚠️ Common Failure Modes
*   **The Vague Start:** Diving into code without clarifying requirements.
*   **The Pattern Trap:** Forcing a pattern (like Visitor) where a simple `if` would work.
*   **The Silence:** Not talking through your design decisions.
*   **Non-Compilable Code:** Spending 30 mins on complex logic that doesn't run. **Working code > Beautiful pseudo-code.**

---

## 🧠 The Anti-Freeze Protocol (If You Get Stuck)

If you feel your brain "blanking" during the 45-minute window, execute these steps immediately:

1.  **Step 1 (0-2 min): Don't Code. Draw.** Use simple boxes for main entities. Visualization breaks the freeze.
2.  **Step 2 (2-4 min): Start with the Interface.** Type `interface Service {}`. Getting *something* on the screen builds momentum.
3.  **Step 3 (4-6 min): Think Out Loud.** Say: *"I'm currently thinking through how to model the relationship between X and Y..."*
4.  **Step 4 (6-8 min): Ask a Depth Question.** Ask: *"Should I focus on the concurrency aspect of seat booking now, or should I finish the core logic first?"* This buys you time while looking smart.

---

## 🗣️ "Explain Aloud" Prompts (SDE-2+ Signal)

Use these scripts to narrate your progress and show architectural maturity:

*   **During Setup:** *"I'm defining the interfaces first because I want to ensure my high-level logic is decoupled from specific implementations, following the Dependency Inversion Principle."*
*   **During Pattern Choice:** *"I'm choosing the Strategy pattern here for pricing. This keeps my `BookingService` closed for modification but open for extension when we add new seasonal discount types."*
*   **During Concurrency:** *"I'm using a `ConcurrentHashMap` here instead of a simple `HashMap` because we're designing for 10k concurrent users, and I need to prevent race conditions during the search phase."*
*   **During Validation:** *"I'll throw a custom `SeatAlreadyBookedException` here. I prefer 'Fail-Fast' and explicit exceptions over returning nulls, which can cause subtle bugs later."*
