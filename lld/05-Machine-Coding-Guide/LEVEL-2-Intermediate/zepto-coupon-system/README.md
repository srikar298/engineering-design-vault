# 🏷️ Zepto Coupon System

> **Interview Time:** 35 Minutes | **Level:** SDE-2 (OCP Focus)

## 📋 The Prompt
Design a flexible coupon and discount system for a quick-commerce platform like Zepto.

### 🛠️ Core Requirements
1.  **Discount Types:** Support "Flat Off" (e.g., $50 off) and "Percentage Off" (e.g., 20% off).
2.  **Constraints:** Support constraints like "Minimum Order Value" and "Max Discount Cap".
3.  **Applicability:** Coupons can be restricted to specific categories (e.g., only on Fruits) or specific users.
4.  **Combinations:** Can a user apply multiple coupons? (Design for exclusivity or stacking).

### ⚙️ Constraints & Invariants
-   The system must be **Open/Closed**: Adding a new "Buy 1 Get 1" rule should not require changing existing logic.
-   Calculate the final price after applying all valid rules.

---

## ✅ Self-Evaluation Checklist
- [ ] **Strategy Pattern:** Did you encapsulate the calculation logic into strategies?
- [ ] **Decorator Pattern:** Did you use decorators to "Stack" multiple discounts? (Senior move).
- [ ] **Factory Pattern:** How do you create the right Coupon object from a database string?
- [ ] **Validation:** Did you separate the "Validity Check" from the "Price Calculation"?

---

## 📂 Practice
Go to the `practice/` folder and implement the `CouponEvaluator` and `DiscountStrategy` interfaces.
