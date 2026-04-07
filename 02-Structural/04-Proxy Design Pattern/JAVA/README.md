# 🛡️ Proxy — Java Implementation Guide

## 📁 Folder Structure

```
JAVA/
├── Main.java                          ← Integration Demo (Run this)
└── proxy/                             
    ├── Image.java                     ← The common Subject Interface
    ├── RealImage.java                 ← The heavy, expensive Real Object
    └── ImageProxy.java                ← The Virtual Proxy wrapper
```

---

## ▶️ How to Run

Navigate to the `JAVA/` directory and compile/run the application:

```bash
cd "02-Structural/04-Proxy Design Pattern/JAVA/"
javac proxy/*.java Main.java
java Main
```

**Expected output:**
```
==================================================
   Proxy Pattern: Virtual Surrogate (Lazy Load)   
==================================================

--- 1. Application Startup (Creating Gallery) ---
[ImageProxy] Surrogate created for: photo_hawaii_4k.png. Heavy object is NOT loaded yet.
[ImageProxy] Surrogate created for: photo_paris_4k.png. Heavy object is NOT loaded yet.
[ImageProxy] Surrogate created for: photo_tokyo_4k.png. Heavy object is NOT loaded yet.

Application UI loaded successfully. (Zero delay!)

--- 2. User scrolls to the first image ---
[ImageProxy] Intercepted display request. Loading real object now...
[RealImage] ⏳ Loading heavy image from disk: photo_hawaii_4k.png ... (takes 5 seconds)
[RealImage] ✅ Finished loading: photo_hawaii_4k.png
[RealImage] 🖼️  Displaying image: photo_hawaii_4k.png

--- 3. User views the first image again ---
[RealImage] 🖼️  Displaying image: photo_hawaii_4k.png

--- 4. User scrolls to the third image ---
[ImageProxy] Intercepted display request. Loading real object now...
[RealImage] ⏳ Loading heavy image from disk: photo_tokyo_4k.png ... (takes 5 seconds)
[RealImage] ✅ Finished loading: photo_tokyo_4k.png
[RealImage] 🖼️  Displaying image: photo_tokyo_4k.png

Notice: The second image (Paris) was NEVER loaded into memory because the user never viewed it. Saved memory and CPU!
```

---

## 📖 Full Documentation
See the top-level [`README.md`](../README.md) for conceptual explanations, FAANG interview Q&A, and real-world system design usage.
