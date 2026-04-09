package iterator;

import model.Product;

/**
 * <h1>The Iterator Interface</h1>
 * 
 * <p>Standardizes how to traverse ANY collection without knowing 
 * whether it's an Array, a LinkedList, or a Graph under the hood.
 */
public interface IIterator {
    boolean hasNext();
    Product getNext();
}
