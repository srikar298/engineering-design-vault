# ⚡ 05 - SLIs, SLOs, and SLAs

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C141 |
| **Category** | Observability |
| **Difficulty** | 🟢 Easy |
| **Interview Frequency** | 🔥 High |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** SLIs, SLOs, and SLAs represent the mathematical frameworks used to measure, target, and contractually guarantee service reliability. An SLI is a quantifiable metric of service performance (e.g. latency), an SLO is the target objective for that indicator over a time window (e.g. 99% of requests < 200ms over 30 days), and an SLA is the legal/business contract defining penalties if the SLO is violated. They are triggered when designing service metrics and thresholds, creating an "Error Budget" that determines when release deployments must be halted in favor of reliability engineering.
*   **Scalability Dimension:** Primary: **System Availability Target vs. Operational/Development Velocity**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### The Service Reliability Hierarchy
```
    [ SLA ]  ──► (Legal Contract: "We pay you back if the system drops below 99.9%")
       │
    [ SLO ]  ──► (Internal Target: "Our goal is 99.95% availability over 30 days")
       │
    [ SLI ]  ──► (Telemetry Measurement: "Successful Requests / Total Requests = 99.98%")
```

### SLI vs. SLO vs. SLA Comparison
| Dimension | SLI (Indicator) | SLO (Objective) | SLA (Agreement) |
| :--- | :--- | :--- | :--- |
| **Definition** | What is the actual performance? | What *should* the performance be? | What happens if we fail to meet the SLO? |
| **Target Audience**| Reliability & Platform Engineers. | Product Managers & Engineers. | Customers, Sales, and Legal teams. |
| **Formula / Unit** | Ratio: `(Good Events / Total Events) * 100` | Target Ratio over window (e.g., 99.9%). | Legal contract with monetary/credit penalties. |
| **Example** | `Successful HTTP 2xx count / Total HTTP count` | `>= 99.9% of HTTP requests succeed over 30 days` | `If availability < 99.0% in a month, refund 25% cost` |
| **Scope** | Technical. | Technical & Business. | Legal & Commercial. |

### The Mathematics of Availability (The "Nines")
Designing for high availability increases infrastructure complexity and cost exponentially:
| Availability | Allowed Downtime per Year | Allowed Downtime per Month | Allowed Downtime per Week | Target Architecture |
| :--- | :--- | :--- | :--- | :--- |
| **99% (Two Nines)** | 3.65 days | 14.6 hours | 1.68 hours | Single-instance server. Simple setup. |
| **99.9% (Three Nines)**| 8.77 hours | 43.8 minutes | 10.1 minutes | Multi-AZ deployment, automated failover. |
| **99.99% (Four Nines)**| 52.6 minutes | 4.38 minutes | 1.01 minutes | Multi-Region, Active-Active, zero-downtime deploys. |
| **99.999% (Five Nines)**| 5.26 minutes | 26.3 seconds | 6.05 seconds | Multi-Region Active-Active with sub-second replication. |

### Error Budgets & Burn Rate Alerting
*   **Error Budget:** The allowable room for failure, calculated as `1 - SLO`. For a 99.9% SLO, the error budget is `0.1%`. If you receive 1,000,000 requests in a month, you are allowed 1,000 failed requests.
*   **Release Gate Integration:** If the error budget is completely consumed (`0% remaining`), code releases are automatically frozen (blocked), and the engineering team shifts 100% of cycles to fixing bugs and reliability issues until the budget recovers.
*   **Burn Rate Alerting:** Alerting when you are consuming your budget too fast:
    *   **Burn Rate = 1:** You will consume 100% of your budget in exactly the window (e.g., 30 days). No immediate action needed.
    *   **Burn Rate = 14.4:** Consuming 100% of budget in 50 hours. Triggers an urgent, page-level alert to on-call engineers.
    *   **Burn Rate = 36:** Consuming 100% of budget in 20 hours. Triggers high-priority pages immediately.

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **Unachievable 100% Availability Goals:**
    *   *Problem:* Product owners asking for "100% availability". In computer networks, 100% is impossible; aiming for it freezes releases permanently, preventing the product from evolving.
    *   *Mitigation:* Educate business teams on the exponential cost of "nines." Negotiate SLOs based on actual customer satisfaction limits (e.g., users rarely notice 99.9% vs 99.99% on standard web apps).
*   **The "Flapping" Alert Storm:**
    *   *Problem:* Simple threshold alerting (e.g., "Alert if error rate > 0.1% in a 5-minute window") creates frequent false alerts due to transient network blips.
    *   *Mitigation:* Transition to **Multi-Window Multi-Burn-Rate Alerts**, requiring both short-window (e.g., 5 mins) high burn rate AND long-window (e.g., 1 hour) sustained burn rate before paging.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Saying "SLA" when they mean "SLO" (e.g., "The developers agreed on an SLA of 200ms latency"). Developers write SLOs; lawyers write SLAs.
*   Suggesting you should alert on CPU utilization rather than Error Budget burn rate. CPU spikes are a cause; error budget depletion is the actual user-facing symptom.

### Interview Tip (The "Strong Hire" Signal)
> *"We manage service reliability using Error Budgets tied to our 99.9% availability SLO over a rolling 30-day window. We monitor this via Multi-Window Multi-Burn-Rate alerting in Prometheus, paging our engineers only when the burn rate indicates we will consume more than 2% of our total budget within 1 hour. If our error budget is exhausted, our CI/CD pipeline automatically blocks non-security code deployments, forcing the team to focus on stability until the budget is restored."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
