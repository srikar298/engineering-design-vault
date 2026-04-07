package visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>10 - Visitor: The "Algorithm Extractor" (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> A Document Structure (Paragraphs, Images). 
 * You need to perform multiple operations: 'Calculate Size', 'Export to HTML', 
 * and 'Find Keywords'. 
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Double Dispatch:</b> This is the magic of Visitor. <code>element.accept(visitor)</code> 
 *    calls <code>visitor.visit(element)</code>. This resolves the correct 
 *    overloaded method at runtime based on BOTH the visitor and the element type.
 * 2. <b>OCP Mastery:</b> You can add a new algorithm (e.g. 'SpellCheckVisitor') 
 *    without changing a single line of code in the Document classes.
 * 3. <b>SRP Compliance:</b> Data classes (Paragraph) only hold data. 
 *    Algorithms (Export) live in the Visitor.
 * 
 * <b>Edge Cases Handled:</b>
 * - <b>Recursive Traversal:</b> The Visitor can be passed down into composite nodes.
 * - <b>Type-Specific Logic:</b> Each 'visit' method handles a specific subclass.
 */

// --- VISITOR INTERFACE ---
interface DocumentVisitor {
    void visit(TextElement t);
    void visit(ImageElement i);
}

// --- ELEMENT INTERFACE ---
interface DocumentElement {
    void accept(DocumentVisitor v);
}

// --- CONCRETE ELEMENTS ---
class TextElement implements DocumentElement {
    public final String text;
    public TextElement(String t) { this.text = t; }
    @Override public void accept(DocumentVisitor v) { v.visit(this); }
}

class ImageElement implements DocumentElement {
    public final int width;
    public ImageElement(int w) { this.width = w; }
    @Override public void accept(DocumentVisitor v) { v.visit(this); }
}

// --- CONCRETE VISITOR ---
class HtmlExportVisitor implements DocumentVisitor {
    @Override
    public void visit(TextElement t) {
        System.out.println("<p>" + t.text + "</p>");
    }

    @Override
    public void visit(ImageElement i) {
        System.out.println("<img src='...' width='" + i.width + "'>");
    }
}

public class VisitorPragmaticSDE2 {
    public static void main(String[] args) {
        // [INTERVIEW_MVP]: Structure setup
        List<DocumentElement> document = new ArrayList<>();
        document.add(new TextElement("Hello SDE-2!"));
        document.add(new ImageElement(800));

        // [INTERVIEW_MVP]: Algorithm execution via Visitor
        DocumentVisitor htmlExporter = new HtmlExportVisitor();
        
        System.out.println("Generating HTML:");
        for (DocumentElement e : document) {
            e.accept(htmlExporter);
        }
        
        System.out.println("✅ Algorithms separated from Data.");
    }
}
