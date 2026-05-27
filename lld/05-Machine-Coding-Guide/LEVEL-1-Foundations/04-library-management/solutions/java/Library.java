package library;

import java.util.*;

public class Library {
    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, BookStatus> inventory = new HashMap<>();

    public void addBook(Book b) {
        books.put(b.getTitle(), b);
        inventory.put(b.getTitle(), BookStatus.AVAILABLE);
    }

    /** [INTERVIEW_MVP]: Search by title */
    public List<Book> search(String query) {
        List<Book> results = new ArrayList<>();
        for (Book b : books.values()) {
            if (b.getTitle().contains(query)) results.add(b);
        }
        return results;
    }

    /** [PRODUCTION_ENHANCEMENT]: Atomic Borrow Logic */
    public synchronized boolean borrowBook(String title, Member m) {
        if (inventory.get(title) == BookStatus.AVAILABLE) {
            inventory.put(title, BookStatus.LOANED);
            System.out.println("✅ " + title + " borrowed successfully.");
            return true;
        }
        System.out.println("❌ " + title + " is not available.");
        return false;
    }
}
