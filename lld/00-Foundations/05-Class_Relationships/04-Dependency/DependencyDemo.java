/**
 * ============================================================================
 * ⚡ DEPENDENCY DEMO: Document Export System
 * ============================================================================
 * Demonstrates:
 * 1. Dependency via method parameter (correct — transient use)
 * 2. Dependency on an INTERFACE (correct — flexible, testable)
 * 3. Anti-pattern: storing the dependency as a field (promotes to Association)
 * ============================================================================
 */

// ── EXPORTER INTERFACE (The Abstraction) ──────────────────────────────────
interface Exporter {
    String getFormat();
    void export(String content, String filename);
}

// ── CONCRETE EXPORTERS ────────────────────────────────────────────────────
class PdfExporter implements Exporter {
    @Override public String getFormat() { return "PDF"; }
    @Override public void export(String content, String filename) {
        System.out.printf("[PDF]  Writing '%s.pdf' | Content size: %d chars%n",
            filename, content.length());
    }
}

class CsvExporter implements Exporter {
    @Override public String getFormat() { return "CSV"; }
    @Override public void export(String content, String filename) {
        System.out.printf("[CSV]  Writing '%s.csv' | Rows: %d%n",
            filename, content.split("\n").length);
    }
}

class HtmlExporter implements Exporter {
    @Override public String getFormat() { return "HTML"; }
    @Override public void export(String content, String filename) {
        System.out.printf("[HTML] Writing '%s.html' | Wrapped in <body>%n", filename);
    }
}

// ── ❌ BAD DOCUMENT (Stores the exporter as a field — wrong!) ──────────────
class BadDocument {
    private String title;
    private String content;
    private PdfExporter exporter; // ❌ Tightly coupled to ONE specific type!

    public BadDocument(String title, String content, PdfExporter exporter) {
        this.title    = title;
        this.content  = content;
        this.exporter = exporter; // Stored — now an Association, not a Dependency
    }

    public void save() {
        // If we ever want CSV or HTML, we must rewrite this entire class.
        exporter.export(content, title);
    }
}

// ── ✅ GOOD DOCUMENT (Uses Dependency — receives exporter per-method) ──────
class Document {
    private final String title;
    private final String content;
    // NO exporter field — the dependency is purely method-scoped

    public Document(String title, String content) {
        this.title   = title;
        this.content = content;
    }

    /**
     * Dependency relationship: Exporter is received as a method parameter.
     * The Document uses it for this call and then forgets it completely.
     * Depends on the INTERFACE, not any concrete type.
     */
    public void exportAs(Exporter exporter) {
        System.out.printf("Exporting '%s' as %s...%n", title, exporter.getFormat());
        exporter.export(content, title);
    }

    public String getTitle()   { return title; }
    public String getContent() { return content; }
}

// ── EXECUTION ─────────────────────────────────────────────────────────────
public class DependencyDemo {
    public static void main(String[] args) {

        System.out.println("=== ❌ Anti-Pattern: Stored Exporter (Tight Coupling) ===");
        BadDocument badDoc = new BadDocument("Invoice", "Order 101", new PdfExporter());
        badDoc.save(); // Can ONLY ever export as PDF — rigid!

        System.out.println("\n=== ✅ Dependency: Method-Scoped Exporter ===");
        Document doc = new Document(
            "Q1-Report",
            "Revenue: $1.2M\nExpenses: $450K\nProfit: $750K"
        );

        // Same Document object, different Exporters — zero code change in Document!
        doc.exportAs(new PdfExporter());
        doc.exportAs(new CsvExporter());
        doc.exportAs(new HtmlExporter());

        System.out.println("\n=== ✅ Testability Proof ===");
        // In unit tests, we can inject a mock/stub exporter — the real ones never run.
        Exporter mockExporter = new Exporter() {
            @Override public String getFormat() { return "MOCK"; }
            @Override public void export(String content, String filename) {
                System.out.println("[MOCK] Test captured export call for: " + filename);
            }
        };
        doc.exportAs(mockExporter); // ✅ Tests run instantly without any file I/O
    }
}
