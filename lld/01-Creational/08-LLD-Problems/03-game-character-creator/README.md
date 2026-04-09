# ⚔️ LLD Problem: Game Character Creator

> **Patterns:** Prototype · Singleton (Registry) · Builder

---

## 📋 Problem Statement

Design a Character Creation System for an RPG game. Requirements:

1. The game has **4 base archetypes**: Warrior, Mage, Archer, Rogue — each with 9 balanced stats (HP, ATK, DEF, SPD, weapon, armor, ability)
2. Players choose a base archetype and **customize** it (name, weapon swap, stat boosts from equipment)
3. Two players choosing "Warrior" must get **independent copies** — one player's customization must never affect another's
4. The **master archetype** must never be mutated — it's the ground truth for the balanced game
5. There must be **one registry** for the entire game session — no duplicate archetype initialization

---

## 🧩 Pattern Mapping

| Sub-Problem | Pattern | Why |
|---|---|---|
| Store and retrieve master archetypes | **Singleton Registry** | One registry = one ground truth. Multiple registries = inconsistent archetypes |
| Create player characters from masters without modifying the master | **Prototype** | Deep clone the master. Any changes on the clone don't affect the master |
| Customize the clone with name, weapon, stat boosts | **Builder** | 9 fields, only 3-4 need customization per player. Builder lets you set only what changes |

---

## 🏗️ Architecture

```mermaid
classDiagram
    class CharacterRegistry {
        <<Singleton + Registry>>
        -registry: EnumMap~CharacterType,Character~
        +getInstance()$ CharacterRegistry
        +spawn(CharacterType) Character
        -buildWarrior() Character
        -buildMage() Character
    }

    class Character {
        <<Prototype>>
        -name, archetype, health
        -attack, defense, speed
        -weapon, armor, specialAbility
        +clone() Character
    }

    class CharacterBuilder {
        <<Builder>>
        -character: Character
        +name(String) Builder
        +weapon(String) Builder
        +boostAttack(int) Builder
        +boostHealth(int) Builder
        +build() Character
    }

    class InstanceHolder {
        <<static inner>>
        -INSTANCE: CharacterRegistry
    }

    CharacterRegistry *-- Character : stores master
    CharacterRegistry ..> Character : spawn() clones
    CharacterBuilder --> Character : customizes clone
    CharacterRegistry +-- InstanceHolder
```

---

## 💻 Code Walk-Through

### Step 1 — Singleton Registry initialized once at startup
```java
// CharacterRegistry loads all 4 masters during class init (Bill Pugh Singleton)
CharacterRegistry registry = CharacterRegistry.getInstance();
// [Registry] Initializing character archetypes...
// [Registry] 4 archetypes ready.
```

### Step 2 — Prototype: clone the master
```java
Character base = registry.spawn(CharacterType.WARRIOR);
// Returns a DEEP CLONE — registry master is untouched
```

### Step 3 — Builder: customize the clone
```java
Character aragorn = new CharacterBuilder(registry.spawn(CharacterType.WARRIOR))
    .name("Aragorn")
    .weapon("Anduril")
    .boostAttack(20)   // +20 on top of base 80 → 100
    .boostHealth(50)   // +50 on top of base 200 → 250
    .build();
```

### Step 4 — Proof: master preserved
```java
Character master = registry.spawn(CharacterType.WARRIOR);
System.out.println(master.getAttack()); // Still 80, not 100
System.out.println(master.getName());   // Still "Warrior", not "Aragorn"
```

### How to Run
```bash
cd JAVA/
javac character/*.java registry/*.java builder/*.java Main.java
java Main
```

---

## 🎭 Junior vs. Senior

| Concern | Junior | Senior |
|---|---|---|
| **Creating characters** | `new Warrior(200, 80, 90, 40, "Longsword", "Plate Mail", "Shield Bash")` (every time!) | Registry clone — one line, always balanced |
| **Customization** | Directly mutates registry master | Clones first, mutates only the clone |
| **Two players picking same class** | Share the same object reference (catastrophic bug) | Each `spawn()` returns an independent clone |
| **Adding new archetype** | Requires modifying factory switch statements | Add `CharacterType.PALADIN` enum constant + `buildPaladin()` method |

---

## 🧠 FAANG Interview Angles

**Q: When would you use Prototype vs. just calling the constructor?**
> Use Prototype when:
> 1. Construction is expensive (reading from DB, complex calculations)
> 2. You want to start from a known-valid baseline and vary only specific fields
> 3. The exact runtime type isn't known — you only have an interface reference but need a copy
> 
> In our case (2) applies: the master has 9 balanced stats. Calling the constructor would require the caller to know all 9 values correctly — error-prone.

**Q: Is this a shallow copy or deep copy? Why does it matter?**
> Our `Character` uses `Object.clone()` (shallow). This is safe here because all fields are primitives or `String` (immutable). If `Character` had a mutable `List<Item> equipment`, the shallow clone would share the list — modifying one player's equipment would affect the other. In that case, you must implement a deep clone that creates a new list and copies each item.

**Q: Why Singleton for the registry, not Static Utility class?**
> Static utility classes can't implement interfaces, making them untestable. A Singleton implementing `ICharacterRegistry` lets tests inject a mock registry with custom archetypes (e.g., a `TestCharacterRegistry` where Warrior has 1 HP for easier test assertions). The Singleton gives you a managed, swappable instance; statics give you globally-fixed, non-swappable state.
