# 🛒 E-Commerce Checkout — Java Implementation Guide

## 📁 Folder Structure

```
JAVA/
├── Main.java                          ← Integration Demo (Run this)
├── facade/                            
│   └── CheckoutFacade.java            ← High-level Orchestrator
├── proxy/                             
│   ├── InventoryService.java
│   ├── DatabaseInventoryService.java  ← Slow Real Subject
│   └── InventoryProxy.java            ← Fast Caching Proxy
├── decorator/                             
│   ├── IPriceComponent.java
│   ├── BaseCart.java
│   ├── PriceDecorator.java
│   ├── TaxDecorator.java              ← Extends PriceDecorator
│   └── VipDiscountDecorator.java      ← Extends PriceDecorator
└── adapter/                           
    ├── IPaymentProcessor.java         ← Pristine Internal Interface
    ├── StripeAdapter.java             ← Object Adapter
    └── StripeLegacyAPI.java           ← Ugly 3rd Party Library
```

---

## ▶️ How to Run

Navigate to the `JAVA/` directory and compile/run the application:

```bash
cd "02-Structural/08-Combined-Patterns/01-ecommerce-checkout/JAVA/"
javac adapter/*.java decorator/*.java proxy/*.java facade/*.java Main.java
java Main
```

**Expected output highlights the Cache Miss vs Cache Hit, dynamic price stacking, and Adapter mapping.**
