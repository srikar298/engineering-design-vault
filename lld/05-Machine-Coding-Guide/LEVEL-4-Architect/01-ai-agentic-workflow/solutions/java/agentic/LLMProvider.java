package agentic;

public interface LLMProvider {
    LLMResponse generate(String prompt) throws Exception;
}
