# State and Behavior (The Core Duality)

> **The One-Liner Summary:** State is the *memory* of an object (what it knows), and behavior is the *capabilities* of an object (what it does). Together, they form the foundation of isolating business logic.

---

## 📌 0. Formal Definitions

### What is State?
> **State** is the collection of data values an object holds at any given moment in time. It represents **"what the object currently knows about itself"**.

State is stored in **fields (instance variables)**. Every object created from the same class gets its own independent copy of the state.

```java
class BankAccount {
    private double balance;     // ← STATE: what this account currently holds
    private boolean isFrozen;   // ← STATE: what condition the account is in
    private String accountId;   // ← STATE: the identity of this specific account
}

BankAccount acc1 = new BankAccount(); // acc1.balance = 0
BankAccount acc2 = new BankAccount(); // acc2.balance = 0
// acc1 and acc2 are INDEPENDENT — changing acc1.balance does not affect acc2
```

**State changes over time** as the object responds to behavior calls:
```
BankAccount created  → balance = 0.0
deposit(1000)        → balance = 1000.0   ← state changed
withdraw(500)        → balance = 500.0    ← state changed again
freeze()             → isFrozen = true    ← different state field changed
```

### What is Behavior?
> **Behavior** is the set of operations an object can perform — either on its own state, or using its state to produce a result. It represents **"what the object can do"**.

Behavior is defined by **methods**. Methods are the ONLY way external code should interact with an object's state.

```java
class BankAccount {
    private double balance;

    // BEHAVIOR: changes state (a "command")
    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Must be positive");
        this.balance += amount;  // ← method guards and modifies the state
    }

    // BEHAVIOR: reads state without changing it (a "query")
    public double getBalance() {
        return this.balance;
    }

    // BEHAVIOR: uses state + produces a business decision
    public boolean canWithdraw(double amount) {
        return this.balance >= amount;
    }
}
```

### The Relationship Between State and Behavior

```
┌─────────────────────────────────────────────────────┐
│                   BankAccount Object                 │
│                                                     │
│  STATE (private)          BEHAVIOR (public)         │
│  ┌───────────────┐        ┌──────────────────────┐  │
│  │ balance=500   │ ←reads─┤ getBalance()         │  │
│  │ isFrozen=false│ ←writes─ deposit(amount)      │  │
│  │ accountId=A01 │        │ withdraw(amount)     │  │
│  └───────────────┘        │ canWithdraw(amount)  │  │
│         ↑                 └──────────────────────┘  │
│         └── State is PRIVATE. Only behavior         │
│              methods may directly touch it.          │
└─────────────────────────────────────────────────────┘
          ↑
    External code sees ONLY the behavior methods (the public surface).
    State is completely hidden — the object is a black box.
```

### The Formal Rule: "Tell, Don't Ask"
> **Tell** the object what you want it to do. Don't **Ask** for its state, compute externally, and write the result back.

```java
// ❌ ASK pattern — external code does the work, bypasses the object's logic
double bal = account.getBalance();
if (bal >= 100) {
    account.setBalance(bal - 100); // ← bypasses validation, no audit log, no event
}

// ✅ TELL pattern — the object owns its own logic
account.withdraw(100); // ← handles validation, events, and state change internally
```

---

## 📖 1. The Conceptual Core (The "Why")
Every object-oriented system starts with one fundamental question: *How do I represent real-world entities in code?*

*   **The Problem:** Without combining state and behavior, your data lives in one place and your logic lives in another. This leads to spaghetti code where any function can modify any data, destroying predictability.
*   **The Real-World Analogy:** Think of a class like a **Recipe** for a cake. The ingredients are the *State* (flour, sugar). The instructions are the *Behavior* (mix, bake). The recipe doesn't produce a cake; following the recipe guarantees a physical cake (*Object*) with its own individual flavor.

---

## 🔍 2. Deep Dive: The Mechanics (The "How")
To truly understand State and Behavior, we must look at where they live—in **Classes** and **Objects**.

### 2.1 The Class Definition (The Blueprint)
*   **Static Concept**: It defines the **Type**, the structure (State), and the capabilities (Behavior).
*   **What it HAS (State/Attributes)**: Defined by fields (`private int battery`). It represents the condition of an object.
*   **What it DOES (Behavior/Methods)**: Defined by methods (`public void charge()`). It manipulates the state or provides services.

### 2.2 The Object Instantiation (The Living Entity)
*   **Dynamic Concept**: It is the physical realization of a Class at runtime.
*   **Independent State**: Each object gets its own mathematical copy of the data defined in the class. When `Corolla` accelerates to 20, the `Mustang` remains at 0 until explicitly accelerated.

### 2.3 The "Tell, Don't Ask" Principle
State should almost always be `private`. Behavior is the only way external objects should interact with that state. You *tell* an object to do something (`phone.charge()`), you don't *ask* for its battery and manually increment it.

---

## ❌ 3. Junior Mistakes vs. ✅ Senior Solutions

