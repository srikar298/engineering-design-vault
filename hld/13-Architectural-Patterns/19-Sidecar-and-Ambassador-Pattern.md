# Sidecar & Ambassador Pattern

| Field       | Value                          |
|-------------|--------------------------------|
| Concept IDs | C089, C100                     |
| Category    | Architectural Patterns         |
| Difficulty  | 🟡 Medium                      |
| Frequency   | 🔥 High                        |
| Tags        | `kubernetes`, `service-mesh`, `envoy`, `istio`, `sidecar`, `ambassador`, `observability`, `cross-cutting-concerns`, `mTLS` |

---

## 1. The Core Concept

### The Problem — Cross-Cutting Concerns at Scale

Imagine you have 50 microservices. They're written in 5 different languages: Java, Go, Python, Node.js, and Rust. Your platform team hands you a list of requirements every service must satisfy:

1. **Observability**: Emit distributed traces (Jaeger/Zipkin). Expose Prometheus metrics. Ship logs to Elasticsearch.
2. **Security**: Mutual TLS (mTLS) for all service-to-service communication. Automatic certificate rotation.
3. **Reliability**: Retries with exponential backoff. Timeouts. Circuit breaking. Bulkhead isolation.
4. **Traffic management**: Canary deployments. A/B traffic splitting. Request mirroring.
5. **Service discovery**: Dynamic resolution of service addresses. Health-check-based routing.

**The naive approach**: Add libraries to each service.
- `opentelemetry-java` in every Java service.
- `opentelemetry-go` in every Go service.
- `opentelemetry-python` in every Python service.
- Repeat for circuit breaking, mTLS, retries…

**The result of the naive approach:**

```
Problem: Cross-Cutting Concern Explosion

Service A (Java)      Service B (Go)       Service C (Python)
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│ Business Logic  │  │ Business Logic  │  │ Business Logic  │
│ ─────────────── │  │ ─────────────── │  │ ─────────────── │
│ OTel-Java       │  │ OTel-Go         │  │ OTel-Python     │
│ Hystrix         │  │ go-resilience   │  │ pybreaker       │
│ Bouncy Castle   │  │ crypto/tls      │  │ ssl/TLS module  │
│ Micrometer      │  │ prom/client_go  │  │ prometheus-py   │
│ Log4j + appender│  │ logrus + hook   │  │ logging + handler│
└─────────────────┘  └─────────────────┘  └─────────────────┘
     ↑                     ↑                     ↑
     Each service carries 5+ libraries for infra concerns.
     Version upgrades must happen in 50 repos simultaneously.
     New concern (e.g., mTLS v1.3) → 50 PRs.
     Bugs in infra code → scattered across 50 codebases.
```

**Problems with embedded libraries:**
| Problem | Impact |
|---------|--------|
| Language fragmentation | Each language needs its own library. Quality varies. |
| Version drift | Service A uses OTel 1.0, Service B uses 1.1. Inconsistent behavior. |
| Upgrade tax | Updating a security patch requires PRs across all 50 services. |
| Coupling | Business logic coupled to infra concerns. Violates Single Responsibility. |
| Coordination overhead | Platform team can't enforce policies centrally. |

**The Sidecar Pattern is the solution.**

---

## 2. Deep Dive — The Sidecar Pattern

### What is a Sidecar?

In Kubernetes, a **Pod** is the smallest deployable unit. A Pod can contain **multiple containers** that share:
- The same **network namespace** (they communicate via `localhost`).
- The same **IPC namespace** (shared memory segments).
- The same **mounted volumes** (shared filesystem).

The **sidecar pattern** deploys a second container alongside your main service container in the same Pod. This sidecar container handles all the cross-cutting infrastructure concerns. The main service container contains only business logic and communicates with the outside world via its local sidecar.

```
Kubernetes Pod — Sidecar Layout:

┌─────────────────────────────────────────────────────────────┐
│                        POD (shared network: 127.0.0.1)      │
│                                                             │
│  ┌──────────────────────┐    ┌──────────────────────────┐   │
│  │   Main Container     │    │    Sidecar Container     │   │
│  │  (Your Service)      │    │    (Envoy Proxy)         │   │
│  │ ────────────────── │    │ ────────────────────────  │   │
│  │  Business Logic      │    │  mTLS termination        │   │
│  │  HTTP on :8080       │    │  Distributed tracing     │   │
│  │  No infra code       │    │  Prometheus metrics      │   │
│  │  Pure domain logic   │    │  Retries + circuit break │   │
│  │                      │    │  Access logging          │   │
│  └──────────┬───────────┘    └──────────┬───────────────┘   │
│             │                           │                    │
│         localhost                    :15001 (inbound)        │
│         :8080                        :15001 (outbound)       │
└─────────────────────────────────────────────────────────────┘
                          │
                 External Traffic (from other services)
                 intercepted by sidecar via iptables rules
```

