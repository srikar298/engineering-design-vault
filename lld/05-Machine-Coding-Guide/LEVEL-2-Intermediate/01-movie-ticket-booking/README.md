# 🍿 Movie Ticket Booking System (BookMyShow)

> **Interview Time:** 35-45 Minutes | **Level:** SDE-2+ (Concurrency Focus)

## 📋 The Prompt
Design a movie ticket booking system where users can browse movies in different cities, select a cinema, and book specific seats for a show.

### 🛠️ Core Requirements
1.  **Search:** Users can search for movies by name and city.
2.  **Cinema Selection:** Select a cinema showing the movie and pick a specific time slot (Show).
3.  **Booking:** Select multiple seats and book them.
4.  **Concurrency (The Senior Bar):** Two users should NOT be able to book the same seat at the same time.

### ⚙️ Constraints & Invariants
-   A cinema has multiple halls; a hall has multiple seats.
-   Seats have types (Silver, Gold, Platinum) with different pricing.
-   A booking is atomic (either all seats are booked, or none).

---

## ✅ Self-Evaluation Checklist
- [ ] **Locking:** Did you handle the "Double Booking" race condition? (e.g., `synchronized`, `ReentrantLock`, or `ConcurrentHashMap`).
- [ ] **Separation of Concerns:** Is the `BookingService` decoupled from the `Cinema` data model?
- [ ] **State Management:** Does a seat know its own status, or is it managed by the `Show`? (Senior move: managed by the `Show` instance).
- [ ] **Clean API:** Did you use meaningful domain methods (`bookSeats()`) instead of raw data setters?

---

## 📂 Practice
Go to the `practice/` folder in your preferred language and start the 35-minute timer.
