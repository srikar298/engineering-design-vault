# ⚡ TypeScript & Node.js Core Insights

Deep-dive technical insights for building enterprise-grade applications with TypeScript.

## 1. The Event Loop & Concurrency
Understanding that Node.js is "Single-threaded" but not "Single-tasked".
- **Microtasks vs Macrotasks**: `process.nextTick` and `Promise.then` execute before `setTimeout`.
- **Worker Threads**: When to offload CPU-intensive tasks (e.g., cryptography, image processing) to avoid blocking the loop.

## 2. Advanced Type System Patterns
- **Branded Types (Nominal Typing)**: Ensuring a `UserId` cannot be accidentally used as an `OrderId` even if both are strings.
  ```typescript
  type Brand<K, T> = K & { __brand: T };
  type UserId = Brand<string, "UserId">;
  ```
- **Discriminated Unions**: The "Gold Standard" for handling API responses and state machines.

## 3. Decorators & Metadata (NestJS Context)
- How Reflection works in TS.
- Building custom decorators for Cross-cutting concerns (Logging, Validation, Auth).

## 4. Performance Pitfalls
- **Memory Leaks**: Closure-bound variables and event listener accumulation.
- **Hidden Classes**: Why adding properties to objects dynamically slows down the V8 engine.
