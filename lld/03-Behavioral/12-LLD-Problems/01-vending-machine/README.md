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
