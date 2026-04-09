# 🛡️ Case Study: Proxy in the Real World

## Where is it used in our repository?

The Proxy pattern is a foundational security and performance mechanism. We utilize two different types of Proxies (Caching, and Protection) in our capstone architecture:

### 1. 🛒 E-Commerce Checkout Engine (`08-Combined-Patterns`)
**Type: Caching Proxy**
During checkout, we must verify if the items are in stock by querying the `DatabaseInventoryService`. Hitting the database on every single cart addition would destroy performance. Instead, we inject an `InventoryProxy`. This proxy catches the request, checks its fast local HashMap cache, and if the data is fresh, returns it immediately. It only delegates back to the slow Database if the cache misses. The client has no idea it talked to a cache.

### 2. 📁 Cloud Storage API (`09-LLD-Problems/01-cloud-storage`)
**Type: Protection Proxy**
We have a massive recursive `Composite` tree of files and folders (similar to AWS S3). When a user requests to delete a folder, we cannot just execute `.delete()`. We wrap all folders in an `AuthProxy`. The Proxy intercepts the `.delete()` call, validates the user's JWT token, and throws an `AccessDeniedException` if they lack permissions, completely shielding the core `Directory` object.

## Key Senior Takeaway
**Proxies intercept, they don't enhance.** Unlike Decorators which stack behaviors, Proxies act as gatekeepers. Use them heavily for Cross-Cutting Concerns: Caching, Authentication, Lazy Loading, and Logging, ensuring your core domain business classes never know about HTTP headers or JWTs.
