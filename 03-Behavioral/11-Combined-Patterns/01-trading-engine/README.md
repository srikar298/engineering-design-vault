# 📈 Combined Behavioral Patterns: Trading Engine

This module demonstrates how multiple Behavioral patterns act together to create a sophisticated, event-driven subsystem.

## 🧠 Architecture Overview

We are building an **Automated Stock Trading Engine**. 

1. **Observer Pattern:** The `StockMarket` is the Publisher. It pushes `tick()` events to registered `TradingBot` Subscribers. The bots do not poll the database; they react instantly to pushed events.
2. **Strategy Pattern:** The `TradingBot` does not hardcode trading rules. It holds an `ITradingStrategy` (`AggressiveStrategy` or `ConservativeStrategy`) which can be swapped out dynamically *during runtime* without redeploying the bot.
3. **Memento Pattern:** Before attempting risky market maneuvers, the Bot utilizes the `Account` (Originator) to generate an `AccountSnapshot` (Memento). If the market faults or the trade fails, the Bot uses its `panicRollback()` feature to revert the account balance safely.

---

## 💻 Tech Stack Highlights
* **Encapsulation:** The Memento object is strictly immutable, preserving the integrity of the Account's state.
* **Open/Closed Principle:** New Trading Strategies can be added by simply implementing an interface. The `TradingBot` class remains untouched.
* **Inversion of Control:** The bots are completely passive until the `StockMarket` ticks.

## ▶️ Execution
To run the Java implementation, go into the `JAVA` folder and see the specific `README.md`.