### How Traffic Interception Works (iptables Magic)

When Istio injects an Envoy sidecar into a Pod, it also runs an **init container** that configures `iptables` rules in the Pod's network namespace. These rules transparently redirect **all inbound and outbound traffic** through Envoy — without any changes to the main service container.

```
Traffic Interception via iptables:

Inbound traffic (from service mesh):
  External :80 → iptables → Envoy :15006
                                   |
                          [mTLS termination]
                          [request tracing]
                          [metrics increment]
                                   |
                             localhost:8080 → Main Container

Outbound traffic (to other services):
  Main Container → localhost:9090
                         |
                   iptables intercept
                         |
                   Envoy :15001 (outbound listener)
                         |
                [Service discovery: payment-svc → 10.0.1.45:8080]
                [Retries: 3 attempts, exponential backoff]
                [Circuit breaker: open if >50% errors in 30s]
                [mTLS: present client certificate]
                         |
                   Payment Service (actual endpoint)
```

**Key insight**: The main service container sees `localhost:9090` as the payment service. It has no knowledge of retries, mTLS, service discovery, or circuit breaking. Envoy handles all of this transparently.

### What the Sidecar Handles

**1. Mutual TLS (mTLS)**

Without mTLS, service-to-service traffic is plaintext. With mTLS, both sides of every connection authenticate with X.509 certificates. Managing certificate issuance, rotation, and validation across 50 services is operationally brutal if done in-service. The sidecar automates this:

