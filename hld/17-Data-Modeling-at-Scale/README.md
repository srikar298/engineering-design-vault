# 📊 17 - Data Modeling at Scale

## 📖 1. The Concept
As a Founding Engineer, the most important decision you make isn't the DB tool, but the **Data Schema**. How you structure your rows determine if you can scale horizontally.

---

## 📊 2. The SDE-2 Trade-off Table: Normalization vs. Denormalization

| Feature | Normalized (3NF) | Denormalized (Flat) |
| :--- | :--- | :--- |
| **Storage** | Efficient (No redundancy). | Inefficient (Data repeated). |
| **Read Speed** | Slower (Requires JOINs). | Faster (No JOINs). |
| **Write Speed** | Faster (One place to update). | Slower (Must update multiple places). |
| **Consistency** | **Strong**. | **Risk of Inconsistency**. |

---

## 🏗️ 3. Modeling for NoSQL (DynamoDB/Cassandra)
In SQL, you model based on **Relationships**. In NoSQL, you model based on **Access Patterns**.
1.  **Understand Queries First**: You can't just "join later."
2.  **GSI (Global Secondary Indexes)**: How to query by a non-primary key.
3.  **Composite Keys**: Partition Key + Sort Key.

---

## 🚀 4. The SDE-3 Edge: Fan-out Strategies
When User A posts a tweet to 1 million followers:
- **Fan-out on Write**: Push the tweet to every follower's "Inbox" (Redis).
    - *Pro:* Read is $O(1)$.
    - *Con:* Celebrity writes are $O(N)$ and slow.
- **Fan-out on Read**: Don't push. When a follower logs in, query all people they follow.
    - *Pro:* Write is $O(1)$.
    - *Con:* Read is $O(N)$ and slow.
- **The Hybrid (The Winner):** Use Fan-out on write for regular users and Fan-out on read for "Celebrities" (Hot Keys).
