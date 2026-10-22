# Pour & Seap

Experimental Fabric 1.20.1 mod.

- **Mod ID:** `pour_and_seap`
- **Java package:** `com.mentality.pour_and_seap`
- **License:** All Rights Reserved (not MIT)
- **Version:** 0.2.0 development prototype

## Features for testing
- Drinking Glass / Wine Glass: placeable 3D blocks with narrow hitboxes.
- Dedicated creative tab **Pour & Seap**, also in Food & Drinks.
- Water and milk support; water bottle and milk bucket can fill a glass.
- In hand: drinking glass main hand, liquid container offhand, right-click air to fill. Hold right-click to drink.
- Placed glass: right-click with water bottle/milk bucket to fill; empty hand to drink; sneak+empty hand to pick up with the contents.
- Breaking a glass in Survival drops an item preserving the liquid.
- Original item pixel-art and new block models, not Let's Do assets.

## Build on Arch Linux
```bash
git clone https://github.com/sqwiziiy/Pour-and-Sip.git
cd Pour-and-Sip
./gradlew build
ls build/libs/
```
Java 17 or higher is required for the Gradle build. The wrapper downloads Gradle 8.8.
Use `build/libs/pour_and_seap-0.2.0.jar` in the Fabric 1.20.1 `mods` folder.

## Test
```mcfunction
/give @s pour_and_seap:drinking_glass
/give @s pour_and_seap:wine_glass
```

The old prototype ID `poursip` is deliberately replaced, not migrated.
Remove old `poursip-0.1.0.jar`, and use a backed-up test world.

**Not yet implemented:** Vinery wines/effects, Homeostatic thirst, partial
liquid levels, full multiplayer gameplay testing, drop handling for explosions.
