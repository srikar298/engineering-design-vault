package collection;

import iterator.IIterator;
import iterator.ProductIterator;
import model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>The Concrete Collection</h1>
 * 
 * <p>Internally, this uses an ArrayList. But the client will NEVER know that, 
 * because they only interact with the Iterator.
 */
public class WarehouseInventory implements IAmazonInventory {
    
    // The underlying data structure is totally hidden from the Client
    private final List<Product> productList;

    public WarehouseInventory() {
        this.productList = new ArrayList<>();
    }

    @Override
    public void addProduct(Product p) {
        productList.add(p);
    }

    @Override
    // Factory Method!
    public IIterator createIterator() {
        return new ProductIterator(this.productList);
    }
}
