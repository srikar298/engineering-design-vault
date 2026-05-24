# 📖 LLD Problem: Library Management System

> **Patterns:** Strategy (For searching/billing), Observer (For notifications)

---

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Difficulty** | 🟢 Easy |
| **SDE-2 Mandatory** | ❌ No |
| **Patterns** | Strategy, Observer |
| **Status** | Not Started |
| **Times Practiced** | 0 |
| **Last Practiced** | YYYY-MM-DD |
| **Next Review** | YYYY-MM-DD |

---

## 📋 The Prompt
Design a Library Management System that allows members to search for, borrow, and return books, and allows librarians to manage books.

### 🛠️ Core Requirements
1.  **Book Search:** Members can search for books by title, author, subject, or publication date.
2.  **Borrowing & Returns:** Members can borrow up to a max limit of books for a configurable duration.
3.  **Fine Calculation:** Calculate fine for overdue books when they are returned.
4.  **Reservation:** Allow members to reserve books that are currently checked out.
5.  **User Roles:** Support at least two roles: Member (can borrow/search) and Librarian (can add/modify book items).

### ⚙️ Out of Scope / Constraints
- Database persistence.
- Payment gateway integration (mocking is fine).

---

## ✅ Self-Evaluation Checklist
- [ ] **Search Decoupling:** Is the search logic separate from the main catalog class using a Strategy pattern?
- [ ] **Book vs. BookItem:** Did you distinguish between a `Book` (metadata: title, author) and `BookItem` (physical copy with unique barcode)?
- [ ] **Fine Logic Separation:** Is the fine calculation encapsulated in a separate service?
- [ ] **Concurrency:** Are multiple members borrowing the same physical book copy prevented from doing so simultaneously?

---

## 📂 Practice
Go to the `practice/` folder in your preferred language and start the 35-minute timer.
- **Reference Solution**: Check the `solutions/` folder for a clean, modular object-oriented design.
