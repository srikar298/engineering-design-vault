package collection;

import iterator.IIterator;
import model.Product;

/**
 * <h1>The Iterable Collection Interface</h1>
 * 
 * <p>Any collection that implements this promises it can hand out 
 * a standardized Iterator.
 */
public interface IAmazonInventory {
    void addProduct(Product p);
    IIterator createIterator();
}
