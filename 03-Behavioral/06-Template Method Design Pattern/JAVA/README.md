# 📝 Template Method — Java Implementation Guide

## 📁 Folder Structure

```
JAVA/
├── Main.java                          ← Integration Demo (Run this)
└── template/                           
    ├── DataMiner.java                 ← Abstract Class (Contains the Template Method)
    ├── PdfMiner.java                  ← Concrete Implementation (rides the Hook)
    └── CsvMiner.java                  ← Concrete Implementation
```

---

## ▶️ How to Run

Navigate to the `JAVA/` directory and compile/run the application:

```bash
cd "03-Behavioral/06-Template Method Design Pattern/JAVA/"
javac template/*.java Main.java
java Main
```

**Expected output proves strict execution order (IoC) with varying internal behaviors:**
```
==================================================
   Template Method: ETL Data Pipeline Demo        
==================================================

--- Scenario 1: Processing a PDF Report ---

[DataMiner] Starting pipeline for annual_report_2024.pdf
   -> Standard Action: Opening file connection...
   -> PDF Custom Action: Executing complex OCR byte extraction.
   -> PDF Custom Action: Parsing extracted bytes into plain text string.
   -> PDF Hook Override: Archiving text and applying NLP sentiment analysis.
   -> Standard Action: Closing file connection.
[DataMiner] Pipeline finished.

--- Scenario 2: Processing a CSV Database Dump ---

[DataMiner] Starting pipeline for users_export.csv
   -> Standard Action: Opening file connection...
   -> CSV Custom Action: Reading comma-separated lines.
   -> CSV Custom Action: Mapping data into relational Array format.
   -> Default Hook Action: Storing parsed data into standard Database.
   -> Standard Action: Closing file connection.
[DataMiner] Pipeline finished.
```

---

## 📖 Full Documentation
See the top-level [`README.md`](../README.md) for conceptual explanations, FAANG interview Q&A, and real-world system design usage.
