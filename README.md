# 🍷 Pour & Seap

[🇷🇺 Читать на русском](README_RU.md)

[![Build](https://github.com/sqwiziiy/Pour-and-Sip/actions/workflows/build.yml/badge.svg)](https://github.com/sqwiziiy/Pour-and-Sip/actions/workflows/build.yml)
![Minecraft 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-3C8527)
![Fabric](https://img.shields.io/badge/Loader-Fabric-DBD0B4)
![Java 17](https://img.shields.io/badge/Java-17-E76F00)
![GPL-3.0-only](https://img.shields.io/badge/License-GPL--3.0--only-3B57A6)

**Pour & Seap** adds placeable drinking glasses and wine goblets to **Minecraft Java 1.20.1 (Fabric)**. Fill them with water, milk, or wines from **[Let's Do] Vinery** and **NetherVinery**, then drink, carry, or put them on a table.

[Releases](https://github.com/sqwiziiy/Pour-and-Sip/releases) · [Installation](#installation) · [How to use](#how-to-use) · [Changelog](CHANGELOG.md) · [Report a bug](https://github.com/sqwiziiy/Pour-and-Sip/issues)

## Features

- **Two 3D vessels:** a drinking glass and a wine goblet, both placeable.
- Empty glasses stack up to **16**; filled glasses do not stack.
- Support for **water**, **milk**, and optional **Vinery / NetherVinery wines**.
- Separate red and light-golden wine appearances.
- Preserves the wine's original item data, including its **age**, when pouring, placing, breaking, and retrieving a glass.
- Wine effects are delegated to the originating mod's drinking behavior.
- Wine can be poured back into an empty `vinery:wine_bottle` without losing the original wine stack.
- Native Minecraft drinking animation and sounds, plus a bottle-pouring sound.
- Dedicated **Pour & Seap** advancements, with English and Russian translations.

## Using the glassware

### Obtain or craft

Find both glassware items in the **Pour & Seap** Creative tab, or use:

```mcfunction
/give @s pour_and_seap:drinking_glass
/give @s pour_and_seap:wine_glass
```

Crafting recipes are available in your recipe viewer (if installed) and from the vanilla crafting table.

### Filling a glass in your hand

Hold the **empty glass in your main hand** and the source in your **offhand**. Right-click the air:

| Source | Contents | Returned container |
| --- | --- | --- |
| Vanilla water potion bottle | Water | Glass bottle |
| Milk bucket | Milk | Empty bucket |
| Vinery / NetherVinery wine | Corresponding wine | Empty Vinery wine bottle |

Hold right-click on a filled glass to drink. After drinking, the empty glass is kept.

### Filling a placed glass

Place a glass on a surface, then **right-click it with the liquid source**. You can also retrieve a placed glass with its contents by **sneaking and right-clicking with an empty main hand**.

Right-click a **filled** placed glass with an empty hand to drink from it directly. Breaking a glass in Survival drops it with its contents.

### Returning wine to its original bottle

Hold an **empty Vinery wine bottle** and right-click a placed glass containing wine. For a held glass, keep the glass in your main hand and the empty Vinery wine bottle in your offhand, then right-click the air.

The original wine item, including its data, is restored.

## Installation

1. Install **Fabric Loader for Minecraft 1.20.1**, **Fabric API**, and Java 17 or newer.
2. Put the Pour & Seap release `.jar` in the `mods/` directory **on both the server and each connecting client**.
3. Install compatible **[Let's Do] Vinery** and/or **NetherVinery** to enable their wine compatibility. **Neither wine mod is mandatory** if you only want water and milk.
4. Remove older Pour & Seap JAR versions. Back up your world before updating.

## Compatibility

| Integration | Status |
| --- | --- |
| Minecraft 1.20.1 / Fabric API | Required |
| Water bottles and milk buckets | Built-in |
| [Let's Do] Vinery | Optional |
| NetherVinery | Optional |
| Shader packs and 3D player-body mods | May vary by setup |

The optional wine mods retain control of their own age-dependent effects. The empty bottle for returning wine must currently be **Vinery's empty wine bottle**, not a generic Minecraft glass bottle.

## Building from source

Use JDK 17 and the included Gradle Wrapper:

```sh
./gradlew clean build
```

Compiled mod: `build/libs/pour_and_seap-0.3.1.jar`. Automated builds validate model resources and produce a downloadable CI artifact.

- **Mod ID:** `pour_and_seap`
- **Java package:** `com.mentality.pour_and_seap`
- **License:** [GNU GPL-3.0-only](LICENSE), covering this project's source code and original assets.
- Gradle Wrapper is a separate third-party build tool distributed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0); it is not relicensed by this project.

© 2026 Mentality. Not affiliated with Mojang Studios, Fabric, or the Let's Do team.
