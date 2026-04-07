# 🚦 Advanced Concurrency Patterns

The Gang of Four patterns assume a single-threaded execution model. In the real world, enterprise backend systems are massively parallel.

These folders contain absolute must-know LLD Concurrency Patterns that form the backbone of modern web frameworks (Spring Boot, Tomcat, Node.js).

### 1. Producer-Consumer (`01-Producer-Consumer`)
The foundation of asynchronous system design. Separates data ingestion from data processing using a `BlockingQueue`. This mitigates massive traffic spikes (Backpressure) and prevents memory exceptions. 
* *Real-world HLD scale: Apache Kafka, RabbitMQ.*

### 2. Thread Pool (`02-Thread-Pool`)
Creating raw OS threads is incredibly expensive. This pattern provisions a permanent pool of threads (e.g. 200 threads) and an infinite queue. Tasks do not get their own thread; they wait in the queue until a pool thread is free.
* *Real-world HLD scale: Tomcat Web Servers, HikariCP Database Connection Pools.*

### 3. Read-Write Lock (`03-Read-Write-Lock`)
Standard Mutex locks (`synchronized`) kill application performance on read-heavy systems. This pattern splits lock access, allowing infinite parallel Readers, but strict single-thread isolation for Writers.
* *Real-world HLD scale: PostgreSQL Shared/Exclusive Row Locks, Distributed caching algorithms.*
