# 🌳 Composite — Java Implementation Guide

## 📁 Folder Structure

```
JAVA/
├── Main.java                          ← Integration Demo (Run this)
└── composite/                             
    ├── FileSystemComponent.java       ← The common Component Interface
    ├── File.java                      ← The Leaf (No children)
    └── Directory.java                 ← The Composite (Holds a list of components)
```

---

## ▶️ How to Run

Navigate to the `JAVA/` directory and compile/run the application:

```bash
cd "02-Structural/05-Composite Design Pattern/JAVA/"
javac composite/*.java Main.java
java Main
```

**Expected output:**
```
==================================================
   Composite Pattern: Tree Transparency           
==================================================

--- Entire File System ---
📁 Root Drive (C:)
   📁 Finance Documents
      📄 budget_2024.xlsx (15000 bytes)
      📄 report.pdf (45000 bytes)
   📁 Personal Stuff
      📄 vacation.jpg (320000 bytes)
   📄 system_config.xml (2048 bytes)

Total Size of Root: 382048 bytes

--- Single Branch (Finance) ---
📁 Finance Documents
   📄 budget_2024.xlsx (15000 bytes)
   📄 report.pdf (45000 bytes)

Total Size of Finance: 60000 bytes

--- Single Leaf (file1) ---
📄 budget_2024.xlsx (15000 bytes)
Size: 15000 bytes
```

---

## 📖 Full Documentation
See the top-level [`README.md`](../README.md) for conceptual explanations, FAANG interview Q&A, and real-world system design usage.
