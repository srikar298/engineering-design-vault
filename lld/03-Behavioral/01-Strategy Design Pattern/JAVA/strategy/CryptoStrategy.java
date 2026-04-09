package strategy;

public class CryptoStrategy implements IPaymentStrategy {
    private final String walletAddress;

    public CryptoStrategy(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    @Override
    public void pay(double amount) {
        System.out.println("Processing Crypto (USDC) payment of $" + amount + " to wallet: " + walletAddress);
        // Smart contract interaction logic would go here
    }
}
