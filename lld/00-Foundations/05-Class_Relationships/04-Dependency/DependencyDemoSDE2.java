package class_relationships.dependency;

/**
 * <h1>04 - Dependency: Transient Interaction / Uses-A (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> A Document Exporter. 
 * A Document doesn't "own" a Printer; it just "uses" it temporarily 
 * to print its content.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Method Scoped:</b> Unlike Association (Field), Dependency is <b>Method-scoped</b>. 
 *    The object is passed as a parameter and then forgotten.
 * 2. <b>Abstraction:</b> Always depend on an <b>Interface</b>, not a concrete class. 
 *    This is the foundation of the <b>Dependency Inversion Principle (DIP)</b>.
 * 3. <b>Testability:</b> This is the easiest relationship to test because 
 *    you can pass a Mock object into the method.
 * 
 * <b>Edge Cases:</b>
 * - <b>Promoting to Association:</b> If you start storing the parameter in a 
 *    field, you've promoted a Dependency to an Association. Avoid this unless necessary.
 */

interface Printer {
    void print(String content);
}

class ConsolePrinter implements Printer {
    @Override public void print(String c) { System.out.println("Printing: " + c); }
}

class Document {
    private final String content;
    public Document(String c) { this.content = c; }

    /**
     * --- [INTERVIEW_MVP] (The Dependency Parameter) ---
     * The document 'depends' on the printer interface only for this call.
     */
    public void printDocument(Printer printer) {
        // [PRODUCTION_ENHANCEMENT]: Null checks & Inversion of Control
        if (printer == null) throw new IllegalArgumentException("Printer required.");
        printer.print(content);
    }
}

public class DependencyDemoSDE2 {
    public static void main(String[] args) {
        Document doc = new Document("SDE-2 Design Principles");
        
        // [INTERVIEW_MVP]: Passing the dependency
        doc.printDocument(new ConsolePrinter());

        // [PRODUCTION_ENHANCEMENT]: Easy Mocking/Swapping
        doc.printDocument(c -> System.out.println("Mock Print: " + c));
        
        System.out.println("✅ Dependency demonstrated via method-level interaction.");
    }
}
