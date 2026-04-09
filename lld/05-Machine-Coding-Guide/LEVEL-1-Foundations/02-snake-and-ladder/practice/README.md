# 🐍 Snake and Ladder (Game Design)

> **Interview Time:** 35 Minutes | **Level:** SDE-1 / Foundation

## 📋 The Prompt
Design a classic Snake and Ladder game for $N$ players on a board of size $10 \times 10$.

### 🛠️ Core Requirements
1.  **Board Setup:** A $100$-cell board with a configurable number of snakes and ladders.
2.  **Dice Logic:** A player rolls a 6-sided die and moves forward.
3.  **Snake/Ladder Logic:** If a player lands on a snake head, they slide down. If they land on a ladder base, they climb up.
4.  **Win Condition:** The first player to reach exactly cell $100$ wins.
5.  **Multiplayer:** Support $2$ or more players taking sequential turns.

### ⚙️ Out of Scope
- UI/Rendering.
- Complex dice rules (e.g., three 6s in a row).
- Different board shapes.

---

## ✅ Self-Evaluation Checklist
- [ ] **Entity Identification:** Did you separate the `Board`, `Snake`, `Ladder`, and `Player` entities?
- [ ] **Modular Dice:** Is the `Dice` logic encapsulated? (Senior move: allows for injecting a 'Mock Dice' for testing).
- [ ] **Jump Logic:** How do you handle a cell that has both a snake and a ladder? (Requirement: a cell can only be one or neither).
- [ ] **State Invariant:** Does the game handle the case where a roll would take a player past 100? (They should stay in place).
