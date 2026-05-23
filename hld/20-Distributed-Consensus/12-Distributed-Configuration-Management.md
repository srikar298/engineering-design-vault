# ⚡ 12 - Distributed Configuration Management

## 📋 Tracker Metadata
| Column | Value / Status |
| :--- | :--- |
| **Concept ID** | C088 |
| **Category** | Distributed Consensus |
| **Difficulty** | 🟡 Medium |
| **Interview Frequency** | 🔥 High |
| **Understanding** | [🔴 None / 🟡 Conceptual / 🟢 Applied] |
| **Can Explain** | [ ] Yes / [ ] No |
| **Whiteboard Drawn** | [ ] Yes / [ ] No |
| **Taught Someone** | [ ] Yes / [ ] No |
| **Next Review** | YYYY-MM-DD |
| **Mastery** | [🔴 Familiar / 🟡 Competent / 🟢 Expert] |

---

## ⚡ 1. The Core Definition & Trigger
*   **Two-Sentence Trigger:** Distributed Configuration Management is a pattern that establishes a centralized, highly consistent, and fault-tolerant store for critical application metadata, dynamic configuration values, and active service endpoints. An architect triggers this choice when coordinating thousands of independent microservices that require real-time configuration updates, feature flagging, or dynamic service discovery, using consensus protocols like Raft or Paxos to prevent conflicting states or race conditions.
*   **Scalability Dimension:** Primary: **Read Throughput** (via client-side local caching and follower reads) vs **Write Latency** (bounded by network roundtrips required to reach quorum agreement). Secondary: **Active Watcher Connection Capacity** (HTTP/2 stream limits) and total database size.

---

## ⚖️ 2. Trade-offs & Deep Dive

### System Architecture: The Quorum and the Watcher Client
Distributed configuration stores (like `etcd`, `ZooKeeper`, or `Consul`) prioritize consistency and partition tolerance (CP in the CAP theorem). They organize themselves into clusters where a single Leader handles writes, replicates logs to Followers, and broadcasts changes to client nodes.

```
       [Client App 1]                          [Client App 2]
    (Subscribed via Watch)                 (Subscribed via Watch)
              │                                      │
              └─────────────── gRPC Streams ─────────┴──────────────┐
                                                                    ▼
 ┌──────────────────────┐      Raft Proposal Log Replication     ┌──────────────────────┐
 │ etcd Follower Node 1 │ ◄────────────────────────────────────► │  etcd Leader Node    │
 └──────────────────────┘                                        └──────────┬───────────┘
                                                                            │ (Writes WAL Log to disk)
                                                                            ▼
                                                                  ┌──────────────────┐
                                                                  │ Write-Ahead Log  │
                                                                  │      (WAL)       │
                                                                  └──────────────────┘
```

#### Core Components and Mechanisms
1.  **Write-Ahead Log (WAL) and MVCC Storage Engine:** Writes (such as configuration changes) are proposed to the Leader. The Leader writes the transaction to a local Write-Ahead Log (WAL), replicates it to followers, and commits it once a quorum ($N/2 + 1$) agrees. The storage engine typically uses Multi-Version Concurrency Control (MVCC) or LSM-Trees to maintain historical revisions of configurations, allowing clients to roll back safely.
2.  **Dynamic Watchers (gRPC/HTTP2):** Instead of clients polling the database, they establish a long-running gRPC connection to watch specific keys or prefixes. When a key is updated, the cluster immediately pushes the change down the open TCP channel to all active client watchers.
3.  **Ephemeral Sessions and Leases:** Service discovery relies on heartbeats. A microservice registers its IP address under a key bound to a "Lease" (e.g., a TTL of 10 seconds). The service must periodically send heartbeats to renew the lease. If the service crashes or becomes partitioned, the heartbeat stops, the lease expires, and the cluster deletes the key, instantly notifying all watchers that the service is offline.
4.  **Atomic Transactions (Compare-And-Swap):** Essential for distributed locks and configuration updates. Allows writes only if the current key version matches the client's expected version, preventing overlapping write operations.

---

### Comparison of Configuration Stores
| Feature / Tool | etcd | Apache ZooKeeper | HashiCorp Consul | Redis (for comparison) |
| :--- | :--- | :--- | :--- | :--- |
| **Consensus Protocol** | Raft | Zab (ZooKeeper Atomic Broadcast) | Raft | None (uses Master-replica replication). |
| **API Interface** | gRPC / HTTP/2 | Custom Java/C client | HTTP REST / DNS | TCP RESP protocol |
| **Data Hierarchy** | Flat key-value with directory-like prefixes. | Hierarchical directory tree (znodes). | Flat key-value, plus built-in Service Discovery framework. | Key-Value, Hash, Lists, Sets. |
| **Consistency Class** | Strong (Quorum-based). | Strong (Linearizable writes, FIFO client order). | Strong (Quorum-based, supports stale read options). | Eventual (risk of lost writes during failover). |
| **Best Use Case** | Kubernetes cluster metadata, feature flags. | Hadoop, Kafka coordination (legacy). | Multi-cloud service discovery, DNS routing. | High-speed cache (not recommended for consensus configs). |

---

## 💥 3. Resiliency & Operations

