# 🔒 Singleton Design Pattern

---

## 🧭 Section 0: Formal Definition

> **A Singleton** is a creational design pattern that ensures a class has **exactly one instance** throughout the application's lifetime, and provides a **global access point** to that instance.

**The Two Guarantees a Singleton Must Make:**
1. Only one instance ever exists — no matter how many threads call `getInstance()`
2. That instance is globally accessible — from anywhere in the codebase

**Invariant (must always hold):**
```java
assert PaymentGateway.getInstance() == PaymentGateway.getInstance(); // Must ALWAYS be true
```

---

## 📖 1. The Core Concept (The "Why")

Some things in your system should genuinely only exist **once**:
- A connection pool (expensive to create, shared by all)
- A configuration loader (reads from disk/env, parsed once)
- A logger (all components write to the same log stream)
- A payment gateway client (one authorized API connection)

Without a Singleton, you risk:
- Creating 100 database connections (one per thread) instead of sharing a pool of 10
- Loading config from disk on every function call
- Having two payment gateway clients authorized simultaneously, causing race conditions in payment state

---

## ⚠️ 2. The Anti-Pattern Debate (Senior Awareness)

> **"Singleton is the most abused pattern in Java."** — Every senior who has ever maintained legacy enterprise code.

The core problem: **Singleton = hidden global state**.

```java
// How you write it:
Logger.getInstance().log("something");

// What you're actually doing:
GLOBAL_LOGGER.log("something");
// → Your class now secretly depends on a global.
// → You cannot test it without the real Logger running.
// → You cannot swap implementations without touching every call site.
```

**The modern alternative: Dependency Injection**
```java
// ❌ Old way — Singleton pulled from global scope
class OrderService {
    public void placeOrder() {
        PaymentGateway.getInstance().charge();   // Untestable, tightly coupled
    }
}

// ✅ Modern way — Singleton managed by DI container (Spring @Bean)
class OrderService {
    private final PaymentGateway gateway;
    
    public OrderService(PaymentGateway gateway) {  // Injected!
        this.gateway = gateway;
    }
}
// Spring ensures only ONE PaymentGateway bean exists.
// Tests inject a mock. Zero global state in your class.
```

**When to still use manual Singletons:**
- You are NOT using a DI framework (embedded systems, Android without Hilt, etc.)
- The Singleton is truly stateless (just a utility/registry)
- You explicitly need eager initialization at class-loading time

---

## 🔬 3. The 4 Evolutionary Stages

### Stage 1a: Naive Lazy Init — ❌ Thread Unsafe
```java
public static Stage1NaiveLazy getInstance() {
    if (instance == null) {           // Thread A and B both see null
        instance = new Stage1NaiveLazy();  // BOTH create an instance!
    }
    return instance;
}
```
**Bug:** Race condition. In high-concurrency, you get 2+ instances.

---

### Stage 1b: Synchronized Method — ❌ Performance Bottleneck
```java
public static synchronized Stage1bSynchronizedMethod getInstance() {
    if (instance == null) {
        instance = new Stage1bSynchronizedMethod();
    }
    return instance;
}
```
**Bug:** The lock is held on EVERY call — even after the instance is created. 1000 threads reading the instance = 1000 sequential lock acquisitions. Kills throughput.

---

### Stage 2: Double-Checked Locking — ✅ Correct, But Complex
```java
private static volatile Stage2DoubleChecked instance;  // volatile is MANDATORY

public static Stage2DoubleChecked getInstance() {
    if (instance == null) {           // Fast path — no lock
        synchronized (Stage2DoubleChecked.class) {
            if (instance == null) {   // Guard after acquiring lock
                instance = new Stage2DoubleChecked();
            }
        }
    }
    return instance;
}
```
**The `volatile` is critical:** Without it, the CPU/compiler can reorder instructions so that `instance` is assigned *before* the constructor finishes running. Another thread sees a non-null but partially-constructed object — one of the most subtle bugs in Java.

---

### Stage 3: Bill Pugh — ⭐⭐⭐ Clean & Lock-Free
```java
public class Stage3BillPugh {
    private static class InstanceHolder {
        private static final Stage3BillPugh INSTANCE = new Stage3BillPugh();
    }
    
    public static Stage3BillPugh getInstance() {
        return InstanceHolder.INSTANCE;   // JVM classloader handles thread-safety
    }
}
```
**Why this works:** The JVM guarantees that class initialization is atomic and happens exactly once. `InstanceHolder` is only loaded when `getInstance()` is first called — and the JVM handles all the synchronization for us, invisibly.

---

### Stage 4: Enum Singleton — ⭐⭐⭐⭐⭐ Unhackable
```java
public enum Stage4EnumSingleton {
    INSTANCE;
    
    public void processPayment(double amount) { ... }
}

// Usage:
Stage4EnumSingleton.INSTANCE.processPayment(100.0);
```
**Why this is the ultimate solution:**
1. **Reflection-proof:** `Constructor.setAccessible(true)` throws an exception for enums
2. **Serialization-proof:** Enum deserialization never creates a new instance
3. **Thread-safe by spec:** Java spec guarantees enum initialization is atomic
4. **Brevity:** Zero boilerplate

---

## 🎭 4. Junior vs. Senior Comparison

| Concern | Junior | Senior |
|---|---|---|
| **Thread Safety** | Slaps `synchronized` on the whole method | Uses Bill Pugh or Enum |
| **`volatile` awareness** | Doesn't know it's needed | Explains instruction reordering without prompting |
| **Testing** | Returns production Singleton in tests | Passes mock via constructor; Singleton managed by DI |
| **Reflection attack** | Doesn't know it's possible | Uses Enum to prevent it; or throws exception in constructor |
| **Use judgment** | Uses Singleton for every shared object | Prefers DI; uses Singleton only when DI framework is absent |

