# 🚗 LLD Problem: Parking Lot (Basic)

> **Patterns:** Singleton (For ParkingLot) · Factory (For Vehicles) · Strategy (For Spot Allocation)

---

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Difficulty** | 🟢 Easy |
| **SDE-2 Mandatory** | ✅ Yes |
| **Patterns** | Singleton, Factory, Strategy |
| **Status** | Not Started |
| **Times Practiced** | 0 |
| **Last Practiced** | YYYY-MM-DD |
| **Next Review** | YYYY-MM-DD |

---

## 📋 The Prompt
Design a basic parking lot system that can assign spots to vehicles as they enter and free them as they leave.

### 🛠️ Core Requirements
1.  **Vehicle Types:** Support multiple vehicle types (e.g., Car, Motorcycle, Truck).
2.  **Spot Assignment:** Automatically find and assign the first available spot that fits the vehicle type.
3.  **Entry/Exit:** Handle a vehicle entering (getting a ticket) and exiting (freeing the spot).
4.  **Capacity Check:** Reject vehicles if the lot is full for their specific type.

### ⚙️ Constraints & Invariants
-   A spot has a fixed size (Small, Medium, Large).
-   A Motorcycle can fit in any spot. A Car needs Medium or Large. A Truck needs Large.
-   The system should maintain a real-time count of available spots.

---

## ✅ Self-Evaluation Checklist
- [ ] **Enums:** Did you use an `Enum` for Vehicle and Spot types?
- [ ] **Inheritance vs. Composition:** Did you use a base `Vehicle` class or interface?
- [ ] **Spot Ownership:** Does the `Spot` know which `Vehicle` is currently parked in it?
- [ ] **Clean Separation:** Is the logic for "Finding a Spot" inside the `ParkingLot` class or a separate `Strategy`? (SDE-1: inside is fine; SDE-2: separate).

---

## 📂 Practice
Go to the `practice/` folder in your preferred language and start the 30-minute timer.
- **Reference Solution**: Check the `solutions/` folder for a clean, modular object-oriented design.
