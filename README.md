# Pour & Seap

Experimental Fabric 1.20.1 mod.

- **Mod ID:** `pour_and_seap`
- **Java package:** `com.mentality.pour_and_seap`
- **License:** All Rights Reserved (not MIT)
- **Version:** 0.2.3 development prototype

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
Use `build/libs/pour_and_seap-0.2.3.jar` in the Fabric 1.20.1 `mods` folder.

## Test
```mcfunction
/give @s pour_and_seap:drinking_glass
/give @s pour_and_seap:wine_glass
```

The old prototype ID `poursip` is deliberately replaced, not migrated.
Remove old `poursip-0.1.0.jar`, and use a backed-up test world.

**Not yet implemented:** Vinery wines/effects, Homeostatic thirst, partial
liquid levels, full multiplayer gameplay testing, drop handling for explosions.

## 0.2.3 model and quality fixes
- Survival collision now matches the glass/goblet selection outline instead of being empty.
- Held glasses use the **same 3D mesh** as placed glassware (with separate first-person and third-person transforms).
- Water and milk use higher-contrast, original colors with a taller filled volume, to be visible through the walls.
- No tooltip on empty vessels; filled ones only say **Water** or **Milk**.
- In-hand drinking uses the vanilla `UseAction.DRINK` animation and the generic drink sound. Placed vessels play a short drink sound.
- A custom player-body animation is deliberately deferred until we can test the particular 3D player-body mod.
- Full in-game visual/collision tests are still required; a successful CI build only proves compilability.


## 0.2.3: simpler solid-liquid glass models

- No custom animation was added: vanilla `UseAction.DRINK` and its sound are preserved.
- Item `display` positions have been corrected separately for tumbler/wine glass and both hands; the wine glass sits higher to make the avatar grip its stem, rather than its bowl. The cup is shifted toward the center during the vanilla drink animation.
- Block/item meshes share only **2 (empty) / 3 (filled) visible cuboid groups for the goblet's stem + bowl**, or **1 / 2 for the tumbler**.
- The side of the glass is an opaque frame with cut-out windows (binary-alpha pixels), not overlapping translucent panes. It uses the client cutout rendering layer to avoid several transparent surfaces masking the liquid, even when shaders are installed.
- Water and milk have new opaque side surfaces, distinctly tinted top surfaces, and a larger volume inside the cup, so the contents are visible from the side.
- The frame and liquid textures are original pixel-art made for Pour & Seap.
- Look-and-feel and hand grip must still be verified in Minecraft with your specific body animation mod and shader pack; Gradle cannot visually validate them.

## v0.2.3 shape, stacking and model improvements

- Both glasses are smaller, and collision/selection boxes are resized.
- Hollow pane-and-rim models with subtle pixel glass glints; opaque blue/white liquid volumes.
- GUI scale reduced so the 3D glass icons remain inside inventory slots.
- **Empty glasses stack to 16.** Filled items are separate registered internal item variants (`*_water`, `*_milk`) that stack to 1. Only empty glasses appear in Creative tabs.
- Filling an empty stack splits one glass off and preserves the other empties. Drinking returns the stackable empty glass.
- Placed glasses retain their liquid when broken or picked up.
- Tooltips say **Liquid: Water** / **Liquid: Milk** (localized to Russian).
- Previous NBT-filled glasses from 0.2.2 can still be read/placed/drunk. Existing block-state values (0/1/2) are unchanged.
- Existing vanilla drinking animation/sound and body-mod-compatible 3D held render are preserved.
- Water source is still a vanilla water potion bottle, not a water bucket. Vinery wines and Homeostatic integration remain deferred.

Note: successful GitHub CI validates compilation and asset PNG checks, **not** actual shader compatibility. Test in a COPY of the world.
