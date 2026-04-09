# Object Contract (equals, hashCode & toString)

> **The One-Liner Summary:** Every Java object inherits three critical methods from `Object` class — overriding them correctly is the difference between a class that works in Collections and one that silently drops your data.

---

## 📖 1. The Conceptual Core (The "Why")
Every class you write silently extends `java.lang.Object`. This brings three critical inherited methods: `equals()`, `hashCode()`, and `toString()`. The default implementations are almost never correct for domain objects.
*   **The Problem:** The default `equals()` uses `==` (reference equality). Two `User` objects with the same `id` fetched from the database at different times will **not** be equal by default, causing `HashMap` lookups to fail and `Set` membership checks to behave incorrectly.

---

## 🔍 2. Deep Dive: The Mechanics (The "How")

### 2.1 The `equals()` Contract (5 Mathematical Rules)
Any correct implementation of `equals()` must satisfy:
1.  **Reflexive**: `x.equals(x)` → always `true`.
2.  **Symmetric**: If `x.equals(y)` is `true`, then `y.equals(x)` must be `true`.
3.  **Transitive**: If `x.equals(y)` and `y.equals(z)`, then `x.equals(z)`.
4.  **Consistent**: Multiple calls with unchanged objects return the same result.
5.  **Null-safe**: `x.equals(null)` → always `false`, never throws.

### 2.2 The `hashCode()` Contract (The Unbreakable Link)
The most violated contract in Java:
*   **Rule**: If `a.equals(b)` is `true`, then `a.hashCode()` **MUST** equal `b.hashCode()`.
*   **Converse (Not required)**: If `a.hashCode() == b.hashCode()`, `a.equals(b)` can still be `false` (hash collision).
*   **The Trap**: If you override `equals()` but NOT `hashCode()`, two logically equal objects will have different hashes, causing `HashMap.get()` to look in the wrong bucket and return `null` — a catastrophic, silent bug in production.

### 2.3 The Standard Implementation Pattern
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;                          // 1. Reference shortcut
    if (o == null || getClass() != o.getClass()) return false; // 2. Type check
    User user = (User) o;
    return Objects.equals(id, user.id);                 // 3. Field comparison
}

@Override
public int hashCode() {
    return Objects.hash(id); // Must use the SAME fields as equals()
}
```

### 2.4 `clone()` vs. Copy Constructor (The Senior Choice)
*   **`Object.clone()`**: Performs a *shallow copy* and requires implementing the broken `Cloneable` marker interface. Nested mutable objects are NOT copied — they share the same reference.
*   **Copy Constructor**: The Senior-preferred alternative. It is explicit, deep-copy capable, and goes through the class constructor (respecting all validation logic).

---

## ❌ 3. Junior Mistakes vs. ✅ Senior Solutions

| ❌ The Junior Approach | ✅ The Senior/LLD Approach | 🧠 Why it matters (The "So What?") |
| :--- | :--- | :--- |
| Overriding `equals()` but forgetting `hashCode()`. | Always override them as a single atomic unit. | Objects become "ghosts" in HashMaps and HashSets. You store them but can never retrieve them. |
| Using mutable fields (like `name`) inside `hashCode()`. | Using only stable, immutable identity fields (like `id`) in `hashCode()`. | If you mutate a field after the object is inserted into a `HashSet`, it becomes unreachable in its own collection. |
| Using `Object.clone()` for copying domain objects. | Using Copy Constructors or dedicated `copy()` factory methods. | `clone()` bypasses constructors (skipping validation) and creates dangerously shallow copies of nested objects. |

---

## 🏗️ 4. Real-World Application (System Design)
In an **API Result Deduplication Cache**:
Imagine fetching `User(id=42)` twice from a database (two separate HTTP calls). You have two distinct Java objects on the Heap with different memory addresses. You want to put them both in a `HashSet` to deduplicate. Without a correct `equals/hashCode` override, the `HashSet` treats them as different users and stores both, creating a duplicate cache entry that inflates memory and causes downstream logic bugs.

---

## 💥 5. FAANG / MNC Interview Preparation

### Q1: "What happens if you put a mutable object into a `HashSet` and then mutate it?"
**The Senior Answer:**
The object becomes permanently unreachable inside the `HashSet`. When you call `set.contains(obj)`, the HashSet computes `obj.hashCode()`, which now returns a different value than what it was when the object was inserted. It looks in the wrong bucket, finds nothing, and returns `false` — even though the object is physically still sitting in the set.

### Q2: "Why should `hashCode()` use the same fields as `equals()`?"
**The Senior Answer:**
Because the contract demands it. If `equals()` decides equality based on `userId`, then `hashCode()` must produce its hash based on `userId`. If `hashCode()` instead uses `email`, two objects that `equals()` considers identical (same `userId`) will produce different hashes, breaking the HashMap/HashSet bucket-lookup chain entirely.

---

## 🛠️ 6. Executable Code Examples
- [ObjectContractDemo.java](./ObjectContractDemo.java): Proves the HashMap ghost-object bug live, demonstrates the mutable hashCode trap, and shows correct Copy Constructor implementation.

---

## 📚 7. Further Reading / Patterns Linked
- Correct `equals/hashCode` is a mandatory prerequisite for **Caching Patterns** (Redis, in-memory caches).
- Copy Constructor patterns are the foundation of **Prototype Design Pattern**.
