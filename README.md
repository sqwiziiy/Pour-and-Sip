# Pour & Sip — Fabric 1.20.1

**This repository contains source code for an actual Fabric mod, NOT a Minecraft datapack.**

## STATUS (important)

**0.1.0 prototype, source only.** In the original source environment dependency downloading was unavailable, so a compiled `.jar` and successful Minecraft runtime test have NOT been produced/verified. Do not copy the SOURCE ZIP to a Minecraft `mods/` folder. The GitHub workflow builds an installable `.jar` after you push source to a new GitHub repository.

## What the prototype includes
- Two **independent** registry items: `poursip:drinking_glass` and `poursip:wine_glass` (not Candlelight's items).
- New, original 16×16 pixel-art glass textures; empty, blue water and white milk variants.
- Fill **drinking glass**: hold empty glass in **main hand**, a vanilla **water potion bottle** or **milk bucket** in **offhand**, right-click into air. The source is consumed in Survival and a glass bottle / bucket is returned.
- Drink: remove the source from offhand, hold right-click with a filled glass. Glass becomes empty and stays in inventory. Milk clears vanilla potion effects. No custom potion effects.
- Crafting recipes for both empty glasses.
- No changes to Candlelight, including its `Table Set` decorative blocks.

## NOT implemented yet
- **Placing glasses on the ground / liquid rendering inside placed blocks.** Item textures are visibly different already, but these are not yet placeable glasses.
- Vinery **red / white wines** and preservation of wine-specific potion effects.
- Homeostatic thirst updates (water and milk drinking currently only have vanilla-like effects; vanilla water itself normally has no thirst unless integrated). Do not claim thirst works yet.
- Full liquid capacity, partial pour/fill levels, compatibility with other drinks, dedicated-server gameplay verification.

The next development phase must cover these features before the mod is described as finished.

## Build on GitHub

1. Open **Actions → Build Fabric mod JAR** and check the first run.
2. If the build succeeds, download the `poursip-Fabric-1.20.1-JAR` artifact. GitHub gives you a wrapper ZIP with **one `.jar` inside**, not a datapack ZIP.
3. Unzip the artifact and put **`poursip-0.1.0.jar`** in the client `mods/` directory. Requires Fabric Loader and Fabric API for Minecraft 1.20.1.

## Local development

Requires **Java 17**, **Gradle 8.8**, internet access for Minecraft/Fabric dependencies. Run `gradle build`; check `build/libs/poursip-0.1.0.jar`. Note: Gradle wrapper binary is intentionally not included; GitHub Actions provides Gradle.

## Design

- A standalone project without the Storm Survival name or dependency. Small nod to the modpack can live in README later.
- Own vessel items. Existing Candlelight and its `Table Set` stay unchanged.
- Planned: shared red wine color for all Vinery red wines, shared pale yellow for white wines, but keep exact source wine ID to replicate its effects. Milk white, water pale blue.
- Kaleidoscope teas and Brewery beer excluded (they have their own drinkware).

## Project limitations / review checklist

This is hand-authored, previously uncompiled source code. Treat as experimental until GitHub build completes and the first in-game test succeeds. No claim of production readiness.
