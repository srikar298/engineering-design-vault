# 📎 Abstract Factory — In the Wild (Case Studies)

This file shows how the Abstract Factory pattern appears **inside larger system designs**, often combined with other patterns.

---

## Case Study 1: Cross-Platform UI → Singleton Factory
*(Conceptual extension of the existing Abstract Factory demo)*

**The combination:**
```
Singleton        → ONE GUIFactory per platform (Mac or Windows). 
                   Determined at app startup. Never changes.

Abstract Factory → That single factory creates the ENTIRE widget family:
                   MacFactory.createButton()   → MacButton
                   MacFactory.createCheckbox() → MacCheckbox
```

**Why Singleton here?** The platform is determined once at startup. It's wasteful and potentially dangerous to create multiple platform-detection passes. The factory is read-only after creation — a perfect Singleton candidate.

```java
// In production:
GUIFactory factory = PlatformDetector.isMac() 
    ? MacFactory.getInstance()     // Singletons
    : WindowsFactory.getInstance(); // Singletons

// Everything downstream just uses IButton and ICheckbox
IButton btn = factory.createButton();
btn.render();
```

---

## Case Study 2: Cloud Provider Factory
*(To be implemented)*

`AWS`, `GCP`, `Azure` each have their own storage, compute, and messaging services. An Abstract Factory (`CloudProviderFactory`) with methods `createStorage()`, `createCompute()`, `createMessaging()` lets you swap the entire cloud provider by swapping ONE factory reference.

---

## 📚 See Also
- [Individual Pattern README](../README.md)
- [Full Combined Patterns Index](../../07-Combined-Patterns/README.md)
