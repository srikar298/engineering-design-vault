# 🥤 Vending Machine LLD — Java Implementation

Ensure you are in the `JAVA` folder.

```bash
cd "03-Behavioral/12-LLD-Problems/01-vending-machine/JAVA/"
javac cor/*.java state/*.java machine/*.java Main.java
java Main
```

**Expected Console Output:**
```
==================================================
   Vending Machine: State + CoR Demo              
==================================================

   [State: IDLE] Waiting for coins...

--- Scenario 1: Buying a soda ---
   [Hardware] Recognized Quarter ($0.25)
      -> Coin accepted! Balance: $0.25
   [State: HAS MONEY] Ready to Vend!
      -> Button Pressed.
   [State: DISPENSING] Motor running...
      -> 🥤 Item Dispensed! Enjoy.
   [State: IDLE] Waiting for coins...

--- Scenario 2: Trying to buy when empty ---
   [Hardware] Recognized Dime ($0.10)
      -> Coin accepted! Balance: $0.1
   [State: HAS MONEY] Ready to Vend!
   [Hardware] Recognized Dime ($0.10)
      -> Extra Coin accepted! Balance: $0.2
   [Hardware] Recognized Dime ($0.10)
      -> Extra Coin accepted! Balance: $0.30000000000000004
      -> Button Pressed.
   [State: DISPENSING] Motor running...
      -> ❌ ITEM OUT OF STOCK! Refunding: $0.30000000000000004
   [State: IDLE] Waiting for coins...

--- Scenario 3: Inserting a fake slug coin ---
   ❌ [Hardware Error] Coin rejected! Spitting it back out.
```
