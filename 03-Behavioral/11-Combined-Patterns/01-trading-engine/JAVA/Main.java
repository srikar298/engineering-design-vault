import engine.StockMarket;
import engine.TradingBot;
import memento.Account;
import strategy.AggressiveStrategy;
import strategy.ConservativeStrategy;

/**
 * <h1>Combined Behavioral Patterns Demonstration</h1>
 * 
 * <p>Demonstrates Observer (Pub/Sub), Strategy (Algorithms), and Memento (Undo/Snapshots).
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   Trading Engine: Observer + Strategy + Memento  ");
        System.out.println("==================================================");

        // 1. Setup the Infrastructure
        StockMarket nasdaq = new StockMarket(); // The Publisher
        Account botAccount = new Account(100.0); // The Originator

        // 2. Setup the Bot
        TradingBot bot = new TradingBot("AlphaBot", botAccount, new ConservativeStrategy());
        
        // 3. Connect the pattern!
        nasdaq.registerSubscriber(bot);

        // 4. Run the simulation
        System.out.println("\n--- Trading Session Starts ---");
        
        nasdaq.executeTick("APPL", 105.0); // Conservative will HOLD
        nasdaq.executeTick("APPL", 95.0);  // Conservative will BUY

        bot.triggerEODSnapshot(); // End of Day

        System.out.println("\n--- Day 2: Market is Bullish. Swapping to Aggressive ---");
        bot.setStrategy(new AggressiveStrategy());

        nasdaq.executeTick("APPL", 45.0); // Aggressive will BUY
        
        // Let's pretend the stock crashes and there's a system error panic
        nasdaq.executeTick("APPL", 5.0);
        
        // 5. The Memento Rollback
        bot.panicRollback(); // Reverts account to End of Day 1
    }
}
