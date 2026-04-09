# 📎 Builder — In the Wild (Case Studies)

This file shows how the Builder pattern appears **inside larger system designs**, often combined with other patterns.

---

## Case Study 1: Notification System — Message Construction
**Location:** [`07-Combined-Patterns/01-notification-system`](../../07-Combined-Patterns/01-notification-system/README.md)

**Role of Builder here:**
```
NotificationMessage.Builder
  Required:  recipient, body
  Optional:  subject, templateId, priority, correlationId

Usage:
  new NotificationMessage.Builder("user@email.com", "Your order is ready")
      .subject("Order Ready")
      .priority(Priority.HIGH)
      .build();
```

**Why not a constructor?**
With 6 fields, using a constructor produces either:
- One 6-arg constructor where callers must pass `null` for unused params
- Or 15 overloaded constructors covering all combinations

Builder eliminates both problems with a clean, readable, self-documenting API.

**Patterns it works with:** Factory Method (factory creates the channel that RECEIVES the message), Singleton (config may influence default field values).

---

## Case Study 2: HTTP Request Builder
*(To be implemented)*

`OkHttpClient`, `HttpRequest.newBuilder()` in Java 11+, and Spring's `WebClient.RequestBodySpec` are all Builder pattern applications — they have required fields (URL/method) and many optional fields (headers, body, timeout).

---

## 📚 See Also
- [Individual Pattern README](../README.md)
- [Full Combined Patterns Index](../../07-Combined-Patterns/README.md)
