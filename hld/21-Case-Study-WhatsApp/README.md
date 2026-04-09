# 📱 21 - Case Study: Design WhatsApp (Gold-Standard Walkthrough)

This case study follows the **[HLD Delivery Framework](../HLD_DELIVERY_FRAMEWORK.md)** to demonstrate a Senior-level interview performance.

---

## 🕒 1. Requirements & Scope
- **Functional**: 1-on-1 chat, Group chat (max 100), Last seen status, Read receipts (double tick).
- **Non-Functional**: **Real-time** latency (< 100ms), **Reliable** (no message loss), 10M DAU, 10k concurrent users.

---

## 🏗️ 2. High-Level Architecture
1.  **Connection**: Mobile clients connect via **WebSockets** (TCP) to a "Chat Service" for bi-directional messaging.
2.  **Message Flow**: Sender -> WebSocket -> Chat Service -> DB (store) -> Message Queue -> Receiver's WebSocket.
3.  **Last Seen**: A dedicated "Status Service" updated on every heart-beat.

---

## 🗄️ 3. Data Modeling (DDIA Mastery)
- **Database Choice**: Cassandra (NoSQL).
- **Why**: WhatsApp is **Write-heavy**. Cassandra uses **LSM-trees**, making writes sequential and extremely fast. 
- **Schema**: `partition_key: chat_id`, `sort_key: timestamp`. This allows $O(1)$ retrieval of the latest messages.

---

## 🔬 4. Senior Deep Dives

### A. How to handle "Offline" users?
Store the message in the DB with status `PENDING`. When the user reconnects (WebSocket established), the "Presence Service" triggers a push of all pending messages.

### B. Group Chat Scaling
- **Problem**: 1 message to a 100-person group requires 100 writes.
- **Solution**: Use a **Message Queue (Kafka)**. The chat service publishes to a group topic. 100 consumer workers (1 per user) pull the message. This decouples the sender from the receivers.

---

## 🚀 5. The SDE-3 Edge: Sequence IDs
Don't use `System.currentTimeMillis()` for message ordering (clocks drift). 
Use a **Distributed ID Generator** (e.g., Snowflake or Zookeeper counters) to provide a monotonically increasing sequence ID for every message in a chat.
