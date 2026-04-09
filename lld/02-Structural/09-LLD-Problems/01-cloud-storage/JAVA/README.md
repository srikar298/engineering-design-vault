# ☁️ Cloud Storage — Java Implementation Guide

## 📁 Folder Structure

```
JAVA/
├── Main.java                          ← Integration Demo (Run this)
└── cloud/                             
    ├── User.java                      ← Basic Payload
    ├── IFileSystemNode.java           ← Common Composite Interface
    ├── File.java                      ← The Leaf
    ├── Folder.java                    ← The Composite (Pure Domain Logic)
    └── FolderAuthProxy.java           ← The Gatekeeper Proxy
```

---

## ▶️ How to Run

Navigate to the `JAVA/` directory and compile/run the application:

```bash
cd "02-Structural/09-LLD-Problems/01-cloud-storage/JAVA/"
javac cloud/*.java Main.java
java Main
```

**Expected output proves recursive traversal and security exceptions:**
```
==================================================
   Cloud Storage: Composite + Proxy Demo          
==================================================

--- Scenario 1: Admin creating the file system ---
✅ Tree structure built successfully by Admin.

--- Scenario 2: Traversal Transparency (Composite) ---
📁 [Folder] Root (C:)
   📁 [Folder] Finance_Data
      📄 budget_2024.xlsx (5000 bytes)
      📄 payroll.csv (15000 bytes)
   📄 system_config.xml (1200 bytes)

Total Storage Used: 21200 bytes

--- Scenario 3: Unauthorized Mutation (Proxy) ---
Guest attempting to upload file to Top_Secret folder...
🚨 403 Forbidden: 'Bob_Guest' cannot upload to 'Top_Secret_ProjectX'
```
