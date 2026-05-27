package library;

public class LibrarySolution {
    public static void main(String[] args) {
        Library lib = new Library();
        lib.addBook(new Book("123", "LLD Mastery", "Senior Dev"));
        
        Member m1 = new Member("M1", "Alice");
        lib.borrowBook("LLD Mastery", m1);
        lib.borrowBook("LLD Mastery", m1); // Should fail
    }
}
