# 🎯 Level 1 Master Checklist: Foundations

Before you move to **Level 2: Intermediate**, ensure you can implement these 5 foundational skills in under 35 minutes:

### 1. Invariant Protection (Encapsulation)
- [ ] Do you hide your arrays/lists and return unmodifiable views?
- [ ] Do your setters/logic methods prevent objects from entering an "Impossible State"?

### 2. State Management (The State Machine)
- [ ] Can you implement a simple **State Pattern**? (e.g., Vending Machine or Game Status).
- [ ] Does your code prevent actions when the system is in the wrong state?

### 3. Grid & Matrix Logic
- [ ] Are you comfortable managing 2D arrays? (Tic-Tac-Toe, 2048, Minesweeper).
- [ ] Can you perform row/column/diagonal checks efficiently?

### 4. Entity Relationships
- [ ] Can you model **Part-Whole** relationships correctly? (e.g., A Library *has* Books).
- [ ] Do you know when to use **Composition** (lifetime tied) vs **Aggregation** (independent)?

### 5. Dependency Injection (Basic)
- [ ] Do you pass "tools" (like a Dice or a Printer) via the constructor rather than creating them inside the class?

> **Rule of Thumb:** If your Level 1 code has public fields or zero interfaces, stay here and practice. If it's modular and handles validation, move to Level 2.
