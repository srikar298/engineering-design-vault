# OOP Basics - The Senior Lens

> "Classes and Objects are not just templates and instances; they are the fundamental building blocks of system state and behavior management."

Welcome to the **OOP Basics** module. This section is structured into modular sub-topics to provide a deep, senior-level understanding of Object-Oriented Programming foundations.

---

## 🗺️ Curriculum Path

### 1. [State and Behavior](./01-State_and_Behavior/README.md)
The core duality of every class. Learn how objects manage their own "condition" and "capabilities."
- **Example**: `Smartphone` (State vs. Behavior).

### 2. [Memory and Identity](./02-Memory_and_Identity/README.md)
Understand where objects live and how to tell them apart.
- **Concepts**: Stack vs. Heap, Reference variables, Object Lifecycle, Identity (`==`) vs. Equality (`.equals()`).
- **Example**: `UserProfile` (Memory & Identity).

### 3. [Constructors Mastery](./03-Constructors/README.md)
The journey of object creation and integrity.
- **Concepts**: Chaining, Overloading, Copy Constructors, Static Factories, and the Builder Pattern.
- **Example**: `ConstructorDeepDive`.

### 4. [Static and Access Modifiers](./04-Static_and_Access_Modifiers/README.md)
Mastering scope, shared state, and the foundation of Encapsulation.
- **Concepts**: Metaspace, Static members, and all 4 Java Access Modifiers.
- **Example**: `CloudService` & `SecureVault`.

### 5. [Advanced Enums](./05-Advanced_Enums/README.md)
Leveraging Enums for type safety, state machines, and design patterns.
- **Concepts**: Enums with State/Behavior, Strategy Pattern with Enums, Singleton Enum.
- **Example**: `OrderStatus` & `DiscountStrategy`.

---

## 🪜 Exercise Ladder
1. **Level 1**: Create a `Book` class with multiple constructors (Minimal vs. Full data) using `this()`.
2. **Level 2**: Implement a `Counter` using a `static` variable to track the total number of objects created.
3. **Level 3**: Fix a "Visibility Bug" where a `protected` field is being modified from an unrelated package.

---

## 🚀 Interview-Grade Summary
> "I prioritize `private` access for state protection and use constructor chaining to ensure object integrity. I use `static` sparingly for constants and stateless utilities to avoid global state complications in concurrent systems."
 utilities to avoid global state complications in concurrent systems."
