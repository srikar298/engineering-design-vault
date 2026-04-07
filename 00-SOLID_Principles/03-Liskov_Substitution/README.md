# Liskov Substitution Principle (LSP) - Deep Dive

> "Subtypes must be substitutable for their base types." — Barbara Liskov

---

## 🔍 Overview
LSP is about **Behavioral Subtyping**. At a Senior level, it means: "If you have a method that takes a base class, you should be able to pass ANY subclass without the method breaking or behaving unexpectedly."

### 🎯 The "Expectation" Contract
LSP is not about syntax; it's about **Trust**. The caller trusts that the subclass will follow the rules established by the parent.

---

## ☣️ The Senior Lens: Pre-conditions & Post-conditions
This is the "Secret Sauce" for a 10/10 interview.

### 1. Pre-conditions (Requirements)
*A subclass cannot be **more restrictive** than the parent.*
- If the parent accepts `any integer`, the subclass cannot say "I ONLY accept positive integers." (Strengthning pre-conditions).
- **Why?**: The caller expects to be able to pass -5. If the subclass fails, the contract is broken.

### 2. Post-conditions (Promises)
*A subclass cannot be **less restrictive** than the parent.*
- If the parent promises to return a `list of 10 items`, the subclass cannot return an `empty list`. (Weakening post-conditions).
- **Why?**: The caller's next step might depend on having those 10 items.

---

## 🧱 Classic LSP Violations

### 1. The Square-Rectangle Problem
- A `Rectangle` has `setWidth()` and `setHeight()`.
- A `Square` extends `Rectangle` and forces `width = height`.
- ❌ **Violation**: A caller writing `rect.setWidth(10); rect.setHeight(5); assert(rect.area() == 50);` will fail if `rect` is actually a `Square`.

### 2. The Ostrich-Bird (Flyable) Problem
- A `Bird` class has a `fly()` method.
- An `Ostrich` extends `Bird` but throws `UnsupportedOperationException`.
- ❌ **Violation**: Any method that takes a `List<Bird>` and calls `fly()` will crash on the Ostrich.

---

## 🏗️ Before & After Examples
Check these Java examples for a practical deep dive:

### 🔰 Level 1: The Square Trap (Expectation Failure)
- ☣️ **Violation**: [SquareViolation.java](SquareViolation.java)
- **Problem**: Changing one property silently changes another, breaking the caller's logic.

### 🥈 Level 2: The Flying Bird (Behavioral Splitting)
- ✅ **Refactored**: [LSP_Correct_Hierarchy.java](LSP_Correct_Hierarchy.java)
- **Solution**: Segregate behaviors into interfaces (`Flyable`, `Swimmable`). Don't force a subtype to inherit behavior it cannot fulfill.

### 🏆 Level 3: Cloud Storage + Java Stack (Advanced Real-World Violations)
- ☣️ **Advanced**: [LSP_Advanced_Violations.java](LSP_Advanced_Violations.java)
- **Violation 1**: `ReadOnlyArchiveStorage extends StorageProvider` silently throws on `upload()` — a post-condition breach invisible to the type system. Caught only at runtime.
- **Violation 2**: Java's own `Stack extends Vector` breaks the LIFO invariant because `add(index, element)` is inherited. The senior fix: use `Deque` instead.

---

## 🚀 Interview-Grade Summary
> "I apply LSP by ensuring my subclasses adhere to the behavioral contract of their parents. I never strengthen pre-conditions or weaken post-conditions, and I avoid inheritance when a subclass cannot fulfill the base class's promises. Instead, I use lean interfaces to represent specific capabilities like 'Flyable' or 'Drawable'."

---

## 🔍 Deep Dive: The Mechanics

### The Behavioral Contract (Beyond Syntax)
LSP is not about method signatures — it's about **behavioral guarantees**. A subclass that has the right method signatures but violates the expected behavior is an LSP violation, even if it compiles perfectly.

