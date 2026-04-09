package composite;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>05 - Composite: The "Tree Orchestrator" (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> An e-commerce catalog. Items can be standalone (Laptop) 
 * or Bundles (WorkFromHome Kit: Laptop + Mouse + Monitor). The 
 * <code>OrderService</code> should treat them all uniformly.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Uniformity vs Safety:</b> I put <code>getPrice()</code> in the base interface 
 *    (Uniformity) but keep <code>add/remove</code> in the Composite class 
 *    (Safety - prevents calling 'add' on a single Product).
 * 2. <b>Recursion:</b> The pattern relies on recursive delegation. 
 * 3. <b>OCP Mastery:</b> Add a new "Holiday Bundle" without changing the 
 *    <code>Cart</code> or <code>Order</code> logic.
 * 
 * <b>Edge Cases Handled:</b>
 * - <b>Empty Bundles:</b> Correctly returns $0.
 * - <b>Nested Bundles:</b> Handles bundles inside other bundles (Infinite recursion).
 * - <b>Price Updates:</b> Changes in leaf prices reflect in the parent total.
 */

interface CatalogItem {
    double getPrice();
    void display(String indent);
}

class Product implements CatalogItem {
    private final String name;
    private final double price;
    public Product(String n, double p) { this.name = n; this.price = p; }
    @Override public double getPrice() { return price; }
    @Override public void display(String indent) {
        System.out.println(indent + "- " + name + ": $" + price);
    }
}

class Bundle implements CatalogItem {
    private final String bundleName;
    private final List<CatalogItem> items = new ArrayList<>();

    public Bundle(String n) { this.bundleName = n; }
    public void addItem(CatalogItem item) { items.add(item); }

    @Override
    public double getPrice() {
        // --- [INTERVIEW_MVP] (Recursive Aggregation) ---
        return items.stream().mapToDouble(CatalogItem::getPrice).sum();
    }

    @Override
    public void display(String indent) {
        // --- [PRODUCTION_ENHANCEMENT] (Hierarchical View) ---
        System.out.println(indent + "+ Bundle: " + bundleName + " (Total: $" + getPrice() + ")");
        for (CatalogItem item : items) {
            item.display(indent + "  ");
        }
    }
}

public class CompositePragmaticSDE2 {
    public static void main(String[] args) {
        Product mouse = new Product("Gamer Mouse", 50.0);
        Product laptop = new Product("ASUS ROG", 1500.0);
        
        Bundle pcKit = new Bundle("PC Essentials");
        pcKit.addItem(mouse);
        pcKit.addItem(laptop);

        Bundle officeSetup = new Bundle("Ultimate Office Setup");
        officeSetup.addItem(pcKit); // Nested Bundle
        officeSetup.addItem(new Product("4K Monitor", 400.0));

        officeSetup.display("");
    }
}
