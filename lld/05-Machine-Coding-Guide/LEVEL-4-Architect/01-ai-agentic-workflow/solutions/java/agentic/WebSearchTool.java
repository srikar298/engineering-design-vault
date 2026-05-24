package agentic;

import java.util.Map;

public class WebSearchTool implements ToolCommand {
    @Override
    public String execute(Map<String, Object> parameters) {
        String query = parameters.getOrDefault("query", "").toString();
        if (query.isEmpty()) {
            return "Error: Empty search query";
        }
        
        System.out.println("   [WebSearchTool] Simulating search query: \"" + query + "\"");
        if (query.toLowerCase().contains("weather")) {
            return "Weather in Seattle: Sunny, 72°F";
        } else if (query.toLowerCase().contains("population")) {
            return "World population: 8 billion";
        }
        return "Search result: Found data for '" + query + "'.";
    }
}
