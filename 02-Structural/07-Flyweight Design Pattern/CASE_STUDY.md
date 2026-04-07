# 🪶 Case Study: Flyweight in the Real World

## Where is it used in our repository?

The Flyweight pattern is the ultimate architectural RAM optimization tool. We showcase its raw power in:

### 1. 🎮 RPG Game Rendering Engine (`09-LLD-Problems/03-rpg-engine`)
Rendering a massive battle scene involves drawing 100,000 arrows flying through the air. If each `Arrow` object stores the 2MB 3D mesh and texture data for rendering an arrow, the game will attempt to allocate 200 Gigabytes of RAM and instantly crash.

**The Flyweight Solution:**
We separate the data into:
- **Intrinsic State:** The shared 2MB 3D mesh. We pull this out into a single, immutable `ArrowMesh` Flyweight object.
- **Extrinsic State:** The unique X, Y coordinates and velocity of each specific arrow in the air. 

To render the battle, we have 100,000 tiny Context objects that just contain an X,Y coordinate and a reference pointer to the SINGLE `ArrowMesh` Flyweight in the factory cache. We reduced 200GB of RAM down to basically 2MB + a few bytes.

## Key Senior Takeaway
**Flyweights save Memory, not CPU.** If you are designing a system that must handle millions of similar objects (e.g. a text editor caching Characters, a stock tick engine caching Symbol metadata, or a JVM caching `String` literals), you must slice the objects in half: cache the heavy Immutable Intrinsic data, and pass in the volatile Extrinsic data at runtime.
