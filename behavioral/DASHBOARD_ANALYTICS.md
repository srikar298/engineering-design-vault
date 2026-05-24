# 📊 Dashboard & Analytics

This document serves as the guide for setting up your **Behavioral Mastery Google Sheet Dashboard**. It compiles all automated metrics, readiness indicators, and quote rotation logic.

---

## 🏗️ Google Sheets Dashboard Formulas

### 1. Overall Behavioral Readiness Score
*   **Formula Location**: Dashboard cell `B3` (or equivalent).
*   **Weights**:
    *   Story Completion (`Stories_Ready / 12`): 25%
    *   Question Coverage (`Questions_Covered / 45`): 20%
    *   Mock Performance (`Mock_Score` out of 5): 30%
    *   Company Research Depth (`Company_Prep` out of 5): 15%
    *   Delivery Confidence (`Confidence_Score` out of 5): 10%

#### 📋 Copy-Paste Formula:
```excel
=ROUND((Stories_Ready/12 * 5 * 0.25) + (Questions_Covered/45 * 5 * 0.20) + (Mock_Score * 0.30) + (Company_Prep_Score * 0.15) + (Confidence_Score * 0.10), 1)
```

---

### 2. Readiness Signal
*   **Formula Location**: Dashboard cell `C3` (adjacent to Readiness Score).
*   **Outputs**: Verbose guidance on when you are ready to begin interview loops.

#### 📋 Copy-Paste Formula:
```excel
=IF(B3>=4.5, "🏆 Exceptional — Stories are compelling", IF(B3>=4.0, "🟢 Strong — Clearly hireable", IF(B3>=3.5, "🟡 Developing — Good foundation", IF(B3>=3.0, "🟠 Building — More practice needed", "🔴 Early Stage — Focus on story building"))))
```

---

### 3. Stories Mastered Tracker
*   **Description**: Pulls the count of stories that have reached `🏆 Mastered` status on the Story Bank worksheet.

#### 📋 Copy-Paste Formula:
```excel
=COUNTIF('Story Bank'!Mastery, "🏆 Mastered") & "/12 stories mastered"
```

---

### 4. Question Coverage Score
*   **Description**: Calculates the percentage of the 45 questions that have a primary story mapped to them.

#### 📋 Copy-Paste Formula:
```excel
=TEXT(COUNTA('Question-Story Map'!Primary_Story_Column)/45, "0%") & " questions covered"
```

---

### 5. Company Readiness Score
*   **Description**: Computes a custom readiness score for a specific target company by taking into account mock scores, story mapping, and values alignment.

#### 📋 Copy-Paste Formula:
```excel
=ROUND((Stories_Ready * 0.30) + (Company_Research * 0.20) + (Mock_Score * 0.30) + (LP_Coverage * 0.20), 1)
```

---

### 6. Mock Trend Indicator
*   **Description**: Analyzes whether your scores are consistently improving or declining.

#### 📋 Copy-Paste Formula:
```excel
=IF(AVERAGE(Last3Mocks) > AVERAGE(First3Mocks), "📈 " & TEXT(AVERAGE(Last3Mocks)-AVERAGE(First3Mocks), "0.0") & " point improvement", "📉 Declining — Review mock review logs")
```

---

### 7. Daily Quote Rotator (Auto-Rotate)
*   **Description**: Displays a motivational behavioral coaching quote that updates automatically based on the day of the week.

#### 📋 Copy-Paste Formula:
```excel
=INDEX({"Your story is your brand. Own it completely."; "Specificity is credibility. Vague answers kill interviews."; "They don't hire resumes. They hire people they believe in."; "The best behavioral answer shows who you ARE, not what you DID."; "Data makes stories believable. Emotion makes them memorable."; "Practice until you can't get it wrong, not just until you get it right."; "Every question is asking: Will you make us better?"}, WEEKDAY(TODAY()))
```
