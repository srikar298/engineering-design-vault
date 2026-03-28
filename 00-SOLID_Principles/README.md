# SOLID Principles: The Foundation of LLD

> *"SOLID principles are not rules to follow blindly — they are lenses to evaluate design decisions. In Senior SDE interviews, you are expected to apply them as first principles before reaching for any design pattern."*

The SOLID principles are five design guidelines that make software designs more understandable, flexible, and maintainable. Every major design pattern in the GoF catalog is a direct application of one or more of these principles.

---

## 📚 Module Index

| # | Principle | Core Rule | Interview Importance | Key Smell |
|---|---|---|---|---|
| [S — SRP](./01-Single_Responsibility/) | Single Responsibility | One reason to change = one stakeholder | ⭐⭐⭐⭐⭐ | God Classes, high entanglement |
| [O — OCP](./02-Open_Closed/) | Open/Closed | Extend by addition, never by modification | ⭐⭐⭐⭐⭐ | if/else type switches that grow |
| [L — LSP](./03-Liskov_Substitution/) | Liskov Substitution | Subtypes must honor the parent's behavioral contract | ⭐⭐⭐⭐⭐ | `UnsupportedOperationException` overrides |
| [I — ISP](./04-Interface_Segregation/) | Interface Segregation | Clients only depend on what they use | ⭐⭐⭐⭐ | Fat interfaces, stub/no-op methods |
| [D — DIP](./05-Dependency_Inversion/) | Dependency Inversion | High-level policy never depends on low-level detail | ⭐⭐⭐⭐⭐ | `new ConcreteService()` inside business logic |

---

## 🧠 How the Five Principles Interconnect (Interview Cheat Sheet)

```
SRP  →  Identifies the RIGHT BOUNDARIES (one class, one change vector)
OCP  →  Protects stable code via EXTENSION POINTS (interfaces + composition)
LSP  →  Ensures SAFE SUBSTITUTION within a hierarchy
ISP  →  Keeps CLIENT CONTRACTS LEAN (no unused method baggage)
DIP  →  Enforces the DIRECTION OF DEPENDENCY (detail depends on policy)
```

> [!TIP]
> ISP violations are often the *cause* of LSP violations. A fat interface forces a class to implement behavior it can't fulfill, breaking behavioral substitutability. Fix ISP → LSP fixes itself.

---

## 🔗 Relationships to Other Modules
- **Builds on** → `00-Foundations/03-OOP_Advanced/01-Interfaces` (ISP, OCP, DIP all require solid interface design)
- **Builds on** → `00-Foundations/05-Class_Relationships` (DIP uses Dependency; OCP uses Association; Composition over Inheritance)
- **Feeds into** → Every Design Pattern (Strategy = OCP in action; Observer = DIP in action; Decorator = OCP + SRP)
