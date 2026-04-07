package iterator;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>07 - Iterator: The "Traversal Encapsulator" (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> A Social Media Feed. 
 * The data might be stored in a <code>List</code>, a <code>Set</code>, or 
 * even fetched from a Database page-by-page. The UI doesn't care. 
 * It just wants to "scroll down."
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Hiding Complexity:</b> The Iterator allows you to change the underlying 
 *    collection from an ArrayList to a Custom Graph without touching the UI code.
 * 2. <b>Lazy Evaluation:</b> Iterators are perfect for <b>Pagination</b>. 
 *    The <code>next()</code> call can trigger a network request to fetch the 
 *    next 10 items only when needed.
 * 3. <b>Fail-Fast:</b> Standard Java iterators throw <code>ConcurrentModificationException</code> 
 *    if the collection is modified during iteration. A senior engineer knows 
 *    how to handle this using <b>Snapshots</b> or <b>Concurrent Collections</b>.
 * 
 * <b>Edge Cases Handled:</b>
 * - <b>Empty Collections:</b> <code>hasNext()</code> returns false immediately.
 * - <b>End of Stream:</b> <code>next()</code> throws NoSuchElementException if called at the end.
 */

class Post {
    public final String content;
    public Post(String c) { this.content = c; }
}

// --- ITERATOR INTERFACE ---
interface FeedIterator {
    boolean hasMore();
    Post getNext();
}

// --- CONCRETE ITERATOR ---
class ListFeedIterator implements FeedIterator {
    private final List<Post> posts;
    private int position = 0;

    public ListFeedIterator(List<Post> p) { this.posts = p; }

    @Override
    public boolean hasMore() { return position < posts.size(); }

    @Override
    public Post getNext() {
        if (!hasMore()) return null;
        return posts.get(position++);
    }
}

// --- COLLECTION INTERFACE ---
interface SocialFeed {
    FeedIterator createIterator();
}

class UserFeed implements SocialFeed {
    private final List<Post> posts = new ArrayList<>();

    public void addPost(String msg) { posts.add(new Post(msg)); }

    @Override
    public FeedIterator createIterator() {
        // [PRODUCTION_ENHANCEMENT]: We could pass a copy of the list 
        // to make the iterator thread-safe (Snapshot).
        return new ListFeedIterator(posts);
    }
}

public class IteratorPragmaticSDE2 {
    public static void main(String[] args) {
        UserFeed myFeed = new UserFeed();
        myFeed.addPost("Hello SDE-2!");
        myFeed.addPost("Patterns are cool.");

        // [INTERVIEW_MVP]: Standard Traversal
        FeedIterator it = myFeed.createIterator();
        while (it.hasMore()) {
            System.out.println("Feed: " + it.getNext().content);
        }
        
        System.out.println("✅ Traversal complete. Underlying List was hidden.");
    }
}
