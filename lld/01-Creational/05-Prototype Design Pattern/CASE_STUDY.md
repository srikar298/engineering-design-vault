# 📎 Prototype — In the Wild (Case Studies)

This file shows how the Prototype pattern appears **inside larger system designs**, often combined with other patterns.

---

## Case Study 1: Game Character Customization System
*(To be implemented)*

**The problem:**
A game has 50 base character archetypes (Warrior, Mage, Archer...). Each base character has pre-configured stats, animations, and equipment. Players customize on top of a base.

**The combination:**
```
Prototype Registry (Singleton) 
  └── Stores 50 pre-configured master Character objects

Factory Method
  └── CharacterFactory.create(CharacterType.WARRIOR)
       ↓
  └── Clones the master Warrior from the registry
       ↓
  └── Returns a deep clone the player can customise

Builder (optional)
  └── PlayerCharacterBuilder customizes the cloned base:
       .name("Aragorn").weapon(SWORD).armor(HEAVY).build()
```

**Why Prototype + Factory?** Creating a Warrior from scratch means setting 200 stats fields. Cloning a pre-validated master means you start from a correct baseline and only change what differs.

---

## Case Study 2: Document Template System
*(Conceptual)*

Google Docs "New from template" — each template is a pre-built `Document` object stored in a `TemplateRegistry` (Singleton). Creating a new document from a template clones the base → pure Prototype pattern.

---

## 📚 See Also
- [Individual Pattern README](../README.md)
- [Full Combined Patterns Index](../../07-Combined-Patterns/README.md)
