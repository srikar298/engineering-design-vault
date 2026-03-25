# OOP Pillars - The Senior Lens

> "The four pillars are not just features of an OOP language; they are the architectural building blocks for managing complexity, risk, and change."

Welcome to the **OOP Pillars** module. This section provides a deep, senior-level dive into the core principles of Object-Oriented Design.

---

## 🗺️ Curriculum Path

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
