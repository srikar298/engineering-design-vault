package engine;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Combined Behavioral Pattern: Trading Engine (SDE-2+ Level)</h1>
 * 
 * <b>Patterns Combined:</b> Observer + Strategy + State
 * 
 * <b>Scenario:</b> A High-Frequency Trading (HFT) Engine. 
 * 1. <b>Observer:</b> The engine listens to real-time market data ticks.
 * 2. <b>Strategy:</b> It executes different algorithms (Momentum vs Mean Reversion).
 * 3. <b>State:</b> The engine can be 'Active', 'Paused' (during high volatility), or 'Halted'.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * - <b>Event-Driven Architecture:</b> Observer is the backbone of real-time systems.
 * - <b>Hot Swapping:</b> Strategy allows swapping trading algos without restarting the engine.
 * - <b>Circuit Breakers:</b> State pattern implements risk management (Halted state prevents trades).
 */

// --- 1. OBSERVER (Market Data Stream) ---
interface MarketListener {
    void onTick(String symbol, double price);
}

class MarketDataFeed {
    private final List<MarketListener> listeners = new ArrayList<>();
    public void attach(MarketListener l) { listeners.add(l); }
    
    public void pushTick(String symbol, double price) {
        for (MarketListener l : listeners) l.onTick(symbol, price);
    }
}

// --- 2. STRATEGY (Trading Algorithms) ---
interface TradingStrategy {
    void analyzeAndTrade(String symbol, double price);
}

class MomentumStrategy implements TradingStrategy {
    @Override public void analyzeAndTrade(String s, double p) {
        System.out.println("   [Momentum Algo] Buying " + s + " at " + p + " (Trend is UP)");
    }
}

class MeanReversionStrategy implements TradingStrategy {
    @Override public void analyzeAndTrade(String s, double p) {
        System.out.println("   [Reversion Algo] Shorting " + s + " at " + p + " (Overbought)");
    }
}

// --- 3. STATE (Engine Lifecycle / Risk Management) ---
interface EngineState {
    void handleTick(TradingEngine engine, String symbol, double price);
}

class ActiveState implements EngineState {
    @Override public void handleTick(TradingEngine engine, String symbol, double price) {
        // Delegate to strategy only if active
        engine.getStrategy().analyzeAndTrade(symbol, price);
        
        // Volatility circuit breaker (Transition)
        if (price < 100.0) {
            System.out.println("   [Risk Alert] Volatility detected! Pausing engine.");
            engine.setState(new PausedState());
        }
    }
}

class PausedState implements EngineState {
    @Override public void handleTick(TradingEngine engine, String symbol, double price) {
        System.out.println("   [Risk Guard] Engine PAUSED. Ignoring tick: " + symbol + "@" + price);
        if (price > 105.0) {
            System.out.println("   [Risk Guard] Volatility subsided. Resuming engine.");
            engine.setState(new ActiveState());
        }
    }
}

// --- CONTEXT (The Trading Engine) ---
class TradingEngine implements MarketListener {
    private TradingStrategy strategy;
    private EngineState state;

    public TradingEngine(TradingStrategy s) {
        this.strategy = s;
        this.state = new ActiveState(); // Initial state
    }

    public void setStrategy(TradingStrategy s) { this.strategy = s; }
    public TradingStrategy getStrategy() { return strategy; }
    public void setState(EngineState s) { this.state = s; }

    @Override
    public void onTick(String symbol, double price) {
        // The engine listens to ticks (Observer), but delegates logic to State, 
        // which in turn delegates to Strategy. Perfect separation of concerns!
        state.handleTick(this, symbol, price);
    }
}

public class TradingEngineSDE2 {
    public static void main(String[] args) {
        MarketDataFeed feed = new MarketDataFeed();
        TradingEngine engine = new TradingEngine(new MomentumStrategy());
        
        feed.attach(engine);

        System.out.println("--- Normal Market ---");
        feed.pushTick("AAPL", 150.0);
        feed.pushTick("AAPL", 152.0);

        System.out.println("\n--- Flash Crash (State Transition) ---");
        feed.pushTick("AAPL", 99.0); // Triggers PausedState
        feed.pushTick("AAPL", 95.0); // Ignored

        System.out.println("\n--- Market Recovers ---");
        engine.setStrategy(new MeanReversionStrategy()); // Hot-swap strategy
        feed.pushTick("AAPL", 106.0); // Resumes and uses new strategy
    }
}
