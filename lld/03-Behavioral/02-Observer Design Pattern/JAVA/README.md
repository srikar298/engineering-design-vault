# 📡 Observer — Java Implementation Guide

## 📁 Folder Structure

```
JAVA/
├── Main.java                          ← Integration Demo (Run this)
├── publisher/                           
│   ├── IOrderPublisher.java           ← The Subject Interface
│   └── OnlineStore.java               ← The Concrete Publisher
└── subscriber/                          
    ├── IOrderSubscriber.java          ← The Common Observer Interface
    ├── EmailService.java              ← Subscriber 1
    ├── MobileApp.java                 ← Subscriber 2
    └── LogisticsDepartment.java       ← Subscriber 3
```

---

## ▶️ How to Run

Navigate to the `JAVA/` directory and compile/run the application:

```bash
cd "03-Behavioral/02-Observer Design Pattern/JAVA/"
javac publisher/*.java subscriber/*.java Main.java
java Main
```

**Expected output:**
```
==================================================
   Observer Pattern: E-Commerce Pub/Sub Demo      
==================================================

--- Scenario 1: Payment Cleared ---

[Online Store] Order 'ORD-999X' status changed to: PAYMENT_SUCCESS
   [Email Service] 📧 Sending email to alice@example.com: 'Your order ORD-999X is now PAYMENT_SUCCESS.'
   [Mobile App] 📱 Pushing notification to Device [Device_iPhone_14]: 'Order ORD-999X updated to PAYMENT_SUCCESS.'
   [Logistics] 📦 Noted status change for ORD-999X (PAYMENT_SUCCESS). Preparing warehouse...

--- Scenario 2: User unsubscribes from annoying Push notifications ---

--- Scenario 3: Order Shipped ---

[Online Store] Order 'ORD-999X' status changed to: SHIPPED
   [Email Service] 📧 Sending email to alice@example.com: 'Your order ORD-999X is now SHIPPED.'
   [Logistics] 🚚 Order ORD-999X has shipped! Allocating tracking number...
```
*(Notice how the Mobile App did not receive the SHIPPED notification because it unsubscribed!)*

---

## 📖 Full Documentation
See the top-level [`README.md`](../README.md) for conceptual explanations, FAANG interview Q&A, and real-world system design usage.
