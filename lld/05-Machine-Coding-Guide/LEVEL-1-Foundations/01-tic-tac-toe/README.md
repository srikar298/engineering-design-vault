# 🎮 LLD Problem: Tic-Tac-Toe Game

> **Patterns:** State · Factory · Command (Optional for Undo)

---

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Difficulty** | 🟢 Easy |
| **SDE-2 Mandatory** | ✅ Yes |
| **Patterns** | State, Factory |
| **Status** | Not Started |
| **Times Practiced** | 0 |
| **Last Practiced** | YYYY-MM-DD |
| **Next Review** | YYYY-MM-DD |

---

## 📋 The Prompt
Design a simple Tic-Tac-Toe game that can be played by two players on a 3x3 grid (or parameterized $N \times N$ grid).

### 🛠️ Core Requirements
1.  **Game Setup:** Initialize a 3x3 board and two players (X and O).
2.  **Make Move:** Allow players to take turns placing their mark on an empty cell.
3.  **Win Condition:** A player wins by completing a row, column, or diagonal.
4.  **Draw Condition:** If the board is full and no one has won, the game is a draw.
5.  **Validation:** Reject moves on occupied cells or moves made after the game has ended.

### ⚙️ Out of Scope / Constraints
- AI Opponent (Minimax algorithm) is out of scope for a standard 35-min interview.
- UI/Rendering (Console output is fine).
- Networked Multiplayer.

---

## 🧭 Delivery Roadmap (Timed)
1.  **Requirements (5m):** Clarify board size (3x3 or NxN?) and win conditions.
2.  **Entities (3m):** Game, Board, Player, Cell.
3.  **Class Design (10m):** Define `makeMove(row, col)` and `checkWin()`.
4.  **Implementation (15m):** Write the MVP logic.
5.  **Verification (2m):** Trace a quick "Diagonal Win" case.

---

## ✅ Self-Evaluation Checklist
- [ ] **State Invariants:** Does the game stop accepting moves immediately after a win?
- [ ] **Encapsulation:** Is the `board` array private? Can it be modified without going through `makeMove()`?
- [ ] **O(1) vs O(N) Win Check:** For a 3x3 board, $O(N)$ is fine. For a senior role, can you explain how to check for a win in $O(1)$ time?
- [ ] **Edge Cases:** Did you handle `row/col` index out of bounds?

---

## 📂 Practice
Go to the `practice/` folder in your preferred language and start the 35-minute timer.
- **Reference Solution**: Check the `solutions/` folder for a clean, modular object-oriented design.
