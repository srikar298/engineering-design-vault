# ⚡ 09 - Chaos Engineering

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C145 |
| **Category** | Observability |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🟡 Medium |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Chaos Engineering is the practice of conducting controlled experiments on software systems in production to uncover hidden architectural vulnerabilities and build confidence in system resiliency. It is triggered by systematically injecting synthetic failures (such as server terminations, network latency, or database partitions) into a system, verifying that fallback paths, automated failovers, and alerting systems operate correctly.
*   **Scalability Dimension:** Primary: **Production Fault Blast Radius Control & Safety Guardrails**.

---

## ⚖️ 2. Trade-offs & Deep Dive

### Chaos Experimentation Lifecycle
Chaos engineering is not random destruction. It is a scientific, structured process:
```
  [ Define Steady State ] ──► [ Formulate Hypothesis ] ──► [ Inject Failure ]
  (Identify normal metrics)   ("DB failover takes < 5s")  (Terminate DB Primary)
                                                                 │
                                                                 ▼
  [ Automated Abort ] ◄──── (If SLO is threatened) ◄────── [ Monitor Metrics ]
  (Activate Kill Switch)      (e.g., Global Error > 0.5%)   (Verify Hypothesis)
```

### Chaos Failure Injection Vectors
Chaos frameworks (like Chaos Mesh, Gremlin, or LitmusChaos) inject faults at different layers:
| Injected Fault | Target Layer | System Under Test (SUT) | Expected Resilient Behavior |
| :--- | :--- | :--- | :--- |
| **Pod / Host Kill** | Infrastructure | Kubernetes deployments, VM host nodes. | Replica sets spin up new instances; traffic redirects instantly. |
| **Network Latency** | Network | Microservice-to-microservice TCP calls. | Timeouts trigger, circuit breakers open, and fallbacks execute. |
| **Packet Drop / Loss** | Network | RPC communication, DB replication. | Reconnection logic, TCP retries, and data consistency checks. |
| **Disk/Memory Fill** | Resource | Application volume, cache servers. | Log rotation, rate-limiting, and memory eviction policies. |
| **DNS Resolution Fail**| Service Discovery| Internal APIs, DNS resolvers. | Local DNS caching and fallback servers handle routing. |

### Enforcing Guardrails & The Blast Radius
Running chaos experiments in production carries risk. Strict guardrails must be in place:
1. **Limit Blast Radius:** 
   * Run experiments in staging first to catch obvious bugs.
   * In production, target only a tiny fraction of users or requests. Use headers (e.g., `X-Chaos-Test: true`) or canary deployments so that only 1% of traffic routes through nodes undergoing chaos testing.
2. **Automated Kill Switch (Aborting):**
   * The chaos orchestration engine must poll your production metrics continuously.
   * If a global SLO metric (such as global API HTTP 5xx error rate) rises above a safety threshold (e.g., 0.5%), the engine must immediately abort the experiment, restore network packets, or boot up terminated nodes to minimize user impact.

---

## 💥 3. Resiliency & Operations

### Operational Pitfalls & Mitigations
*   **Runaway Chaos Damage (Stateful Loss):**
    *   *Problem:* An experiment deletes a stateful database node or corrupts data volumes, causing permanent loss of production data.
    *   *Mitigation:* Never inject destructive chaos experiments (like raw disk deletions) on primary production databases. Limit database chaos to network latency injections or replica/read-only node terminations.
*   **Chaos during Active Incidents:**
    *   *Problem:* Chaos tools inject synthetic failures while the system is already suffering from an unrelated real production outage, compounding the disaster.
    *   *Mitigation:* Wire the chaos runner to check PagerDuty or the incident management API. If any active high-severity incident is open, lock all chaos runners from executing.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   Assuming chaos engineering is about "causing random chaos in production to see what happens." It is a highly controlled, hypothesis-driven science.
*   Suggesting chaos engineering for an unstable system. If your application already crashes daily under normal load, you don't need chaos testing; you need to fix your bugs and basic architectural flaws first.

### Interview Tip (The "Strong Hire" Signal)
> *"We practice Chaos Engineering to proactively test our microservice boundaries. We form a hypothesis, such as 'injecting a 300ms latency on the recommendation API will trigger our API Gateway circuit breaker and serve a fallback without degrading checkout metrics.' We run these experiments in production targeting only canary instances, guarded by automated abort rules. If our global 5xx error rate crosses 0.1%, our Prometheus-linked kill-switch terminates the experiment instantly."*

---

## 💡 5. My Custom Study Notes & Whiteboard
*Use this section to document your sketches, code blocks, or personal notes.*
