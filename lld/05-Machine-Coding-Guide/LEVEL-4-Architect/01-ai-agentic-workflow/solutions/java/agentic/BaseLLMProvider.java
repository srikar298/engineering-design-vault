package agentic;

public abstract class BaseLLMProvider implements LLMProvider {
    protected final String providerName;
    protected final LLMProvider fallbackProvider;

    protected BaseLLMProvider(String providerName, LLMProvider fallbackProvider) {
        this.providerName = providerName;
        this.fallbackProvider = fallbackProvider;
    }

    public String getProviderName() {
        return providerName;
    }

    public LLMResponse generateWithFallback(String prompt) {
        try {
            System.out.println(String.format("   [LLD-Chain] Querying %s...", providerName));
            return this.generate(prompt);
        } catch (Exception e) {
            System.out.println(String.format("   [LLD-Chain] WARNING: %s failed: %s", providerName, e.getMessage()));
            if (fallbackProvider != null) {
                if (fallbackProvider instanceof BaseLLMProvider) {
                    return ((BaseLLMProvider) fallbackProvider).generateWithFallback(prompt);
                } else {
                    try {
                        System.out.println("   [LLD-Chain] Querying final fallback provider...");
                        return fallbackProvider.generate(prompt);
                    } catch (Exception ex) {
                        System.out.println("   [LLD-Chain] ERROR: Fallback chain exhausted! Error: " + ex.getMessage());
                        throw new RuntimeException("All LLM providers failed", ex);
                    }
                }
            }
            throw new RuntimeException("Primary provider failed and no fallback configured", e);
        }
    }
}
