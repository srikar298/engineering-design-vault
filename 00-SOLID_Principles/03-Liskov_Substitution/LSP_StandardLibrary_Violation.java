package liskov_substitution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <h1>LSP: The "Standard Library" Trap (SDE-2+ Edge Case)</h1>
 * 
 * INTERVIEW QUESTION: "Does Java's List interface follow LSP?"
 * ANSWER: No. Collections.unmodifiableList() violates LSP.
 */
public class LSP_StandardLibrary_Violation {

    public static void addData(List<String> list) {
        // The List contract (Parent) says .add() should work.
        list.add("New Data"); 
    }

    public static void main(String[] args) {
        // 1. Works as expected
        List<String> mutableList = new ArrayList<>();
        addData(mutableList);
        System.out.println("✅ Mutable List worked.");

        // 2. EXPLODES at runtime
        // Collections.unmodifiableList returns a SUBTYPE of List.
        // But it BREAKS the parent's contract (the ability to add).
        List<String> readOnlyList = Collections.unmodifiableList(new ArrayList<>(Arrays.asList("A", "B")));
        
        try {
            addData(readOnlyList); // ❌ RUNTIME ERROR: UnsupportedOperationException
        } catch (UnsupportedOperationException e) {
            System.err.println("❌ LSP VIOLATION: UnmodifiableList is a subtype of List but " +
                               "cannot be substituted because it breaks the .add() behavior.");
        }
    }
}

/**
 * 🎓 SDE-2+ INSIGHT:
 * This is why modern languages (like Kotlin or Swift) have separate 
 * MutableList and List interfaces. It enforces LSP at compile time.
 */
