# 📎 Factory Method — In the Wild (Case Studies)

This file shows how the Factory Method pattern appears **inside larger system designs**, often combined with other patterns.

---

## Case Study 1: Notification System — Channel Factory
**Location:** [`07-Combined-Patterns/01-notification-system`](../../07-Combined-Patterns/01-notification-system/README.md)

**Role of Factory Method here:**
```
IChannelFactory (Creator Interface)
  ├── EmailChannelFactory  → creates EmailChannel
  ├── SmsChannelFactory    → creates SmsChannel
  └── PushChannelFactory   → creates PushChannel

ChannelRegistry dispatches via EnumMap<ChannelType, IChannelFactory>
```

**The OCP win:**
```java
// Adding WhatsApp — touch ZERO existing classes:
// 1. Create WhatsAppChannel.java   (product)
// 2. Create WhatsAppChannelFactory.java   (creator)
// 3. Register in ChannelRegistry static block
```

**Patterns it works with:** Singleton (config consumed during channel init), Builder (message constructed before passing to channel).

---

## Case Study 2: Spring `FactoryBean<T>`
*(Conceptual — no code needed)*

Spring's `FactoryBean<T>` is textbook Factory Method. You implement `getObject()` and Spring calls it to create your custom bean. The Spring container is the *client* — your `FactoryBean` is the *concrete creator*.

---

## 📚 See Also
- [Individual Pattern README](../README.md)
- [Full Combined Patterns Index](../../07-Combined-Patterns/README.md)
