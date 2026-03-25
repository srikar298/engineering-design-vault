# Java 8+ Features for LLD

> *"Modern Java lets you write less boilerplate and more focus on the 'What' instead of the 'How' — a critical skill for clean, senior-level LLD."*

This module covers the Java 8+ features that appear in every real-world LLD interview: Lambdas, Streams, and Optional. These are not just syntax sugar — they directly eliminate entire categories of bugs (NPEs, brittle null checks, verbose loops) that plague junior codebases.

---

## 📚 Module Index

| # | Topic | Core Concept | Interview Importance |
|---|---|---|---|
| [01-Lambdas_and_Streams](./01-Lambdas_and_Streams/) | Functional Interfaces, Lambdas, Stream pipelines | Strategy without classes, declarative pipelines | ⭐⭐⭐⭐⭐ |
| [02-Optional](./02-Optional/) | `Optional<T>`, null elimination, chaining | Eliminating `NullPointerException` in domain models | ⭐⭐⭐⭐⭐ |

---

## 🔗 Relationships to Other Modules
- **Lambdas** directly use → `03-OOP_Advanced/01-Interfaces` (Functional Interfaces)
- **Streams** demonstrate → `02-OOP_Pillars/03-Polymorphism` (method references = polymorphic dispatch)
- **Optional** enforces → `03-OOP_Advanced/03-Object_Contract` (null-safe API design)
