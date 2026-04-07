# 🔌 Case Study: Adapter in the Real World

## Where is it used in our repository?

The Adapter pattern is a cornerstone of enterprise integration. We apply it heavily in two of our massive capstone projects:

### 1. 🛒 E-Commerce Checkout Engine (`08-Combined-Patterns`)
When writing our E-Commerce Checkout engine, we define a pristine internal interface: `IPaymentProcessor`. 
However, the company decides to accept payments via **Stripe** and **PayPal**. These external vendors provide their own Java SDKs which have completely different, incompatible methods (e.g., `Stripe.chargeCard()` vs `PayPal.executeTransaction()`).
We use the **Adapter Pattern** to wrap these third-party libraries, forcing them to conform to our internal `IPaymentProcessor`. This ensures our core business logic never gets polluted by third-party vendor code.

### 2. 🏠 Smart Home Hub (`09-LLD-Problems/02-smart-home`)
We are building a clean, modern Bridge architecture for connecting Smart Devices to various Vendor APIs. Suddenly, the product manager demands support for a 15-year-old legacy Thermostat that communicates over raw TCP sockets.
Instead of ruining our pristine architecture, we build a **LegacyDeviceAdapter** that implements our modern `Device` interface but translates the calls down into raw TCP socket commands.

## Key Senior Takeaway
**Adapters protect your core domain.** Never let 3rd-party DTOs, HTTP clients, or legacy classes leak into your business logic. Build a boundary interface, and write Adapters that connect the dirty outside world to your pristine boundary. 
