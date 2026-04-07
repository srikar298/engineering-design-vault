# 🏗️ SOLID Principles: The SDE-2+ Master Foundation

> **"SOLID is the 'Grammar' of Object-Oriented Design. Design Patterns are the 'Vocabulary'."**

For an SDE-2+ candidate, SOLID isn't about memorizing acronyms. It is about **Software Complexity Management.** If you can't justify your design using these five principles, you aren't ready for a 10k+ concurrent user environment.

---

## 🛠️ The "Meta-Principles" (The 10/10 Foundations)

Before you apply SOLID, you must understand these 4 "Guardrails":

1.  **Composition Over Inheritance:** Inheritance is rigid and leads to LSP violations. Composition is flexible and enables OCP. **Senior Rule:** Only use inheritance for "Behavioral Subtyping," not code reuse.
2.  **KISS (Keep It Simple, Stupid):** Over-applying SOLID leads to "Interface Explosion." If a piece of code is 10 lines and won't change for 2 years, don't force OCP/DIP on it.
3.  **DRY (Don't Repeat Yourself) vs. SRP:** Juniors merge code if it looks similar. **Seniors keep it separate** if it changes for different reasons. (Example: `UserDTO` and `UserEntity` look identical, but changing the Database shouldn't force a change in the API response).
4.  **YAGNI (You Ain't Gonna Need It):** Don't build an "Abstract Plugin Factory" if you only have one payment method. Refactor to OCP only when the *second* variant arrives.
5.  **Law of Demeter (LoD):** "Only talk to your immediate friends." Don't reach through objects (`user.getAccount().getProfile().getZipCode()`). This prevents **"Train Wrecks"** and tight coupling.

---

## 🚀 SOLID to Design Patterns: The Launchpad
...
| **DIP** | **Dependency Injection**, **Bridge**, **Repository** |

---

## 🌍 The Polyglot Perspective: Senior Edge Cases

### 🟢 Node/TypeScript: Structural Typing vs. Nominal Intent (LSP)
In TypeScript, interfaces are **Structural** (if it looks like a duck, it is a duck). 
*   **The Edge Case:** An object might match the *shape* of an interface but violate its *behavioral intent*.
*   **The Trap:** 
    ```typescript
    interface Authenticator { authenticate(): void; }
    // A 'Mock' that just logs but doesn't actually check credentials 
    // satisfies the type but violates the LSP behavioral contract.
    ```
*   **Senior Insight:** *"In TS, I use **Branded Types** or **Type Guards** to ensure that an object isn't just the right shape, but has been through the correct validation logic, preventing 'accidental' LSP violations."*

### 🔵 Golang: Using DIP to Break Circular Dependencies
Go strictly forbids circular imports (Package A imports B, and B imports A).
*   **The Edge Case:** Two packages are logically related but need each other's types.
*   **The Senior Solution:** **Dependency Inversion**.
*   **The Strategy:** Move the shared interface to a third, "Higher-level" package (e.g., `domain`) or have Package A define an interface that Package B implements.
*   **Senior Insight:** *"In Go, DIP isn't just for testing; it's a **requirement** for compilation. I use interfaces at package boundaries to break circular dependencies, which naturally enforces a clean, one-way dependency graph."*

---

## ✅ Final SDE-2+ SOLID Readiness Checklist

*Use these "Senior Answer" scripts to create a Strong Hire impact during your interview:*

1.  **"Who should own the interface in Dependency Inversion?"**
    *   **Senior Answer:** *"The **High-level module** must own the interface. This is the 'Inversion' in DIP. If the low-level module (like a Database SDK) owns the interface, the high-level policy is still forced to import that package, which breaks the architectural boundary. By having the Domain layer define the interface, we force the Infrastructure to conform to our business needs."*

2.  **"Is the classic Square-extends-Rectangle an LSP violation?"**
    *   **Senior Answer:** *"Yes. While it makes sense in geometry, it fails in software because it breaks the **Invariants** of the parent. A Rectangle contract implies that width and height can vary independently. A Square forces them to be equal. Any caller trusting the Rectangle contract will get unexpected behavior when passing a Square."*

3.  **"How do you distinguish between SRP and ISP?"**
    *   **Senior Answer:** *"I view **SRP** as a principle of **Internal Cohesion** (what belongs inside a class) and **ISP** as a principle of **External Coupling** (what a client is forced to see). SRP says: 'Don't change for more than one reason.' ISP says: 'Don't force a client to depend on what they don't use.' They are two sides of the same 'Isolation' coin."*

4.  **"Why is a large `switch` statement often an OCP violation?"**
    *   **Senior Answer:** *"Because every time you add a new type, you must **modify** that switch statement. This increases regression risk in stable code. A senior approach is to replace the switch with a **Registry (Map)** of Strategy objects, making the selection logic 'Closed for Modification' but 'Open for Extension' via new Map entries."*

5.  **"When does DRY (Don't Repeat Yourself) conflict with SRP?"**
    *   **Senior Answer:** *"DRY is about code duplication; SRP is about **Responsibility**. If two pieces of code look identical but serve different stakeholders (e.g., a `User` entity for the DB and a `User` DTO for the API), merging them is a mistake. Changing the DB schema shouldn't accidentally break the API contract. In this case, **duplication is better than coupling**."*

6.  **"What is a 'Train Wreck' in code, and which principle does it violate?"**
    *   **Senior Answer:** *"A 'Train Wreck' is a long chain of method calls like `a.getB().getC().doSomething()`. This violates the **Law of Demeter**. It makes the caller dependent on the internal structure of B and C. To fix it, I'd apply **Encapsulation** and ask A to perform the action directly, hiding the internal navigation."*

---

> **Ready to apply these foundations? Move on to [01-Creational](../01-Creational/README.md) patterns.**
