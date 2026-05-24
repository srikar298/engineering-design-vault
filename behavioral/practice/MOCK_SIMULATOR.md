# 🎯 Mock Simulator

Simulating the environment of a real interview is critical. Use this worksheet to track individual mock sessions, timing breakdowns, and delivery diagnostics.

---

## 🏗️ 1. Pre-Mock Session Setup Log

Create these setup fields in your log:
*   **Mock_ID**: (Type: `Auto-increment / Row Number`)
*   **Date**: (Type: `Date`)
*   **Target_Company**: (Type: `Dropdown: Google/Meta/Amazon/Microsoft/Startup/Generic`)
*   **Interviewer_Type**: (Type: `Dropdown: Peer/Friend/Coach/AI/Self-Recording`)
*   **Interview_Round**: (Type: `Dropdown: Phone Screen/Hiring Manager/Bar Raiser/System Design-Behavioral`)
*   **Target_Duration_Mins**: (Type: `Number`, Default: `45`)

---

## 📊 2. Question-by-Question Scoring Sheet
Log each question asked during a mock session:

| # | Question Asked | Story Used | STAR Complete (Y/N/P) | Had Metrics (Y/N) | Was Specific (Y/N) | Duration (sec) | Delivery Grade (1-5) | Content Grade (1-5) | Follow-up Qs Count |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Q1** | | | | | | | | | |
| **Q2** | | | | | | | | | |
| **Q3** | | | | | | | | | |
| **Q4** | | | | | | | | | |

---

## ⏱️ 3. Spoken Timing Tracker
A standard 45-minute loop should manage time as follows:

*   **Tell Me About Yourself**: Target: `120s` (2 min) | Actual: `____s`
*   **Why This Company**: Target: `60s` (1 min) | Actual: `____s`
*   **Behavioral Q1**: Target: `180s` (3 min with follow-up) | Actual: `____s`
*   **Behavioral Q2**: Target: `180s` (3 min with follow-up) | Actual: `____s`
*   **Behavioral Q3**: Target: `180s` (3 min with follow-up) | Actual: `____s`
*   **Behavioral Q4**: Target: `180s` (3 min with follow-up) | Actual: `____s`
*   **Your Questions**: Target: `300s` (5 min) | Actual: `____s`

---

## 🗣️ 4. Filler Word Diagnostics
Track your filler rate per response:
*   *Fillers counted*: `"Um/Uh"`, `"Like"`, `"Basically"`, `"You know"`, `"Right?"`, `"So yeah"`.

### 📋 Google Sheets Filler Rate Indicator (Cell `G2` or equivalent)
```excel
=IF(SUM(Filler_Word_Count)/Questions_Asked>5, "🔴 High Fillers — Practice deliberate pauses", IF(SUM(Filler_Word_Count)/Questions_Asked>2, "🟡 Moderate — Improving", "🟢 Clean Delivery"))
```

---

## 🏆 5. Overall Mock Session Assessment
At the end of each session, log these grades:
*   **Opening_Strength** (1-5)
*   **Story_Quality_Avg** (Auto-calculated average of question content grades)
*   **Specificity_Score** (1-5)
*   **Authenticity_Feel** (1-5)
*   **Energy_Level** (1-5)
*   **Recovery_When_Stuck** (1-5)
*   **Questions_Asked_Them** (1-5)
*   **Nervousness_Level** (1-5)

### 📋 Google Sheets Overall Mock Score Formula (Cell `P2`)
```excel
=ROUND((Opening_Strength * 0.10) + (Story_Quality_Avg * 0.25) + (Specificity_Score * 0.15) + (Authenticity_Feel * 0.15) + (Energy_Level * 0.10) + (Recovery_When_Stuck * 0.10) + (Questions_Asked_Them * 0.10) + ((5 - Nervousness_Level/5*4) * 0.05), 1)
```

### 📋 Google Sheets Hire Signal Formula (Cell `Q2`)
```excel
=IF(Overall_Mock_Score>=4.5, "🏆 Strong Hire — Exceptional storyteller", IF(Overall_Mock_Score>=4.0, "🟢 Hire — Solid behavioral performance", IF(Overall_Mock_Score>=3.5, "🟡 Borderline — Needs polish", IF(Overall_Mock_Score>=3.0, "🟠 No Hire — Stories lack depth", "🔴 Strong No Hire — Fundamental gaps"))))
```
