import character.Character;
import registry.CharacterRegistry;
import registry.CharacterRegistry.CharacterType;
import builder.CharacterBuilder;

/**
 * <h1>Game Character Creator — Main Demo</h1>
 *
 * <p><b>Patterns at work:</b>
 * <ul>
 *   <li><b>Singleton (Bill Pugh)</b> — {@code CharacterRegistry.getInstance()} is
 *       the one registry for the entire game session.</li>
 *   <li><b>Prototype</b> — {@code registry.spawn()} deep-clones a master archetype.
 *       The master is NEVER mutated.</li>
 *   <li><b>Builder</b> — {@code CharacterBuilder} customizes the clone fluently
 *       without touching unrelated fields.</li>
 * </ul>
 */
public class Main {
    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║       ⚔️  Game Character Creator Demo            ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

        CharacterRegistry registry = CharacterRegistry.getInstance();

        // ── Player 1: Customized Warrior ──────────────────────────────────────
        System.out.println("--- Player 1: Creating Warrior ---");
        Character warrior = new CharacterBuilder(registry.spawn(CharacterType.WARRIOR))
            .name("Aragorn")
            .weapon("Anduril (Flame of the West)")
            .boostAttack(20)
            .boostHealth(50)
            .build();
        System.out.println(warrior);

        // ── Player 2: Customized Mage ─────────────────────────────────────────
        System.out.println("\n--- Player 2: Creating Mage ---");
        Character mage = new CharacterBuilder(registry.spawn(CharacterType.MAGE))
            .name("Gandalf")
            .weapon("Staff of the White Council")
            .specialAbility("You Shall Not Pass!")
            .boostAttack(40)
            .build();
        System.out.println(mage);

        // ── Player 3: Another Warrior (different customization) ───────────────
        System.out.println("\n--- Player 3: Another Warrior ---");
        Character warrior2 = new CharacterBuilder(registry.spawn(CharacterType.WARRIOR))
            .name("Boromir")
            .armor("Gondor Captain Armor")
            .boostDefense(15)
            .build();
        System.out.println(warrior2);

        // ── Prototype proof: master archetype was NEVER mutated ───────────────
        System.out.println("\n--- Prototype Proof: Master Archetype Unchanged ---");
        Character masterWarrior = registry.spawn(CharacterType.WARRIOR);
        System.out.println("Master Warrior name   : " + masterWarrior.getName());   // "Warrior"
        System.out.println("Master Warrior attack : " + masterWarrior.getAttack()); // 80, not 100/95

        System.out.println("\n--- Singleton Proof ---");
        CharacterRegistry registry2 = CharacterRegistry.getInstance();
        System.out.println("Same registry instance: " + (registry == registry2));

        System.out.println("\n✅ 3 unique characters created from 2 archetype clones. Master never touched.");
    }
}
