# S01 — Biggest Achievement

* **Primary Question**: *"Tell me about your greatest professional accomplishment."*
* **Core Signal**: High ownership, major business impact, technical depth.

---

## 📝 Story Builder (STAR+)

### [S] Situation (Context)
*   **Prompt**: Define the company, project, size of codebase/team, and target scale (e.g., QPS, database size).
*   **Draft**: 
    *   

### [T] Task (Your Responsibility)
*   **Prompt**: What was *your* exact role and assignment? (e.g., Lead developer tasked with reducing transaction failures).
*   **Draft**: 
    *   

### [A] Action (Your Steps)
*   **Prompt**: Detail 3-4 specific technical steps YOU took. Explain the *Why* behind your choices.
*   **Draft**:
    1.  
    2.  
    3.  

### [R] Result (The Metrics)
*   **Prompt**: Quantifiable outcomes. Include at least two metrics (e.g., Latency: 500ms -> 80ms, transaction success rate: 97% -> 99.98%).
*   **Draft**: 
    *   

### [+] Reflection (Lessons Learned)
*   **Prompt**: What did this win prove to you about system architecture or operational safety? What would you do differently today?
*   **Draft**: 
    *   

---

## 🗣️ Spoken Draft Script (Max 2 Minutes)
*(Write out the complete narrative exactly as you would speak it aloud).*

> *"[Placeholder Baseline Script]*
> *At [Company], our payment service was silently dropping 3% of incoming webhook transactions, costing the business roughly ₹40L/month in manual reconciliations. As the lead engineer on the gateway team, I was tasked with redesigning the ingestion pipeline to ensure exactly-once processing.*
> 
> *First, I analyzed the ingestion logs and identified a race condition in our DB write lock. I proposed replacing the pessimistic locking mechanism with an idempotency layer using Redis for distributed locking with token hashing.*
> *Second, I led the implementation of a DLQ (Dead Letter Queue) strategy with exponential backoff on our event consumers.*
> *Finally, I introduced open telemetry tracing across the boundary services to trace failed requests in real-time.*
> 
> *As a result of this architecture shift, silent failures dropped to 0.02% in 6 weeks, and manual engineering triage hours were reduced from 15 hours/week to zero. This taught me that observability is not a nice-to-have; it's a core operational requirement."*

---

## 🕵️ Rehearsal Log

| Date | Duration (sec) | Confidence (1-5) | Filler Count | Peer/Self Feedback |
| :--- | :--- | :--- | :--- | :--- |
| | | | | |
