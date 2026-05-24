# 🔄 Story Variants

Same experience, different company, different angle. This workbook helps you adapt your 12 core stories to align with the distinct engineering cultures of top-tier companies.

---

## 🏗️ 1. Cultural Focus Guide

### 🟢 Google Focus: Scale, Systems & Googleyness
*   **Emphasis**: Algorithmic complexity (big-O), deep correctness, scale handling, data-driven decisions, intellectual humility, and emergent leadership.
*   **Key Question to Ask Yourself**: *"How did I ensure correctness and fault tolerance at scale?"*

### 🔵 Meta Focus: Speed, Iteration & Boldness
*   **Emphasis**: Shipping fast, rapid iteration, taking bold technical risks, direct user metric impact, and simple designs.
*   **Key Question to Ask Yourself**: *"How quickly did we ship, and what was the direct user impact?"*

### 🟠 Amazon Focus: Leadership Principles (16 LPs)
*   **Emphasis**: Direct mapping to LPs. Customer obsession, bias for action, deep diving into metrics, insisting on the highest standards.
*   **Key Question to Ask Yourself**: *"What was the customer problem, and what specific LP does this demonstrate?"*

### 🔴 Startup Focus: Scrappiness & Multi-Hat Ownership
*   **Emphasis**: Generalist resourcefulness, wearing multiple hats, building from scratch with limited resources, speed over process.
*   **Key Question to Ask Yourself**: *"How did I get this done with no playbook and limited resources?"*

---

## 📊 2. Amazon Leadership Principle (LP) Mapping Matrix

Track your story mapping to the 16 Amazon LPs:

| Leadership Principle | Primary Story ID | Primary LP Angle | Rehearsed LP Strength (1-5) |
| :--- | :--- | :--- | :--- |
| **Customer Obsession** | S01 | Starting with customer pain | |
| **Ownership** | S05 | Solving out-of-scope system bugs | |
| **Invent & Simplify** | S02 | Reducing complex code complexity | |
| **Are Right, A Lot** | S09 | Disagreeing with bad architecture | |
| **Learn & Be Curious** | S06 | Mastering new language in 3 days | |
| **Hire & Develop Best** | S12 | Mentoring junior dev past spillovers | |
| **Highest Standards** | S08 | Setting testing guidelines in crisis | |
| **Think Big** | S01 | Designing system for 10x growth | |
| **Bias for Action** | S05 | Shifting to actions without permissions | |
| **Frugality** | S11 | Tuning JVM memory to reduce servers | |
| **Earn Trust** | S03 | Taking blame for database downtime | |
| **Dive Deep** | S02 | Profiling CPU usage with flame graphs | |
| **Disagree & Commit** | S04 | Committing to team choice after benchmark | |
| **Deliver Results** | S08 | Shipping portal in high-stress sprint | |
| **Earth's Best Employer**| S12 | Creating psychological safety in guild | |
| **Scale & Responsibility**| S09 | Protecting customer privacy in database | |

### 📋 Google Sheets LP Coverage Formula (Cell `E2` or equivalent)
```excel
=TEXT(COUNTIF(Strength_Column, ">=3")/16, "0%") & " of Amazon LPs covered with strong stories"
```
*(Aim for a coverage score of 80% or higher before interviewing at Amazon).*
