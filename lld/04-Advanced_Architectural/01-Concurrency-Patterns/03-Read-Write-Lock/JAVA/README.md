# 📖 Read-Write Lock Pattern — Java Implementation

Ensure you are in the `JAVA` folder.

```bash
cd "04-Advanced_Architectural/01-Concurrency-Patterns/03-Read-Write-Lock/JAVA/"
javac database/*.java workers/*.java Main.java
java Main
```

**Expected Console Output (Notice how the first 3 Readers execute SIMULTANEOUSLY, but the Writer blocks everything else):**
```
==================================================
   Concurrency: Read-Write Lock Demo              
==================================================

🚀 Firing 3 Readers...
   📖 [pool-1-thread-2] Reading stock: 100
   📖 [pool-1-thread-1] Reading stock: 100
   📖 [pool-1-thread-3] Reading stock: 100

🚀 Firing 1 Writer and 2 more Readers...
   📝 [pool-1-thread-4] WRITING new stock: 999
   ✅ [pool-1-thread-4] Write completed!
   📖 [pool-1-thread-6] Reading stock: 999
   📖 [pool-1-thread-5] Reading stock: 999

✅ Database simulation complete!
```
