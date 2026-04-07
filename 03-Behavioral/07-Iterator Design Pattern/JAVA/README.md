# 🔄 Iterator — Java Implementation Guide

## 📁 Folder Structure

```
JAVA/
├── Main.java                          ← Integration Demo (Run this)
├── model/                           
│   └── Product.java                   ← Simple POJO
├── iterator/                          
│   ├── IIterator.java                 ← The cross-platform Iterator
│   └── ProductIterator.java           ← Tracks the array cursor
└── collection/                        
    ├── IAmazonInventory.java          ← Iterable interface (Factory Method)
    └── WarehouseInventory.java        ← Inner Array hidden completely
```

---

## ▶️ How to Run

Navigate to the `JAVA/` directory and compile/run the application:

```bash
cd "03-Behavioral/07-Iterator Design Pattern/JAVA/"
javac model/*.java iterator/*.java collection/*.java Main.java
java Main
```

**Expected output proves that the loop functions without accessing any List indexes directly:**
```
==================================================
   Iterator Pattern: Amazon Inventory Demo        
==================================================

--- Scenario: Iterating over hidden data structure ---
   -> Found: MacBook Pro ($2400.0)
   -> Found: AirPods ($200.0)
   -> Found: Magic Mouse ($80.0)

Total Warehouse Value: $2680.0
```

---

## 📖 Full Documentation
See the top-level [`README.md`](../README.md) for conceptual explanations, FAANG interview Q&A, and real-world system design usage.
