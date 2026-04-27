# ☕ Java & JVM Core Insights

Enterprise-scale architecture and the Java Virtual Machine.

## 1. JVM Internals
- **Memory Regions**: Heap (Young/Old Gen), Metaspace, Stack.
- **Garbage Collection**: Choosing between G1, ZGC, and Shenandoah based on latency vs throughput.

## 2. Spring Framework Magic
- **Proxy Pattern**: How `@Transactional` and `@Cacheable` work under the hood via CGLIB or JDK dynamic proxies.
- **Dependency Injection Lifecycle**: Bean definitions, instantiation, and post-processing.

## 3. Concurrency (Project Loom)
- **Virtual Threads**: Moving beyond the one-thread-per-request model to handle millions of concurrent connections.
- **Structured Concurrency**: Organizing sub-tasks for better error propagation.

## 4. Modern Java (17+)
- **Records**: Immutable data carriers.
- **Pattern Matching**: Simplifying complex logic chains.
- **Sealed Classes**: Controlled inheritance hierarchies.
