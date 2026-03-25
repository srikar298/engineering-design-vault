# Composition (The "Strong Ownership" Relationship)

> **The One-Liner Summary:** Composition is a "has-a" relationship where the **whole exclusively owns and creates the part** — when the whole dies, the parts die with it, because they have no independent meaning or existence outside of it.

---

## 📖 1. The Conceptual Core (The "Why")
*   **The Problem:** If you use Aggregation where Composition is correct, you end up with "orphan" objects — parts that float in memory with no owning context, leading to data inconsistency (e.g., a Heart that outlives the Patient).
*   **The Metaphor:** Think of a **House and its Rooms**. The rooms are part of the house. If you demolish the house, the rooms are demolished too — "Room 101" of a demolished building doesn't float in some abstract universal room registry. The House is the sole owner and creator of its Rooms. They have no independent existence.

---

## 🔍 2. Deep Dive: The Mechanics (The "How")

### 2.1 The Exclusive Ownership Rule
The defining characteristic of Composition:
> **"If the Whole is destroyed, the Parts are destroyed with it. The Part has no independent existence or meaning."**

This is implemented in code by **creating** part objects *inside* the whole's constructor using `new`. The part is never exposed to the outside world as a direct reference, because no external code should hold a handle to a part that can be orphaned.

### 2.2 UML Representation
```
House ◆─────── Room
  1           1..*
```
- The **filled diamond** (`◆`) sits on the "whole" end (`House`).
- Multiplicity: `1` House to `1..*` Rooms.

### 2.3 How It Looks in Code
The key signal is **internal construction** — the part is born inside the whole:
```java
// ✅ COMPOSITION: Heart is created *inside* the Human constructor
class Human {
    private final Heart heart;   // Exclusive ownership

    public Human() {
        this.heart = new Heart(); // NOT injected — created here
    }
    // When Human is garbage collected, Heart is garbage collected too.
}
```

### 2.4 The Three Composition Signals
1. **Created internally**: The part is instantiated with `new` inside the whole's constructor or an init method.
2. **`private final`**: The part field is almost always `private final` — never exposed, never replaced.
3. **Never shared**: No external class should ever get a direct reference to the part.

---

## ❌ 3. Junior Mistakes vs. ✅ Senior Solutions

| ❌ The Junior Approach | ✅ The Senior/LLD Approach | 🧠 Why it matters (The "So What?") |
| :--- | :--- | :--- |
| Returning a direct reference to the composed part via a getter. | Returning a defensive copy or an unmodifiable view, or exposing behavior-only methods. | If external code holds a reference to the part, it can outlive the whole. Composition's exclusivity guarantee is broken. |
| Accepting the composed part via the constructor (making it Aggregation). | Creating the part internally. If the external world needs to configure it, pass configuration primitives, not pre-built objects. | Accepting a `Heart` parameter means someone else already "owns" that `Heart`, destroying the exclusive ownership semantics. |
| Treating all `private` fields as composition. | Using the lifecycle test: if I delete this object, does the field's value still make sense elsewhere? YES = Aggregation. NO = Composition. | A `Car` has a `private Engine engine` — but if the engine was supplied from a factory and can be reused, it's Aggregation, not Composition. |

---

## 🏗️ 4. Real-World Application (System Design)
In an **Order Management System**:
*   An `Order` **composes** `OrderItem` objects (Composition — an `OrderItem` for "2x Laptop" has zero meaning outside its specific parent Order. If the Order is cancelled and deleted, the items go with it).
*   An `Order` composes a `ShippingAddress` (Composition — this address snapshot belongs to this specific moment in time; it's not the user's profile address).
*   An `Order` *aggregates* a `Customer` (Aggregation — the Customer existed before the Order and continues to exist after the Order is fulfilled or cancelled).

---

## 💥 5. FAANG / MNC Interview Preparation

### Q1: "How does Composition support Encapsulation better than Aggregation?"
**The Senior Answer:**
In Composition, the whole controls the entire lifecycle of the part — including its creation, configuration, and access. Since no external code can access the part directly (it's `private` and created internally), the whole can change its internal structure (e.g., replace `HeapStorage` with `DirectByteBuffer` inside a `MemoryManager`) without any downstream impact. In Aggregation, the part is shared — any change to the part's implementation can affect all other containers that reference it.

### Q2: "Is Java's `String` composition or aggregation with respect to its internal `char[]`?"
**The Senior Answer:**
Composition. The `char[]` array inside `String` is created and owned exclusively by the `String` object. The array is `private final`, never exposed externally, and has absolutely no meaning or existence outside of its parent `String` object. When the `String` is garbage collected, the `char[]` goes with it. This is textbook Composition, and it's precisely why `String` can guarantee immutability.

---

## 🛠️ 6. Executable Code Examples
- [CompositionDemo.java](./CompositionDemo.java): An Order Management System proving the lifecycle death of composed parts, versus the survival of aggregated parts.

---

## 📚 7. Further Reading / Patterns Linked
- Composition is the structural mechanism behind the **Composite Design Pattern** (tree structures of part/whole).
- Exclusive internal ownership is the pattern used in **Builder Pattern** (building up composed internal parts step by step).
