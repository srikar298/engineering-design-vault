package agentic;

import java.util.Map;

public class CalculatorTool implements ToolCommand {
    @Override
    public String execute(Map<String, Object> parameters) {
        try {
            double op1 = Double.parseDouble(parameters.getOrDefault("op1", "0").toString());
            double op2 = Double.parseDouble(parameters.getOrDefault("op2", "0").toString());
            String operation = parameters.getOrDefault("operation", "add").toString();

            switch (operation.toLowerCase()) {
                case "add":
                    return String.valueOf(op1 + op2);
                case "multiply":
                    return String.valueOf(op1 * op2);
                case "subtract":
                    return String.valueOf(op1 - op2);
                case "divide":
                    if (op2 == 0) return "Error: Division by zero";
                    return String.valueOf(op1 / op2);
                default:
                    return "Error: Unsupported operation " + operation;
            }
        } catch (NumberFormatException e) {
            return "Error: Invalid number format in calculator inputs";
        }
    }
}
