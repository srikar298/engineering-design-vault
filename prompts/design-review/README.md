# 🤖 AI Prompts: Architecture & Design Review

Use these structured prompts with LLMs (Claude, GPT-4, Gemini) to audit your designs.

## 1. High-Level Design (HLD) Auditor
**Goal**: Identify single points of failure, scaling bottlenecks, and data consistency issues.

**Prompt**:
```markdown
Act as a Senior Staff Engineer. I will provide a system design overview. 
Analyze it for the following:
1. Scaling Bottlenecks: Where will this fail at 100x current load?
2. Availability: What happens if [Component X] goes down?
3. Data Integrity: Analyze the consistency model (Eventual vs Strong) for our use case.
4. Latency: Identify high-latency paths in the request lifecycle.

Design to Review: [INSERT YOUR DESIGN SUMMARY]
```

## 2. The "Pre-Mortem" Prompt
**Goal**: Anticipate disasters before they happen.

**Prompt**:
```markdown
Imagine we have deployed this architecture and it is 6 months from now. 
The system has suffered a catastrophic failure. 
List the top 3 likely technical reasons why it failed based on this design and suggest remediations for each.
```
