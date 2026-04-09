# 📎 Singleton — In the Wild (Case Studies)

This file shows how the Singleton pattern appears **inside larger system designs**, often combined with other patterns.

---

## Case Study 1: Notification System Config
**Location:** [`07-Combined-Patterns/01-notification-system`](../../07-Combined-Patterns/01-notification-system/README.md)

**Role of Singleton here:**
```
NotificationConfig (Bill Pugh Singleton)
  └── Holds: smtpHost, smsApiKey, pushServiceUrl
  └── Consumed by: EmailChannel, SmsChannel, PushChannel
  └── Ensures: Config is read from environment exactly ONCE across all 3 channel types
```

**The key insight for interviews:**
> The Singleton pattern is not just about "one instance." It's about **one expensive initialization shared across the system**. Config reading (disk/network) is expensive — that's exactly what Singleton is designed for.

**Patterns it works with:** Factory Method (factories are also singletons internally), Builder (channels use config during construction).

---

## Case Study 2: Database Connection Pool
*(To be implemented)*

The `HikariDataSource` in Spring is effectively a Singleton — one pool per datasource config, shared across hundreds of concurrent requests. The pool internally uses Factory Method to create individual `Connection` objects.

---

## 📚 See Also
- [Individual Pattern README](../README.md)
- [Full Combined Patterns Index](../../07-Combined-Patterns/README.md)