- **Istiod** (Istio's control plane) acts as a Certificate Authority (CA).
- It issues short-lived certificates to each Envoy sidecar via the xDS API.
- Certificates auto-rotate every 24 hours.
- The main service container never sees a certificate. It talks to `localhost`.
- Envoy upgrades the plaintext `localhost` connection to mTLS when talking to other services.

**2. Distributed Tracing**

Every inbound request gets a trace context (W3C `traceparent` header or Zipkin `X-B3-TraceId`). Envoy propagates this context automatically to all outbound calls. The tracing backend (Jaeger, Zipkin, AWS X-Ray) receives spans from every Envoy in the mesh and assembles the full trace.

The main service doesn't need to instrument any tracing code — but it **does** need to propagate incoming trace headers to outbound calls. Most service meshes handle this, but some require the service to forward headers.

**3. Prometheus Metrics**

Envoy exposes a `/stats/prometheus` endpoint with hundreds of pre-built metrics:
- Request rate, error rate, latency histograms (p50, p90, p99).
- Active connections, connection errors.
- Upstream health check results.
- Circuit breaker state (open/closed/half-open).

Prometheus scrapes each sidecar. Service developers don't write `@Timed` annotations or custom metric collectors.

**4. Log Shipping**

A log-shipping sidecar (e.g., Fluentd, Filebeat) runs alongside the main container. It tails the container's log files or collects from stdout and ships to Elasticsearch, Splunk, or Datadog.

```
Log shipping sidecar:

Main Container
    |
    | writes logs to: /var/log/app/access.log
    |                  (shared volume)
    |
    ↓
Fluentd Sidecar Container
    |  reads from shared volume
    |  parses JSON or text logs
    |  enriches with pod metadata (namespace, deployment name)
    |  ships to Elasticsearch
    ↓
[Elasticsearch / Kibana]
```

**5. Health Checking and Readiness**

The sidecar can perform sophisticated health checks — including mTLS-enabled health probes — that Kubernetes' kubelet cannot perform natively. This allows the mesh to drain traffic from unhealthy pods before they fail liveness checks.

### Real-World Sidecar Implementations

| Product | Sidecar | What it Handles |
|---------|---------|-----------------|
| **Istio** | Envoy Proxy | mTLS, retries, circuit breaking, tracing, metrics, traffic shaping |
| **Datadog Agent** | Datadog Agent container | APM traces, custom metrics, logs, process monitoring |
| **Fluentd / Filebeat** | Log shipper sidecar | Log collection, parsing, enrichment, forwarding to ELK |
| **Vault Agent** | HashiCorp Vault Agent | Secret injection, certificate fetching, token renewal |
| **AWS App Mesh** | Envoy (AWS-managed) | mTLS, traffic routing, observability in ECS/EKS |
| **Linkerd** | Linkerd Proxy | Lightweight alternative to Envoy, mTLS, metrics |

---

## 3. Deep Dive — The Ambassador Pattern

### What is an Ambassador?

The **Ambassador** pattern is a **specialization of the Sidecar pattern** focused specifically on **outbound** service-to-service communication. The sidecar in ambassador mode acts as a local representative (ambassador) of the remote services your application needs to call.

**Core idea**: Instead of your service calling `payment-service.production.svc.cluster.local:8080` directly, it calls `localhost:8080`. The ambassador resolves the actual endpoint, applies reliability policies, and proxies the call.

```
Ambassador Pattern — Outbound Communication:

Your Service (main container)
    |
    | "I want to call the Payment Service"
    | HTTP POST localhost:8080/charge
    |
    ↓
Ambassador Sidecar
    |
    ├── Service Discovery: payment-service → 10.0.1.45:8080 (from K8s DNS)
    ├── Load Balancing: Round-robin across 3 replicas
    ├── Retry Policy: 3 retries, exponential backoff (100ms, 200ms, 400ms)
    ├── Circuit Breaker: If >50% of requests fail in 30s → open circuit
    ├── Timeout: Hard timeout of 2 seconds per attempt
    ├── mTLS: Present client certificate, verify server certificate
    └── Tracing: Add span to distributed trace
    |
    ↓
Payment Service (actual endpoint)
```

**Why "Ambassador"?** Like a country's ambassador who speaks on behalf of their government in a foreign land, the ambassador sidecar speaks on behalf of your service when communicating with external (remote) services. It handles the diplomacy — routing, authentication, retries — so your service just says what it wants done.

### Ambassador vs Generic Sidecar

| Dimension | Sidecar (Generic) | Ambassador (Specialized) |
|-----------|-------------------|--------------------------|
| Traffic direction | Inbound + Outbound | Primarily Outbound |
| Focus | All cross-cutting concerns | Service-to-service proxy |
| Use case | Full service mesh | Simplified outbound proxy |
| Examples | Istio/Envoy (full sidecar) | Azure Application Gateway, AWS App Mesh egress |

### Ambassador Pattern: Concrete Example with Kubernetes

```yaml
# Kubernetes Pod spec with Ambassador sidecar
apiVersion: v1
kind: Pod
metadata:
  name: order-service
spec:
  containers:
  # Main service container
  - name: order-service
    image: mycompany/order-service:1.2.0
    ports:
    - containerPort: 3000
    env:
    # Service talks to localhost — ambassador resolves the actual backend
    - name: PAYMENT_SERVICE_URL
      value: "http://localhost:8080"
    - name: INVENTORY_SERVICE_URL
      value: "http://localhost:8081"

  # Ambassador sidecar
  - name: envoy-ambassador
    image: envoyproxy/envoy:v1.28.0
    volumeMounts:
    - name: envoy-config
      mountPath: /etc/envoy
    args: ["-c", "/etc/envoy/envoy.yaml"]

  volumes:
  - name: envoy-config
    configMap:
      name: envoy-ambassador-config
```

```yaml
# Envoy ambassador config: routes localhost → actual services
# envoy.yaml (simplified)
static_resources:
  listeners:
  - name: payment_listener
    address:
      socket_address: { address: 127.0.0.1, port_value: 8080 }
    filter_chains:
    - filters:
      - name: envoy.filters.network.http_connection_manager
        typed_config:
          route_config:
            virtual_hosts:
            - name: payment_service
              routes:
              - match: { prefix: "/" }
                route:
                  cluster: payment_cluster
                  timeout: 2s
                  retry_policy:
                    retry_on: "5xx,connect-failure"
                    num_retries: 3

  clusters:
  - name: payment_cluster
    connect_timeout: 0.5s
    type: STRICT_DNS
    load_assignment:
      cluster_name: payment_cluster
      endpoints:
      - lb_endpoints:
        - endpoint:
            address:
              socket_address:
                address: payment-service.production.svc.cluster.local
                port_value: 8080
    transport_socket:
      name: envoy.transport_sockets.tls   # mTLS to upstream
```

---

## 4. Service Mesh — Sidecar at Scale

### What is a Service Mesh?

When **every** service in your cluster has an Envoy sidecar, those sidecars collectively form a **service mesh** — a dedicated infrastructure layer for service-to-service communication.

```
Service Mesh Topology (Istio):

        CONTROL PLANE
    ┌──────────────────────┐
    │       Istiod         │
    │ ─────────────────── │
    │  Pilot (xDS config) │
    │  Citadel (certs)    │  ← Pushes config to all Envoys
    │  Galley (validation)│
    └──────────┬───────────┘
               │ xDS API (gRPC)
    ┌──────────┴────────────────────────────────────────┐
    │                  DATA PLANE                        │
    │                                                    │
    │  Pod A                Pod B               Pod C    │
    │ ┌─────────┐          ┌─────────┐         ┌───────┐ │
    │ │App │Envoy│◄────────►│App │Envoy│◄───────►│App│Env│ │
    │ └─────────┘  mTLS    └─────────┘  mTLS   └───────┘ │
    │                                                    │
    │   All service-to-service traffic flows through     │
    │   Envoy sidecars. Control plane configures them.   │
    └────────────────────────────────────────────────────┘
```

### Control Plane vs Data Plane

| Component | Role | Examples |
|-----------|------|---------|
| **Control Plane** | Configures sidecars. Pushes routing rules, certificates, policies via xDS API. Does NOT handle request traffic. | Istiod (Istio), Linkerd control plane |
| **Data Plane** | Envoy proxies that actually handle request traffic. Enforce the policies set by the control plane. | Envoy (Istio), Linkerd proxy |
| **xDS API** | The protocol between control plane and sidecars. Envoy subscribes to LDS (listeners), RDS (routes), CDS (clusters), EDS (endpoints). | gRPC-based, Envoy-native |

### Service Mesh Capabilities

**1. Traffic Management**

```
Canary Deployment via Service Mesh (Istio VirtualService):

apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: payment-service
spec:
  http:
  - route:
    - destination:
        host: payment-service
        subset: v1     ← stable version
      weight: 90
    - destination:
        host: payment-service
        subset: v2     ← canary version
      weight: 10
```

Without a service mesh, you'd need to run 10x as many pods of v1 as v2 to achieve 90/10 splitting (coarse-grained). The service mesh achieves this at the routing layer with exactly 1 pod per version (fine-grained).

**2. Observability (The "Golden Signals")**

```
Envoy sidecar auto-generates metrics for all 4 golden signals:

Rate:     envoy_cluster_upstream_rq_total (requests per second)
Errors:   envoy_cluster_upstream_rq_5xx (5xx error rate)
Latency:  envoy_cluster_upstream_rq_time (histogram: p50, p99)
Saturation: envoy_cluster_upstream_cx_active (active connections)

All services emit these metrics with zero code changes.
```

**3. Zero-Trust Security via mTLS**

```
mTLS Authentication Flow in Istio:

Service A Envoy                               Service B Envoy
    │                                               │
    │──── TLS ClientHello ─────────────────────────>│
    │<─── TLS ServerHello + Certificate (B's cert) ─│
    │     [Verify B's cert: signed by Istiod CA?]   │
    │     [Extract B's SPIFFE identity]              │
    │──── Client Certificate (A's cert) ────────────>│
    │     [B verifies A's cert]                      │
    │     [Apply AuthorizationPolicy: is A allowed?] │
    │◄════ Encrypted Channel Established ════════════│
    │                                               │
    │  AuthorizationPolicy example:                 │
    │  "Only payment-service can call /charge"      │
    │  Enforced by Envoy, not by application code.  │
```

**4. Circuit Breaking**

```
Circuit Breaker States in Envoy:

Normal operation:         CLOSED  ←──────────────────┐
Request count: 1000/s                                  │
Error rate: 2%                                         │ Success threshold
                                                       │ met
Error rate spikes to 60%:                              │
→ Circuit OPENS          OPEN ───────────────── HALF-OPEN
  All requests fail-fast   ↑                       ↑
  No traffic to bad svc    │   Wait interval       │
  Return 503 immediately   └─── (30s) ─────────────┘
                                                   Test with 1 request
```

### Service Mesh Comparison

| Dimension | Istio (Envoy) | Linkerd (Linkerd Proxy) | AWS App Mesh | Consul Connect |
|-----------|---------------|-------------------------|--------------|----------------|
| Sidecar | Envoy | Linkerd Proxy (Rust) | Envoy | Envoy |
| Resource overhead | High (~150MB/sidecar) | Low (~10MB/sidecar) | Moderate | Moderate |
| Feature richness | ✅ Highest | ⚠️ Good | ⚠️ Good | ✅ High |
| Operational complexity | ❌ High | ✅ Low | ✅ Low (managed) | ⚠️ Moderate |
| Protocol support | HTTP/1, HTTP/2, gRPC, TCP | HTTP/1, HTTP/2, gRPC | HTTP/1, HTTP/2, gRPC | HTTP/1, HTTP/2, gRPC |
| mTLS | ✅ Auto | ✅ Auto | ✅ Auto | ✅ Auto |
| Best for | Large clusters, rich features | Simplicity, low overhead | AWS-native workloads | Multi-cloud, HashiCorp stack |

---

## 5. Pattern Comparison: Sidecar Specializations

| Pattern | Relationship | Traffic Direction | Primary Concern |
|---------|-------------|-------------------|-----------------|
| **Sidecar** (generic) | Same Pod, helper container | Both inbound and outbound | All cross-cutting concerns |
| **Ambassador** | Sidecar specialization | Outbound only | Remote service communication (retries, discovery, auth) |
| **Adapter** | Sidecar specialization | Inbound only | Normalize upstream data/format to main service format |
| **Service Mesh** | Sidecar at cluster scale | Both | Full observability, security, traffic management mesh |

### The Adapter Sidecar (Often Overlooked)

The adapter pattern (not to be confused with the GoF Adapter design pattern) is a sidecar that normalizes data **into** the main service:

```
Adapter Sidecar Example — Monitoring:

Monitoring System              Adapter Sidecar             Main Service
(Prometheus, pull)                                       (custom metrics format)
     │                               │                          │
     │── GET /metrics ──────────────>│                          │
     │                               │── GET /internal/stats -->│
     │                               │<── { "req_count": 42 } ──│
     │                               │                          │
     │                               │  [Convert to Prometheus  │
     │                               │   format:                │
     │                               │   myapp_requests_total 42]
     │<── Prometheus metrics ─────────│                          │
```

The main service exposes a non-standard metrics format. The adapter sidecar translates it to Prometheus format so the standard monitoring stack works. Zero changes to the main service.

---

## 6. Real-World Usage

### Istio at Lyft and Airbnb

**Lyft** was one of the earliest adopters of Envoy and helped drive its design. Their motivation: 150+ microservices in different languages, needing consistent observability and reliability. Embedding libraries in each was untenable. Envoy as a sidecar unified their networking layer.

**Airbnb** uses Istio/Envoy sidecars to enforce that all internal service communication is mTLS. Every service must present a valid SPIFFE identity. Services that don't have proper identity are blocked at the network layer — no application code changes needed to enforce this policy.

### Netflix Envoy Migration

Netflix moved from their homegrown JVM-based proxy library (Hystrix, Ribbon, Eureka in every service) to a sidecar-based model. The driver: they couldn't keep all services on the same version of Hystrix. A sidecar decoupled the infra version from the service version.

### Kubernetes Leader Election via Ambassador

The Kubernetes `kube-scheduler` and `kube-controller-manager` themselves use an ambassador-like pattern. Multiple replicas run, but each uses `client-go`'s leader election (backed by etcd). The "ambassador" for leader election is the `leaderelection` package — it handles all coordination so the scheduler binary only needs to call `OnStartedLeading()` and `OnStoppedLeading()`.

### Datadog Agent as Sidecar

In containerized environments, Datadog runs as a DaemonSet (one agent per node) or as a sidecar (one agent per pod for better isolation). The sidecar:
- Scrapes StatsD metrics sent by the main container to `localhost:8125`.
- Tails log files from a shared volume.
- Proxies APM traces received on `localhost:8126` to the Datadog backend.

The main service only needs to know: "send metrics to localhost:8125". All the Datadog-specific configuration, batching, and forwarding is the sidecar's responsibility.

---

## 7. SDE-2 Interview Script

**Interviewer: "How would you add observability to 50 microservices written in 5 different languages without changing their code?"**

---

**Opening — identify the constraint:**

> "The key constraint here is 'without changing their code' — which means we can't add per-language libraries. This is the classic cross-cutting concerns problem, and the canonical solution is the Sidecar pattern. Let me walk you through how I'd implement this."

**Explain the Sidecar pattern:**

> "In Kubernetes, a Pod can run multiple containers that share a network namespace. So I'd inject a sidecar container — specifically Envoy proxy — into every service's Pod. The sidecar intercepts all inbound and outbound traffic via iptables rules set up by an init container. The main service container just sees localhost. It has no idea Envoy is there."

**Address each observability concern:**

> "For distributed tracing, Envoy automatically creates spans for every inbound and outbound request and reports them to Jaeger or Zipkin. For metrics, Envoy exposes a Prometheus metrics endpoint with request rate, error rate, and latency histograms — the four golden signals — for every upstream and downstream service. For logging, I'd add a second sidecar — a Fluentd container — that tails the main container's logs from a shared volume and ships them to Elasticsearch. All of this happens with zero changes to any of the 50 services."

**Address security (bonus points):**

> "I'd use Istio's control plane — Istiod — to manage all the sidecars centrally. Istiod acts as a Certificate Authority and automatically issues and rotates X.509 certificates to every Envoy sidecar. This gives us mutual TLS across all 50 services for free. We can also enforce AuthorizationPolicies at the mesh level — 'only the order-service can call the payment-service's /charge endpoint' — without any code changes."

**Discuss trade-offs:**

> "The trade-off is resource overhead. Each Envoy sidecar adds about 50-150MB of memory and some CPU. At 50 services, that's meaningful. Linkerd is a lighter alternative — its Rust-based proxy is much smaller — but has fewer features. I'd evaluate whether Istio's richness justifies the overhead for our scale."

**Ambassador specialization:**

> "If the team wants something lighter than a full service mesh to start with, I'd suggest the Ambassador pattern — a sidecar that specifically handles outbound traffic. Services call localhost, the ambassador handles service discovery, retries, circuit breaking, and mTLS. This gets us most of the reliability benefits without the full control plane overhead of a service mesh."

---

## 8. SDE-2+ Readiness Checklist

- [ ] Can explain why embedding infra libraries in each service fails at scale (version drift, upgrade tax, language fragmentation).
- [ ] Can define the Sidecar pattern: same Pod, shared network namespace, cross-cutting concerns in helper container.
- [ ] Knows how iptables rules transparently intercept traffic without main container changes.
- [ ] Can explain what Envoy proxy handles: mTLS, retries, circuit breaking, tracing, metrics.
- [ ] Can describe the mTLS flow in Istio: Istiod as CA, automatic certificate issuance and rotation, SPIFFE identities.
- [ ] Can draw the Service Mesh topology: control plane (Istiod) vs data plane (Envoy proxies).
- [ ] Knows what xDS API is: the protocol Istiod uses to push config to Envoy (LDS, RDS, CDS, EDS).
- [ ] Can explain the Ambassador pattern as a specialization of Sidecar for outbound communication.
- [ ] Can give a concrete Ambassador example: service calls localhost, ambassador resolves real endpoint.
- [ ] Knows the Adapter sidecar pattern: normalizes inbound data formats (e.g., custom metrics → Prometheus).
- [ ] Can compare Istio vs Linkerd: richness vs resource overhead.
- [ ] Can explain circuit breaker states (Closed, Open, Half-Open) and how Envoy enforces them.
- [ ] Knows traffic splitting in Istio VirtualService and why it's more precise than replica-count-based splitting.
- [ ] Can name real examples: Lyft (Envoy origin), Netflix (Hystrix → sidecar), Datadog agent sidecar.
- [ ] Can articulate the resource trade-off: ~100MB per sidecar × N services.
- [ ] Can explain the four golden signals (Rate, Errors, Latency, Saturation) and how sidecars expose them automatically.
- [ ] Can explain why the sidecar model enables polyglot architecture — Java, Go, Python all get the same infra capabilities.
- [ ] Understands that service headers (like W3C `traceparent`) still need to be propagated by the main service for full trace continuity, even with a sidecar.
