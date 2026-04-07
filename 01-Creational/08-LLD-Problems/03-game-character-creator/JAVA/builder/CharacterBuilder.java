package builder;

import character.Character;

/**
 * <h1>CharacterBuilder — Builder Pattern for Customization</h1>
 *
 * <p>After the Prototype Registry clones a base archetype, the player
 * customizes it through this builder — setting a personal name, swapping
 * weapons, boosting specific stats.
 *
 * <p><b>Why Builder after clone?</b> The clone gives us a valid, balanced
 * baseline. The builder lets us change only the fields we care about in
 * a fluent, readable, type-safe way — without touching the 15 fields we
 * want to keep from the archetype.
 */
public final class CharacterBuilder {

    private final Character character;

    /** Start from a cloned archetype from the registry */
    public CharacterBuilder(Character clonedBase) {
        if (clonedBase == null) throw new IllegalArgumentException("Base character required");
        this.character = clonedBase;
    }

    public CharacterBuilder name(String name) {
        character.setName(name);
        return this;
    }

    public CharacterBuilder weapon(String weapon) {
        character.setWeapon(weapon);
        return this;
    }

    public CharacterBuilder armor(String armor) {
        character.setArmor(armor);
        return this;
    }

    /** Boost a stat by a flat amount (e.g. from equipment bonuses) */
    public CharacterBuilder boostAttack(int bonus) {
        character.setAttack(character.getAttack() + bonus);
        return this;
    }

    public CharacterBuilder boostDefense(int bonus) {
        character.setDefense(character.getDefense() + bonus);
        return this;
    }

    public CharacterBuilder boostHealth(int bonus) {
        character.setHealth(character.getHealth() + bonus);
        return this;
    }

    public CharacterBuilder specialAbility(String ability) {
        character.setSpecialAbility(ability);
        return this;
    }

    /** Returns the fully configured character — the clone has NOT been mutated globally */
    public Character build() {
        return character;
    }
}
