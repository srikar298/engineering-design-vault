# 🧬 Prototype Design Pattern

## 📖 1. The Core Concept (The "Why")
The **Prototype** pattern is a creational design pattern that lets you copy existing objects without making your code dependent on their classes.

### ⚠️ The Problem: Costly Creation
1. **Expensive Initialization**: Some objects require complex database queries or network calls to initialize.
2. **Similar Objects**: You need 100 "Orc" enemies in a game that are 99% identical. Doing `new Orc()` 100 times and setting 50 fields each time is slow and redundant.

### ✅ The Solution: Cloning
Instead of creating a fresh object from scratch, you take an existing "Prototype" object and **clone** it. This follows the "Copy-Paste-Modify" approach.

---

## 📈 2. The Evolution (The Evolutionary Path)

### Stage 0: Shallow Copy Mess ([evolution/Stage0ShallowCopy.java](file:///e:/job-hunt/LLD/LLD-Design-Patterns-main/01-Creational/05-Prototype%20Design%20Pattern/JAVA/evolution/Stage0ShallowCopy.java))
The most common bug. You copy the object, but you accidentally share the internal references (like a `Stats` object).
- **The Bug**: Modifying the health of the clone accidentally modifies the health of the original!

### Stage 1: Java's `Cloneable` ([evolution/Stage1Cloneable.java](file:///e:/job-hunt/LLD/LLD-Design-Patterns-main/01-Creational/05-Prototype%20Design%20Pattern/JAVA/evolution/Stage1Cloneable.java))
The traditional Java approach using `super.clone()`.
- **The Verdict**: **Broken.** It requires manual deep cloning of every nested object, throws checked exceptions, and is generally avoided in modern enterprise Java.

### Stage 2: Copy Constructors ([evolution/Stage2CopyConstructor.java](file:///e:/job-hunt/LLD/LLD-Design-Patterns-main/01-Creational/05-Prototype%20Design%20Pattern/JAVA/evolution/Stage2CopyConstructor.java))
The "Senior" way. A constructor that takes an instance of the same class and copies its values.
- **The Benefit**: Clean, type-safe, handles deep copies natively, and doesn't require casting.

### Stage 3: Prototype Registry ([registry/Stage3Registry.java](file:///e:/job-hunt/LLD/LLD-Design-Patterns-main/01-Creational/05-Prototype%20Design%20Pattern/JAVA/registry/Stage3Registry.java))
The "Enterprise" manager. A centralized cache (HashMap) of pre-configured prototypes.
- **The Benefit**: Clients don't even need to know the prototypes exist; they just ask for "Warrior" or "Mage" by name.

---

## 🏗️ 3. Architectural Blueprint

### Deep vs. Shallow Copy
```mermaid
graph TD
    subgraph Original
        Obj1[Object A] --> Ref1[Stats Instance: 0x123]
    end
    subgraph Shallow_Clone
        Obj2[Object B] --> Ref1
    end
    subgraph Deep_Clone
        Obj3[Object C] --> Ref2[Stats Instance: 0x456]
    end
    style Ref1 fill:#f96,stroke:#333
    style Ref2 fill:#9f9,stroke:#333
```

---

## 🎭 4. Junior vs. Senior Implementation

| Feature | Junior Developer | Senior Developer |
|---|---|---|
| **Cloning Method** | Uses `Cloneable` or manual field copying in `Main`. | Uses **Copy Constructors** or specialized **Deep Copy** utilities. |
| **Object Graph** | Forget to clone nested objects (Shallow copy bugs). | Implements a recursive deep copy or uses Builders for cloning. |
| **Centralization** | Keeps prototypes scattered in `static` variables. | Uses a **Prototype Registry** for centralized management. |
| **Immutability** | Clones mutable objects frequently. | Prefers **Immutable** objects to avoid the need for cloning entirely. |

---

## 🏢 5. Real-World System Design

1.  **Game Engines (Unity/Unreal)**:
    "Prefabs" are essentially prototypes. When you "Spawn" an enemy, you are cloning a prefab prototype.
2.  **OS Process Forking**:
    The `fork()` command in Linux clones the entire parent process to create a child.
3.  **UI Frameworks**:
    In complex dashboards, "Template Widgets" are cloned to fill the screen with data without re-rendering the layout from scratch.

---

## 🧠 6. FAANG Interview Q&A

**Q: Why is Java's `Cloneable` considered a mistake?**
* **A:** It’s a marker interface without a `clone()` method. It forces you to deal with `CloneNotSupportedException` and doesn't enforce deep cloning of final fields.

**Q: Deep Copy vs. Shallow Copy?**
* **A:** Shallow Copy copies values and references (shared nested objects). Deep Copy creates completely new instances of nested objects (independent copies).

**Q: When should I avoid Prototype?**
* **A:** When objects are very simple or if you have a massive object graph with circular references, which makes deep cloning extremely complex and error-prone.
