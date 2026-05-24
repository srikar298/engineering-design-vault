# 🏢 SD057 - Design Multiplayer Game Matchmaker

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Problem ID** | SD057 |
| **Category** | Gaming & Streaming |
| **Difficulty** | 🔴 Expert |
| **Interview Frequency** | ⚡ Very Common (2024–2026) |
| **Target Companies** | Riot Games, Valve, Epic Games, Electronic Arts |
| **SDE-2 Mandatory** | ❌ No (SDE-3 / Senior Focus) |
| **Status** | Completed |
| **Times Practiced** | 1 |
| **Last Practiced** | 2026-05-24 |
| **Next Review** | 2026-06-24 |
| **Confidence** | 🟢 Applied |
| **Mastery** | 🟢 Expert |

---

## 📋 1. Core Requirements & Scale

### Functional Requirements
- Players queue for matchmaking as solo or parties (1 to 5 players).
- Match players with similar Matchmaking Rating (MMR).
- Group players dynamically based on network latency to edge servers (ping).
- Spin up dedicated game server instances dynamically for matched players and route them.

### Non-Functional Requirements
- **Low Matchmaking Latency**: Form matches in < 15 seconds.
- **Fair Match Quality**: MMR variance between teams should be minimal.
- **Ping Limits**: Ensure matched players have a ping < 80ms to the designated game server.
- **Horizontally Scalable Matchmaker**: Scale matching pools under peak player loads.

### Scale Targets (Back-of-the-Envelope)
- **Concurrent Active Players (CCU)**: 5M globally.
- **Active Matchmaking Queue**: 500,000 players at any instant.
- **Match Size**: 10 players (5v5 format).
- **Match Ingestion Rate**: ~50,000 players entering the queue per second.

---

## 📐 2. High-Level Architecture

```
                            [ Players (WebSockets Gateway) ]
                                           │
                                           ▼
                                [ Matchmaking Gateway ]
                                           │
                                           ▼
                                [ Latency Evaluator ] ◄──► [ Regional Ping Beacons ]
                                           │
                                           ▼
                     [ Regional Queue Managers (Redis Clusters) ]
                                           │
                                           ▼
                       [ Distributed Matchmaking Engine (Go) ]
                     (Bipartite Matcher + Dynamic Windowing)
                                           │
                                           ▼
                             [ Game Session Coordinator ]
                                           │
                    ┌──────────────────────┴──────────────────────┐
                    ▼                                             ▼
       [ Fleet Allocator (Agones/K8s) ]                   [ Lobby Sync Broker ]
                    │                                             │
                    ▼                                             ▼
       [ Dedicated Game Server (AWS) ]                  [ WebSocket Push Gateway ]
                    ▲                                             │
                    └─────────────────────────────────────────────┘
                                  (Connect Match Payload)
```

---

## ⚖️ 3. Deep Dive & Core Components

### A. Matchmaking Queue Partitioning and Architecture
Holding 500k players in a single global queue creates an $O(N^2)$ sorting nightmare. We partition the queue:
* **Region & Latency Sharding**:
  * Clients ping regional latency beacons. We group players into regional latency buckets (e.g. `US-West`, `US-East`, `EU-Central`).
  * Each bucket is managed by an independent matchmaking queue running on Redis Sorted Sets (`ZSET`), sharded by game mode.
* **MMR Bucket Partitioning**:
  * Within each latency group, players are sorted in the queue by their MMR scores.

### B. The Matchmaking Algorithm: Dynamic Window Expansion
To balance fair MMR match quality with quick queue times:
* **Dynamic Relaxing Search**:
  * When a player joins, the matcher searches for opponents within a tight MMR variance (e.g., $\pm 50$ points).
  * If no match is found within 5 seconds, the search window expands (e.g., $\pm 100$ points) and latency thresholds relax slightly.
  * We use a sliding interval sweep: workers pull batches from the Redis queue and perform bipartite matching algorithms to pair teams.

### C. Game Server Fleet Allocation (Agones / Kubernetes)
Once 10 players are matched, they must be routed to a game server:
* **Agones System**: An open-source Kubernetes operator designed for dedicated game servers.
* **Flow**:
  * Matchmaker requests a server allocation from Agones.
  * Agones selects an active, warm game server instance in the selected region, transitions its state to `Allocated`, and returns its IP and port.
  * The matchmaker broadcasts this allocation payload to all 10 players via their persistent WebSocket gateway connections.

---

## 🚫 4. Common Mistakes & Interview Playbook

### Common Mistakes (The "Junior" Signals)
- Storing active matchmaking queues in standard relational SQL databases and polling with `SELECT * FROM players WHERE searching = true` (destroys performance instantly).
- Forgetting to account for party sizing (e.g., matching a group of 4 with a solo player to form a 5v5 team).
- Routing players from different continents to the same server, resulting in unplayable network pings (> 300ms) for some players.

### Interview Tip (The "Strong Hire" Signal)
> *"We partition our matchmaking queues using latency buckets in Redis Sorted Sets, avoiding global sorting bottlenecks. We implement dynamic window expansion to progressively relax MMR matching criteria over time, and utilize Agones on Kubernetes to dynamically allocate game server instances at the edge, pushing configuration payloads to players via a stateful WebSocket connection tier."*
