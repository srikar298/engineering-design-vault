package addons.cache.decomposed;

/**
 * Custom Generic Doubly Linked List implementation to support O(1) recency updates and evictions.
 * This class is not thread-safe on its own and relies on external synchronization.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class DoublyLinkedList<K, V> {
    private final Node<K, V> head;
    private final Node<K, V> tail;
    private int size;

    public DoublyLinkedList() {
        // Initialize sentinels
        head = new Node<>(null, null);
        tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    /**
     * Adds the node to the head (Most Recently Used position).
     */
    public void addToHead(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
        node.active = true;
        size++;
    }

    /**
     * Removes the specified node from the doubly linked list.
     */
    public void removeNode(Node<K, V> node) {
        if (node == null || node == head || node == tail) {
            return;
        }
        if (node.prev != null && node.next != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.prev = null;
            node.next = null;
            node.active = false;
            size--;
        }
    }

    /**
     * Moves an existing active node to the head of the list.
     */
    public void moveToHead(Node<K, V> node) {
        if (node == null || node == head || node == tail) {
            return;
        }
        // Only move if the node is active and not already at the head
        if (node.active && head.next != node) {
            // Unlink
            node.prev.next = node.next;
            node.next.prev = node.prev;
            
            // Relink at head
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }
    }

    /**
     * Removes and returns the tail node (Least Recently Used element).
     * Returns null if the list is empty.
     */
    public Node<K, V> removeTail() {
        if (isEmpty()) {
            return null;
        }
        Node<K, V> tailNode = tail.prev;
        removeNode(tailNode);
        return tailNode;
    }

    /**
     * Checks if the list is empty (contains only sentinels).
     */
    public boolean isEmpty() {
        return head.next == tail;
    }

    /**
     * Returns the size of the list.
     */
    public int size() {
        return size;
    }

    /**
     * Clears the list by detaching all elements and resetting sentinels.
     */
    public void clear() {
        // Traverse and deactivate all nodes to prevent concurrent get threads from accessing them
        Node<K, V> current = head.next;
        while (current != tail) {
            current.active = false;
            Node<K, V> next = current.next;
            current.prev = null;
            current.next = null;
            current = next;
        }
        head.next = tail;
        tail.prev = head;
        size = 0;
    }
}
