# 🧪 08 — Guided LLD Problems (Creational Patterns)

> These are **guided** problems — the pattern hints are given. Learn the *implementation*, *trade-offs*, and *interview justifications* here.
> After finishing all pattern categories, attempt the **blind problems** in `99-Blind-LLD-Problems/`.

---

## 🧠 SDE-2+ Problem Solving Strategy

When an interviewer asks an LLD question like *"Design a Pizza Ordering System,"* they are testing more than just your coding skills. They are looking for **Architectural Maturity.**

### 1. Clarify Requirements (The Senior First Step)
Before writing any pattern, ask questions:
*   *"Are there multiple brands of Pizza (Abstract Factory) or just one (Factory Method)?"*
*   *"Do we need to customize every topping (Builder) or just select from a menu?"*
*   *"Is the system multi-threaded (Singleton considerations)?"*

### 2. Identify the "Problem Center"
| If the problem is... | Use this Pattern |
| :--- | :--- |
| **Object variety** (many types of products) | **Factory Method / Abstract Factory** |
| **Configuration** (shared resource) | **Singleton** |
| **Complex Customization** (optional fields) | **Builder** |
| **Resource Bottlenecks** (DB connections) | **Singleton / Object Pool (Prototype variation)** |

### 3. Trade-off Analysis (The "Strong Hire" Differentiator)
Don't just implement the pattern; explain the trade-off:
*   *"I'm using a **Builder** for the Pizza because the number of topping combinations is huge. While it adds more classes, it prevents the **Telescoping Constructor** mess and ensures the pizza is immutable once it enters the oven."*

---

## 🎓 FAANG Tip: "The Evolution Talk"
During the interview, show how the design evolves:
1.  *"I'll start with a **Simple Factory** for the Pizzas..."*
2.  *"...but to handle 10k concurrent users across different regions, I'll refactor to **Abstract Factory** to ensure region-specific ingredients (Consistency)."*

This shows you build for the present while planning for the future.

| # | Problem | Patterns Used | Difficulty |
|---|---|---|---|
| [01](./01-pizza-ordering/README.md) | **Pizza Ordering System** | Abstract Factory + Builder | ⭐⭐ Medium |
| [02](./02-connection-pool/README.md) | **Database Connection Pool** | Singleton + Object Pool | ⭐⭐⭐ Hard |
| [03](./03-game-character-creator/README.md) | **Game Character Creator** | Prototype Registry + Builder | ⭐⭐⭐ Hard |

---

## 🎯 How to Use These

1. **Read** the problem statement in each README
2. **Attempt** to sketch the design yourself (5-10 min)
3. **Compare** with the provided implementation
4. **Focus** on the "Pattern Mapping" and "FAANG Interview Angles" sections
5. **Explain** the design out loud as if in an interview

---

## 🗺️ Pattern Coverage Map

```
Abstract Factory ──── Pizza Ordering (store variants: Italian vs American)
Builder          ──── Pizza Ordering (toppings, crust, size) + Game Character (equipment)
Singleton        ──── Connection Pool (one pool per datasource) 
                      + Character Registry (one registry per game session)
Prototype        ──── Game Character Creator (clone base archetypes)
Object Pool      ──── Connection Pool (reuse expensive connections)
```
