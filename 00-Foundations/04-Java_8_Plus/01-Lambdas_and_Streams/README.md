# Lambdas & Streams (Declarative OOP)

> **The One-Liner Summary:** Lambdas allow you to pass behavior as data — enabling concise inline implementations of Functional Interfaces; Streams give you a declarative pipeline to process collections without a single mutable loop variable.

---

## 📖 1. The Conceptual Core (The "Why")
*   **The Problem:** Traditional `for` loops are inherently *imperative* — they tell the computer exactly *how* to iterate. They mix business logic (filtering, transforming) with control-flow plumbing (indices, `i++`, `break`). This leads to verbose, bug-prone, hard-to-read code.
*   **The Metaphor:** Think of a factory assembly line. Each worker does exactly one job (filter, transform, collect). You snap them together as a pipeline. You describe *what* must happen at each station — not *how* the belt mechanism physically moves the boxes.

---

## 🔍 2. Deep Dive: The Mechanics (The "How")

### 2.1 Functional Interfaces (The Lambda Contract)
A **Functional Interface** has exactly one abstract method. Lambdas are inline implementations of these interfaces. The 4 core built-in ones cover almost every scenario:
| Interface | Signature | Purpose |
| :--- | :--- | :--- |
| `Predicate<T>` | `boolean test(T t)` | Test/filter (returns true/false) |
| `Function<T, R>` | `R apply(T t)` | Transform T → R |
| `Consumer<T>` | `void accept(T t)` | Perform a side effect on T |
| `Supplier<T>` | `T get()` | Produce/create a T with no input |

### 2.2 Method References (`::`)
A shorthand for a Lambda that just calls one existing method:
*   `User::getName` = `(user) -> user.getName()` (**Instance method on parameter**)
*   `System.out::println` = `(x) -> System.out.println(x)` (**Instance method on instance**)
*   `User::new` = `() -> new User()` (**Constructor reference**)

### 2.3 The Stream Pipeline (3 Stages)
A Stream is a sequence of elements supporting aggregate operations. Every Stream pipeline has exactly 3 phases:
1. **Source** → `list.stream()`, `Stream.of(...)`, `Arrays.stream(...)`
2. **Intermediate Operations** (lazy, chainable) → `.filter()`, `.map()`, `.sorted()`, `.distinct()`
3. **Terminal Operation** (triggers execution) → `.collect()`, `.count()`, `.findFirst()`, `.reduce()`

> [!IMPORTANT]
> Streams are **lazy**. Intermediate operations do nothing until a terminal operation is called. This is a key performance optimization — if you call `.findFirst()`, the stream stops processing the moment it finds the first match, even in a list of 10 million elements.

---

## ❌ 3. Junior Mistakes vs. ✅ Senior Solutions

| ❌ The Junior Approach | ✅ The Senior/LLD Approach | 🧠 Why it matters (The "So What?") |
| :--- | :--- | :--- |
| Imperative `for` loops with running `total +=` mutable state. | `stream().mapToDouble().sum()` | Stateless, parallelizable, reads like English. |
| Creating 10 separate Strategy classes for simple single-line operations. | Using Lambdas for lightweight, inline strategies. | If the strategy is one line, a full class file is overengineering. |
| Calling `.stream()` a second time on a used stream. | Always creating a fresh stream from the source. | A Stream can only be consumed once. A second `terminal` call on a used stream throws `IllegalStateException`. |

---

## 🏗️ 4. Real-World Application (System Design)
In a **Financial Risk Engine**:
You have 100,000 `Transaction` objects. Using a parallel Stream (`list.parallelStream()`), you can filter only `HIGH_RISK` transactions, map them to their amounts, and sum the total exposure in milliseconds. The same logic in an imperative `for` loop would require manual thread management, `synchronized` blocks, and an accumulator variable — hundreds of lines vs. 3.

---

## 💥 5. FAANG / MNC Interview Preparation

### Q1: "What is the difference between `map()` and `flatMap()` in Streams?"
**The Senior Answer:**
`map()` applies a function to each element and wraps the result — if the function returns a `List`, you get a `Stream<List<String>>` (nested).
`flatMap()` applies the function and then "flattens" one level of nesting — if the function returns a `List`, you get a `Stream<String>` (flat). Use `flatMap()` whenever a single element maps to multiple results (e.g., one `Order` has many `Items`).

### Q2: "Are Streams faster than `for` loops?"
**The Senior Answer:**
Sequential streams have roughly the same performance as `for` loops, sometimes slightly worse due to boxing overhead. The real value of Streams is `parallelStream()`, which distributes work across CPU cores using the ForkJoinPool — this can be significantly faster for large datasets and CPU-bound operations. For simple small collections, a `for` loop is perfectly fine.

---

## 🛠️ 6. Executable Code Examples
- [LambdaStreamsMastery.java](./LambdaStreamsMastery.java): Complete demonstration of all 4 functional interfaces, method references, and a multi-stage Stream pipeline on a real Order processing domain.

---

## 📚 7. Further Reading / Patterns Linked
- Lambdas provide inline **Strategy Pattern** implementations without needing separate classes.
- `Stream.parallel()` uses the **Fork/Join Framework** under the hood.