| ❌ The Junior Approach | ✅ The Senior/LLD Approach | 🧠 Why it matters (The "So What?") |
| :--- | :--- | :--- |
| Exposing State directly (`public int speed`). | Hiding data, exposing behavior (`private int speed`, `public accelerate()`). | Prevents external classes from putting the object into an illegal state (e.g., `-100 speed`). |
| "Anemic" Domain Models (Data bags with only Getters/Setters). | "Rich" Domain Models (Objects do their own work and protect invariants). | High cohesion; you "Tell" the object to do something, you don't "Ask" for its data to do the math externally. |

---

## 🏗️ 4. Real-World Application (System Design)
In a real enterprise **Food Delivery Platform**:
If Order State (`totalPrice`, `isPlaced`) and Behavior (`addItem()`, `checkout()`) are separated, a bug could allow a user to add items to an order *after* they have checked out.
By bringing them together in a `FoodOrder` class, the object protects its own state. The `addItem()` method simply returns an error if `isPlaced == true`. The object acts as an impenetrable fortress of business logic.

---

## 💥 5. FAANG / MNC Interview Preparation

### Q1: "What is an Anemic Domain Model and why is it considered an anti-pattern by senior engineers?"
**The Senior Answer:**
An Anemic Domain Model is a class that contains only data fields and getters/setters, with zero business logic. It violates the core principle of Object-Oriented Programming because it separates data from the behavior that modifies it. This forces external "Service" classes to extract the data, calculate things, and stuff the data back in, completely destroying Encapsulation.

### Q2: "Explain 'Tell, Don't Ask' in the context of State and Behavior."
**The Senior Answer:**
Instead of *asking* an object for its state, performing a calculation, and setting the state back, you should simply *tell* the object what you want it to accomplish. For example, instead of `if (wallet.getBalance() >= 50) { wallet.setBalance(wallet.getBalance() - 50); }`, you tell the wallet: `wallet.deduct(50);`. The object manages its own state internally.

### Q3: "What is the difference between a 'command' method and a 'query' method?"
**The Senior Answer:**
This is the **Command-Query Separation (CQS)** principle. A **command** changes state and returns `void` (`deposit()`, `withdraw()`, `freeze()`). A **query** reads state and returns a value without changing anything (`getBalance()`, `canWithdraw()`). Mixing the two — a method that changes state AND returns a value — makes code unpredictable and hard to test. The senior practice is to keep them strictly separated.

---

## 🛠️ 6. Executable Code Examples
- [StateBehaviorDemo.java](./StateBehaviorDemo.java): The foundation of State and Behavior using a `Smartphone` example.
- [CarExample.java](./CarExample.java): Demonstrates how multiple `Car` objects maintain independent state.
- [FoodOrderApp.java](./FoodOrderApp.java): A realistic business application where behavior protects state (`isPlaced`).

---

## 📚 7. Further Reading / Patterns Linked
- Mastering State and Behavior leads directly to the **State Design Pattern**, where an object alters its behavior when its internal state changes.
- **Command-Query Separation (CQS)** — formalizes the "command vs query" distinction from Q3 above.
- **Domain-Driven Design (DDD)** — the "Rich Domain Model" is the backbone of DDD Aggregates and Entities.

---

## 🌍 8. Cross-Language: State and Behavior in Python, TypeScript, and Go

> The concept is identical across languages. The syntax for defining state, protecting it, and exposing behavior differs — as does the level of enforcement.

---

### 🐍 Python

```python
class BankAccount:
    # STATE: defined inside __init__ with self. prefix
    def __init__(self, account_id: str, initial_balance: float = 0.0):
        self.__balance = initial_balance    # ← STATE (name-mangled, pseudo-private)
        self.__is_frozen = False            # ← STATE
        self.account_id = account_id       # ← STATE (public — intentional)

    # BEHAVIOR: command — changes state
    def deposit(self, amount: float) -> None:
        if amount <= 0:
            raise ValueError("Amount must be positive")
        if self.__is_frozen:
            raise RuntimeError("Account is frozen")
        self.__balance += amount

    # BEHAVIOR: query — reads state, no change
    def get_balance(self) -> float:
        return self.__balance

    # BEHAVIOR: uses state + produces decision
    def can_withdraw(self, amount: float) -> bool:
        return not self.__is_frozen and self.__balance >= amount
```

**Key Python differences:**
| Concept | Java | Python |
|---|---|---|
| State fields | `private double balance` | `self.__balance` in `__init__` |
| Privacy enforcement | Compiler enforced | Name-mangling only — `acc._BankAccount__balance` still works |
| "Tell, Don't Ask" | Same principle | Same principle — but nothing enforces you don't expose `self.balance` |
| Independent object state | Each `new Account()` has own copy | Each `BankAccount()` call has own `self.__balance` |

---

### 🟦 TypeScript

