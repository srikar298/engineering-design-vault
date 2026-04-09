package pizza;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <h1>Pizza — The Product (Builder Pattern)</h1>
 *
 * <p>A Pizza has two required fields (name, crust) and several optional ones
 * (toppings, size, extraCheese, vegan). The Builder prevents a telescoping
 * constructor and makes construction readable and safe.
 */
public final class Pizza {

    public enum Crust  { THIN, THICK, STUFFED }
    public enum Size   { SMALL, MEDIUM, LARGE, XL }

    // Required
    private final String name;
    private final Crust  crust;

    // Optional
    private final Size          size;
    private final List<String>  toppings;
    private final boolean       extraCheese;
    private final boolean       vegan;

    private Pizza(Builder b) {
        this.name        = b.name;
        this.crust       = b.crust;
        this.size        = b.size;
        this.toppings    = Collections.unmodifiableList(new ArrayList<>(b.toppings));
        this.extraCheese = b.extraCheese;
        this.vegan       = b.vegan;
    }

    @Override
    public String toString() {
        return String.format(
            "Pizza{name='%s', size=%s, crust=%s, toppings=%s, extraCheese=%s, vegan=%s}",
            name, size, crust, toppings, extraCheese, vegan);
    }

    // ── Accessors ───────────────────────────────────────────────────────────
    public String       getName()        { return name; }
    public Crust        getCrust()       { return crust; }
    public Size         getSize()        { return size; }
    public List<String> getToppings()    { return toppings; }
    public boolean      hasExtraCheese() { return extraCheese; }
    public boolean      isVegan()        { return vegan; }

    // ── Builder ─────────────────────────────────────────────────────────────
    public static final class Builder {

        // Required
        private final String name;
        private final Crust  crust;

        // Optional — defaults
        private Size         size        = Size.MEDIUM;
        private List<String> toppings    = new ArrayList<>();
        private boolean      extraCheese = false;
        private boolean      vegan       = false;

        public Builder(String name, Crust crust) {
            if (name  == null || name.isBlank())  throw new IllegalArgumentException("name required");
            if (crust == null)                    throw new IllegalArgumentException("crust required");
            this.name  = name;
            this.crust = crust;
        }

        public Builder size(Size size)           { this.size = size;                  return this; }
        public Builder topping(String topping)   { this.toppings.add(topping);        return this; }
        public Builder extraCheese()             { this.extraCheese = true;           return this; }
        public Builder vegan()                   { this.vegan = true;                 return this; }

        public Pizza build()                     { return new Pizza(this); }
    }
}