The three formal rules:
1. **Pre-conditions cannot be strengthened**: If the parent accepts any `Integer`, the child cannot restrict to only positive integers. The caller is unaware of the restriction and their code breaks.
2. **Post-conditions cannot be weakened**: If the parent guarantees to return a non-empty list, the child cannot return an empty one. The caller's next operation depends on that guarantee.
3. **Invariants must be preserved**: Properties that are always true for the parent must remain true for the subclass (e.g., a `Rectangle`'s area = width × height must hold for any shape in the hierarchy).

### Covariant Return Types & Contravariant Parameters
At an advanced level, LSP formalizes:
- **Return types can be narrowed (covariant)**: A child's override can return a more specific type (`Cat` where parent returns `Animal`) — this is safe.
- **Parameter types must stay the same or widen (contravariant)**: A child cannot require a *more specific* input type than the parent declared.

---

## ❌ Junior Mistakes vs. ✅ Senior Solutions

| ❌ The Junior Approach | ✅ The Senior/LLD Approach | 🧠 Why it matters |
| :--- | :--- | :--- |
| `Ostrich extends Bird` then `throw new UnsupportedOperationException()` in `fly()`. | `interface Flyable { fly(); }`. `Sparrow implements Flyable`. `Ostrich` does not. | Any method taking `Bird` and calling `fly()` won't crash on an Ostrich. The hierarchy models reality accurately. |
| `Square extends Rectangle` overriding setWidth to also set height. | Separate `Square` and `Rectangle` as siblings implementing a `Shape` interface. | The `Rectangle` invariant (area = w × h) is destroyed by `Square`. Every `Rectangle` consumer silently breaks. |
| Adding `override` to a method but adding an extra null-check pre-condition inside it. | Honor the parent's contract: if the parent accepted `null`, the child must too. | Callers pass null because the parent said it was safe. The child silently throws NPE. Contract broken. |

---

## 🏗️ Real-World Application (System Design)
In a **Cloud Storage Abstraction**:
`StorageProvider` has `upload(File, maxSizeMb)`. The concrete `LocalStorage` honors the contract. A new `ReadOnlyArchiveStorage` is added as a subclass. It throws `UnsupportedOperationException` on `upload()`. Any `StorageService` that takes a `StorageProvider` and calls `upload()` now silently explodes at runtime when given an `ArchiveStorage`. The fix: `ReadOnlyStorage` should implement a `ReadableStorage` interface, not extend `StorageProvider`.

---

## 💥 FAANG / MNC Interview Preparation

### Q1: "What is the 'Square-Rectangle problem' and what does it teach us about LSP?"
**The Senior Answer:**
A `Rectangle` contract says: width and height are independent. `setWidth(5)` followed by `setHeight(10)` yields area = 50. If `Square extends Rectangle` and forces `setWidth` to also change height, calling `setWidth(5).setHeight(10)` on a `Square` yields area = 100, not 50. The caller's invariant is silently violated. The lesson: mathematical IS-A (a square IS a rectangle geometrically) does not imply code IS-A. Behavioral substitutability is what matters, not geometric truth.

### Q2: "How do you detect an LSP violation in a code review?"
**The Senior Answer:**
Two clear signals: (1) A subclass method throws `UnsupportedOperationException` — it's inheriting a capability it physically cannot fulfill. (2) A caller does `instanceof` checking before calling a method — it means the caller doesn't trust that any subclass will behave correctly, which is a direct admission that the hierarchy is broken.

### Q3: "What is the relationship between LSP and ISP?"
**The Senior Answer:**
They are partners. Many LSP violations (like Ostrich forced to implement `fly()`) are actually caused by ISP violations — giant interfaces that force a class to implement behavior it cannot fulfill. Fixing the ISP violation (segregating `Flyable` from `Bird`) automatically eliminates the LSP violation. They address the same root problem from different angles: ISP at the interface level, LSP at the subclass behavior level.

---

## 🪜 LSP Exercise Ladder
1. **Beginner**: Find the LSP violation in `Duck extends Bird implements Flyable` when `Duck` cannot fly.
2. **Intermediate**: Redesign a `Vehicle` hierarchy where `Bicycle` doesn't have an engine but `Car` does.
3. **Senior**: Prove that `List` and `Stack`'s relationship in Java (`Stack extends Vector`) violates LSP by demonstrating a behavior the caller cannot trust.

---

## 🚀 SDE-2+ Pragmatic Perspective: The "Behavioral" Contract

**LSP (Liskov Substitution Principle)** is about **Correctness**.
- **The Core Rule:** A subclass should not just "be a" subtype of its parent; it must **behave** like its parent in every situation the parent promised.

### 🏗️ Why it matters for Scaling (10k+ Concurrency)
In your experience as a Founding Engineer:
1.  **Program Correctness:** If you have a `NotificationService` that takes a `Channel` object, you expect every `Channel` to "send" the message. If one channel (like `ArchiveChannel`) throws an `UnsupportedOperationException`, it crashes your background processing loop for 10k users. That is an LSP violation.
2.  **Trustworthy Abstractions:** LSP ensures that when you write code against an interface, you don't need to check `instanceof` to know how a specific subclass will behave. Checking `instanceof` is a "Code Smell" that your abstractions are broken.

---

## 🎓 Interview Tips: Creating "Strong Hire" Impact

### 1. "Pre-conditions & Post-conditions"
*   **What to say:** *"LSP is defined by three rules: 
    1. Don't strengthen **Pre-conditions** (don't demand more than the parent).
    2. Don't weaken **Post-conditions** (don't promise less than the parent).
    3. Maintain **Invariants** (the rules that must always be true)."*

