# S11 — Data-Driven Impact

* **Primary Question**: *"Tell me about a time you used data to make an engineering decision or optimize a system."*
* **Core Signal**: Analytical reasoning, metrics instrumentation, database indexing profiling, A/B testing.

---

## 📝 Story Builder (STAR+)

### [S] Situation (Context)
*   **Prompt**: Describe a scenario where a system was underperforming, but the cause was unclear until you set up telemetry, dashboards, or queried profiling logs.
*   **Draft**: 
    *   

### [T] Task (Your Responsibility)
*   **Prompt**: What was your responsibility?
*   **Draft**: 
    *   

### [A] Action (Your Steps)
*   **Prompt**: What telemetry did you instrument (e.g., Prometheus metrics, Grafana charts)? How did you analyze the metrics? What solution did you implement based on that analysis?
*   **Draft**:
    1.  
    2.  
    3.  

### [R] Result (The Metrics)
*   **Prompt**: Quantifiable gains (e.g., latency reduction %, resource savings in dollars).
*   **Draft**: 
    *   

### [+] Reflection (Lessons Learned)
*   **Prompt**: What did this teach you about the value of pre-production profiling and observability frameworks?
*   **Draft**: 
    *   

---

## 🗣️ Spoken Draft Script (Max 2 Minutes)
*(Write out the complete narrative exactly as you would speak it)*

> *"[Placeholder Baseline Script]*
> *At [Company], our checkout database was experiencing periodic lock saturation, but our query logs didn't show any single query exceeding the timeout threshold. I was assigned to investigate the root cause using system data.*
> 
> *First, I instrumented detailed database lock metrics using Prometheus and set up a Grafana dashboard to map transaction timelines against CPU consumption.*
> *Second, I analyzed the dashboards and identified that a background inventory reconciliation job was running concurrent updates on the same database table partitions as the checkout queries, causing lock contention.*
> *Finally, I refactored the background job to execute updates in batches during off-peak hours and partitioned the checkout tables.*
> 
> *As a result, database lock saturation dropped from 12% to less than 0.1%, and checkout transaction failure rate dropped to zero. This taught me that you cannot optimize what you do not measure, and dashboard metrics are the only source of truth in production systems."*

---

## 🕵️ Rehearsal Log

| Date | Duration (sec) | Confidence (1-5) | Filler Count | Peer/Self Feedback |
| :--- | :--- | :--- | :--- | :--- |
| | | | | |
