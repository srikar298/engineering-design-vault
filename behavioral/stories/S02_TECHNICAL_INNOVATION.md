# S02 — Technical Innovation

* **Primary Question**: *"Tell me about a complex technical problem you solved."*
* **Core Signal**: Architecture, design patterns, analytical troubleshooting, trade-off analysis.

---

## 📝 Story Builder (STAR+)

### [S] Situation (Context)
*   **Prompt**: Describe a complex, non-obvious engineering bottleneck (e.g., memory leak, N+1 query issue, high write-amplification in database).
*   **Draft**: 
    *   

### [T] Task (Your Responsibility)
*   **Prompt**: What was your responsibility?
*   **Draft**: 
    *   

### [A] Action (Your Steps)
*   **Prompt**: Detail the steps you took to diagnose (e.g., profile memory, flame graphs) and resolve the issue. What alternatives did you reject?
*   **Draft**:
    1.  
    2.  
    3.  

### [R] Result (The Metrics)
*   **Prompt**: Performance metrics (e.g., throughput increased by 200%, p99 latency dropped from 2s to 150ms).
*   **Draft**: 
    *   

### [+] Reflection (Lessons Learned)
*   **Prompt**: What did you learn about profiling, system limits, or caching strategies?
*   **Draft**: 
    *   

---

## 🗣️ Spoken Draft Script (Max 2 Minutes)
*(Write out the complete narrative exactly as you would speak it)*

> *"[Placeholder Baseline Script]*
> *At [Company], our core product catalog search service was experiencing CPU spikes up to 98% during peak hours, causing search requests to time out. I was assigned to resolve the performance issue before Black Friday.*
> 
> *I ran profiling tools and generated flame graphs, which revealed that we were facing an extreme N+1 query pattern on the ElasticSearch index mapping layer. I evaluated two alternatives: implementing a Redis query cache or redesigning the indexing mapping to pre-bake associations. I chose the pre-baking strategy because a cache would still require database hits on cache misses.*
> *I refactored our data pipeline to compile associations asynchronously using CDC (Change Data Capture) with Kafka. I also batch-loaded records in our application layer using the DataLoader pattern.*
> 
> *As a result, CPU utilization during peak hours dropped from 98% to 35%, and search latency dropped from 2.2 seconds to 80ms under a load of 15k QPS. This taught me to always analyze database access patterns before attempting to scale compute."*

---

## 🕵️ Rehearsal Log

| Date | Duration (sec) | Confidence (1-5) | Filler Count | Peer/Self Feedback |
| :--- | :--- | :--- | :--- | :--- |
| | | | | |
