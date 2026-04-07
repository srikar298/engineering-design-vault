# 🏠 Smart Home Hub — Java Implementation Guide

## 📁 Folder Structure

```
JAVA/
├── Main.java                          ← Integration Demo (Run this)
├── devices/                           ← The Bridge Abstractions
│   ├── Device.java
│   └── SmartThermostat.java           
├── platforms/                         ← The Bridge Implementations
│   ├── IPlatform.java                 
│   └── GoogleNestAPI.java             
└── adapter/                           ← The Legacy Integrations
    ├── LegacyPlatformAdapter.java     ← The Adapter
    └── OldTcpThermostat.java          ← The Adaptee
```

---

## ▶️ How to Run

Navigate to the `JAVA/` directory and compile/run the application:

```bash
cd "02-Structural/09-LLD-Problems/02-smart-home/JAVA/"
javac adapter/*.java devices/*.java platforms/*.java Main.java
java Main
```

**Expected output proves dynamic Bridge swapping and Adapter translation:**
```
==================================================
   Smart Home Hub: Bridge + Adapter Demo          
==================================================

--- Scenario 1: Modern Device on Modern Platform ---

[Thermostat] Powering on HVAC system...
   [Google Nest API] Sending HTTP POST -> /api/v1/hvac_power?val=ON

[Thermostat] Setting target temperature to 22°C...
   [Google Nest API] Sending HTTP POST -> /api/v1/set_temp?val=22

--- Scenario 2: Swapping the Platform (Bridge Power) ---
Oh no! The Google API is down. Let's swap to the Legacy Thermostat in the basement.

[Thermostat] Setting target temperature to 22°C...
   [Adapter] Translating modern command ('set_temp') to TCP...
   [Legacy TCP] Opening socket connection on port 8080...
   [Legacy TCP] Sending raw bytes: CMD=SET_TEMP;VAL=22
   [Legacy TCP] Closing socket.
```
