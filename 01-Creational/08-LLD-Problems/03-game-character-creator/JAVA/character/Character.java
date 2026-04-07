package character;

/**
 * <h1>Character — The Prototype</h1>
 *
 * <p>Each Character is a pre-configured archetype (Warrior, Mage, Archer).
 * Instead of constructing a new Warrior from scratch (setting 20+ stats),
 * the Prototype Pattern clones a master archetype stored in the registry.
 *
 * <p>Override {@code clone()} to perform a proper <b>deep copy</b>.
 */
public final class Character implements Cloneable {

    private String  name;
    private String  archetype;
    private int     health;
    private int     attack;
    private int     defense;
    private int     speed;
    private String  weapon;
    private String  armor;
    private String  specialAbility;

    // Public constructor — for registry and builders
    public Character() {}

    /** Deep clone — all fields are primitives/immutable Strings, so MemberwiseClone is safe */
    @Override
    public Character clone() {
        try {
            return (Character) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Should never happen", e);
        }
    }

    // ── Setters (Public — for registry & builder access) ──
    public void setName(String name)                   { this.name = name; }
    public void setArchetype(String archetype)         { this.archetype = archetype; }
    public void setHealth(int health)                  { this.health = health; }
    public void setAttack(int attack)                  { this.attack = attack; }
    public void setDefense(int defense)                { this.defense = defense; }
    public void setSpeed(int speed)                    { this.speed = speed; }
    public void setWeapon(String weapon)               { this.weapon = weapon; }
    public void setArmor(String armor)                 { this.armor = armor; }
    public void setSpecialAbility(String ability)      { this.specialAbility = ability; }

    // ── Public getters ───────────────────────────────────────────────────────
    public String getName()          { return name; }
    public String getArchetype()     { return archetype; }
    public int    getHealth()        { return health; }
    public int    getAttack()        { return attack; }
    public int    getDefense()       { return defense; }
    public int    getSpeed()         { return speed; }
    public String getWeapon()        { return weapon; }
    public String getArmor()         { return armor; }
    public String getSpecialAbility(){ return specialAbility; }

    @Override
    public String toString() {
        return String.format(
            "Character{name='%s', archetype='%s', HP=%d, ATK=%d, DEF=%d, SPD=%d, weapon='%s', armor='%s', ability='%s'}",
            name, archetype, health, attack, defense, speed, weapon, armor, specialAbility);
    }
}
