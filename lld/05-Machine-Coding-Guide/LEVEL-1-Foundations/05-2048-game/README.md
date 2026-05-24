# 🧩 LLD Problem: 2048 Game

> **Patterns:** None (OOP & Coordinate Manipulation Focus)

---

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Difficulty** | 🟡 Medium |
| **SDE-2 Mandatory** | ❌ No |
| **Patterns** | None |
| **Status** | Not Started |
| **Times Practiced** | 0 |
| **Last Practiced** | YYYY-MM-DD |
| **Next Review** | YYYY-MM-DD |

---

## 📋 The Prompt
Design a console-based 2048 game played on a $4 \times 4$ grid where tile values merge as they slide in four directions (Left, Right, Up, Down).

### 🛠️ Core Requirements
1.  **Board Setup:** A $4 \times 4$ grid initialized with two random tiles (each containing a 2 or a 4).
2.  **Move Directions:** Support moving tiles left, right, up, and down.
3.  **Merge Logic:** Adjacent tiles with the same value merge into a single tile with double the value.
    *   *Rules:* A tile can only merge once per move. Merges cascade in the direction of the slide.
4.  **Random Tile Spawn:** After every valid move, spawn a new tile (value 2 or 4) in a random empty cell.
5.  **Win/Loss Condition:**
    *   **Win:** A tile reaches value 2048.
    *   **Loss:** No empty cells remain and no adjacent tiles have the same value (no valid moves possible).

### ⚙️ Out of Scope / Constraints
- Graphical user interface (GUI).
- High-score persistence.

---

## ✅ Self-Evaluation Checklist
- [ ] **Slide and Merge Separation:** Did you split the move operation into sliding (shifting non-zero elements) and merging?
- [ ] **Single Merge Rule:** Did you ensure that a tile doesn't merge multiple times in a single slide (e.g. `[2, 2, 4, 0] -> Left` should become `[4, 4, 0, 0]`, not `[8, 0, 0, 0]`)?
- [ ] **State Invariant:** Does the game verify if a valid move was actually made before spawning a new tile? (If the slide did not change the board, no tile should spawn).
- [ ] **Game Over Check:** Did you implement a check to detect when no moves are possible?

---

## 📂 Practice
Go to the `practice/` folder in your preferred language and start the 35-minute timer.
- **Reference Solution**: Check the `solutions/` folder for a clean, modular object-oriented design.
