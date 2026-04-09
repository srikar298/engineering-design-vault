# 📡 12 - Observability & Ops (SDE-2+ Excellence)

You can't fix what you can't measure. Observability is about understanding the internal state of a system by looking at its outputs.

---

## 📊 1. The Three Pillars

| Pillar | Purpose | Tools |
| :--- | :--- | :--- |
| **Metrics** | Numerical data over time (aggregatable). | Prometheus, Grafana, Datadog. |
| **Logging** | Discrete events (textual). | ELK Stack (Elastic, Logstash, Kibana), Loki. |
| **Tracing** | Following a request across service boundaries. | Jaeger, Zipkin, AWS X-Ray. |

---

## ⚖️ 2. SLIs, SLOs, and SLAs

- **SLI (Service Level Indicator)**: The actual metric (e.g., "Latency is 150ms").
- **SLO (Service Level Objective)**: The target goal (e.g., "99% of requests must be < 200ms").
- **SLA (Service Level Agreement)**: The legal contract (e.g., "If availability drops below 99.9%, we refund the customer").

---

## 🚨 3. Monitoring & Alerting

- **Pull-based (Prometheus)**: Server scrapes metrics from the app.
- **Push-based (CloudWatch)**: App sends metrics to the server.
- **Alert Fatigue**: Only alert on **Symptoms** (e.g., "High Error Rate"), not **Causes** (e.g., "CPU is 90%"). High CPU might be normal during a peak, but high errors are always bad.

---

## 🚀 The SDE-2 Interview Tip
When asked about debugging a slow request in a microservices environment, say: **"I would use Distributed Tracing with a unique Correlation ID to follow the request through the API Gateway, Auth Service, and DB, identifying exactly which component is the bottleneck."**
