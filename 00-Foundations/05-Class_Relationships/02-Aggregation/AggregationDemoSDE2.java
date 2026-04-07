package class_relationships.aggregation;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>02 - Aggregation: Weak Ownership / Shared Lifecycle (SDE-2+ Level)</h1>
 * 
 * <b>Scenario:</b> A University System. 
 * Departments "have" Professors. If a Department closes, the Professors 
 * don't disappear; they just look for a job in another department.
 * 
 * <b>Senior SDE-2 Insights:</b>
 * 1. <b>Injection:</b> Aggregated objects are created <b>outside</b> the container 
 *    and passed in (Dependency Injection).
 * 2. <b>Defensive Copying:</b> When receiving a list of aggregated objects, senior 
 *    engineers copy the list to prevent external modification of internal state.
 * 3. <b>Shared Ownership:</b> An aggregated object can belong to multiple parents 
 *    simultaneously (e.g. a Professor in both CS and Math).
 * 
 * <b>Edge Cases:</b>
 * - <b>Soft Deletes:</b> Nullifying the parent doesn't trigger GC for the child.
 */

class Professor {
    private final String name;
    public Professor(String n) { this.name = n; }
    public String getName() { return name; }
}

class Department {
    private final String deptName;
    // --- [INTERVIEW_MVP] (The Aggregated Reference) ---
    private final List<Professor> professors;

    public Department(String name, List<Professor> profs) {
        this.deptName = name;
        // --- [PRODUCTION_ENHANCEMENT] (Defensive Copying) ---
        // We own the list, but not the items inside it.
        this.professors = new ArrayList<>(profs);
    }

    public void addProfessor(Professor p) { professors.add(p); }
    public List<Professor> getProfessors() { return List.copyOf(professors); }
}

public class AggregationDemoSDE2 {
    public static void main(String[] args) {
        // [INTERVIEW_MVP]: Independent lifecycles
        Professor p1 = new Professor("Dr. Strange");
        List<Professor> list = new ArrayList<>();
        list.add(p1);

        Department magicDept = new Department("Mystic Arts", list);

        // [PRODUCTION_ENHANCEMENT]: The Death Test
        magicDept = null; 
        System.out.println("Department is gone (null).");
        System.out.println("But Professor " + p1.getName() + " still exists! (Aggregation)");
    }
}
