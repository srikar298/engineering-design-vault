package library;

public class Book {
    private final String isbn;
    private final String title;
    private final String author;

    public Book(String i, String t, String a) {
        this.isbn = i;
        this.title = t;
        this.author = a;
    }

    public String getTitle() {
        return title;
    }
}
