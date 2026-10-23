# Pour & Seap

A Minecraft **1.20.1 Fabric** mod that adds placeable drinking glasses and wine goblets. Glassware holds water, milk and (optionally) wines from **[Let's Do] Vinery** and **NetherVinery**.

- Mod ID: `pour_and_seap`
- Java package: `com.mentality.pour_and_seap`
- License: **All Rights Reserved** (Gradle and other third-party tools retain their own licenses)
- Fabric API is **required**; Vinery and NetherVinery are **optional**.

## Features

- Two types of placeable, 3D glassware; empty vessels stack to 16, filled to 1.
- Fill water from a *water potion bottle*, milk from a milk bucket.
- Fill wine from Vinery or NetherVinery into a glass held in your main hand (bottle in offhand), or into an already placed glass.
- Wine is displayed as red or pale gold. The original wine ItemStack is preserved, including NBT, and drinking delegates to its original mod effect logic.
- Pour wine back into its original bottle using a **Vinery empty wine bottle**. This works from held and placed glassware.
- Drinking sound and vanilla held-drink animation; a pouring sound for wine.
- Own **Pour & Seap** advancement tab. Pouring wine into a placed glass counts toward Wine Tasting.
- Russian and English localization.

## Development

Use Java 17, Fabric Loader, and Fabric API for Minecraft 1.20.1.

```bash
./gradlew clean build
```

The built mod is `build/libs/pour_and_seap-0.3.1.jar`.
The source repository's main branch runs a CI build; download the build artifact from GitHub Actions for tests.

## Compatibility and limitations

Vinery and NetherVinery are supported via optional item tags and item inspection. Wine bottle contents and their age data are persisted when the glass is placed and picked back up. The age-dependent effect is calculated by the originating wine mod when consumed.

The compatibility of every wine variant, shader pack and third-person body mod has **not** been automatically verified. Do a world-backup test before updating an existing save. The empty bottle accepted for undo is currently `vinery:wine_bottle`.

Bakery sink ↔ Kaleidoscope kettle is a separate planned compatibility task in [Storm Survival Culinary Compat](https://github.com/sqwiziiy/Storm-Survival-Culinary-Compat/issues/18), not a feature of Pour & Seap.

## Publishing

`.github/workflows/release.yml` is prepared for a verified release tag `v0.3.1` (or a later matching Gradle version).

A Modrinth project must exist, and the repository must have `MODRINTH_TOKEN` as an Actions secret plus `MODRINTH_PROJECT` as an Actions variable (project slug or ID). On release tags, the workflow checks the version, creates a GitHub Release and then publishes to Modrinth **only when both settings are available and the version does not already exist**. No development push to main is published to Modrinth.