*   **Observability (The "Signal"):**
    *   `Raft Leader Elections Count`: Spikes in elections indicate network partitions or high CPU steal time on nodes, rendering the configuration store temporarily read-only.
    *   `Active Watchers Count / Client Connection Churn`: High churn on watcher connections indicates client-side networking issues or unstable application container deployments.
    *   `Disk Sync Duration (p99)`: The time taken to write the WAL to non-volatile storage. Since etcd/Consul blocks commits until the WAL is flushed to disk, slow disk I/O immediately increases write latency.
*   **Blast Radius (The "Impact"):**
    *   If the configuration store cluster loses quorum (e.g., 3 of 5 nodes crash), it stops accepting write updates, preventing new microservices from registering or configuration values from being altered.
    *   *Mitigation:* Client SDKs must implement **local memory caching** of configuration values. If the cluster goes offline, clients continue using their last-known-good configuration stored in RAM, preventing a total system outage.

---

## 🚫 4. Interview Playbook

### Common Mistakes (The "Junior" Signals)
*   **Treating the Store like a Database:** Recommending `etcd` or `ZooKeeper` to store large application database objects, user profile photos, or massive log payloads. These tools load the entire key space into RAM; storing large payloads will trigger out-of-memory crashes. Keep total data size < 2-5 GB.
*   **Failing to Cache Locally:** Querying the config store cluster on every incoming user API request. This degrades cluster performance. Clients must load configurations once at boot, establish a Watch, and maintain the state in memory.
*   **Omitting Lease Renewal Heartbeats:** Not scheduling lease renewals in background threads, causing healthy services to be prematurely deregistered due to CPU starvation on the application client.

### Interview Tip (The "Strong Hire" Signal)
> "When designing a configuration engine for our global microservices fleet, we separate the control plane from the data plane. We use etcd to host dynamic configurations and feature flags, but clients do not query the etcd cluster directly for every request. Instead, at startup, each client service fetches the initial config and establishes a gRPC Watch stream to update a local thread-safe memory map. If the etcd cluster experiences a network partition, client nodes continue operating on their local cached copy, degrading gracefully rather than crashing."

---

## 💡 5. My Custom Study Notes & Whiteboard

### Managing Configuration with `etcdctl` (CLI Examples)
Below are the standard shell commands used to interact with etcd for configuration management, key watching, and lease allocation.

```bash
# 1. Store a configuration parameter
etcdctl put /config/payment_service/max_retries "5"

# 2. Retrieve a configuration parameter
etcdctl get /config/payment_service/max_retries

# 3. Create a 30-second TTL Lease for service discovery registration
etcdctl lease grant 30
# Output: lease 694d57142b6a550c granted with TTL(30s)

# 4. Bind a service endpoint key to the lease
etcdctl put --lease=694d57142b6a550c /services/payment/node-1 "10.0.1.55:8080"

# 5. Keep the lease alive (run as a background heartbeat daemon process)
etcdctl lease keep-alive 694d57142b6a550c

# 6. Watch a prefix folder for dynamic updates in real-time
etcdctl watch /config/payment_service/ --prefix
```

### Python Mock: Local Config Cache with Watcher Fallback
This client-side code loads configurations into memory, listens for updates using a background thread, and relies on cached values if the connection is interrupted.

```python
import time
import threading
from typing import Dict, Any

# Mock etcd client
class MockEtcdClient:
    def __init__(self):
        self.store = {"/config/rate_limit": "100", "/config/maintenance_mode": "false"}
        self.watchers = []

    def get(self, key: str) -> str:
        # Simulate network read
        return self.store.get(key, "")

    def put(self, key: str, value: str):
        self.store[key] = value
        for watcher_cb in self.watchers:
            watcher_cb(key, value)

    def register_watcher(self, callback):
        self.watchers.append(callback)

# Client Application Wrapper
class ConfigCacheManager:
    def __init__(self, etcd_client: MockEtcdClient):
        self.client = etcd_client
        self.cache: Dict[str, str] = {}
        self.lock = threading.Lock()
        self.cluster_available = True

        # Initialize Cache
        self._load_initial_config()
        # Start background watcher
        self.client.register_watcher(self._on_config_update)

    def _load_initial_config(self):
        with self.lock:
            try:
                # Load key variables
                self.cache["rate_limit"] = self.client.get("/config/rate_limit")
                self.cache["maintenance_mode"] = self.client.get("/config/maintenance_mode")
                print(f"[BOOT] Config Loaded: {self.cache}")
            except Exception as e:
                print(f"[ERROR] Failed to connect to config store: {e}")
                self.cluster_available = False

    def _on_config_update(self, key: str, value: str):
        # Callback triggered by etcd stream push
        with self.lock:
            cache_key = key.split("/")[-1]
            self.cache[cache_key] = value
            print(f"[WATCHER ALERT] Config updated. {cache_key} set to {value}")

    def get_config(self, key: str) -> str:
        with self.lock:
            # Fallback to local memory cache ensures high availability (stale read capability)
            return self.cache.get(key, "")

# Demo execution
if __name__ == "__main__":
    etcd = MockEtcdClient()
    config_manager = ConfigCacheManager(etcd)

    # 1. Normal read from memory cache
    print(f"Current Rate Limit: {config_manager.get_config('rate_limit')}")

    # 2. Dynamic Update happens on the cluster
    time.sleep(1)
    etcd.put("/config/rate_limit", "250")

    # 3. Read value again (should reflect update pushed via watch)
    time.sleep(0.5)
    print(f"Updated Rate Limit: {config_manager.get_config('rate_limit')}")
```
