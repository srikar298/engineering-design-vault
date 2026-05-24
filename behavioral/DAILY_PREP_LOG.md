# ⚡ Daily Prep Log (Sheet 2)

---

## 🟢 QUICK MODE — Use This Daily (6 Columns)

> Open [COCKPIT.md](file:///e:/job-hunt/LLD/LLD-Design-Patterns-main/behavioral/COCKPIT.md) first. Come back here only to log.

Copy these 6 column headers into Google Sheet **Sheet: Quick_Log**, Row 1:

```
Date | Story_Used | Question_Category | Duration_Sec | Filler_Count | Hire_Signal
```

**Hire_Signal values**: `Y` (felt sharp, specific, had metric) / `N` (vague, rushed, no metric)

### 📋 Weekly Hire Rate Formula (paste into a summary cell):
```excel
=TEXT(COUNTIF(F2:F100,"Y")/COUNTA(F2:F100),"0%") & " hire rate this week"
```

### 📋 Filler Trend Formula:
```excel
=IFERROR(AVERAGE(E2:E100),"No data") & " avg fillers/session"
```

> **Switch to Advanced Mode below only once per week** for a deeper audit.

---

## 🔵 ADVANCED MODE — Weekly Audit (26 Columns)

*Use this for your end-of-week retrospective, not daily.*



The **Daily Prep Log** is your ledger of execution. Every day you practice, you record a row here. Over 8 weeks, this log builds the momentum and consistency needed to pass senior-level loops.

---

## 🏗️ Google Sheets Column Schema (Columns A-Z)

Copy these column headers into your Google Sheet starting at cell `A1`:

### 🔹 Group A: Daily Identity
*   **Col A: Date** (Type: `Date`) — The calendar date.
*   **Col B: Day_Number** (Type: `Number`, Formula: `=ROW()-1`) — Track days since you started.
*   **Col C: Week** (Type: `Number`, Formula: `=ROUNDUP(B2/7, 0)`) — Track prep weeks.
*   **Col D: Days_To_Interview** (Type: `Number`) — Countdown to target interview date.
*   **Col E: Prep_Type** (Type: `Dropdown`) — What was the focus?
    *   *Options*: `Story Crafting`, `Rehearsal`, `Mock Simulator`, `Company Research`, `Question Practice`, `Reflection`

### 🔹 Group B: What You Did
*   **Col F: Story_Worked_On** (Type: `Dropdown`) — Which story code was practiced?
    *   *Options*: `S01`, `S02`, `S03`, `S04`, `S05`, `S06`, `S07`, `S08`, `S09`, `S10`, `S11`, `S12`, `Multiple`, `None`
*   **Col G: Activity** (Type: `Dropdown`) — What was the delivery channel?
    *   *Options*: `Write`, `Refine`, `Rehearse`, `Record`, `Peer Review`, `Mock`
*   **Col H: Duration_Mins** (Type: `Number`) — Total time spent in minutes.
*   **Col I: Questions_Practiced** (Type: `Number`) — Count of questions answered.
*   **Col J: Company_Researched** (Type: `Dropdown`) — Which company?
    *   *Options*: `Google`, `Meta`, `Amazon`, `Microsoft`, `Startup`, `Generic`
*   **Col K: Resource_Used** (Type: `Dropdown`) — What resource did you leverage?
    *   *Options*: `YouTube`, `Book`, `Peer`, `Mirror`, `Recording`, `Coach`, `AI`

### 🔹 Group C: Quality Assessment
*   **Col L: Story_Quality** (Type: `Rating (1-5)`) — Grade the content of the story draft.
*   **Col M: STAR_Complete** (Type: `Dropdown: Y/N/Partial`) — Did you cover Situation, Task, Action, Result, and Reflection?
*   **Col N: Had_Numbers** (Type: `Dropdown: Y/N`) — Did you use concrete metrics?
*   **Col O: Was_Specific** (Type: `Dropdown: Y/N`) — Did you specify *what* you did (avoiding vague generalizations)?
*   **Col P: Showed_Growth** (Type: `Dropdown: Y/N`) — Did the reflection show lessons learned?
*   **Col Q: Said_We_Not_Just_I** (Type: `Dropdown: Y/N`) — Did you credit the team appropriately while keeping the action on yourself?
*   **Col R: Under_2_Minutes** (Type: `Dropdown: Y/N`) — Was your spoken answer under 2 minutes (120 seconds)?
*   **Col S: Practiced_Aloud** (Type: `Dropdown: Y/N`) — Did you speak it, or just write/read it?
*   **Col T: Recorded_Self** (Type: `Dropdown: Y/N`) — Did you video/audio record your rehearsal?

### 🔹 Group D: Confidence & Wins
*   **Col U: Confidence_Before** (Type: `Rating (1-5)`) — Confidence level before starting today's session.
*   **Col V: Confidence_After** (Type: `Rating (1-5)`) — Confidence level after the session.
*   **Col W: Biggest_Win** (Type: `Text`) — Write what went exceptionally well.
*   **Col X: Biggest_Gap** (Type: `Text`) — Highlight what felt shaky or needs rework.
*   **Col Y: Tomorrow_Focus** (Type: `Text`) — The primary action item for tomorrow.
*   **Col Z: Nervousness_Level** (Type: `Rating (1-5)`) — Level of anxiety/nervousness today.

### 🔹 Group E: Auto Scores (Auto calculated columns)
*   **Col AA: Daily_Quality** (Type: `Number`, Formula below) — Automated daily quality rating out of 5.
*   **Col AB: Daily_Score** (Type: `Number`, Formula below) — Overall day score combining quality and confidence improvement.
*   **Col AC: Day_Rating** (Type: `Text`, Formula below) — Verbal day rating.

---

## 📊 Sheet Formulas (Copy-Paste)

### Col AA: Daily_Quality Formula (Cell `AA2`)
```excel
=ROUND(AVERAGE(L2, IF(N2="Y", 5, 2), IF(O2="Y", 5, 2), IF(R2="Y", 5, 3), IF(S2="Y", 5, 1)), 1)
```

### Col AB: Daily_Score Formula (Cell `AB2`)
```excel
=ROUND((AA2 * 0.5) + (V2 * 0.3) + ((5 - Z2) * 0.2), 1)
```

### Col AC: Day_Rating Formula (Cell `AC2`)
```excel
=IF(AB2>=4.5, "🔥 Outstanding", IF(AB2>=4.0, "✅ Strong", IF(AB2>=3.0, "🟡 Solid", IF(AB2>=2.0, "🟠 Needs Work", "🔴 Restart"))))
```

---

## 🎯 Target Thresholds & Goals
*   **Minimum Duration**: 20 minutes/day.
*   **Ideal Pace**: Under 2 minutes spoken.
*   **Metric Rigor**: > 80% of log entries must have `Had_Numbers` as `Y`.
*   **Practice Format**: Speak it aloud (`Practiced_Aloud` = `Y`) at least 4 days a week.
