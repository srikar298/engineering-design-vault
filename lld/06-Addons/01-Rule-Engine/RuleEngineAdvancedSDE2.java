package addons.ruleengine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * <h1>01 - Advanced Rule Engine (Composite + Strategy + Specification)</h1>
 * 
 * <b>Scenario:</b> You are building a <b>Promotional Engine</b> for an E-commerce platform. 
 * A promotion applies only if: (User is VIP AND (Total > $500 OR First Order)) AND User is not Blacklisted.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Avoid If-Else Hell:</b> Hardcoding complex logic makes it fragile and untestable.
 * 2. <b>Composable Rules:</b> Each rule is an isolated strategy. Composite pattern allows 
 *    nesting these rules into a tree structure.
 * 3. <b>Fluent API:</b> Designing a readable API for the domain experts to use.
 * 
 * <b>Modern Patterns:</b>
 * - <b>Specification Pattern:</b> Encapsulates logic into a boolean-returning object.
 * - <b>Functional Predicates:</b> Leverages Java 8+ <code>Predicate</code> for concise logic.
 */

class Order {
    public final double amount;
    public final boolean isFirstOrder;
    public final boolean isVip;
    public final boolean isBlacklisted;

    public Order(double amount, boolean isFirstOrder, boolean isVip, boolean isBlacklisted) {
        this.amount = amount;
        this.isFirstOrder = isFirstOrder;
        this.isVip = isVip;
        this.isBlacklisted = isBlacklisted;
    }
}

// --- CORE ABSTRACTION (The Strategy) ---
interface Rule extends Predicate<Order> {
    static Rule from(Predicate<Order> predicate) {
        return predicate::test;
    }

    // --- COMPOSITE OPERATORS ---
    default Rule and(Rule other) {
        return order -> this.test(order) && other.test(order);
    }

    default Rule or(Rule other) {
        return order -> this.test(order) || other.test(order);
    }

    default Rule not() {
        return order -> !this.test(order);
    }
}

// --- CONCRETE RULES (Isolated Strategies) ---
class VipRule implements Rule {
    @Override public boolean test(Order o) { return o.isVip; }
}

class HighValueRule implements Rule {
    @Override public boolean test(Order o) { return o.amount > 500; }
}

class FirstOrderRule implements Rule {
    @Override public boolean test(Order o) { return o.isFirstOrder; }
}

class BlacklistRule implements Rule {
    @Override public boolean test(Order o) { return o.isBlacklisted; }
}

/**
 * The Rule Engine: Orchestrates the execution of rules.
 */
public class RuleEngineAdvancedSDE2 {
    public static void main(String[] args) {
        // --- DEFINE THE DOMAIN RULES ---
        Rule isVip = new VipRule();
        Rule isHighValue = new HighValueRule();
        Rule isFirstOrder = new FirstOrderRule();
        Rule isBlacklisted = new BlacklistRule();

        // --- COMPOSE COMPLEX LOGIC (The Composite Tree) ---
        // (VIP AND (Total > 500 OR First Order)) AND NOT Blacklisted
        Rule promoEligibility = isVip
                .and(isHighValue.or(isFirstOrder))
                .and(isBlacklisted.not());

        // --- TEST SCENARIOS ---
        Order order1 = new Order(600, false, true, false); // Eligible (VIP + HighValue)
        Order order2 = new Order(100, true, true, false);  // Eligible (VIP + FirstOrder)
        Order order3 = new Order(600, false, true, true);  // NOT Eligible (Blacklisted)

        System.out.println("Order 1 Eligible: " + promoEligibility.test(order1)); // true
        System.out.println("Order 2 Eligible: " + promoEligibility.test(order2)); // true
        System.out.println("Order 3 Eligible: " + promoEligibility.test(order3)); // false

        // Senior Pro-Tip: This structure allows you to load rules from a Database/JSON 
        // at runtime without changing code!
    }
}
