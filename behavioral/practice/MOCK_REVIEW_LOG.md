# 📝 Mock Review Log

A weekly retrospective of your mock performances allows you to identify which stories are hitting the mark and which categories need rework.

---

## 🏗️ 1. Weekly Mock Diagnostics Log

Track your aggregate weekly progress:

| Week | Mocks Completed | Average Mock Score | Best Question / Story | Weakest Question / Story | Hire Rate (% Hire Signals) | Top Priority for Next Week |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Week 1** | | | | | | |
| **Week 2** | | | | | | |
| **Week 3** | | | | | | |
| **Week 4** | | | | | | |

---

## 📊 2. Individual Story Performance Matrix
Identify which stories are "high-converting" versus those that receive pushback:

| Story ID | Times Used | Average Delivery Grade | Interviewer Reaction (Engaged / Skeptical) | Needs Re-Drafting? (Y/N) | Best Suited Company Type |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **S01** | | | | | |
| **S02** | | | | | |
| **S03** | | | | | |
| **S04** | | | | | |
| **S05** | | | | | |
| **S06** | | | | | |
| **S07** | | | | | |
| **S08** | | | | | |
| **S09** | | | | | |
| **S10** | | | | | |
| **S11** | | | | | |
| **S12** | | | | | |

---

## 🧠 3. Question Category Strength Map
Pinpoint which categories of behavioral questions you struggle to answer:

| Question Category | Mock Questions Handled | Average Score (1-5) | Strength Status (Auto-calculated) | Target Action Item |
| :--- | :--- | :--- | :--- | :--- |
| **Achievement** | | | | |
| **Technical Excellence**| | | | |
| **Failure & Growth** | | | | |
| **Conflict & Collab** | | | | |
| **Leadership** | | | | |
| **Ambiguity** | | | | |
| **Pressure / Delivery** | | | | |
| **Data Decisions** | | | | |
| **Growth / Mentorship** | | | | |

### 📋 Google Sheets Strength Status Formula (Cell `D2` on sheet)
```excel
=IF(Average_Score>=4.5, "🏆 Mastered", IF(Average_Score>=4.0, "🟢 Strong", IF(Average_Score>=3.5, "🟡 Developing", "🔴 Critical Gap")))
```
