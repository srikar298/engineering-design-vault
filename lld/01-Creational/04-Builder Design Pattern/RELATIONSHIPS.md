# 🔗 Builder Pattern & Object Relationships

In Low-Level Design (LLD), the "Standard" Builder implementation changes depending on the relationship between the Client, the Director, and the Product.

---

## 1. 🤝 Association (Client ↔️ Builder)
In its simplest form, the relationship is a **temporary association**. The client uses the builder as a "one-off" helper and then forgets about it.

### Code Manifestation:
```java
// Builder is a local variable, not a field.
public void createOrder() {
    Order order = new OrderBuilder()
                    .setItem("Laptop")
                    .setPrice(1000)
                    .build(); 
}
```
### When to use:
- When the construction is simple and doesn't require a Director.
- When you want the most decoupled approach possible.

---

## 2. 🧺 Aggregation (Director 💎 Builder)
In the "Classical" GoF pattern, the Director has an **Aggregation** relationship with the Builder. The Builder exists independently of the Director and is passed into it (Dependency Injection).

### Code Manifestation:
```java
public class Director {
    private Builder builder; // Aggregation (has-a)

    public Director(Builder builder) {
        this.builder = builder;
    }

    public void construct() {
        builder.step1();
        builder.step2();
    }
}
```
### When to use:
- When you have **different representations** (e.g., MacBuilder vs. WindowsBuilder) but the same construction steps.
- When the Builder needs to survive the Director's lifecycle (e.g., the Builder is reused elsewhere).

---

## 3. 🍱 Composition (Product 🔒 Builder)
In modern Java (Lombok style), the Builder is typically a **Static Inner Class** of the Product. This is a form of **Strict Composition** where the Builder is tightly bound to the Product's private state.

### Code Manifestation:
```java
public class User {
    private final String name;
    
    private User(Builder b) { this.name = b.name; }

    public static class Builder { // Composition
        private String name;
        public User build() { return new User(this); }
    }
}
```
### When to use:
- **Immutability Is Key**: The Builder needs access to the `private` constructor of the Product.
- **Atomic Creation**: You want to ensure the Product *cannot* exist without its Builder.
- **Industry Standard**: This is the most common pattern in FAANG-level Java development.

---

## 🕒 Summary Table: When to use what?

| Relationship | Interaction Type | Lifecycle | Best For... |
|---|---|---|---|
| **Association** | "Uses-a" | Short (Method scope) | Simple, one-off construction. |
| **Aggregation** | "Has-a" | Independent | Reusable directors & multiple platforms. |
| **Composition** | "Part-of" | Tightly Bound | Thread-safe, Immutable objects. |

### 💡 The Senior Insight:
*   Use **Composition** for your domain entities (Users, Orders, Accounts) to ensure they are always valid and immutable.
*   Use **Aggregation** for your Infrastructure layers (Report Generators, Document Converters) where the "process" is formal and needs to be swapped out easily.
