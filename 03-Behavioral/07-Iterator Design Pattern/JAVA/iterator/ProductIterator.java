package iterator;

import model.Product;
import java.util.List;

/**
 * <h1>The Concrete Iterator</h1>
 * 
 * <p>Contains the specific cursor tracking logic for traversing a standard List.
 * If the collection was a Tree, we would build a 'TreeDepthFirstIterator' here instead.
 */
public class ProductIterator implements IIterator {

    private final List<Product> products;
    private int cursor; // The internal tracking state!

    public ProductIterator(List<Product> products) {
        this.products = products;
        this.cursor = 0;
    }

    @Override
    public boolean hasNext() {
        return cursor < products.size();
    }

    @Override
    public Product getNext() {
        if (!hasNext()) {
            return null;
        }
        Product item = products.get(cursor);
        cursor++;
        return item;
    }
}
