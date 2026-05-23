# HLD Mastery OS: Markdown Templates for Content Modification

Use these templates to update existing HLD concepts, components, and case studies to ensure they integrate seamlessly with your Google Sheet tracker.

---

## 1. Concept/Component README Template
Add this section to the bottom of every file in `hld/01` to `hld/20`.

```markdown
---

## 🧠 Tracker Integration ([Concept ID, e.g., C081])

*   **Core Trade-off:** [The primary decision to defend, e.g., Latency vs Throughput]
*   **The "Senior Signal":** [Advanced concept/internals to mention, e.g., Consistent Hashing Virtual Nodes]
*   **Interview Trap:** [Common candidate mistake in this area]

### ⚔️ Trade-Off Matrix Hook
| Decision | Option A | Option B | Choose A When | Choose B When |
| :--- | :--- | :--- | :--- | :--- |
| [Title] | [Name] | [Name] | [Reason] | [Name] |

### 🔬 Self-Assessment Prompts
1. [Question to test depth]
2. [Question to test scale awareness]
3. [Question to test failure scenario knowledge]
```

---

## 2. Case Study README Template
Add this section to the bottom of every case study in `hld/21` and similar folders.

```markdown
---

## 🔬 Tracker Diagnostics ([Problem ID, e.g., SD008])

*   **Primary Technologies:** [List key components, e.g., Kafka, Redis, Cassandra]
*   **The "Freeze Trap":** [Where candidates usually get stuck or over-engineer]
*   **Architecture Checklist:**
    *   [ ] [Core Component 1]
    *   [ ] [Core Component 2]
    *   [ ] [Distributed Pattern Used]
*   **Trade-off Audit:**
    *   **[Component]:** [Choice A] over [Choice B] ([Short Reason])
```

---

## 3. HLD Delivery Framework (Updated Phase)

Ensure your `hLD_DELIVERY_FRAMEWORK.md` includes the **HLD Anti-Freeze Protocol** and **Explain Aloud Prompts** (already updated in this repository).

### Explain Aloud Cheat Sheet (General)
*   **Capacity:** "To keep us moving, I'll use a placeholder of X peak QPS for the math."
*   **DB:** "I'm choosing [DB] because its storage engine is optimized for [Read/Write] patterns at this scale."
*   **Availability:** "Since availability is a priority over strict consistency for this feature, I'll use [Pattern]."
*   **Scaling:** "We'll implement [Consistent Hashing/Virtual Nodes] here to ensure uniform distribution as we scale out."
