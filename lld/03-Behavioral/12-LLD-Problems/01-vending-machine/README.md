# 🥤 LLD Case Study: Vending Machine

This module demonstrates how multiple Behavioral patterns act together to represent hardware systems and complex deterministic workflows.

## 🧠 Architecture Overview

We are building a robust **Hardware Vending Machine**. 

1. **Chain of Responsibility Pattern:** The coin slot represents hardware validation. When a coin drops, it physical drops down a tube through multiple sensors (`QuarterValidator` -> `DimeValidator`). If none of the sensors recognize the weight/magnetism, it escapes the chain and is spit back out, preventing slugs from entering the machine's state.
2. **State Pattern:** The Vending Machine itself is a Finite State Machine (FSM). It shifts between `IdleState`, `HasMoneyState`, and `DispensingState`. This mathematically guarantees that a user cannot press the "Dispense" button when the machine is `Idle`, completely eliminating messy boolean `if(hasMoney)` checks from the main logic.

---

## 💻 Tech Stack Highlights
* **Transition Delegation:** State transitions are handled internally by the State classes.
* **Separation of Hardware/Software:** By utilizing CoR for hardware sensing and State for the overarching software, we enforce a pristine separation of concerns.

## ▶️ Execution
To run the Java implementation, go into the `JAVA` folder and see the specific `README.md`.

---

## 🔬 Tracker Diagnostics

*   **Primary Patterns:** State (for Machine logic), Chain of Responsibility (for coin validation), Singleton (for the VendingMachine instance).
*   **The "Freeze Trap":** Candidates often struggle with where to store the "inventory" and "balance." (Senior Answer: Keep the `Inventory` and `Balance` inside the `VendingMachine` (Context) class. The `State` objects should manipulate these values via public/protected methods on the context).
*   **Class Design Checklist:**
    *   [ ] `VendingMachine` (Context)
    *   [ ] `IVendingState` (Interface)
    *   [ ] `IdleState`, `HasMoneyState`, `DispensingState` (Concrete States)
    *   [ ] `Inventory` (Wrapper around `Map<Product, Integer>`)
    *   [ ] `CoinHandler` (Abstract class for CoR)
*   **SOLID Violations to Watch For:**
    *   **OCP:** Ensure that adding a new State (e.g., `OutOfStockState`) doesn't require modifying existing state classes if possible (or keep modifications minimal).
    *   **LSP:** Every State must implement *all* methods of the `IVendingState` interface, even if just to throw a "Not Allowed" message.
