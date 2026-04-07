package engine;

import memento.Account;
import memento.AccountSnapshot;
import observer.IStockSubscriber;
import strategy.ITradingStrategy;

/**
 * <h1>The Trading Bot</h1>
 * 
 * Takes on THREE roles:
 * 1. Subscriber (Observer pattern) - Listens for price updates.
 * 2. Context (Strategy pattern) - Holds the algorithm to decide what to do.
 * 3. Caretaker (Memento pattern) - Triggers account snapshots before trading.
 */
public class TradingBot implements IStockSubscriber {
    private final String botName;
    private final Account account;
    private ITradingStrategy strategy; // Dynamic Strategy
    private AccountSnapshot lastEodSnapshot; // Last End-Of-Day snapshot

    public TradingBot(String botName, Account account, ITradingStrategy strategy) {
        this.botName = botName;
        this.account = account;
        this.strategy = strategy;
        
        // Take an initial snapshot
        this.lastEodSnapshot = account.save();
    }

    // Ability to swap strategies mid-day
    public void setStrategy(ITradingStrategy strategy) {
        System.out.println("   [" + botName + "] Swapped algorithm to: " + strategy.getClass().getSimpleName());
        this.strategy = strategy;
    }

    @Override
    public void updatePrice(String ticker, double newPrice) {
        // Strategy Evaluation
        String decision = strategy.evaluate(ticker, newPrice);
        
        System.out.println("   [" + botName + "] Evaluated " + ticker + ": " + decision);

        // Execute action based on Strategy's String response
        if (decision.contains("BUY")) {
            if (account.getBalance() >= newPrice) {
                account.debit(newPrice);
                System.out.println("      -> Executed Buy. Cash remaining: $" + account.getBalance());
            } else {
                System.out.println("      -> Insufficient Funds for Buy.");
            }
        } else if (decision.contains("SELL")) {
            account.credit(newPrice);
            System.out.println("      -> Executed Sell. Cash remaining: $" + account.getBalance());
        }
    }

    // --- Memento Caretaker Methods ---
    
    public void triggerEODSnapshot() {
        System.out.println("\n   [" + botName + "] Generating End-Of-Day Snapshot...");
        this.lastEodSnapshot = account.save();
    }

    public void panicRollback() {
        System.out.println("\n   [" + botName + "] 🚨 PANIC: Market crashed! Rolling back to last EOD state...");
        account.restore(lastEodSnapshot);
    }
}
