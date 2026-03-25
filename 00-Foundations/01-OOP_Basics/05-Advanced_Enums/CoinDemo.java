/**
 * Represents U.S. coins and their denominations.
 * This demonstrates a "Rich Enum" -> An Enum that has State and Behavior.
 */
enum Coin {
    PENNY(1),
    NICKEL(5),
    DIME(10),
    QUARTER(25);

    // State: the value in cents
    private final int value;

    // Constructor: Assigns the state to the Enum instance
    Coin(int value) {
        this.value = value;
    }

    // Behavior: Retrieving the state
    public int getValue() {
        return value;
    }
}

public class CoinDemo {
    public static void main(String[] args) {
        // Enums eliminate the need for lookup tables/maps.
        int total = Coin.DIME.getValue() + Coin.QUARTER.getValue();
        System.out.println("Total cents: " + total); // Prints 35

        System.out.println("The value of a Nickel is: " + Coin.NICKEL.getValue());
    }
}
