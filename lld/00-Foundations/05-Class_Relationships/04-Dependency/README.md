# Dependency (The "Temporary Uses-A" Relationship)

> **The One-Liner Summary:** Dependency is the weakest and most transient relationship — Class A uses Class B only for the duration of a single method call, with no lasting field reference retained afterward.

---

## 📖 1. The Conceptual Core (The "Why")
*   **The Problem:** If you accidentally upgrade a transient "use" into a permanent field reference (Association), you couple the lifecycle of two objects that should have no long-term affinity, increasing memory usage and creating hidden dependencies.
*   **The Metaphor:** Think of a `Person` and a `Taxi`. The person uses the taxi to get somewhere. For that 20-minute ride, they are in a relationship. But the moment they get out, no permanent bond remains. The person doesn't hold a taxi reference in their pocket forever. This is Dependency — a temporary, method-scoped use.

---

## 🔍 2. Deep Dive: The Mechanics (The "How")

### 2.1 The Transience Rule
The defining characteristic of Dependency:
> **"Class A uses Class B only as a method parameter, local variable, or return type — never as a stored field."**

The moment a reference to Class B is stored as an instance field, it is no longer a Dependency — it has been promoted to Association.

### 2.2 UML Representation
```
Document - - - -> Printer
```
- **Dashed arrow** (`- - ->`) points from the user class to the used class.
- No multiplicity labels (the relationship is ephemeral, not structural).
- The arrowhead says: "Document uses Printer".

### 2.3 Three Forms of Dependency in Code
```java
// Form 1: Method PARAMETER (most common)
public void export(PdfRenderer renderer) {
    renderer.render(this.content);
}

// Form 2: LOCAL VARIABLE (dependency lives only in this stack frame)
public void save() {
    FileWriter writer = new FileWriter("output.txt"); // Used and discarded
    writer.write(this.content);
}

// Form 3: RETURN TYPE (method produces a dependent type)
public Report generateReport(ReportBuilder builder) {
    return builder.build(this.data);
}
```

---

## ❌ 3. Junior Mistakes vs. ✅ Senior Solutions

| ❌ The Junior Approach | ✅ The Senior/LLD Approach | 🧠 Why it matters (The "So What?") |
| :--- | :--- | :--- |
| Storing a method parameter as a field (`this.logger = logger`) when it's only needed for one operation. | Keeping the reference local to the method — pass it in, use it, discard it. | Storing it creates a permanent Association, loading `Logger` into the object graph unnecessarily. |
| Passing concrete implementations as dependencies: `print(LaserPrinter p)`. | Depending on interfaces: `print(Printer p)`. | Depending on a concrete class couples the caller to one specific implementation. Depending on the interface allows any compatible printer. |
| Not realizing that `import` statements in Java code represent compile-time Dependencies. | Minimizing imports/dependencies in each class to only what it truly needs. | Every import is a coupling. The more classes a module depends on, the harder it is to test in isolation. |

---

## 🏗️ 4. Real-World Application (System Design)
In a **Document Export Service**:
A `Document` object can be exported to multiple formats. It does NOT hold a permanent field reference to a `PdfExporter` or `CsvExporter`. Instead, the exporter is passed in as a method parameter:
```java
document.exportAs(new PdfExporter());
document.exportAs(new CsvExporter());
```
The `Document` class depends on the `Exporter` interface — the relationship is entirely method-scoped. This keeps `Document` clean, testable, and completely independent of all exporter implementations.

---

## 💥 5. FAANG / MNC Interview Preparation

### Q1: "What is the relationship between Dependency and the Dependency Inversion Principle (SOLID)?"
**The Senior Answer:**
The Dependency Inversion Principle (DIP) says: "High-level modules should not depend on low-level modules — both should depend on abstractions." This directly governs *how* Dependency relationships are coded. Instead of `Document` depending on the concrete `PdfRenderer`, it should depend on the abstract `Renderer` interface. The DIP transforms fragile concrete dependencies into flexible, testable abstract dependencies.

### Q2: "How does `Dependency` differ from `Association` in terms of coupling risk?"
**The Senior Answer:**
Dependency is the **weakest coupling** — it exists only during the execution of a single method and disappears the moment that stack frame is popped. It doesn't survive between method calls. Association is a **persistent coupling** — once an object is stored as a field, any change to that object's API can break the parent class silently. Dependency is always preferable when the relationship is truly transient.

---

## 🛠️ 6. Executable Code Examples
- [DependencyDemo.java](./DependencyDemo.java): A Document Export system showing Dependency via method parameters versus the wrong approach of storing an exporter as a field.

---

## 📚 7. Further Reading / Patterns Linked
- Dependency (on interfaces) directly enables the **Dependency Inversion Principle** — the "D" in SOLID.
- It is the relationship type leveraged in **Dependency Injection** frameworks (Spring, Guice, Dagger).
