# Aggregation (The "Weak Ownership" Relationship)

> **The One-Liner Summary:** Aggregation is a "has-a" relationship where the **whole contains the part, but the part can survive independently** — like a Department that has Professors, but those Professors continue to exist even if the Department shuts down.

---

## 📖 1. The Conceptual Core (The "Why")
*   **The Problem:** Many developers confuse Aggregation with Composition. Getting this wrong means your code destroys objects that should survive (or keeps objects alive that should die), causing memory leaks, data loss, and broken business logic.
*   **The Metaphor:** Think of a **Playlist and Songs**. A playlist *contains* many songs. But if you delete the playlist, the songs themselves are **not deleted** — they still exist in the music library. The playlist is the "whole", the songs are the "parts", but the parts' existence doesn't depend on the whole.

---

## 🔍 2. Deep Dive: The Mechanics (The "How")

### 2.1 The Lifecycle Independence Rule
The defining characteristic of Aggregation:
> **"If the Whole is destroyed, the Parts continue to exist."**

This is implemented in code by **injecting** the part objects from outside (via constructor or setter), rather than creating them internally with `new`. The part exists before the whole is created and will continue to exist after.

### 2.2 UML Representation
```
Department ◇─────── Professor
    1           0..*
```
- The **hollow diamond** (`◇`) sits on the "whole" end (`Department`).
- The arrow (optional) points toward the "part" (`Professor`).
- Multiplicity: `1` Department to `0..*` Professors.

### 2.3 How It Looks in Code
The key signal is **injection** — the part is passed in, not constructed internally:
```java
// ✅ AGGREGATION: Department receives Professor from outside
class Department {
    private List<Professor> professors;

    // The Professor is created EXTERNALLY and passed IN
    public Department(List<Professor> professors) {
        this.professors = new ArrayList<>(professors);
    }
}
```
Compare with Composition where the part is created **inside** the whole.

---

## ❌ 3. Junior Mistakes vs. ✅ Senior Solutions

| ❌ The Junior Approach | ✅ The Senior/LLD Approach | 🧠 Why it matters (The "So What?") |
| :--- | :--- | :--- |
| Creating the "part" object inside the "whole's" constructor (`new Professor()`). | Injecting `Professor` objects from outside. If you create them inside, it's Composition, not Aggregation — and they'll be orphaned when the Department is deleted. | Getting lifecycle wrong means either memory leaks (keeping dead parts alive) or data loss (destroying independent parts). |
| Holding a raw reference to the caller's list directly (`this.professors = professors`). | Deep copying the input list: `this.professors = new ArrayList<>(professors)`. | If you hold the caller's original reference and they mutate their list, your Department's roster changes unexpectedly. |
| Treating Aggregation and Composition as the same concept. | Explicitly asking: **"Can the part exist without the whole?"** If YES → Aggregation. If NO → Composition. | This distinction is a direct FAANG interview question and a common LLD design mistake. |

---

## 🏗️ 4. Real-World Application (System Design)
In a **University Management System**:
*   A `Department` has `Professor` objects (Aggregation — Professors can transfer to another department or exist independently).
*   A `Department` has `Course` objects (Aggregation — a Course could be shared across departments or exist temporarily without one).
*   A `Department` has its own `Budget` and `Address` (Composition — these have no existence or meaning outside the Department).

This demonstrates that a single real-world entity can have **both** Aggregation and Composition relationships simultaneously.

---

## 💥 5. FAANG / MNC Interview Preparation

### Q1: "What is the precise difference between Aggregation and Composition?"
**The Senior Answer:**
The difference is **lifecycle ownership and creation responsibility**. In Aggregation, the "part" is created externally and injected into the "whole" — and it outlives the whole if it's destroyed. In Composition, the "part" is created *inside* the "whole's" constructor using `new` — and it is destroyed when the whole is destroyed. In code: if you see `new Part()` inside the constructor, it's Composition. If you see the part passed as a parameter, it's Aggregation.

### Q2: "Can an object be part of multiple aggregates simultaneously?"
**The Senior Answer:**
Yes — this is precisely what distinguishes Aggregation from Composition. A `Professor` can be associated with `Department A` (as head) and also in `ResearchLab B` (as a contributor). The Professor is a shared resource across multiple containers. In Composition, a "part" can only ever belong to exactly one "whole" — shared ownership is impossible.

---

## 🛠️ 6. Executable Code Examples
- [AggregationDemo.java](./AggregationDemo.java): University system demonstrating Department/Professor aggregation — proving that deleting the Department does not delete the Professors.

---

## 📚 7. Further Reading / Patterns Linked
- Aggregation maps naturally to **Repository Pattern** (a repository aggregates domain objects it doesn't own).
- Shared Aggregation is the structural basis of the **Flyweight Pattern** (sharing reusable parts across many containers).
