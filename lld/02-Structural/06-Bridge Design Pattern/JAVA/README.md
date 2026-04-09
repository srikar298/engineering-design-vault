# 🌉 Bridge — Java Implementation Guide

## 📁 Folder Structure

```
JAVA/
├── Main.java                          ← Integration Demo (Run this)
├── abstraction/                       ← Client-facing high-level API
│   ├── NavigationSystem.java          ← Abstract base (Holds the Bridge)
│   ├── UberEats.java
│   └── UberRide.java
└── implementation/                    ← Low-level primitive operations
    ├── NavigationImpl.java            ← Implementation interface
    ├── AppleMaps.java
    └── GoogleMaps.java
```

---

## ▶️ How to Run

Navigate to the `JAVA/` directory and compile/run the application:

```bash
cd "02-Structural/06-Bridge Design Pattern/JAVA/"
javac abstraction/*.java implementation/*.java Main.java
java Main
```

**Expected output:**
```
==================================================
   Bridge Pattern: Decoupling Hierarchies         
==================================================

--- Scenario 1 ---
[Uber Ride] Dispatching Driver 'Keerti' -> Calculating optimal route to [Central Park] using Google Maps API... 📡
[Uber Eats] Delivery picked up from 'Pizza Palace' -> Routing to [123 HSR Layout] prioritizing privacy via Apple Maps API... 🍏

--- Scenario 2: Dynamic Swapping at Runtime ---
Switching mapping engines dynamically...

[Uber Ride] Dispatching Driver 'Keerti' -> Routing to [JFK Airport] prioritizing privacy via Apple Maps API... 🍏
```

---

## 📖 Full Documentation
See the top-level [`README.md`](../README.md) for conceptual explanations, FAANG interview Q&A, and real-world system design usage.
