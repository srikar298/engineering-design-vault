# Class Relationships

> *"In the real world, nothing exists in isolation. Software is no different — how you define the connections between your objects determines the flexibility, maintainability, and correctness of your entire system."*

This module covers the four fundamental relationship types between classes in OOP. These are the core building blocks of every UML diagram, LLD interview, and real-world system design.

---

# 🔗 Class Relationships: The Lifecycle Guide

> **"A junior sees lines on a UML diagram. A senior sees Ownership and Memory Lifecycle."**

In an SDE-2+ interview, class relationships are the bridge between **Clean Design** and **Efficient Infrastructure.**

---

## 🛠️ The 4 Pillars of Coupling

| Relationship | Strength | Analogy | 🏗️ Senior "Strong Hire" Insight |
| :--- | :--- | :--- | :--- |
| **Dependency** | Weakest | **Tool** | *"Method level. Essential for decoupling. If my service 'depends' on a Logger, I pass it as a parameter, making it easy to mock."* |
| **Association** | Weak | **Peer** | *"Field level. Objects know about each other but remain independent peers (e.g. Teacher and Student)."* |
| **Aggregation** | Medium | **Container** | *"Part-Whole relationship, but the 'Part' can exist without the 'Whole'. Use this for shared resources like a Library holding Books."* |
| **Composition** | Strongest | **Vital Organ** | *"Part-Whole where the 'Part' CANNOT exist without the 'Whole'. Critical for managing internal state and cascading deletes."* |

---

## 🌍 The Polyglot Perspective (Node/TS vs. Go vs. Java)

### 🟢 Node/TS: The "Structural" View
In TypeScript, relationships are often defined by **Interfaces and Types** rather than rigid class hierarchies.
*   **Composition:** Often achieved through **Object Spreading** or nesting interfaces.
*   **Dependency:** Achieved via **Higher-Order Functions** or standard parameter passing.

### 🔵 Golang: The "Embedding" View
Go is unique because it lacks inheritance.
*   **Composition:** Go uses **Struct Embedding**. If `Car` embeds `Engine`, `Car` gets all `Engine` methods. This is "Composition over Inheritance" built into the language.
*   **Ownership:** Since Go uses pointers, the difference between Aggregation and Composition is explicitly managed by whether you pass a pointer (`*T`) or a value (`T`).

---

## 🎓 Interview Tips: Creating "Strong Hire" Impact

### 1. "Composition over Inheritance"
*   **What to say:** *"I always prefer **Composition** (Has-a) over **Inheritance** (Is-a). Inheritance is a design-time decision that is hard to change. Composition allows me to swap behaviors at runtime (Bridge/Strategy patterns) and keeps my classes decoupled."*

### 2. "Aggregation vs. Composition (The Death Test)"
*   **What to say:** *"To distinguish between them, I use the 'Death Test'. If the container object is destroyed, what happens to the child? If the child dies too, it's **Composition** (e.g. Order and OrderItems). If the child lives on, it's **Aggregation** (e.g. Team and Player)."*

### 3. "Dependency Injection (DI)"
*   **What to say:** *"I use **Dependencies** at the constructor level to enable **Inversion of Control**. By not instantiating dependencies inside my class (avoiding hard Composition), I make my code 100% testable with mocks."*

---

## ✅ SDE-2+ Readiness Check
*   [ ] Can you explain the "Death Test" for Aggregation vs. Composition?
*   [ ] Why is Dependency the best relationship for Unit Testing?
*   [ ] How does Go's Struct Embedding implement Composition?

---

## 📚 Module Index

| # | Relationship | Strength | Lifecycle | UML Symbol | Key Question |
|---|---|---|---|---|---|
| [01-Association](./01-Association/) | Uses / References | 🟢 Weak | Independent | Solid line `---` | "Does A interact with B?" |
| [02-Aggregation](./02-Aggregation/) | Has-a (Weak) | 🟡 Medium | Part survives Whole | Hollow diamond `◇---` | "Can the part exist alone?" |
| [03-Composition](./03-Composition/) | Has-a (Strong) | 🔴 Strong | Part dies with Whole | Filled diamond `◆---` | "Does A own B completely?" |
| [04-Dependency](./04-Dependency/) | Uses temporarily | ⚪ Weakest | Method-scoped only | Dashed arrow `- - ->` | "Does A use B just for one operation?" |

---

## 🧠 The Relationship Spectrum (Interview Cheat Sheet)

```
WEAKEST                                                        STRONGEST
  │                                                                 │
Dependency ──── Association ──── Aggregation ──── Composition
(method param)  (field ref)     (shared owner)   (exclusive owner)
```

---

## 🔗 Relationships to Other Modules
- **Builds on** → `02-OOP_Pillars/02-Inheritance` (IS-A vs HAS-A distinction)
- **Feeds into** → `00-SOLID_Principles` (Dependency Inversion uses Dependency relationships)
- **Powers** → Design Patterns (Strategy uses Association, Decorator uses Composition)
