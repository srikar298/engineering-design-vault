# S08 — Under Pressure

* **Primary Question**: *"Tell me about a high-stress situation you navigated at work."*
* **Core Signal**: Resilience, cool-headed prioritization, incident command, post-incident analysis.

---

## 📝 Story Builder (STAR+)

### [S] Situation (Context)
*   **Prompt**: Describe a high-stress production emergency (e.g., site outage during holiday traffic, data loss threat) or a tight deadline threat.
*   **Draft**: 
    *   

### [T] Task (Your Responsibility)
*   **Prompt**: What was your role in managing the incident or meeting the deadline?
*   **Draft**: 
    *   

### [A] Action (Your Steps)
*   **Prompt**: What did you do to triage the problem? How did you isolate variables? How did you organize the team during the incident?
*   **Draft**:
    1.  
    2.  
    3.  

### [R] Result (The Metrics)
*   **Prompt**: What was the recovery time? What did you build post-incident to prevent it from happening again?
*   **Draft**: 
    *   

### [+] Reflection (Lessons Learned)
*   **Prompt**: What did you learn about operational runbooks, system limits, or incident communication protocols?
*   **Draft**: 
    *   

---

## 🗣️ Spoken Draft Script (Max 2 Minutes)
*(Write out the complete narrative exactly as you would speak it)*

> *"[Placeholder Baseline Script]*
> *At [Company], during our annual sale event, our primary relational database CPU spiked to 100% and stayed there, locking all write operations. Our site was down, and we were losing transaction volume every minute. I was the on-call incident commander.*
> 
> *First, I immediately initiated a blameless incident bridge, designated a scribe to manage stakeholder updates, and focused the engineers on isolating the read and write traffic.*
> *Second, I checked our query logs and identified a slow-running analytics query that was missing an index, blocking the main database thread pool. I executed a kill command on the queries to restore database access.*
> *Finally, I set up a read-replica database specifically for our analytics traffic to isolate read queries from write transactions.*
> 
> *The site was fully functional in 18 minutes, and we implemented index validation on all query changes in the pipeline. This taught me that keeping a calm, structured approach is essential during production incident management."*

---

## 🕵️ Rehearsal Log

| Date | Duration (sec) | Confidence (1-5) | Filler Count | Peer/Self Feedback |
| :--- | :--- | :--- | :--- | :--- |
| | | | | |
