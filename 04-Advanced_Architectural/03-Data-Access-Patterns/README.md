# 🌍 Data Access Patterns

This folder contains high-level architectural patterns for managing data persistence and object lifecycles in complex enterprise systems.

### 💾 1. DAO vs Repository (`01-Repository-DAO`)
The foundation of clean data layers. We split the low-level database operations (DAO) from the high-level business queries (Repository). This allows you to swap your database (e.g. SQL to NoSQL) without touching your business logic.

### 💼 2. Unit Of Work (`02-Unit-Of-Work`)
Tracks multiple operations in a single atomic transaction. It buffers changes to repositories and pushes them all at once during a `commit()`, ensuring data consistency and reducing network round-trips.

### 🧱 3. Dependency Injection (`03-Dependency-Injection`)
Decouples your classes by passing dependencies from the outside. Utilizing an **Inversion of Control (IoC)** container, this pattern makes your code highly testable, modular, and easy to maintain by removing hardcoded object creation.
