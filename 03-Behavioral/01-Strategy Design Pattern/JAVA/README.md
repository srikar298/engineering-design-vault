# ♟️ Strategy — Java Implementation Guide

## 📁 Folder Structure

```
JAVA/
├── Main.java                          ← Integration Demo (Run this)
├── context/                           
│   └── ShoppingCart.java              ← The Context that requests strategies
└── strategy/                          
    ├── IPaymentStrategy.java          ← The Common Algorithm Interface
    ├── CreditCardStrategy.java        ← Concrete Algorithm 1
    ├── PayPalStrategy.java            ← Concrete Algorithm 2
    └── CryptoStrategy.java            ← Concrete Algorithm 3
```

---

## ▶️ How to Run

Navigate to the `JAVA/` directory and compile/run the application:

```bash
cd "03-Behavioral/01-Strategy Design Pattern/JAVA/"
javac context/*.java strategy/*.java Main.java
java Main
```

**Expected output:**
```
==================================================
   Strategy Pattern: E-Commerce Payment Demo      
==================================================

--- Scenario 1: Customer buys a laptop using Credit Card ---
Added item worth $1200.0. New total: $1200.0
Added item worth $50.0. New total: $1250.0

Initiating checkout for total: $1250.0
Processing credit card payment of $1250.0 for Alice Smith (Card ends in 3456)
Checkout complete. Cart emptied.

--- Scenario 2: Customer buys a coffee using PayPal ---
Added item worth $5.5. New total: $5.5

Initiating checkout for total: $5.5
Processing PayPal payment of $5.5 using account: alice.smith@example.com
Checkout complete. Cart emptied.

--- Scenario 3: Customer buys a server using Crypto ---
Added item worth $5000.0. New total: $5000.0

Initiating checkout for total: $5000.0
Processing Crypto (USDC) payment of $5000.0 to wallet: 0x1A2B3C4D5E6F7G8H9I0J
Checkout complete. Cart emptied.
```

---

## 📖 Full Documentation
See the top-level [`README.md`](../README.md) for conceptual explanations, FAANG interview Q&A, and real-world system design usage.
