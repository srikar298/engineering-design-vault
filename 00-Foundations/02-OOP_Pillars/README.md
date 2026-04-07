# OOP Pillars - The Senior Lens

> "The four pillars are not just features of an OOP language; they are the architectural building blocks for managing complexity, risk, and change."

Welcome to the **OOP Pillars** module. This section provides a deep, senior-level dive into the core principles of Object-Oriented Design.

---

# 🏛️ The 4 Pillars of OOP: Strategic Foundation

> **"Basics teach you what the pillars are. Senior preparation teaches you how to use them to manage software complexity at scale."**

In an SDE-2+ interview, the pillars are the "First Principles" you use to justify your high-level design decisions.

---

## 🛠️ Strategic Purpose of the Pillars

| Pillar | Focus | 🏗️ Senior "Strong Hire" Insight |
| :--- | :--- | :--- |
| **Encapsulation** | **Maintenance** | *"It's not just 'private fields.' It's about maintaining **Invariants**. I hide the internal state to ensure that the object can never enter an inconsistent state (e.g., negative bank balance)."* |
| **Inheritance** | **Classification** | *"Use sparingly. Inheritance creates the tightest form of coupling. I only use it for **Behavioral Subtyping** (IS-A), never for code reuse alone (where I prefer Composition)."* |
| **Polymorphism** | **Decoupling** | *"The 'Magic' of OOP. It allows high-level logic to remain agnostic of the underlying implementations. It is the core of the **Strategy** and **Observer** patterns."* |
| **Abstraction** | **Cognitive Load** | *"Abstraction is about **Hiding Complexity**. I define clean interfaces so that a developer using my class only needs to know 'What' it does, not 'How' it does it."* |

---

## ⚠️ Senior Edge Cases & Pitfalls

### 1. The Fragile Base Class (Inheritance)
Changing a single line in a base class can break 50 subclasses in unexpected ways. This is why we say: **"Favor Composition Over Inheritance."**

### 2. Leaky Abstractions (Abstraction)
"All non-trivial abstractions, to some degree, are leaky." (Joel Spolsky). For example, an `IDatabase` interface hides SQL, but if the network is slow, the abstraction "leaks" through as latency that the caller must handle.

### 3. Covariant Returns (Polymorphism)
A senior engineer knows that a subclass can override a method and return a **more specific** type than the parent (e.g., `CarFactory.create()` returning `Tesla` instead of just `Vehicle`).

---

## 🌍 The Polyglot Perspective (Node/TS vs. Go vs. Java)

### 🟢 Node/TS: Structural Pillars
*   **Encapsulation:** Achieved via `#private` fields or closures.
*   **Polymorphism:** Natively "Duck Typed." If it has the method, it works.

### 🔵 Golang: Compositional Pillars
*   **No Inheritance:** Go literally does not have classes or extends. It forces **Composition** (Embedding) as the only way to build complex structures.
*   **Encapsulation:** Enforced at the **Package Level** via capitalization (Uppercase = Exported).

---

## ✅ Final SDE-2+ Pillars Readiness Checklist

1.  [ ] **"Why do we prefer Composition over Inheritance?"** (Senior Answer: Because inheritance is static and tightly coupled; composition is dynamic and flexible).
2.  [ ] **"What is 'Tell, Don't Ask' in Encapsulation?"** (Senior Answer: Don't ask an object for its data to make a decision; tell the object what to do based on its internal state).
3.  [ ] **"How does Polymorphism enable the Open/Closed Principle?"** (Senior Answer: By allowing us to add new implementations of an interface without changing the code that uses that interface).

---

## 🗺️ Module Index

### 1. [Encapsulation](./01-Encapsulation/README.md)
Beyond private fields. Learn about state protection, invariants, and the "Tell, Don't Ask" principle.
- **Example**: `EncapsulationMastery`.

### 2. [Inheritance](./02-Inheritance/README.md)
The double-edged sword. Understand IS-A vs. HAS-A and why you should favor Composition.
- **Example**: `InheritanceMastery` (Composition over Inheritance).

### 3. [Polymorphism](./03-Polymorphism/README.md)
The ultimate decoupler. Dynamic Dispatch and Interface Polymorphism.
- **Example**: `PolymorphismMastery` (Notification Engine).

### 4. [Abstraction](./04-Abstraction/README.md)
Designing extension points and stable contracts.
- **Concepts**: Abstract Classes (Identity) vs. Interfaces (Behavior).
- **Example**: `AbstractionMastery` (Pluggable Storage).

---

## 🪜 Pillars Exercise Ladder
1. **Level 1**: Refactor a class with public fields into one that uses private fields and "Tell, Don't Ask" methods.
2. **Level 2**: Implement a `ShapeDrawer` that works on a `List<Shape>` without knowing the specific shapes.
3. **Level 3**: Create a `CloudStorage` system where you can swap AWS for GCP at runtime using Abstraction and Polymorphism.

---

## 🚀 Interview-Grade Summary
> "I use encapsulation to enforce business invariants through 'Tell, Don't Ask' patterns, and I leverage polymorphism to decouple high-level logic from specific implementation details, which is the foundational stone for clean architecture."