---

## 🏢 5. Real-World System Design Uses

| System | How Singleton Appears |
|---|---|
| **Spring `@Bean` (singleton scope)** | Spring ApplicationContext manages one bean instance — the framework IS the Singleton pattern |
| **Java `Runtime.getRuntime()`** | Classic Bill Pugh-style singleton to access JVM runtime |
| **Android `Application` class** | One instance per process — effectively a Singleton |
| **Database Connection Pool (HikariCP)** | One `HikariDataSource` per datasource config, shared across all threads |
| **Logging frameworks (SLF4J/Log4j)** | Single logging registry per JVM; loggers are cached by name |

---

## 🧠 6. FAANG Interview Q&A

**Q: Can you break a Singleton with Java Reflection?**
```java
// Yes — on Stage2/Stage3 (class-based singletons):
Constructor<Stage3BillPugh> c = Stage3BillPugh.class.getDeclaredConstructor();
c.setAccessible(true);
Stage3BillPugh hacked = c.newInstance();  // Creates a SECOND instance!

// Fix: Throw in constructor if already initialized
private Stage3BillPugh() {
    if (InstanceHolder.INSTANCE != null) {
        throw new IllegalStateException("Use getInstance()");
    }
}
// OR: Use Enum Singleton (immune by specification)
```

**Q: Can you break a Singleton via Serialization?**
```java
// Yes — deserializing a Singleton creates a NEW object!
// Fix for class-based: implement readResolve()
protected Object readResolve() {
    return getInstance();  // Returns existing instance instead of deserialized copy
}
// OR: Use Enum Singleton (immune by specification)
```

**Q: What happens with multiple ClassLoaders?**
> Each classloader has its own namespace. If two classloaders load the same class, you get two `static` fields — two "singletons." This is relevant in OSGi, application servers (Tomcat), and plugins. Fix: Use a shared parent classloader or a registry pattern.

**Q: How does Spring manage Singletons without the pattern?**
> Spring's `ApplicationContext` internally uses a `ConcurrentHashMap<String, Object>` as a bean registry. Singleton scope = store the bean after first creation and return the same reference every time. No `synchronized getInstance()` needed — the container manages all of it.

---

## 🌍 7. Cross-Language: Singleton in Python, TypeScript, and Go

### 🐍 Python

Python modules are Singletons by default — they are only loaded once per interpreter session.

```python
# The simplest Python singleton: just use a module
# config.py
_api_key = "stripe_live_key_xyz"   # Module-level variable

def get_api_key():
    return _api_key

# In any file:
import config
config.get_api_key()  # Same value everywhere — module loaded once
```

**Class-based Python Singleton:**
```python
class PaymentGateway:
    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
        return cls._instance

# Thread-safe Python:
import threading
_lock = threading.Lock()

class PaymentGateway:
    _instance = None

    def __new__(cls):
        if cls._instance is None:
            with _lock:
                if cls._instance is None:
                    cls._instance = super().__new__(cls)
        return cls._instance
```

| Java | Python |
|---|---|
| Enum Singleton | Module-level variable (simplest) |
| Bill Pugh / Double-checked | `__new__` + threading.Lock |
| DI Framework | No enforcement; convention-based |

---

### 🟦 TypeScript

TypeScript modules are also Singletons by nature (Node.js module cache).

```typescript
// The simplest TS singleton: just export an instance from a module
// paymentGateway.ts
class PaymentGateway {
    private apiKey: string;
    private constructor() { this.apiKey = process.env.STRIPE_KEY!; }
    charge(amount: number) { console.log(`Charging $${amount}`); }
}

export const paymentGateway = new PaymentGateway();
// ↑ Module cache ensures this is created exactly once
// Any file that imports paymentGateway gets THE SAME instance
```

**Traditional class-based:**
```typescript
class PaymentGateway {
    private static instance: PaymentGateway;
    private constructor() {}

    static getInstance(): PaymentGateway {
        if (!PaymentGateway.instance) {
            PaymentGateway.instance = new PaymentGateway();
        }
        return PaymentGateway.instance;
    }
}
```

| Java | TypeScript |
|---|---|
| Enum Singleton | Module export (Node.js caches it) |
| `volatile` + double-check | Not needed — JS is single-threaded |
| Serialization attack | Not applicable |

---

### 🐹 Go

Go uses `sync.Once` — the idiomatic and bulletproof Go Singleton.

```go
import "sync"

type PaymentGateway struct {
    apiKey string
}

var (
    gateway *PaymentGateway
    once    sync.Once
)

func GetGateway() *PaymentGateway {
    once.Do(func() {
        gateway = &PaymentGateway{apiKey: os.Getenv("STRIPE_KEY")}
    })
    return gateway
}
```

`sync.Once` is Go's equivalent of Bill Pugh — guaranteed to run exactly once, even under concurrent access, with zero developer-managed locking.

| Java | Go |
|---|---|
| `volatile` + double-check | `sync.Once` |
| Bill Pugh | `sync.Once` |
| Enum Singleton | No direct equivalent — use `sync.Once` |
| Reflection attack | Not applicable (Go has no reflection-based constructor bypass) |

---

## 📚 8. Further Reading / Patterns Linked

- **Singleton + Factory Method** → The factory itself is often a Singleton (see `07-Combined-Patterns`)
- **Singleton + Abstract Factory** → One platform factory per runtime (see `07-Combined-Patterns`)
- **Singleton (Enum) ties back to** → `00-Foundations/01-OOP_Basics/05-Advanced_Enums` — Enums are the only truly unbreakable Singleton in Java
