# 🔌 Adapter — Java Implementation Guide

## 📁 Folder Structure

```
JAVA/
├── Main.java                          ← Integration Demo (Run this)
├── object_adapter/                    ← Preferred compositional approach
│   ├── IAnalyticsTool.java            ← Target Interface
│   ├── LegacyXmlProcessor.java        ← Adaptee
│   └── XmlToJsonObjectAdapter.java    ← The Adapter
└── class_adapter/                     ← Legacy inheritance approach
    ├── IDatabaseReader.java
    ├── LegacySystem.java
    └── DatabaseClassAdapter.java
```

---

## ▶️ How to Run

Navigate to the `JAVA/` directory and compile/run the application:

```bash
cd "02-Structural/01-Adapter Design Pattern/JAVA/"
javac object_adapter/*.java class_adapter/*.java Main.java
java Main
```

**Expected output:**
```
==================================================
   1. Object Adapter (Modern/Preferred Approach)  
==================================================

[Adapter] Received JSON: {"user": "John", "action": "login"}
[Adapter] Translating JSON into XML...
[Adapter] Delegating to legacy processor...
[LegacyXmlProcessor] Successfully analyzed XML data: <xml><data>"user": "John", "action": "login"</data></xml>

==================================================
   2. Class Adapter (Legacy Approach)             
==================================================

[Class Adapter] Mapping readData() call directly to inherited fetchExistingRecords()
[LegacySystem] Fetching records using ancient APIs...
```

---

## 📖 Full Documentation
See the top-level [`README.md`](../README.md) for conceptual explanations, FAANG interview Q&A, and real-world system design usage.
