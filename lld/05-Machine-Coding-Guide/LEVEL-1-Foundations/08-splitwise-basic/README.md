# 💸 LLD Problem: Splitwise (Basic)

> **Patterns:** Strategy (For split calculation strategies)

---

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Difficulty** | 🟡 Medium |
| **SDE-2 Mandatory** | ✅ Yes |
| **Patterns** | Strategy |
| **Status** | Not Started |
| **Times Practiced** | 0 |
| **Last Practiced** | YYYY-MM-DD |
| **Next Review** | YYYY-MM-DD |

---

## 📋 The Prompt
Design a basic Splitwise application that allows users to add expenses and split them among group members using various split strategies.

### 🛠️ Core Requirements
1.  **User & Group Management:** Register users and form groups.
2.  **Add Expense:** Users can add expenses to a group. An expense consists of:
    *   The paying user.
    *   The total amount.
    *   A split strategy.
3.  **Split Strategies:**
    *   **Equal Split:** Split the cost equally among all participants.
    *   **Exact Split:** Specify the exact share of each participant (validating that the sum equals the total amount).
    *   **Percentage Split:** Specify percentage shares (validating that the sum equals 100%).
4.  **Balance Sheet:** Calculate and display the net balance of each user (how much they owe or are owed).
5.  **Simplify Expenses (Optional/Bonus):** Minimize the number of transactions required to settle up.

### ⚙️ Out of Scope / Constraints
- Actual payment gateway transfers.
- Database storage (in-memory structures are sufficient).

---

## ✅ Self-Evaluation Checklist
- [ ] **Strategy Pattern:** Did you encapsulate split calculations (`Equal`, `Exact`, `Percentage`) into separate strategy classes?
- [ ] **Data Model:** Are balances stored as a directed graph or a double map (e.g. `Map<User, Map<User, Double>>`)?
- [ ] **Floating Point Rounding:** Did you handle rounding errors (e.g., splitting $10.00 among 3 people equals $3.33, $3.33, $3.34)?
- [ ] **Validation:** Does the system reject invalid split inputs (e.g. percentages summing to 99%) before modifying balances?

---

## 📂 Practice
Go to the `practice/` folder in your preferred language and start the 35-minute timer.
- **Reference Solution**: Check the `solutions/` folder for a clean, modular object-oriented design.
