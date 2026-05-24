# 🐍 LLD Problem: Snake and Ladder Game

> **Patterns:** Strategy (For dice rolling or movement rules)

---

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Difficulty** | 🟢 Easy |
| **SDE-2 Mandatory** | ✅ Yes |
| **Patterns** | Strategy |
| **Status** | Not Started |
| **Times Practiced** | 0 |
| **Last Practiced** | YYYY-MM-DD |
| **Next Review** | YYYY-MM-DD |

---

## 📋 The Prompt
Design a classic Snake and Ladder game for $N$ players on a board of size $10 \times 10$ (or parameterized $M \times M$).

### 🛠️ Core Requirements
1.  **Board Setup:** A $100$-cell board with a configurable number of snakes and ladders.
2.  **Dice Logic:** A player rolls a 6-sided die and moves forward.
3.  **Snake/Ladder Logic:** If a player lands on a snake head, they slide down to the tail. If they land on a ladder base, they climb up to the top.
4.  **Win Condition:** The first player to reach exactly cell $100$ wins.
5.  **Multiplayer:** Support $2$ or more players taking sequential turns.

### ⚙️ Out of Scope / Constraints
- UI/Rendering (Console logging of movements is fine).
- Complex dice rules (e.g. three 6s in a row rolls again) are out of scope.
- A cell can only have at most one Snake head or Ladder base, never both. No recursive loops (e.g., snake tail ending at a ladder base).

---

## ✅ Self-Evaluation Checklist
- [ ] **Entity Identification:** Did you separate the `Board`, `Snake`, `Ladder`, `Dice`, and `Player` entities?
- [ ] **Modular Dice:** Is the `Dice` logic encapsulated? (Senior move: allows for injecting a 'Mock Dice' for testing).
- [ ] **Jump Logic:** How do you handle a cell that has both a snake and a ladder? (Requirement: a cell can only be one or neither).
- [ ] **State Invariant:** Does the game handle the case where a roll would take a player past 100? (They should stay in place).

---

## 📂 Practice
Go to the `practice/` folder in your preferred language and start the 35-minute timer.
- **Reference Solution**: Check the `solutions/` folder for a clean, modular object-oriented design.
