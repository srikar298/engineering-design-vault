# 📚 DDIA Master Synopsis: Theory to Architecture

This document maps the core concepts from **"Designing Data-Intensive Applications" (DDIA)** by Martin Kleppmann to the implementations and guides in this repository. Use this for SDE-3 (Senior) level deep dives.

---

## 🏗️ Part I: Foundations of Data Systems

### Chapter 1: Reliability, Scalability, Maintainability
- **Core Concept**: P99 Latency and SLOs.
- **In-Repo Guide**: [Scale From Zero](../hld/02-Scale-From-Zero/README.md)
- **Senior Signal**: "We design for the 99.9th percentile to ensure our most active (and valuable) users aren't frustrated by tail latency."

### Chapter 3: Storage and Retrieval
- **Core Concept**: B-Trees vs. LSM-Trees.
- **In-Repo Guide**: [Database Engines](../hld/05-Databases/README.md)
- **Senior Signal**: "Choosing the right storage engine is a trade-off between Read throughput and Write amplification."

### Chapter 4: Encoding and Evolution
- **Core Concept**: Forward/Backward Compatibility and Binary Protocols.
- **In-Repo LLD**: [Binary Serialization](../lld/06-Addons/14-Binary-Serialization/)
- **Senior Signal**: "Internal service-to-service communication uses Protobuf to minimize CPU overhead and bandwidth cost."

---

## 🌐 Part II: Distributed Data

### Chapter 5: Replication
- **Core Concept**: Replication Lag, Read-after-write consistency, and Conflict Resolution.
- **In-Repo LLD**: [Version Vectors](../lld/06-Addons/16-Version-Vectors/)
- **Deep Dive**: How to handle "Concurrent Writes" in a multi-region setup.

### Chapter 6: Partitioning (Sharding)
- **Core Concept**: Consistent Hashing and The Celebrity (Hot Key) Problem.
- **In-Repo Guide**: [Database Scaling](../hld/07-Database-Scaling/README.md)
- **Senior Signal**: "Sharding by UserID is simple, but we implement virtual nodes to prevent hot-spotting during viral events."

### Chapter 7: Transactions
- **Core Concept**: Isolation Levels and Race Conditions (Read Skew, Phantom Reads).
- **In-Repo Guide**: [Isolation Levels](../hld/10-Consistency-Models/README.md)

### Chapters 8 & 9: Distributed Systems Trouble & Consensus
- **Core Concept**: Lamport Clocks, Linearizability, and Leader Election.
- **In-Repo LLD**: [Lamport Clocks](../lld/06-Addons/15-Lamport-Clocks/), [Leader Election](../lld/06-Addons/17-Leader-Election/)
- **Senior Signal**: "We use Lamport Clocks to order events globally because NTP-based wall clocks drift in distributed environments."

---

## 🌊 Part III: Derived Data

### Chapter 11: Stream Processing
- **Core Concept**: Change Data Capture (CDC) and Idempotent Consumers.
- **In-Repo LLD**: [Transactional Outbox](../lld/06-Addons/08-Transactional-Outbox/)
- **Senior Signal**: "Our Event-Driven architecture uses the Outbox pattern to ensure total consistency between the DB and the Message Broker."

---

> **"Data is the center of the universe. Code is just the medium we use to move it."**