### 2. "The square/rectangle problem"
*   **What to say:** *"The classic Square/Rectangle example isn't about geometry. It's about **Invariants**. A Rectangle allows you to change width and height independently. A Square forces them to be the same. By making Square a subtype of Rectangle, you break the Rectangle's 'Independent Dimensions' invariant."*

### 3. "Composition over Inheritance"
*   **What to say:** *"When I see an LSP violation, it usually means I'm using **Inheritance** for code reuse when I should have used **Composition**. If a subclass doesn't share the same behavioral contract as the parent, it shouldn't be a subclass."*

---

## ⚠️ Edge Cases & Pitfalls
*   **Silent Failures:** If a subclass returns `null` or an empty value where the parent promised a result, it weakens the post-condition.
*   **Strengthening Rules:** If your `AdvancedUser` requires a `token` to login but the base `User` only requires a `password`, you've strengthened the pre-condition, breaking LSP.

---

## 🌍 The Polyglot Perspective

### 🟢 Node/TS (Founding Engineer Context)
In TS, LSP is enforced by the **Type System**, but behavior is still up to you.
```typescript
interface Storage {
    save(id: string, data: any): void;
}

class ReadOnlyStorage implements Storage {
    save(id: string, data: any) {
        // ❌ LSP VIOLATION: Subtype breaks the promise of the interface
        throw new Error("Read-only");
    }
}
```
In a Node app handling 10k users, this `throw` would crash your event loop if not caught everywhere.

### 🔵 Golang
In Go, LSP is very natural. Because interfaces are implicit, if your struct doesn't behave like the interface expects, it simply **won't compile** as an implementation of that interface. Go's design makes it very hard to accidentally violate LSP at the interface level.

---

## ✅ SDE-2+ Readiness Check
*   [ ] Can you define LSP using "Pre-conditions" and "Post-conditions"?
*   [ ] Why is `instanceof` often a sign of an LSP violation?
*   [ ] How does the "Square/Rectangle" problem translate to a real-world "Account" or "Storage" scenario?

---

## 📚 Further Reading / Patterns Linked
- LSP violations are best fixed by applying **ISP** (Interface Segregation) to split bloated hierarchies.
- The covariant return type rule is what enables type-safe **Factory Method Pattern** overrides in subclasses.
