# 🐹 Go (Golang) Core Insights

High-performance engineering patterns and idiomatic Go.

## 1. Concurrency Primitives
- **Goroutines**: Why they are "cheap" (2KB stack) but not "free".
- **Channels**: "Don't communicate by sharing memory; share memory by communicating."
- **Select Statement**: Pattern for non-blocking communication and timeouts.

## 2. Memory Management & Escape Analysis
- **Heap vs Stack**: Understanding when a variable "escapes" to the heap (pointers, large structs).
- **Zero-allocation patterns**: Using `sync.Pool` for frequent allocations.

## 3. Implicit Interfaces
- Why Go's interfaces are powerful for decoupled architecture.
- **Consumer-side interfaces**: Define interfaces where they are *used*, not where they are *implemented*.

## 4. Error Handling (The Go Way)
- Why errors are values.
- **Wrapping and Unwrapping**: Using `%w` in `fmt.Errorf` for context without losing the original error.
