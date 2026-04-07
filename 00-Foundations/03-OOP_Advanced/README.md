# Advanced OOP (The Senior Lens)

> *"Advanced OOP is not about knowing more syntax — it's about knowing how to structure objects to survive years of changing requirements."*

This module covers the OOP concepts that separate a Senior from a Junior in FAANG/MNC interviews. Basics teach you the rules; this module teaches you when to break them, when to combine them, and the traps that are invisible to less experienced developers.

---

# 🚀 OOP Advanced: The Senior Deep Dive

> **"A junior understands how OOP works. A senior understands where it breaks."**

In an SDE-2+ interview, "Advanced OOP" is where you demonstrate your understanding of **Language Internals**, **Memory Safety**, and **Modern Domain Modeling.**

---

## 🛠️ The 3 Modern Pillars of OOP

| Topic | Core Problem | 🏗️ Senior "Strong Hire" Insight |
| :--- | :--- | :--- |
| **Abstract vs Interface** | Multiple Inheritance & State | *"Interfaces define **Capabilities** (What can it do?). Abstract classes define **Identity** (What is it?). Always prefer interfaces for decoupling unless you need to share internal state."* |
| **Deep Immutability** | Thread-Safety & Side Effects | *"Final fields are not enough. You must perform **Defensive Copying** for collections and mutable objects to ensure a true 'Deep' immutable contract."* |
| **Sealed Hierarchies** | Domain Integrity | *"Use **Sealed Classes** (Java 17+) to restrict extensibility. This enables **Algebraic Data Types** and exhaustive switch-matching, making your domain model much safer."* |

---

## 🌍 The Polyglot Perspective (Node/TS vs. Go vs. Java)

### 🟢 Node/TS: The "Structural" OOP
*   **Interfaces:** In TS, interfaces are virtual and disappear at runtime. There are no "Abstract Classes" in the same bytecode sense as Java.
*   **Immutability:** Often achieved via `Readonly<T>` types or libraries like `Immer`.

### 🔵 Golang: The "Compositional" OOP
*   **No Classes:** Go uses **Structs and Interfaces**.
*   **No Inheritance:** Go favors **Embedding**.
*   **Interfaces are Implicit:** You don't "implement" an interface; you just satisfy it. This is the ultimate "Behavioral" OOP.

---

## 🎓 Interview Tips: Creating "Strong Hire" Impact

### 1. "When do you use a Default Method?"
*   **What to say:** *"I use **Default Methods** in interfaces to provide optional behavior without breaking existing implementations. However, I never use them to store state. If I find myself writing complex logic in a default method, it's a sign I might actually need an **Abstract Class**."*

### 2. "The Record vs. Class Decision"
*   **What to say:** *"I use **Java Records** for pure data carriers (DTOs, Value Objects). They are concise and immutable by design. For classes that have **Behavior and State** (Services, Controllers), I stick to standard classes to maintain full control over the lifecycle."*

### 3. "Composition over Inheritance"
*   **What to say:** *"Inheritance is the tightest form of coupling. In advanced OOP, I use **Composition** to build behavior. This allows me to swap components at runtime and avoids the 'Fragile Base Class' problem where changing a parent breaks 50 subclasses."*

---

## ✅ SDE-2+ Readiness Check
*   [ ] Can you explain why `Collections.unmodifiableList()` is still "Shallowly" immutable?
*   [ ] What is the main difference between an Abstract Class and an Interface in Java 8+? (Fields/State).
*   [ ] How do Sealed Classes improve Security and Domain Modeling?

---

## 📚 Module Index

| # | Topic | Core Concept | Interview Importance |
|---|---|---|---|
| [01-Interfaces](./01-Interfaces/) | Role Interfaces, Functional Interfaces, `default` methods | Interfaces vs Abstract Class decision | ⭐⭐⭐⭐⭐ |
| [02-Immutability](./02-Immutability/) | `final` keyword, 5-rule recipe, thread-safety, reference trap | Why `String` is immutable, `final` field trap | ⭐⭐⭐⭐⭐ |
| [03-Object_Contract](./03-Object_Contract/) | `equals`, `hashCode`, `clone` vs Copy Constructor | Ghost object bug in HashMaps | ⭐⭐⭐⭐⭐ |
| [04-OOP_Pitfalls](./04-OOP_Pitfalls/) | Fragile Base Class, Circular Deps, Fat Interfaces | Yo-Yo problem, ISP violations | ⭐⭐⭐⭐ |

---

## 🔗 Relationships to Other Modules
- **Interfaces** build on → `02-OOP_Pillars/04-Abstraction`  
- **Immutability** builds on → `01-OOP_Basics/04-Static_and_Access_Modifiers`  
- **Object Contract** builds on → `01-OOP_Basics/02-Memory_and_Identity`  
- **OOP Pitfalls** build on → `02-OOP_Pillars/02-Inheritance`
