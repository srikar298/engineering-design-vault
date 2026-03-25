# Class Relationships

> *"In the real world, nothing exists in isolation. Software is no different — how you define the connections between your objects determines the flexibility, maintainability, and correctness of your entire system."*

This module covers the four fundamental relationship types between classes in OOP. These are the core building blocks of every UML diagram, LLD interview, and real-world system design.

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
