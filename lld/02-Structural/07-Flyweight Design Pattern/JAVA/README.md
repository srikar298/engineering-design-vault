# 🪶 Flyweight — Java Implementation Guide

## 📁 Folder Structure

```
JAVA/
├── Main.java                          ← Integration Demo (Run this)
├── context/                           ← The Extrinsic (Volatile) state
│   ├── Forest.java                    ← Client managing millions of trees
│   └── Tree.java                      ← Tiny wrapper holding x, y, and a reference
└── flyweight/                         ← The Intrinsic (Shared) state
    ├── TreeFactory.java               ← The cache manager
    └── TreeType.java                  ← The heavy, immutable shared object
```

---

## ▶️ How to Run

Navigate to the `JAVA/` directory and compile/run the application:

```bash
cd "02-Structural/07-Flyweight Design Pattern/JAVA/"
javac flyweight/*.java context/*.java Main.java
java Main
```

**Expected output:**
```
==================================================
   Flyweight Pattern: Massive RAM Optimization    
==================================================

--- Planting 1000000 heavy trees ---
[TreeType] Heavy object loaded into memory: Summer Oak (summer_oak_1mb_texture.png)
[TreeType] Heavy object loaded into memory: Autumn Oak (autumn_oak_1mb_texture.png)
[TreeType] Heavy object loaded into memory: Winter Pine (winter_pine_1mb_texture.png)

[Demo] Rendering the first 5 trees:
   └─ Rendering [Autumn Oak] tree at coordinates (10,20) with color Orange
   └─ Rendering [Summer Oak] tree at coordinates (15,25) with color Green

==================================================
                 MEMORY REPORT                    
==================================================
Total Trees Planted (Contexts) : 1000002
Unique Tree Types (Flyweights) : 3
Memory consumed without Flyweight: ~1 Terabyte
Memory consumed WITH Flyweight   : ~30 Megabytes
==================================================
```

---

## 📖 Full Documentation
See the top-level [`README.md`](../README.md) for conceptual explanations, FAANG interview Q&A, and real-world system design usage.
