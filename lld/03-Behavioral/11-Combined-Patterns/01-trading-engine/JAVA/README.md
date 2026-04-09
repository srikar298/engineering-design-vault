# 📈 Trading Engine — Java Implementation

Ensure you are in the `JAVA` folder.

```bash
cd "03-Behavioral/11-Combined-Patterns/01-trading-engine/JAVA/"
javac memento/*.java observer/*.java strategy/*.java engine/*.java Main.java
java Main
```

**Expected Console Output:**
```
==================================================
   Trading Engine: Observer + Strategy + Memento  
==================================================

   [Originator] Saving state to Memento...

--- Trading Session Starts ---

[MARKET EVENT] APPL is now trading at $105.0
   [AlphaBot] Evaluated APPL: HOLD

[MARKET EVENT] APPL is now trading at $95.0
   [AlphaBot] Evaluated APPL: BUY_1_SHARE
      -> Executed Buy. Cash remaining: $5.0

   [AlphaBot] Generating End-Of-Day Snapshot...
   [Originator] Saving state to Memento...

--- Day 2: Market is Bullish. Swapping to Aggressive ---
   [AlphaBot] Swapped algorithm to: AggressiveStrategy

[MARKET EVENT] APPL is now trading at $45.0
   [AlphaBot] Evaluated APPL: BUY_MAX
      -> Insufficient Funds for Buy.

[MARKET EVENT] APPL is now trading at $5.0
   [AlphaBot] Evaluated APPL: BUY_MAX
      -> Executed Buy. Cash remaining: $0.0

   [AlphaBot] 🚨 PANIC: Market crashed! Rolling back to last EOD state...
   [Originator] State restored from Memento.
   [System] Account Balance rolled back to: $5.0
```
