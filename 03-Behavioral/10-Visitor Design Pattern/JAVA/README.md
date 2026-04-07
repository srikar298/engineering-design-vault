# 🕵️ Visitor — Java Implementation Guide

## 📁 Folder Structure

```
JAVA/
├── Main.java                          ← Integration Demo (Run this)
├── element/                           
│   ├── IPropertyElement.java          ← Contains 'accept(IVisitor)'
│   ├── Residential.java               ← Concrete Element
│   ├── Commercial.java                ← Concrete Element
│   └── Factory.java                   ← Concrete Element
└── visitor/                          
    ├── IVisitor.java                  ← Method overloads for Double Dispatch
    └── InsuranceAgentVisitor.java     ← Concrete Algorithm
```

---

## ▶️ How to Run

Navigate to the `JAVA/` directory and compile/run the application:

```bash
cd "03-Behavioral/10-Visitor Design Pattern/JAVA/"
javac element/*.java visitor/*.java Main.java
java Main
```

**Expected output proves Double Dispatch algorithm scaling:**
```
==================================================
   Visitor Pattern: Insurance Agent Demo          
==================================================

--- Scenario 1: Agent calculates insurance across all properties ---
   [Insurance Agent] Visiting Residential property.
      -> Medical risk is high. Premium calculated: $3000
   [Insurance Agent] Visiting Commercial property.
      -> Theft risk is high. Premium calculated: $25000
   [Insurance Agent] Visiting Factory property.
      -> Fire/Hazard risk is high. Premium calculated: $20000
```

---

## 📖 Full Documentation
See the top-level [`README.md`](../README.md) for conceptual explanations, FAANG interview Q&A, and real-world system design usage.
