# 🪙 LLD Problem: Vending Machine

> **Patterns:** State · Chain of Responsibility

---

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Difficulty** | 🟡 Medium |
| **SDE-2 Mandatory** | ✅ Yes |
| **Patterns** | State, Chain of Responsibility |
| **Status** | Not Started |
| **Times Practiced** | 0 |
| **Last Practiced** | YYYY-MM-DD |
| **Next Review** | YYYY-MM-DD |

---

## 📋 The Prompt
Design a Vending Machine that supports selecting items, accepting coins/cash, dispensing products, and returning change.

### 🛠️ Core Requirements
1.  **State Transitions:** The machine must support standard lifecycle states:
    *   `IdleState`: Waiting for money insertion.
    *   `HasMoneyState`: Money inserted, waiting for product selection or refund request.
    *   `DispensingState`: Dispensing the product, transition back to Idle once done.
    *   `OutOfStockState`: Item is sold out.
2.  **Money Acceptance:** Support inserting coins/bills of various denominations. Keep track of inserted balance.
3.  **Selection:** Support selecting a product via a unique code (e.g. A1, B2).
4.  **Dispense & Change:** Dispense the product and return change if the inserted money exceeds the item price.
5.  **Refund:** Allow the user to cancel the transaction and get a full refund of their inserted money at any point before dispensing.

### ⚙️ Out of Scope / Constraints
- Physical hardware details.
- Inventory restocking logistics (can be a simple admin interface).

---

## ✅ Self-Evaluation Checklist
- [ ] **State Pattern:** Did you avoid a massive `switch-case` block by encapsulating behavior inside distinct `State` classes?
- [ ] **State Transitions:** Does each state handle its specific transitions correctly (e.g., cannot dispense in `IdleState`)?
- [ ] **Change Dispensing:** Did you implement a change dispenser that returns the correct denominations?
- [ ] **Inventory Update:** Does the system decrement inventory only after a successful dispense?

---

## 📂 Practice
Go to the `practice/` folder in your preferred language and start the 35-minute timer.
- **Reference Solution**: Check the `solutions/` folder for a clean, modular object-oriented design.
