# 💣 LLD Problem: Minesweeper

> **Patterns:** None (OOP & Matrix Recursion Focus)

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
Design a console-based Minesweeper game on an $N \times M$ grid with a configurable number of mines.

### 🛠️ Core Requirements
1.  **Board Setup:** An $N \times M$ grid where $K$ random cells contain mines. Other cells display the count of adjacent mines (0 to 8).
2.  **User Actions:**
    *   **Reveal:** Select a cell to reveal its contents.
    *   **Flag:** Place or remove a flag on a cell suspected to contain a mine.
3.  **Recursive Reveal:** If a user reveals a cell with 0 adjacent mines, automatically reveal all adjacent non-mine cells recursively.
4.  **Win/Loss Condition:**
    *   **Loss:** The user reveals a cell containing a mine.
    *   **Win:** All non-mine cells are successfully revealed.

### ⚙️ Out of Scope / Constraints
- GUI or complex animations.
- Time tracking or leaderboard storage.

---

## ✅ Self-Evaluation Checklist
- [ ] **Recursive Reveal Algorithm:** Did you implement a BFS/DFS or simple recursion to handle revealing empty cells?
- [ ] **State Invariants:** Can flagged cells be accidentally revealed? (Requirement: flagged cells should be protected from accidental reveals until unflagged).
- [ ] **First Move Safety:** (Optional but highly recommended) Does the board generate mines *after* the user's first move, ensuring they never hit a mine on their first click?
- [ ] **Clean Separation:** Is the `Cell` state separate from the `Board` orchestration?

---

## 📂 Practice
Go to the `practice/` folder in your preferred language and start the 35-minute timer.
- **Reference Solution**: Check the `solutions/` folder for a clean, modular object-oriented design.
