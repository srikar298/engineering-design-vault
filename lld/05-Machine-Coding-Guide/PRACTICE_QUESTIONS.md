# 🎯 Machine Coding & Low-Level Design (LLD) Roadmap

> **"A Junior Developer implements the requirements. A Senior Engineer designs for the scale, concurrency, and extensibility of 2026."**

This roadmap is categorized by difficulty. For each level, master the **Invariants** and **Concurrency** before moving up. SDE-2 mandatory problems are marked with **`🔥 Mandatory`** and must be practiced before interviews.

---

## 🟢 Level 1: Foundations (SDE-1 / Junior)
*Focus: OOP Pillars, SOLID, and Simple State.*

### 🛠️ Interactive Practice Sheets (Implemented on Disk)
1.  **[Tic-Tac-Toe](LEVEL-1-Foundations/01-tic-tac-toe/README.md)** (Grid logic) — **`🔥 Mandatory`**
2.  **[Snake and Ladder](LEVEL-1-Foundations/02-snake-and-ladder/README.md)** (Turn management) — **`🔥 Mandatory`**
3.  **[Vending Machine](LEVEL-1-Foundations/03-vending-machine/README.md)** (State transitions & balance) — **`🔥 Mandatory`**
4.  **[Library Management](LEVEL-1-Foundations/04-library-management/README.md)** (Resource relationships)
5.  **[2048 Game](LEVEL-1-Foundations/05-2048-game/README.md)** (Grid coordinate sliding & merging)
6.  **[Minesweeper](LEVEL-1-Foundations/06-minesweeper/README.md)** (Matrix recursive reveal logic)
7.  **[Parking Lot (Basic)](LEVEL-1-Foundations/07-parking-lot-basic/README.md)** (Entity modeling & capacity check) — **`🔥 Mandatory`**
8.  **[Splitwise (Basic)](LEVEL-1-Foundations/08-splitwise-basic/README.md)** (Equal/Exact/Percent splits) — **`🔥 Mandatory`**

---

## 🟡 Level 2: Intermediate (SDE-2 / Professional)
*Focus: Design Patterns, Thread-Safety, and Workflows.*

### 🛠️ Interactive Practice Sheets (Implemented on Disk)
1.  **[Movie Ticket Booking (BookMyShow)](LEVEL-2-Intermediate/01-movie-ticket-booking/README.md)** (Concurrency & seat locking) — **`🔥 Mandatory`**
2.  **[Parking Lot (Advanced/Concurrency)](LEVEL-2-Intermediate/02-parking-lot/README.md)** (Thread-safe spot assignment & Observer display) — **`🔥 Mandatory`**
3.  **[Rate Limiter](LEVEL-2-Intermediate/03-rate-limiter/README.md)** (Concurrency & Token Bucket/Leaky Bucket) — **`🔥 Mandatory`**
4.  **[Internet Download Manager (IDM)](LEVEL-2-Intermediate/04-internet-download-manager/README.md)** (Multithreaded byte-range requests)
5.  **[Elevator System](LEVEL-2-Intermediate/05-elevator-system/README.md)** (State pattern & SCAN scheduling algorithm) — **`🔥 Mandatory`**
6.  **[Zepto Coupon System](LEVEL-2-Intermediate/06-zepto-coupon-system/README.md)** (Open/Closed strategy & discount decorator)
7.  **[High-Performance Task Scheduler](LEVEL-2-Intermediate/07-task-scheduler/README.md)** (PriorityBlockingQueue & dynamic cancel thread-safety)

### 📚 Conceptual / Advanced Roadmap
*   **OYO / Airbnb** (Inventory state & overbooking prevention)
*   **Thread Pool** (Work queue & worker thread lifecycle)
*   **Google Calendar** (Date-range conflict checks)
*   **Logging Library (Log4j)** (Singleton & Decorator output streams)
*   **JSON Parser** (Composite syntax trees)

---

## 🟠 Level 3: Advanced (SDE-2+ / SDE-3 / Senior)
*Focus: Infrastructure, Distributed Invariants, & Peer-to-Peer.*

### 🛠️ Interactive Practice Sheets (Implemented on Disk)
1.  **[AWS S3 Service](LEVEL-3-Advanced/01-aws-s3-service/README.md)** (Metadata storage, strategies, and ACL Proxy)
2.  **[Enterprise Chat System (Slack/WhatsApp)](LEVEL-3-Advanced/02-chat-system/README.md)** (Real-time message routing, Group Observer, & Offline proxy buffering)

### 📚 Conceptual / Advanced Roadmap
*   **Google Drive** (Version control & block-level file sync)
*   **GitHub Internals** (Git objects: Blobs, Trees, Commits)
*   **Tinder Dating App** (Geospatial matching & feed strategies)
*   **Google Docs** (Collaborative editing: CRDT/OT)
*   **Crypto Exchange** (High-throughput order book & matching engine)
*   **Payment Recommendation** (Ranking & dynamic routing strategies)
*   **Video Conferencing (Zoom)** (WebRTC signaling coordinate logic)
*   **Chess Game** (Move validation & command undo/redo)

---

## 🔴 Level 4: Architect / Founding Engineer (SDE-4)
*Focus: Agentic AI, Distributed Systems logic, & Performance Engines.*

### 🛠️ Interactive Practice Sheets (Implemented on Disk)
1.  **[AI Agentic Workflow Orchestrator](LEVEL-4-Architect/01-ai-agentic-workflow/README.md)** (Planning state machine, Command tools, LLM Strategy, Fallback Chain)

### 📚 Conceptual / Advanced Roadmap
*   **Vector Database Indexer** (Embedding & Semantic Hashing)
*   **Game Engine (Unreal Lite)** (System decoupling: Physics/Logic/Render)
*   **Distributed L1/L2 Cache** (Replication & consistency models)
*   **Alexa / Voice Assistant** (Intent mapping & pipeline orchestration)
*   **Service Discovery Sidecar** (Health checks & dynamic routing)

---

## 📈 Learning Path
1.  **Start Level 1:** Get it compilable and working in under 25 minutes.
2.  **Master Level 2:** Implement thread-safety and at least two design patterns in 35 minutes.
3.  **Crush Level 3:** Explain the design trade-offs of performance and memory in 45 minutes.
4.  **Innovate Level 4:** Lead discussions on agentic workflows and advanced decoupling strategies.
