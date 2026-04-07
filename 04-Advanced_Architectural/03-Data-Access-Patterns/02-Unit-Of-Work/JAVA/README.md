# 💾 Unit Of Work — Java Implementation

Ensure you are in the `JAVA` folder.

```bash
cd "04-Advanced_Architectural/03-Data-Access-Patterns/02-Unit-Of-Work/JAVA/"
# Point to shared models and DAOs as needed
javac -cp ".;../../01-Repository-DAO/JAVA/" model/*.java dao/*.java repository/*.java uow/*.java Main.java
java -cp ".;../../01-Repository-DAO/JAVA/" Main
```

*(Note: In this simple demo, I've duplicated some files or simplified the classpath for ease of execution)*

**Expected Console Output:**
```
==================================================
   Data Access: Unit Of Work Demo                 
==================================================

Scenario: Adding a user and their initial task in one transaction.

[UoW] --- Starting Transaction Commit ---
   [DAO] Persisted User: Charlie
   [TaskDAO] Saved Task for User 101: Setup Workstation
[UoW] --- Transaction Committed Successfully! ---

Scenario: Verifying retrieval...
Fetched from Repo: User{id=101, name='Charlie', email='charlie@dev.com'}
```
