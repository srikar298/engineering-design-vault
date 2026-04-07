# 🎮 RPG Rendering Engine — Java Implementation Guide

## 📁 Folder Structure

```
JAVA/
├── Main.java                          ← Integration Demo (Run this)
├── facade/                            ← The API Boundary
│   └── GraphicRenderFacade.java       
├── context/                           ← The Volatile State
│   └── Particle.java                  ← Millons of these exist
└── flyweight/                         ← The Shared Immutable State
    ├── ParticleMesh.java              ← The heavy VRAM blob
    └── MeshFactory.java               ← The cache governor
```

---

## ▶️ How to Run

Navigate to the `JAVA/` directory and compile/run the application:

```bash
cd "02-Structural/09-LLD-Problems/03-rpg-engine/JAVA/"
javac context/*.java facade/*.java flyweight/*.java Main.java
java Main
```

**Expected output proves Facade API simplicity and Flyweight memory savings:**
```
==================================================
   RPG Engine: Facade + Flyweight Demo            
==================================================

[Facade] Spawning Explosion Event with 5000 particles...
   [GPU Allocation] Loaded 5MB Mesh to VRAM: FIRE_PARTICLE
   [GPU Allocation] Loaded 5MB Mesh to VRAM: SMOKE_PARTICLE

[Facade] --- Rendering Frame (5000 active objects) ---
      -> Rendering Mesh [FIRE_PARTICLE] at coords (100,207) moving at 7m/s
      -> Rendering Mesh [SMOKE_PARTICLE] at coords (102,201) moving at 1m/s
      -> Rendering Mesh [FIRE_PARTICLE] at coords (104,204) moving at 4m/s
      ... (and 4997 more particles drawn)

[Facade] --- Rendering Frame (5000 active objects) ---
      -> Rendering Mesh [FIRE_PARTICLE] at coords (100,214) moving at 7m/s
      -> Rendering Mesh [SMOKE_PARTICLE] at coords (102,202) moving at 1m/s
      -> Rendering Mesh [FIRE_PARTICLE] at coords (104,208) moving at 4m/s
      ... (and 4997 more particles drawn)

[GPU Stats] Active objects: 5000
[GPU Stats] VRAM Used: 10 MB
[GPU Stats] Theoretical RAM w/o Flyweight: 25000 MB

SUCCESS: Game did not crash.
```
