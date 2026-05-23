# 🍕 LLD Problem: Pizza Ordering System

> **Patterns:** Abstract Factory · Builder

---

## 📋 Problem Statement

Design a Pizza Ordering System for a chain that has multiple store franchises — each with its own style (Italian, American). The system must:

1. Each store creates the **same menu items** (Margherita, Pepperoni, Veg Deluxe) but in its own signature style
2. Adding a new store franchise must require **zero modification** to existing code
3. Customers can also create **fully custom pizzas** with any combination of toppings, sizes and crust types
4. Object construction must be **readable** — no 8-argument constructors

---

## 🧩 Pattern Mapping

| Sub-Problem | Pattern | Why |
|---|---|---|
| Multiple store franchises creating the same menu differently | **Abstract Factory** | Each store is a factory that produces a "family" of products (its style of pizza). Adding a new franchise = new class only |
| Pizza has 6 fields, 4 optional (toppings, size, cheese, vegan) | **Builder** | Prevents telescoping constructors. Order of optional fields becomes irrelevant and self-documenting |

---

## 🏗️ Architecture

```mermaid
classDiagram
    class IPizzaStore {
        <<Abstract Factory>>
        +createMargherita() Pizza
        +createPepperoni() Pizza
        +createVegDeluxe() Pizza
    }

    class ItalianPizzaStore {
        +createMargherita() Pizza
        +createPepperoni() Pizza
        +createVegDeluxe() Pizza
    }

    class AmericanPizzaStore {
        +createMargherita() Pizza
        +createPepperoni() Pizza
        +createVegDeluxe() Pizza
    }

    class Pizza {
        <<Product - Builder>>
        -name: String
        -crust: Crust
        -size: Size
        -toppings: List
        -extraCheese: boolean
        -vegan: boolean
    }

    class Pizza_Builder {
        +Builder(name, crust)
        +size(Size) Builder
        +topping(String) Builder
        +extraCheese() Builder
        +vegan() Builder
        +build() Pizza
    }

    IPizzaStore <|.. ItalianPizzaStore
    IPizzaStore <|.. AmericanPizzaStore
    IPizzaStore ..> Pizza : creates
    Pizza +-- Pizza_Builder : inner class
```

---

## 💻 Code Walk-Through

### Abstract Factory in Action
```java
// The client only knows IPizzaStore — not the concrete store
IPizzaStore store = new ItalianPizzaStore();  // or AmericanPizzaStore
Pizza margherita = store.createMargherita();

// Swap the entire franchise — same client code!
store = new AmericanPizzaStore();
margherita = store.createMargherita();  // Different pizza, zero code change
```

### Builder in Action
```java
Pizza custom = new Pizza.Builder("My BBQ Chicken", Crust.STUFFED)
    .size(Size.XL)
    .topping("BBQ Sauce")
    .topping("Grilled Chicken")
    .topping("Jalapeños")
    .extraCheese()
    .build();
```

### How to Run
```bash
cd JAVA/
javac pizza/*.java Main.java
java Main
```

---

## 🎭 Junior vs. Senior

| Concern | Junior | Senior |
|---|---|---|
| **Multiple store styles** | Big `if/else` in one `PizzaFactory` class | Abstract Factory — each store is its own class |
| **Optional toppings** | `new Pizza("Marg", "THIN", null, null, true, false)` | Builder — `new Pizza.Builder("Marg", THIN).vegan().build()` |
| **Adding new franchise** | Modifies central factory class | Creates new class implementing `IPizzaStore`. Done. |
| **Vegan flag** | Passed as positional boolean arg (which arg was that again?) | Named method `.vegan()` — self-documenting |

---

## 🧠 FAANG Interview Angles

**Q: Why Abstract Factory instead of Factory Method here?**
> Factory Method creates ONE product. Abstract Factory creates a **family** of related products. A pizza store creates *multiple* menu items that all share the same style (crust, sauce philosophy). Abstract Factory is the right tool when the created products must be consistent with each other.

**Q: What if I need to add a "Japanese" pizza store?**
> Create `JapanesePizzaStore implements IPizzaStore`. Implement `createMargherita()` with miso base, `createPepperoni()` with teriyaki chicken. Zero existing classes modified.

**Q: Why not just have a `PizzaFactory.create(storeType, pizzaType)` with a switch?**
> That's a Simple Factory with two enum parameters — a cartesian product switch statement. `(3 stores × 3 pizzas) = 9 cases today`. Add 2 stores and 2 pizza types → 25 cases. Abstract Factory keeps each combination isolated in its own class.

---

## 🔬 Tracker Diagnostics

*   **Primary Patterns:** Abstract Factory, Builder.
*   **The "Freeze Trap":** Candidates often struggle with where to put the `Builder` — does the Factory use the Builder, or does the Client? (Senior Answer: The *Factory* uses the Builder for standard menu items; the *Client* uses the Builder for custom pizzas).
*   **Class Design Checklist:**
    *   [ ] `IPizzaStore` (Interface or Abstract Class)
    *   [ ] `ItalianPizzaStore`, `AmericanPizzaStore` (Concrete Factories)
    *   [ ] `Pizza` (Product)
    *   [ ] `Pizza.Builder` (Inner static class for customization)
*   **SOLID Violations to Watch For:**
    *   **SRP:** Don't put the pricing logic inside the `Pizza` class; it belongs in a `PricingService` or `PricingStrategy`.
    *   **OCP:** Ensure `IPizzaStore` is used everywhere in the client logic so new franchises don't break anything.
    *   **LSP:** Don't have `ItalianPizzaStore` throw an `UnsupportedOperationException` for `createPepperoni()` if it doesn't like pepperoni; it must fulfill the contract.