```typescript
class BankAccount {
    // STATE: typed fields with access modifiers
    private balance: number;        // ← STATE (TypeScript-enforced compile-time only)
    private isFrozen: boolean;
    public readonly accountId: string;  // ← STATE (public but immutable)

    constructor(accountId: string, initialBalance: number = 0) {
        this.accountId = accountId;
        this.balance = initialBalance;
        this.isFrozen = false;
    }

    // BEHAVIOR: command
    deposit(amount: number): void {
        if (amount <= 0) throw new Error("Amount must be positive");
        if (this.isFrozen) throw new Error("Account is frozen");
        this.balance += amount;
    }

    // BEHAVIOR: query
    getBalance(): number { return this.balance; }

    // BEHAVIOR: decision using state
    canWithdraw(amount: number): boolean {
        return !this.isFrozen && this.balance >= amount;
    }
}
```

**Key TypeScript differences:**
| Concept | Java | TypeScript |
|---|---|---|
| State fields | `private double balance` | `private balance: number` |
| Privacy enforcement | JVM enforced (runtime) | TypeScript compiler only — `(acc as any).balance` bypasses it |
| True runtime private | `private` keyword | `#balance` (ES2022 hard private) |
| `readonly` state | `final` field | `readonly` — set once in constructor, immutable after |
| Independent state | Each `new Account()` | Each `new BankAccount()` — same as Java |

---

### 🐹 Go

Go has **no classes** — State lives in **structs**, Behavior lives in **methods with receivers**.

```go
package bank

// STATE: defined in a struct
type BankAccount struct {
    balance   float64  // lowercase = unexported (Go's "private")
    isFrozen  bool
    AccountID string   // uppercase = exported (Go's "public")
}

// "Constructor" function — Go convention: NewXxx
func NewBankAccount(id string, initialBalance float64) *BankAccount {
    return &BankAccount{
        AccountID: id,
        balance:   initialBalance,
        isFrozen:  false,
    }
}

// BEHAVIOR: command — receiver is a pointer (can mutate state)
func (a *BankAccount) Deposit(amount float64) error {
    if amount <= 0 {
        return fmt.Errorf("amount must be positive")
    }
    if a.isFrozen {
        return fmt.Errorf("account is frozen")
    }
    a.balance += amount  // ← mutates the struct's state
    return nil
}

// BEHAVIOR: query — receiver is a value (cannot mutate state — enforced by convention)
func (a BankAccount) GetBalance() float64 {
    return a.balance
}

// BEHAVIOR: decision using state
func (a BankAccount) CanWithdraw(amount float64) bool {
    return !a.isFrozen && a.balance >= amount
}
```

```go
// Usage — identical "Tell, Don't Ask" concept:
acc := bank.NewBankAccount("A01", 0)
acc.Deposit(1000)            // TELL — method owns the logic
acc.GetBalance()             // QUERY — read-only
// acc.balance = 9999        // ❌ COMPILE ERROR — unexported field
```

**Key Go differences:**
| Concept | Java | Go |
|---|---|---|
| State definition | Fields in a class | Fields in a `struct` |
| Behavior definition | Methods in a class | Functions with a **receiver** (`func (a *Account) Method()`) |
| Privacy enforcement | `private` keyword | Lowercase first letter — compile-time enforced |
| "Tell, Don't Ask" | Same principle | Same principle — `acc.balance` is a compile error from outside the package |
| Pointer vs value receiver | N/A | `*Account` receiver = can mutate, `Account` receiver = read-only copy |
| "Constructor" | `new Account()` / factory | `func NewAccount() *Account` — package-level function |

---

### 📊 Master Comparison Table

| Concept | ☕ Java | 🐍 Python | 🟦 TypeScript | 🐹 Go |
|---|---|---|---|---|
| **State defined in** | Class fields | `self.x` in `__init__` | Class fields with types | Struct fields |
| **Behavior defined in** | Class methods | Class methods | Class methods | Receiver functions |
| **Private state** | `private` (runtime enforced) | `__x` (name-mangled, not enforced) | `private` (compile) / `#x` (runtime) | `lowercase` (compile enforced) |
| **Read-only state** | `final` field | `@property` with no setter | `readonly` | Value receiver (by convention) |
| **Independent per-object state** | ✅ Each `new` call | ✅ Each `__init__` call | ✅ Each `new` call | ✅ Each struct literal / `New()` |
| **"Tell, Don't Ask" enforcement** | By design (private fields) | Convention only | Compile-time only | Compile-time (unexported fields) |
| **Anemic model risk** | High (Lombok @Data) | Very high (dataclasses with no methods) | High (plain interfaces / DTOs) | Lower (no getters/setters culture) |

> **The Senior Takeaway:** State and Behavior are universal OOP concepts. Every language implements them, but Go is the most different — it separates the data type (struct) from the behavior (receiver functions) syntactically, while Java, Python, and TypeScript co-locate them in a class body. The principle "Tell, Don't Ask" applies equally in all four, but only Java and Go enforce state privacy at compile time.
