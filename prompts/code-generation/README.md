# 🤖 AI Prompts: Code Generation & LLD

Use these prompts to generate clean, SOLID-compliant code structures.

## 1. SOLID LLD Generator
**Goal**: Generate boilerplate that follows the Single Responsibility and Open-Closed principles.

**Prompt**:
```markdown
I need to implement a [FEATURE NAME, e.g., Notification System].
Requirements: [LIST REQUIREMENTS]
Constraints: Must use [LANGUAGE] and follow SOLID principles.

Please provide:
1. A class diagram description.
2. The core interfaces and abstractions.
3. A concrete implementation of the Strategy pattern for [SPECIFIC COMPONENT].
4. Dependency injection setup for these components.
```

## 2. Refactoring to Design Patterns
**Goal**: Identify when to use a specific pattern.

**Prompt**:
```markdown
I have the following spaghetti code that uses nested if-else statements for [LOGIC TYPE].
[PASTE CODE]

Suggest a Design Pattern (e.g., State, Strategy, or Command) to refactor this. 
Provide the refactored code and explain how it improves extensibility.
```
