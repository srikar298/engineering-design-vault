package registry;

import character.Character;
import java.util.EnumMap;
import java.util.Map;

/**
 * <h1>CharacterRegistry — Singleton + Prototype Registry</h1>
 *
 * <p><b>Singleton (Bill Pugh):</b> One registry per game session.
 * All party members are spawned from the same master archetypes.
 *
 * <p><b>Prototype Registry:</b> Pre-configured master {@link Character} objects
 * are stored here. When a player selects "Warrior", we clone the master —
 * we do NOT construct from scratch (which would mean setting 20+ stat fields
 * correctly for each creation).
 */
public final class CharacterRegistry {

    public enum CharacterType { WARRIOR, MAGE, ARCHER, ROGUE }

    private final Map<CharacterType, Character> registry = new EnumMap<>(CharacterType.class);

    private CharacterRegistry() {
        System.out.println("[Registry] Initializing character archetypes...");
        registry.put(CharacterType.WARRIOR, buildWarrior());
        registry.put(CharacterType.MAGE,    buildMage());
        registry.put(CharacterType.ARCHER,  buildArcher());
        registry.put(CharacterType.ROGUE,   buildRogue());
        System.out.println("[Registry] " + registry.size() + " archetypes ready.\n");
    }

    private static final class InstanceHolder {
        private static final CharacterRegistry INSTANCE = new CharacterRegistry();
    }

    public static CharacterRegistry getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Returns a DEEP CLONE of the archetype — the original master is never modified.
     */
    public Character spawn(CharacterType type) {
        Character master = registry.get(type);
        if (master == null) throw new IllegalArgumentException("Unknown archetype: " + type);
        return master.clone();  // deep copy, safe to customize
    }

    // ── Master archetype builders (called only once at init) ─────────────────

    private Character buildWarrior() {
        Character c = new Character();
        c.setArchetype("Warrior");
        c.setName("Warrior");
        c.setHealth(200);  c.setAttack(80);   c.setDefense(90);  c.setSpeed(40);
        c.setWeapon("Longsword");
        c.setArmor("Plate Mail");
        c.setSpecialAbility("Shield Bash");
        return c;
    }

    private Character buildMage() {
        Character c = new Character();
        c.setArchetype("Mage");
        c.setName("Mage");
        c.setHealth(80);   c.setAttack(150);  c.setDefense(30);  c.setSpeed(60);
        c.setWeapon("Arcane Staff");
        c.setArmor("Cloth Robes");
        c.setSpecialAbility("Fireball");
        return c;
    }

    private Character buildArcher() {
        Character c = new Character();
        c.setArchetype("Archer");
        c.setName("Archer");
        c.setHealth(120);  c.setAttack(110);  c.setDefense(50);  c.setSpeed(90);
        c.setWeapon("Longbow");
        c.setArmor("Leather Armor");
        c.setSpecialAbility("Multishot");
        return c;
    }

    private Character buildRogue() {
        Character c = new Character();
        c.setArchetype("Rogue");
        c.setName("Rogue");
        c.setHealth(100);  c.setAttack(130);  c.setDefense(40);  c.setSpeed(110);
        c.setWeapon("Twin Daggers");
        c.setArmor("Shadow Cloak");
        c.setSpecialAbility("Backstab");
        return c;
    }
}
