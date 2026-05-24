# 🎙️ Recording Log

The single highest-ROI behavioral prep habit. Every spoken attempt logged here builds a traceable arc from "rough draft" to "mastered delivery."

> **Rule**: Speak it before you write it. Record every attempt. You cannot hear yourself think.

---

## 📁 File Naming Convention
Save all recordings using this format:
```
YYYY-MM-DD_S0X_attempt_N.[m4a|mp3|wav]
Example: 2026-05-24_S01_attempt_3.m4a
```

Recommended tools:
- **Phone voice memo** (easiest, always available)
- **Otter.ai** (auto-transcribes, highlights filler words)
- **Loom** (video + audio, best for body language review)
- **OBS** (full desktop recording for screen + webcam)

---

## 📊 Recording Attempt Log

Copy this table into your Google Sheet as **Sheet: Recording_Log**:

| Date | Story | Attempt # | Tool Used | Duration (sec) | Filler Count | Under 2 Min? | Key Observation | Next Fix |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| | S01 | 1 | Voice Memo | | | | | |
| | S01 | 2 | Voice Memo | | | | | |
| | | | | | | | | |

---

## 🧠 Self-Review Protocol (After Each Recording)

Listen to your recording **twice**:

### First Listen — Delivery (No content focus)
*   [ ] **Pacing**: Did you rush at any point? Where?
*   [ ] **Filler words**: Count every "um", "uh", "like", "basically", "you know"
*   [ ] **Energy**: Does your voice convey confidence or uncertainty?
*   [ ] **Pauses**: Are pauses intentional (strategic) or panicked (blank)?
*   [ ] **Under 120 sec?**: Did you stay within the time limit?

### Second Listen — Content
*   [ ] **Situation**: Is it set up in under 15 seconds?
*   [ ] **Metric in Result**: Did you say a number (%, ms, $, hrs)?
*   [ ] **The "I" check**: Did you use "I" enough vs "we"?
*   [ ] **The "Why" check**: Did you explain *why* you chose your approach?
*   [ ] **Reflection**: Did you land a learning? Not just "it went well."

---

## 📈 Progress Tracking Per Story

Track attempts per story to see convergence toward mastery:

| Story | Attempt 1 Duration | Attempt 5 Duration | Attempt 10 Duration | Filler @ Attempt 1 | Filler @ Attempt 10 | Mastered? |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **S01** | | | | | | |
| **S02** | | | | | | |
| **S03** | | | | | | |
| **S04** | | | | | | |
| **S05** | | | | | | |
| **S06** | | | | | | |
| **S07** | | | | | | |
| **S08** | | | | | | |
| **S09** | | | | | | |
| **S10** | | | | | | |
| **S11** | | | | | | |
| **S12** | | | | | | |

### 📋 Google Sheets Mastery by Recording Formula (Cell `G2` on sheet)
```excel
=IF(AND(Attempt_10_Duration<=120, Filler_at_10<=2), "🏆 Mastered Delivery", IF(Attempt_5_Duration<=130, "🟢 On Track", "🟡 Needs More Reps"))
```

---

## 🎯 Convergence Targets

| Metric | Attempt 1 (Typical) | Target by Attempt 10 |
| :--- | :--- | :--- |
| Duration | 150-180 sec (too long) | 90-110 sec (sweet spot) |
| Filler words | 8-15 | ≤ 2 |
| Confidence | 2/5 | 4.5/5 |
| Metric cited | Often forgotten | Always present |
| Reflection | Generic | Specific & insightful |
