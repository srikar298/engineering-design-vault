# ⚡ 07 - Alerting & On-Call Excellence

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C143 |
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
*   **Two-Sentence Trigger:** Alerting is the automated operational mechanism that monitors telemetry data streams and dispatches notifications to on-call engineers when system performance violates acceptable reliability targets. It is triggered when high-level metric anomalies (such as elevated error rates or tail latencies) are detected, routing incident data to paging platforms like PagerDuty or Opsgenie to initiate active remediation.
*   **Scalability Dimension:** Primary: **Signal-to-Noise Ratio (Alert Fatigue Mitigation)** under cluster expansions.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Incident Routing & Escalation Flow
A healthy alerting loop filters out noise and escalates critical issues to the correct engineer:
```
  [ Prometheus / Datadog ] ──► (Symptom-based Alert) ──► [ Alert Manager ] (Deduplicates & groups)
                                                               │
                                                       (PagerDuty Webhook)
                                                               ▼
                                                      [ Primary On-Call ] (Acknowledge within 15 min)
                                                               │
                                                       (No ACK / Timeout)
                                                               ▼
                                                     [ Secondary Escalate ]
```

### Symptom-Based vs. Cause-Based Alerting
*   **Cause-Based Alerting (The Trap):**
    *   *Trigger:* Alerting on internal system causes, e.g., `CPU usage > 85%` or `Container restarted`.
    *   *Result:* Leads to massive **Alert Fatigue**. Often, a CPU spike is just a healthy batch job, and a restarted container is handled gracefully by Kubernetes. On-call engineers receive hundreds of useless pages, eventually silencing or ignoring them.
*   **Symptom-Based Alerting (The Gold Standard):**
    *   *Trigger:* Alerting on direct user impact, e.g., `HTTP 5xx rate > 1%` or `p99 checkout latency > 1.5 seconds`.
    *   *Result:* Low-frequency, high-value alerts. If a symptom is detected, the user is experiencing degradation. Engineers are only paged when the business SLO is threatened.

### Alert Classification Matrix
| Severity | Type | Trigger Example | Target Routing | Impact |
| :--- | :--- | :--- | :--- | :--- |
| **Critical (Page)** | Symptom | Global HTTP 5xx error rate > 2% for 3 mins. | PagerDuty SMS / Phone Call. | Wakes up primary on-call immediately. |
| **Warning (Ticket)** | Cause | Host Disk space usage > 80%. | Jira ticket created in backlog. | Requires resolution within 24-48 hours. |
| **Informational** | Audit | A manual configuration change was applied. | Slack channel notification / Audit log. | No immediate action required. |

### Heartbeats & "Dead Man's Snitch"
What happens if your entire alerting pipeline crashes (e.g., Prometheus goes out of memory or internet gateway fails)? The system goes down silently, and nobody gets paged.
*   **Solution:** Implement **Dead Man's Snitch (Heartbeat alerts)**. 
*   An external, independent monitoring service (like Healthchecks.io) waits for a periodic "ping" (HTTP GET request) from your internal Cron job every 1 minute.
*   If the external service does not receive the ping within its window, it assumes your infrastructure is dead and fires a critical alert directly to PagerDuty.

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **Alert Storming during Cascading Outages:**
    *   *Problem:* When a core network switch fails, hundreds of microservices simultaneously fire "API Gateway Connection Timeout" alerts, resulting in hundreds of pages hitting the on-call engineer at 3:00 AM.
    *   *Mitigation:* Use **Alert Grouping and Inhibitions**. In Prometheus Alertmanager, configure rules to suppress (inhibit) downstream microservice alerts if a high-level API Gateway outage alert is already active.
*   **Flapping Alerts:**
    *   *Problem:* A metric hovers exactly around the threshold value (e.g., alternating between 80.1% and 79.9%), triggering and clearing the alert repeatedly.
    *   *Mitigation:* Enforce **hysteresis** in alert evaluation: require the metric to stay above the threshold for 5 consecutive minutes to fire, and fall well below the threshold (e.g., < 75%) to clear.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Advocating for alerts on CPU utilization, thread pool size, or memory leaks as high-severity pages. These are diagnostic clues (causes), not user impact (symptoms).
*   Routing alerts to a shared Slack channel or email group without individual ownership. "If everyone is on-call, no one is on-call."

### Interview Tip (The "Strong Hire" Signal)
> *"We completely eliminate alert fatigue by enforcing symptom-based alerting tied directly to our user-facing SLOs (the RED method). We never page engineers on CPU spikes or memory fluctuations. Instead, we page on error rates and latency percentiles. To protect our teams from alert floods during network partitions, we use Prometheus Alertmanager's inhibition rules to silence downstream microservice alerts if our edge API Gateway is already reporting an outage."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
