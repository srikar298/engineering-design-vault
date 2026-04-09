/**
 * ============================================================================
 * ☣️ THE FRAGILE BASE CLASS: A Senior LLD Trap
 * ============================================================================
 * 
 * SCENARIO: 
 * A 'LoggingList' that tries to count every addition by extending ArrayList.
 */

import java.util.*;

// ❌ VIOLATION: The Fragile Inheritance
class LoggingList<E> extends ArrayList<E> {
    private int addCount = 0;

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c); // ⚠️ DANGER! super.addAll() calls add() internally in ArrayList.
    }

    public int getAddCount() { return addCount; }
}

public class FragileBaseDemo {
    public static void main(String[] args) {
        LoggingList<String> list = new LoggingList<>();
        
        // Expected: 3 additions
        list.addAll(Arrays.asList("A", "B", "C"));
        
        // Actual result will be 6! 
        // Because ArrayList.addAll() calls add() for every element.
        // We double-counted. This is the "Fragile Base Class" problem.
        System.out.println("Add count: " + list.getAddCount()); 
    }
}
/**
 * ✅ THE FIX:
 * Use COMPOSITION instead of Inheritance.
 * Wrap the List, don't extend it.
 */
